// @ts-ignore
/* eslint-disable */
import request from "../../../utils/request";

/** 此处后端没有提供注释 GET /coresource/health */
export async function healthCheck(options?: { [key: string]: any }) {
  return request<string>(`/resource/coresource/health`, {
    method: "GET",
    ...(options || {}),
  });
}

/** 上传文档 POST /coresource/upload/document */
export async function uploadDocument(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.uploadDocumentParams,
  body: {},
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseCoResourceVO>(
    `/resource/coresource/upload/document`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      params: {
        ...params,
      },
      data: body,
      ...(options || {}),
    }
  );
}

/** 上传图片 POST /coresource/upload/picture */
export async function uploadPicture(
  body: {},
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseUploadPictureResult>(
    `/resource/coresource/upload/picture`,
    {
      method: "POST",
      headers: {
        "Content-Type": 'multipart/form-data',
      },
      data: body,
      ...(options || {}),
    }
  );
}
