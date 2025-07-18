// @ts-ignore
/* eslint-disable */
import request from "../../../utils/request";

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
export async function getChatListByConversationId(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getChatListByConversationIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseChatListVO>(
    `/ai/chatList/getChatListByConversationId`,
    {
      method: "GET",
      params: {
        ...params,
      },
      ...(options || {}),
    }
  );
}

/** 获取当前用户的聊天会话列表 GET /chatList/getUserChatList */
export async function getUserChatList(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getUserChatListParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseListChatListVO>(
    `/ai/chatList/getUserChatList`,
    {
      method: "GET",
      params: {
        ...params,
      },
      ...(options || {}),
    }
  );
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
