declare namespace API {
  type BaseResponseBoolean = {
    code?: number;
    data?: boolean;
    message?: string;
  };

  type BaseResponseChatListVO = {
    code?: number;
    data?: ChatListVO;
    message?: string;
  };

  type BaseResponseFluxString = {
    code?: number;
    data?: string[];
    message?: string;
  };

  type BaseResponseListChatListVO = {
    code?: number;
    data?: ChatListVO[];
    message?: string;
  };

  type BaseResponseString = {
    code?: number;
    data?: string;
    message?: string;
  };

  type ChatListAddRequest = {
    userId?: string;
    conversationId?: string;
    conversationTitle?: string;
  };

  type ChatListUpdateRequest = {
    id?: number;
    userId?: string;
    conversationId?: string;
    conversationTitle?: string;
  };

  type ChatListVO = {
    id?: number;
    userId?: string;
    conversationId?: string;
    conversationTitle?: string;
    createTime?: string;
    updateTime?: string;
    chatMessages?: ChatMessage[];
  };

  type ChatMessage = {
    id?: number;
    conversationId?: string;
    content?: string;
    role?: string;
    tokens?: number;
    createTime?: string;
    updateTime?: string;
    isDelete?: number;
  };

  type doChatByStreamMonoParams = {
    userPrompt: string;
    chatId: string;
  };

  type doChatByStreamParams = {
    userPrompt: string;
    chatId: string;
  };

  type doChatWithRagByStreamParams = {
    userPrompt: string;
    chatId: string;
  };

  type doChatWithToolAndRagParams = {
    userPrompt: string;
    chatId: string;
  };

  type getChatListByConversationIdParams = {
    arg0: string;
  };

  type getUserChatListParams = {
    arg0: string;
  };

  type uploadChatParams = {
    userPrompt: string;
    chatId: string;
  };
}
