import request from "@/utils/request";

// healthCheck
export function getInfo() {
  return request({
    url: `/resource/coresource/health`,
    method: "get",
  });
}

// 上传文档
export function uploadDocument(data) {
  return request({
    url: `/resource/coresource/upload/document`,
    method: "post",
    data,
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
}

// 上传图片
export function uploadPicture(data) {
  return request({
    url: `/resource/coresource/upload/picture`,
    method: "post",
    data,
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
}