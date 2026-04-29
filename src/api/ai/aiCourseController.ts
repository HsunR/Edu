// @ts-ignore
/* eslint-disable */
import request from "../../utils/request";

/** 基础的聊天接口仅提供记忆功能 GET /aiCourse/doChatByStream */
export async function doChatByStream(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.doChatByStreamParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseString[]>(`/ai/aiCourse/doChatByStream`, {
    method: "GET",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /aiCourse/doChatByStreamMono */
export async function doChatByStreamMono(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.doChatByStreamMonoParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseFluxString>(`/ai/aiCourse/doChatByStreamMono`, {
    method: "GET",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 开启了RAG的聊天接口 GET /aiCourse/doChatWithRagByStream */
export async function doChatWithRagByStream(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.doChatWithRagByStreamParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseString[]>(`/ai/aiCourse/doChatWithRagByStream`, {
    method: "GET",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 开启了工具调用和RAG的聊天接口(多模态响应) GET /aiCourse/doChatWithToolAndRagByStream */
export async function doChatWithToolAndRag(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.doChatWithToolAndRagParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseString[]>(
    `/ai/aiCourse/doChatWithToolAndRagByStream`,
    {
      method: "GET",
      params: {
        ...params,
      },
      ...(options || {}),
    }
  );
}

/** 此处后端没有提供注释 POST /aiCourse/uploadChat */
export async function uploadChat(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.uploadChatParams,
  body: {},
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseString[]>(`/ai/aiCourse/uploadChat`, {
    method: "POST",
    headers: {
      "Content-Type": "multipart/form-data",
    },
    params: {
      ...params,
    },
    data: body,
    ...(options || {}),
  });
}
