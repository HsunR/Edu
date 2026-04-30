// @ts-ignore
/* eslint-disable */
import request from "../../utils/request";

/**  POST /upload */
export async function uploadFile(body: {}, options?: { [key: string]: any }) {
  return request<string>(`/ai/upload`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
