# OpenAPI definition


**简介**:OpenAPI definition


**HOST**:http://192.168.101.1:8892/api/resource


**联系人**:


**Version**:v0


**接口路径**:/api/resource/v3/api-docs


[TOC]






# 资源管理


## 分页查询我的资源


**接口地址**:`/api/resource/resources`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>根据查询条件分页查询当前用户上传的资源列表</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|arg0|资源查询请求对象|query|true|ResourceQueryRequest|ResourceQueryRequest|
|&emsp;&emsp;current|||false|integer(int32)||
|&emsp;&emsp;pageSize|||false|integer(int32)||
|&emsp;&emsp;sortField|||false|string||
|&emsp;&emsp;sortOrder|||false|string||
|&emsp;&emsp;resourceName|资源名称（模糊搜索）||false|string||
|&emsp;&emsp;resourceType|资源大类：1=视频 2=文档 3=图片||false|integer(int32)||
|&emsp;&emsp;fileFormat|文件格式：pdf/mp4/docx 等（精确匹配）||false|string||
|&emsp;&emsp;uploadStatus|上传状态：0=待确认 1=成功 2=失败,可用值:1,2,3||false|string||
|&emsp;&emsp;createdFrom|创建时间范围 - 开始||false|string(date-time)||
|&emsp;&emsp;createdTo|创建时间范围 - 结束||false|string(date-time)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|PageResourceVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|records||array|ResourceVO|
|&emsp;&emsp;resourceId||integer(int64)||
|&emsp;&emsp;resourceName||string||
|&emsp;&emsp;resourceType||integer(int32)||
|&emsp;&emsp;fileFormat||string||
|&emsp;&emsp;fileSize||integer(int64)||
|&emsp;&emsp;accessUrl||string||
|&emsp;&emsp;uploadStatus||integer(int32)||
|&emsp;&emsp;createdAt||string(date-time)||
|total||integer(int64)|integer(int64)|
|size||integer(int64)|integer(int64)|
|current||integer(int64)|integer(int64)|
|orders||array|OrderItem|
|&emsp;&emsp;column||string||
|&emsp;&emsp;asc||boolean||
|optimizeCountSql||PageResourceVO|PageResourceVO|
|&emsp;&emsp;records||array|ResourceVO|
|&emsp;&emsp;&emsp;&emsp;resourceId||integer||
|&emsp;&emsp;&emsp;&emsp;resourceName||string||
|&emsp;&emsp;&emsp;&emsp;resourceType||integer||
|&emsp;&emsp;&emsp;&emsp;fileFormat||string||
|&emsp;&emsp;&emsp;&emsp;fileSize||integer||
|&emsp;&emsp;&emsp;&emsp;accessUrl||string||
|&emsp;&emsp;&emsp;&emsp;uploadStatus||integer||
|&emsp;&emsp;&emsp;&emsp;createdAt||string||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;size||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;orders||array|OrderItem|
|&emsp;&emsp;&emsp;&emsp;column||string||
|&emsp;&emsp;&emsp;&emsp;asc||boolean||
|&emsp;&emsp;optimizeCountSql||PageResourceVO|PageResourceVO|
|&emsp;&emsp;searchCount||PageResourceVO|PageResourceVO|
|&emsp;&emsp;optimizeJoinOfCountSql||boolean||
|&emsp;&emsp;maxLimit||integer(int64)||
|&emsp;&emsp;countId||string||
|&emsp;&emsp;pages||integer(int64)||
|searchCount||PageResourceVO|PageResourceVO|
|&emsp;&emsp;records||array|ResourceVO|
|&emsp;&emsp;&emsp;&emsp;resourceId||integer||
|&emsp;&emsp;&emsp;&emsp;resourceName||string||
|&emsp;&emsp;&emsp;&emsp;resourceType||integer||
|&emsp;&emsp;&emsp;&emsp;fileFormat||string||
|&emsp;&emsp;&emsp;&emsp;fileSize||integer||
|&emsp;&emsp;&emsp;&emsp;accessUrl||string||
|&emsp;&emsp;&emsp;&emsp;uploadStatus||integer||
|&emsp;&emsp;&emsp;&emsp;createdAt||string||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;size||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;orders||array|OrderItem|
|&emsp;&emsp;&emsp;&emsp;column||string||
|&emsp;&emsp;&emsp;&emsp;asc||boolean||
|&emsp;&emsp;optimizeCountSql||PageResourceVO|PageResourceVO|
|&emsp;&emsp;searchCount||PageResourceVO|PageResourceVO|
|&emsp;&emsp;optimizeJoinOfCountSql||boolean||
|&emsp;&emsp;maxLimit||integer(int64)||
|&emsp;&emsp;countId||string||
|&emsp;&emsp;pages||integer(int64)||
|optimizeJoinOfCountSql||boolean||
|maxLimit||integer(int64)|integer(int64)|
|countId||string||
|pages||integer(int64)|integer(int64)|


