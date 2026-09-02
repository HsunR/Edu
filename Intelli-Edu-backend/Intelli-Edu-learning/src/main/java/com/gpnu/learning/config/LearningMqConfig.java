package com.gpnu.learning.config;

import com.gpnu.common.constants.mq.MqConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LearningMqConfig {

    @Bean
    public Queue profileSheetGradedQueue() {
        return new Queue(MqConstants.Queue.PROFILE_SHEET_GRADED, true);
    }

    @Bean
    public Binding profileSheetGradedBinding(Queue profileSheetGradedQueue,
                                             TopicExchange intelliEduExchange) {
        return BindingBuilder.bind(profileSheetGradedQueue)
                .to(intelliEduExchange)
                .with(MqConstants.RoutingKey.EXAM_SHEET_GRADED);
    }
}
