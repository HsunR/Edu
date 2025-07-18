// @ts-ignore
/* eslint-disable */
import request from "../../../utils/request";

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
        "Content-Type": "application/json",
      },
      data: body,
      ...(options || {}),
    }
  );
}

/** 上传视频 POST /coresource/upload/video */
export async function uploadVideo(body: {}, options?: { [key: string]: any }) {
  return request<API.BaseResponseCoResourceVO>(
    `/resource/coresource/upload/video`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      data: body,
      ...(options || {}),
    }
  );
}