**响应示例**:
```javascript
{
	"records": [
		{
			"resourceId": 0,
			"resourceName": "",
			"resourceType": 0,
			"fileFormat": "",
			"fileSize": 0,
			"accessUrl": "",
			"uploadStatus": 0,
			"createdAt": ""
		}
	],
	"total": 0,
	"size": 0,
	"current": 0,
	"orders": [
		{
			"column": "",
			"asc": true
		}
	],
	"optimizeCountSql": {
		"records": [],
		"total": 0,
		"size": 0,
		"current": 0,
		"orders": [],
		"optimizeCountSql": "",
		"searchCount": "",
		"optimizeJoinOfCountSql": true,
		"maxLimit": 0,
		"countId": "",
		"pages": 0
	},
	"searchCount": {
		"records": [],
		"total": 0,
		"size": 0,
		"current": 0,
		"orders": [],
		"optimizeCountSql": "",
		"searchCount": "",
		"optimizeJoinOfCountSql": true,
		"maxLimit": 0,
		"countId": "",
		"pages": 0
	},
	"optimizeJoinOfCountSql": true,
	"maxLimit": 0,
	"countId": "",
	"pages": 0
}
```


## 获取资源详情


**接口地址**:`/api/resource/resources/{resourceId}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>根据资源ID获取资源的详细信息</p>



**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResourceDetailVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|resourceId||integer(int64)|integer(int64)|
|resourceName||string||
|resourceType||integer(int32)|integer(int32)|
|fileFormat||string||
|fileSize||integer(int64)|integer(int64)|
|accessUrl||string||
|uploadStatus||integer(int32)|integer(int32)|
|createdAt||string(date-time)|string(date-time)|
|uploaderId|上传者id|integer(int64)|integer(int64)|
|videoMeta||VideoMetaVO|VideoMetaVO|
|&emsp;&emsp;duration||integer(int32)||
|&emsp;&emsp;coverUrl||string||
|&emsp;&emsp;definition||string||
|&emsp;&emsp;transcodeStatus||integer(int32)||


**响应示例**:
```javascript
{
	"resourceId": 0,
	"resourceName": "",
	"resourceType": 0,
	"fileFormat": "",
	"fileSize": 0,
	"accessUrl": "",
	"uploadStatus": 0,
	"createdAt": "",
	"uploaderId": 0,
	"videoMeta": {
		"duration": 0,
		"coverUrl": "",
		"definition": "",
		"transcodeStatus": 0
	}
}
```


## 删除资源


**接口地址**:`/api/resource/resources/{resourceId}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>根据资源ID删除资源，只有资源所有者可以删除</p>



**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 确认资源上传完成


**接口地址**:`/api/resource/resources/confirm`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>客户端上传完成后调用此接口，确认资源已成功上传并持久化相关信息</p>



**请求示例**:


