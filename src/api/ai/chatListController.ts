// @ts-ignore
/* eslint-disable */
import { url } from "inspector";
import request from "../../utils/request";

/** 创建聊天会话 POST /chatList/createChatList */
export async function createChatList(
  body: API.ChatListAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>(`/ai/chatList/createChatList`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 根据会话ID获取聊天记录 GET /chatList/getChatListByConversationId */
export async function getChatListByConversationId(conversationId) {
  return request<API.BaseResponseChatListVO>({
    url: `/ai/chatList/getChatListByConversationId?conversationId=${conversationId}`,
  });
}

/** 获取当前用户的聊天会话列表 GET /chatList/getUserChatList */
export async function getUserChatList(userId) {
  return request<API.BaseResponseListChatListVO>({
    url: `/ai/chatList/getUserChatList?userId=${userId}`,
  });
}

/** 更新聊天会话 POST /chatList/updateChatList */
export async function updateChatList(
  body: API.ChatListUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>(`/ai/chatList/updateChatList`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
