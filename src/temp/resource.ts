// @ts-ignore
/* eslint-disable */
import request from "../utils/request";

/** 上传文档 POST /coresource/upload/document */
export async function uploadDocument(data) {
  return request<API.BaseResponseCoResourceVO>({
    url: `/resource/coresource/upload/document`,
    method: "POST",
    headers: {
      "Content-Type": "multipart/form-data",
      },
    data
  });
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
        "Content-Type": "multipart/form-data",
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
        "Content-Type": "multipart/form-data",
      },
      data: body,
      ...(options || {}),
    }
  );
}
