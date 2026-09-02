package com.gpnu.learning.consumer;

import com.gpnu.api.event.SheetGradedEvent;
import com.gpnu.common.constants.mq.MqConstants;
import com.gpnu.common.mq.MqIdempotentHelper;
import com.gpnu.learning.service.GradedEventHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GradedEventConsumer {

    @Resource
    private GradedEventHandler gradedEventHandler;

    @Resource
    private MqIdempotentHelper mqIdempotentHelper;

    @RabbitListener(queues = MqConstants.Queue.PROFILE_SHEET_GRADED)
    public void onSheetGraded(SheetGradedEvent event, Message message) {
        String messageId = message.getMessageProperties().getMessageId();
        log.info("收到答卷批改事件: messageId={}, sheetId={}, submitCount={}",
                messageId, event != null ? event.getSheetId() : null,
                event != null ? event.getSubmitCount() : null);

        if (!mqIdempotentHelper.tryConsume(messageId)) {
            return;
        }

        try {
            gradedEventHandler.handle(event);
            mqIdempotentHelper.markConsumed(messageId);
        } catch (Exception e) {
            mqIdempotentHelper.removeMark(messageId);
            log.error("处理答卷批改事件失败: messageId={}, sheetId={}", messageId,
                    event != null ? event.getSheetId() : null, e);
            throw e;
        }
    }
}
