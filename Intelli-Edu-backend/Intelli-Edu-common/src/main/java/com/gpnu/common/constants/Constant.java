package com.gpnu.common.constants;


public final class Constant {

    private Constant() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }



    public static final String REQUEST_ID_HEADER = "X-Request-Id";


    public static final String REQUEST_ID_MDC_KEY = "requestId";

    // 数据字段 - id
    public static final String DATA_FIELD_NAME_ID = "id";

    // 数据字段 - create_time
    public static final String DATA_FIELD_NAME_CREATE_TIME = "created_at";
    public static final String DATA_FIELD_NAME_CREATE_TIME_CAMEL = "createdAt";

    // 数据字段 - update_time
    public static final String DATA_FIELD_NAME_UPDATE_TIME = "updated_at";
    public static final String DATA_FIELD_NAME_UPDATE_TIME_CAMEL = "updatedAt";

    // 数据字段 - liked_times
    public static final String DATA_FIELD_NAME_LIKED_TIME = "liked_times";
    public static final String DATA_FIELD_NAME_LIKED_TIME_CAMEL = "likedTimes";

    // 数据字段 - creater
    public static final String DATA_FIELD_NAME_CREATER = "creater";

    // 数据字段 - updater
    public static final String DATA_FIELD_NAME_UPDATER = "updater";

    // 数据已经删除标识值
    public static final boolean DATA_DELETE = true;
    // 数据未删除标识值
    public static final boolean DATA_NOT_DELETE = false;

    // 响应结果是否被R标记过
    public static final String BODY_PROCESSED_MARK_HEADER = "IS_BODY_PROCESSED";
}
