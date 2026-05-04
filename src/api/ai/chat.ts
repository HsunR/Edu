import request from '@/api/request'
import type { ChatListVO, ChatListAddRequest, ChatListUpdateRequest } from './types'

export function createChatList(data: ChatListAddRequest) {
  return request.post<boolean>('/api/ai/chatList/createChatList', data)
}

export function getChatListByConversationId(conversationId: string) {
  return request.get<ChatListVO>('/api/ai/chatList/getChatListByConversationId', {
    params: { conversationId }
  })
}

export function getUserChatList(userId: string) {
  return request.get<ChatListVO[]>('/api/ai/chatList/getUserChatList', {
    params: { userId }
  })
}

export function updateChatList(data: ChatListUpdateRequest) {
  return request.post<boolean>('/api/ai/chatList/updateChatList', data)
}
