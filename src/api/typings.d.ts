declare namespace API {
  // 登录和用户
  type BaseResponseBoolean = {
    code?: number;
    data?: boolean;
    message?: string;
  };

  type BaseResponseLoginResult = {
    code?: number;
    data?: LoginResult;
    message?: string;
  };

  type BaseResponsePageUsUserVO = {
    code?: number;
    data?: PageUsUserVO;
    message?: string;
  };

  type BaseResponseUsUserVO = {
    code?: number;
    data?: UsUserVO;
    message?: string;
  };

  type getUserInfoParams = {
    userId: number;
  };

  type LoginRequest = {
    loginType: number;
    username: string;
    password: string;
    mobile: string;
    email: string;
    code: string;
    openId: string;
  };

  type LoginResult = {
    userId?: number;
    userType?: string;
    accessToken?: string;
    refreshToken?: string;
  };

  type OrderItem = {
    column?: string;
    asc?: boolean;
  };

  type PageUsUserVO = {
    records?: UsUserVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageUsUserVO;
    searchCount?: PageUsUserVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type refreshTokenParams = {
    refreshToken: string;
  };

  type RegisterRequest = {
    name: string;
    password: string;
    registerType: number;
    mobile: string;
    email: string;
    code: string;
  };

  type SendLoginCodeRequest = {
    loginType: number;
    mobile: string;
    email: string;
  };

  type SendRegisterCodeRequest = {
    registerType: number;
    mobile: string;
    email: string;
  };

  type UsUserDeleteRequest = {
    userId?: number;
  };

  type UsUserQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    userId?: number;
    name?: string;
    password?: string;
    type?: string;
    sex?: number;
    email?: string;
    mobile?: string;
    school?: string;
  };

  type UsUserUpdateRequest = {
    userId?: number;
    name?: string;
    password?: string;
    sex?: number;
    headPortrait?: string;
    personalSignature?: string;
    school?: string;
  };

  type UsUserVO = {
    userId?: number;
    name?: string;
    password?: string;
    type?: string;
    openId?: string;
    sex?: number;
    email?: string;
    mobile?: string;
    headPortrait?: string;
    personalSignature?: string;
    school?: string;
    createTime?: string;
    updateTime?: string;
  };

  // 资源管理
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