```javascript
{
  "resourceId": 123456789
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|uploadConfirmRequest|上传确认请求|body|true|UploadConfirmRequest|UploadConfirmRequest|
|&emsp;&emsp;resourceId|申请预签名返回的 resourceId||true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResourceVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|resourceId||integer(int64)|integer(int64)|
|resourceName||string||
|resourceType||integer(int32)|integer(int32)|
|fileFormat||string||
|fileSize||integer(int64)|integer(int64)|
|accessUrl||string||
|uploadStatus||integer(int32)|integer(int32)|
|createdAt||string(date-time)|string(date-time)|


**响应示例**:
```javascript
{
	"resourceId": 0,
	"resourceName": "",
	"resourceType": 0,
	"fileFormat": "",
	"fileSize": 0,
	"accessUrl": "",
	"uploadStatus": 0,
	"createdAt": ""
}
```


## 确认视频资源上传完成


**接口地址**:`/api/resource/resources/confirm/video`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>客户端上传完成后调用此接口，确认视频资源已成功上传并持久化相关信息</p>



**请求示例**:


```javascript
{
  "resourceId": 12345,
  "vodSessionKey": "abcde12345"
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|videoConfirmRequest|VideoConfirmRequest|body|true|VideoConfirmRequest|VideoConfirmRequest|
|&emsp;&emsp;resourceId|申请预签名返回的 resourceId||true|integer(int64)||
|&emsp;&emsp;vodSessionKey|申请上传返回的 sessionKey||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResourceDetailVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|resourceId||integer(int64)|integer(int64)|
|resourceName||string||
|resourceType||integer(int32)|integer(int32)|
|fileFormat||string||
|fileSize||integer(int64)|integer(int64)|
|accessUrl||string||
|uploadStatus||integer(int32)|integer(int32)|
|createdAt||string(date-time)|string(date-time)|
|uploaderId|上传者id|integer(int64)|integer(int64)|
|videoMeta||VideoMetaVO|VideoMetaVO|
|&emsp;&emsp;duration||integer(int32)||
|&emsp;&emsp;coverUrl||string||
|&emsp;&emsp;definition||string||
|&emsp;&emsp;transcodeStatus||integer(int32)||


**响应示例**:
```javascript
{
	"resourceId": 0,
	"resourceName": "",
	"resourceType": 0,
	"fileFormat": "",
	"fileSize": 0,
	"accessUrl": "",
	"uploadStatus": 0,
	"createdAt": "",
	"uploaderId": 0,
	"videoMeta": {
		"duration": 0,
		"coverUrl": "",
		"definition": "",
		"transcodeStatus": 0
	}
}
```


## 生成文档资源的预签名URL


**接口地址**:`/api/resource/resources/presign/document`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>根据请求参数生成用于上传文档资源的预签名URL</p>



**请求示例**:


```javascript
{
  "fileName": "example.jpg",
  "fileSize": 102400
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|presignRequest|预签名请求对象|body|true|PresignRequest|PresignRequest|
|&emsp;&emsp;fileName|原始文件名（含后缀）||true|string||
|&emsp;&emsp;fileSize|文件大小||true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|PresignedUrlVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|resourceId|预创建的资源记录 ID|integer(int64)|integer(int64)|
|uploadUrl|预签名上传 URL|string||
|storageKey|COS 存储键（前端上传时需要）|string||
|accessUrl|上传完成后的访问地址|string||
|expiresIn|预签名 URL 的过期时间（秒）|integer(int64)|integer(int64)|


**响应示例**:
```javascript
{
	"resourceId": 12345,
	"uploadUrl": "https://bucket-name.cos.region.myqcloud.com/object-key?signature=xxx",
	"storageKey": "object-key",
	"accessUrl": "https://bucket-name.cos.region.myqcloud.com/object-key",
	"expiresIn": 3600
}
```


## 生成图片资源的预签名URL


**接口地址**:`/api/resource/resources/presign/image`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>根据请求参数生成用于上传图片资源的预签名URL</p>



**请求示例**:


```javascript
{
  "fileName": "example.jpg",
  "fileSize": 102400
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|presignRequest|预签名请求对象|body|true|PresignRequest|PresignRequest|
|&emsp;&emsp;fileName|原始文件名（含后缀）||true|string||
|&emsp;&emsp;fileSize|文件大小||true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|PresignedUrlVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|resourceId|预创建的资源记录 ID|integer(int64)|integer(int64)|
|uploadUrl|预签名上传 URL|string||
|storageKey|COS 存储键（前端上传时需要）|string||
|accessUrl|上传完成后的访问地址|string||
|expiresIn|预签名 URL 的过期时间（秒）|integer(int64)|integer(int64)|


**响应示例**:
```javascript
{
	"resourceId": 12345,
	"uploadUrl": "https://bucket-name.cos.region.myqcloud.com/object-key?signature=xxx",
	"storageKey": "object-key",
	"accessUrl": "https://bucket-name.cos.region.myqcloud.com/object-key",
	"expiresIn": 3600
}
```


## 生成视频资源的预签名URL


**接口地址**:`/api/resource/resources/presign/video`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>根据请求参数生成用于上传视频资源的预签名URL</p>



**请求示例**:


```javascript
{
  "fileName": "example.jpg",
  "fileSize": 102400
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|presignRequest|预签名请求对象|body|true|PresignRequest|PresignRequest|
|&emsp;&emsp;fileName|原始文件名（含后缀）||true|string||
|&emsp;&emsp;fileSize|文件大小||true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|VodPresignedUrlVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|resourceId||integer(int64)|integer(int64)|
|vodSessionKey||string||
|mediaUploadUrls||array||
|coverUploadUrl||string||
|expiresIn||integer(int64)|integer(int64)|


**响应示例**:
```javascript
{
	"resourceId": 0,
	"vodSessionKey": "",
	"mediaUploadUrls": [],
	"coverUploadUrl": "",
	"expiresIn": 0
}
```