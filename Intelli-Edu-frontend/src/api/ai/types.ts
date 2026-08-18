export interface ChatMessage {
  id: string
  conversationId: string
  content: string
  role: 'user' | 'assistant' | 'system'
  tokens: number
  createTime: string
  updateTime: string
}

export interface ChatListVO {
  id: string
  userId: string
  conversationId: string
  conversationTitle: string
  createTime: string
  updateTime: string
  chatMessages: ChatMessage[]
}

export interface ChatListAddRequest {
  userId?: string
  conversationId?: string
  conversationTitle?: string
}

export interface ChatListUpdateRequest {
  id: string
  userId?: string
  conversationId?: string
  conversationTitle?: string
}

export interface ChatStreamParams {
  userPrompt: string
  chatId: string
}

export interface UploadChatParams {
  userPrompt: string
  chatId: string
}
