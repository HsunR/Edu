declare namespace API {
  type BaseResponseCoResourceVO = {
    code?: number;
    data?: CoResourceVO;
    message?: string;
  };

  type BaseResponseUploadPictureResult = {
    code?: number;
    data?: UploadPictureResult;
    message?: string;
  };

  type CoResourceVO = {
    resourceId?: number;
    resourceName?: string;
    courseId?: number;
    type?: number;
    resourceLink?: string;
    resourceUuid?: string;
  };

  type uploadDocumentParams = {
    arg0: number;
  };

  type UploadPictureResult = {
    resourceUuid?: string;
    resourceName?: string;
    resourceType?: string;
    resourceLink?: string;
    resourceSize?: number;
    picWidth?: number;
    picHeight?: number;
    picScale?: number;
    thumbnailUrl?: string;
  };
}
