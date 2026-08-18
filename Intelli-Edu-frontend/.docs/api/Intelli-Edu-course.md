# OpenAPI definition


**简介**:OpenAPI definition


**HOST**:http://192.168.101.1:8893/api/course


**联系人**:


**Version**:v0


**接口路径**:/api/course/v3/api-docs


[TOC]






# 班级管理


## 创建班级


**接口地址**:`/api/course/classes`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "courseId": 123,
  "className": "2024级软件工程1班",
  "maxStudents": 100,
  "startDate": "2024-09-01T00:00:00Z",
  "endDate": "2025-06-30T23:59:59Z"
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|classCreateRequest|创建班级请求对象|body|true|ClassCreateRequest|ClassCreateRequest|
|&emsp;&emsp;courseId|课程id||true|integer(int64)||
|&emsp;&emsp;className|班级名称||true|string||
|&emsp;&emsp;maxStudents|班级学生人数上限||false|integer(int32)||
|&emsp;&emsp;startDate|开课日期||false|string(date-time)||
|&emsp;&emsp;endDate|结课日期||false|string(date-time)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ClassVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|classId||integer(int64)|integer(int64)|
|courseId||integer(int64)|integer(int64)|
|courseName||string||
|className||string||
|teacherId||integer(int64)|integer(int64)|
|teacherName||string||
|inviteCode||string||
|maxStudents||integer(int32)|integer(int32)|
|currentStudents||integer(int32)|integer(int32)|
|startDate||string(date-time)|string(date-time)|
|endDate||string(date-time)|string(date-time)|
|status||integer(int32)|integer(int32)|
|createdAt||string(date-time)|string(date-time)|


**响应示例**:
```javascript
{
	"classId": 0,
	"courseId": 0,
	"courseName": "",
	"className": "",
	"teacherId": 0,
	"teacherName": "",
	"inviteCode": "",
	"maxStudents": 0,
	"currentStudents": 0,
	"startDate": "",
	"endDate": "",
	"status": 0,
	"createdAt": ""
}
```


## 更新班级信息


**接口地址**:`/api/course/classes/{classId}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "className": "2024级软件工程1班",
  "maxStudents": 100,
  "startDate": "2024-09-01T00:00:00Z",
  "endDate": "2025-06-30T23:59:59Z",
  "status": 0
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|classId||path|true|integer(int64)||
|classUpdateRequest|更新班级信息请求对象|body|true|ClassUpdateRequest|ClassUpdateRequest|
|&emsp;&emsp;className|班级名称||true|string||
|&emsp;&emsp;maxStudents|班级学生人数上限||false|integer(int32)||
|&emsp;&emsp;startDate|开课日期||false|string(date-time)||
|&emsp;&emsp;endDate|结课日期||false|string(date-time)||
|&emsp;&emsp;status|班级状态：0=招生中 1=进行中 2=已结束||false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ClassVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|classId||integer(int64)|integer(int64)|
|courseId||integer(int64)|integer(int64)|
|courseName||string||
|className||string||
|teacherId||integer(int64)|integer(int64)|
|teacherName||string||
|inviteCode||string||
|maxStudents||integer(int32)|integer(int32)|
|currentStudents||integer(int32)|integer(int32)|
|startDate||string(date-time)|string(date-time)|
|endDate||string(date-time)|string(date-time)|
|status||integer(int32)|integer(int32)|
|createdAt||string(date-time)|string(date-time)|


**响应示例**:
```javascript
{
	"classId": 0,
	"courseId": 0,
	"courseName": "",
	"className": "",
	"teacherId": 0,
	"teacherName": "",
	"inviteCode": "",
	"maxStudents": 0,
	"currentStudents": 0,
	"startDate": "",
	"endDate": "",
	"status": 0,
	"createdAt": ""
}
```


## 班级成员列表


**接口地址**:`/api/course/classes/{classId}/members`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|classId||path|true|integer(int64)||
|pageRequest||query|true|PageRequest|PageRequest|
|&emsp;&emsp;current|||false|integer(int32)||
|&emsp;&emsp;pageSize|||false|integer(int32)||
|&emsp;&emsp;sortField|||false|string||
|&emsp;&emsp;sortOrder|||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|PageClassMemberVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|records||array|ClassMemberVO|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;studentId||integer(int64)||
|&emsp;&emsp;studentName||string||
|&emsp;&emsp;avatarUrl||string||
|&emsp;&emsp;status||integer(int32)||
|&emsp;&emsp;joinedAt||string(date-time)||
|total||integer(int64)|integer(int64)|
|size||integer(int64)|integer(int64)|
|current||integer(int64)|integer(int64)|
|orders||array|OrderItem|
|&emsp;&emsp;id|章节id|integer(int64)||
|&emsp;&emsp;orderIndex|排序索引，0开始|integer(int32)||
|optimizeCountSql||PageClassMemberVO|PageClassMemberVO|
|&emsp;&emsp;records||array|ClassMemberVO|
|&emsp;&emsp;&emsp;&emsp;id||integer||
|&emsp;&emsp;&emsp;&emsp;studentId||integer||
|&emsp;&emsp;&emsp;&emsp;studentName||string||
|&emsp;&emsp;&emsp;&emsp;avatarUrl||string||
|&emsp;&emsp;&emsp;&emsp;status||integer||
|&emsp;&emsp;&emsp;&emsp;joinedAt||string||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;size||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;orders|章节排序项|array|OrderItem|
|&emsp;&emsp;&emsp;&emsp;id|章节id|integer||
|&emsp;&emsp;&emsp;&emsp;orderIndex|排序索引，0开始|integer||
|&emsp;&emsp;optimizeCountSql||PageClassMemberVO|PageClassMemberVO|
|&emsp;&emsp;searchCount||PageClassMemberVO|PageClassMemberVO|
|&emsp;&emsp;optimizeJoinOfCountSql||boolean||
|&emsp;&emsp;maxLimit||integer(int64)||
|&emsp;&emsp;countId||string||
|&emsp;&emsp;pages||integer(int64)||
|searchCount||PageClassMemberVO|PageClassMemberVO|
|&emsp;&emsp;records||array|ClassMemberVO|
|&emsp;&emsp;&emsp;&emsp;id||integer||
|&emsp;&emsp;&emsp;&emsp;studentId||integer||
|&emsp;&emsp;&emsp;&emsp;studentName||string||
|&emsp;&emsp;&emsp;&emsp;avatarUrl||string||
|&emsp;&emsp;&emsp;&emsp;status||integer||
|&emsp;&emsp;&emsp;&emsp;joinedAt||string||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;size||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;orders|章节排序项|array|OrderItem|
|&emsp;&emsp;&emsp;&emsp;id|章节id|integer||
|&emsp;&emsp;&emsp;&emsp;orderIndex|排序索引，0开始|integer||
|&emsp;&emsp;optimizeCountSql||PageClassMemberVO|PageClassMemberVO|
|&emsp;&emsp;searchCount||PageClassMemberVO|PageClassMemberVO|
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
			"id": 0,
			"studentId": 0,
			"studentName": "",
			"avatarUrl": "",
			"status": 0,
			"joinedAt": ""
		}
	],
	"total": 0,
	"size": 0,
	"current": 0,
	"orders": [
		{
			"id": 0,
			"orderIndex": 0
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


## 移除学生


**接口地址**:`/api/course/classes/{classId}/members/{memberId}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|classId||path|true|integer(int64)||
|memberId||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 退出班级


**接口地址**:`/api/course/classes/{classId}/quit`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|classId||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 通过邀请码加入班级


**接口地址**:`/api/course/classes/join`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "inviteCode": "AS3C3A31"
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|joinClassRequest|加入班级请求对象|body|true|JoinClassRequest|JoinClassRequest|
|&emsp;&emsp;inviteCode|邀请码||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ClassVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|classId||integer(int64)|integer(int64)|
|courseId||integer(int64)|integer(int64)|
|courseName||string||
|className||string||
|teacherId||integer(int64)|integer(int64)|
|teacherName||string||
|inviteCode||string||
|maxStudents||integer(int32)|integer(int32)|
|currentStudents||integer(int32)|integer(int32)|
|startDate||string(date-time)|string(date-time)|
|endDate||string(date-time)|string(date-time)|
|status||integer(int32)|integer(int32)|
|createdAt||string(date-time)|string(date-time)|


**响应示例**:
```javascript
{
	"classId": 0,
	"courseId": 0,
	"courseName": "",
	"className": "",
	"teacherId": 0,
	"teacherName": "",
	"inviteCode": "",
	"maxStudents": 0,
	"currentStudents": 0,
	"startDate": "",
	"endDate": "",
	"status": 0,
	"createdAt": ""
}
```


## 我加入的班级列表


**接口地址**:`/api/course/classes/my`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ClassVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|classId||integer(int64)|integer(int64)|
|courseId||integer(int64)|integer(int64)|
|courseName||string||
|className||string||
|teacherId||integer(int64)|integer(int64)|
|teacherName||string||
|inviteCode||string||
|maxStudents||integer(int32)|integer(int32)|
|currentStudents||integer(int32)|integer(int32)|
|startDate||string(date-time)|string(date-time)|
|endDate||string(date-time)|string(date-time)|
|status||integer(int32)|integer(int32)|
|createdAt||string(date-time)|string(date-time)|


**响应示例**:
```javascript
[
	{
		"classId": 0,
		"courseId": 0,
		"courseName": "",
		"className": "",
		"teacherId": 0,
		"teacherName": "",
		"inviteCode": "",
		"maxStudents": 0,
		"currentStudents": 0,
		"startDate": "",
		"endDate": "",
		"status": 0,
		"createdAt": ""
	}
]
```


# 节管理


## 添加节


**接口地址**:`/api/course/chapters/{chapterId}/sections`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "title": "1-1 JAVA的发展历史",
  "isFree": 0
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|chapterId||path|true|integer(int64)||
|sectionCreateRequest|课程节点创建请求对象|body|true|SectionCreateRequest|SectionCreateRequest|
|&emsp;&emsp;title|课程节点标题||true|string||
|&emsp;&emsp;isFree|是否免费预览,0-否，1-是||false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|SectionVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|sectionId||integer(int64)|integer(int64)|
|chapterId||integer(int64)|integer(int64)|
|title||string||
|orderIndex||integer(int32)|integer(int32)|
|isFree||integer(int32)|integer(int32)|
|resources||array|SectionResourceVO|


**响应示例**:
```javascript
{
	"sectionId": 0,
	"chapterId": 0,
	"title": "",
	"orderIndex": 0,
	"isFree": 0,
	"resources": [
		{}
	]
}
```


## 批量调整节排序


**接口地址**:`/api/course/chapters/{chapterId}/sections/order`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
[
  {
    "id": 0,
    "orderIndex": 0
  }
]
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|chapterId||path|true|integer(int64)||
|orderItems|章节排序项|body|true|array|OrderItem|
|&emsp;&emsp;id|章节id||true|integer(int64)||
|&emsp;&emsp;orderIndex|排序索引，0开始||true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 更新节


**接口地址**:`/api/course/sections/{sectionId}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "title": "1-1 计算机网络发展",
  "isFree": 0
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|sectionId||path|true|integer(int64)||
|sectionUpdateRequest|小节更新请求对象|body|true|SectionUpdateRequest|SectionUpdateRequest|
|&emsp;&emsp;title|小节标题||false|string||
|&emsp;&emsp;isFree|是否免费预览,0-否，1-是||false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|SectionVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|sectionId||integer(int64)|integer(int64)|
|chapterId||integer(int64)|integer(int64)|
|title||string||
|orderIndex||integer(int32)|integer(int32)|
|isFree||integer(int32)|integer(int32)|
|resources||array|SectionResourceVO|


**响应示例**:
```javascript
{
	"sectionId": 0,
	"chapterId": 0,
	"title": "",
	"orderIndex": 0,
	"isFree": 0,
	"resources": [
		{}
	]
}
```


## 删除节


**接口地址**:`/api/course/sections/{sectionId}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>级联删除节-资源关联</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|sectionId||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 获取节详情


**接口地址**:`/api/course/sections/{sectionId}/detail`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>含资源访问地址，需权限校验</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|sectionId||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|SectionDetailVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|sectionId||integer(int64)|integer(int64)|
|chapterId||integer(int64)|integer(int64)|
|title||string||
|orderIndex||integer(int32)|integer(int32)|
|isFree||integer(int32)|integer(int32)|
|resources||array|SectionResourceVO|
|resourceDetails||array|ResourceSimpleDTO|
|&emsp;&emsp;resourceId||integer(int64)||
|&emsp;&emsp;resourceName||string||
|&emsp;&emsp;resourceType||integer(int32)||
|&emsp;&emsp;fileFormat||string||
|&emsp;&emsp;accessUrl||string||
|&emsp;&emsp;fileSize||integer(int64)||


**响应示例**:
```javascript
{
	"sectionId": 0,
	"chapterId": 0,
	"title": "",
	"orderIndex": 0,
	"isFree": 0,
	"resources": [
		{}
	],
	"resourceDetails": [
		{
			"resourceId": 0,
			"resourceName": "",
			"resourceType": 0,
			"fileFormat": "",
			"accessUrl": "",
			"fileSize": 0
		}
	]
}
```


## 添加资源到节


**接口地址**:`/api/course/sections/{sectionId}/resources`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "resourceId": 123456,
  "resourceType": "VIDEO"
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|sectionId||path|true|integer(int64)||
|sectionResourceAddRequest|章节资源添加请求对象|body|true|SectionResourceAddRequest|SectionResourceAddRequest|
|&emsp;&emsp;resourceId|资源id，视频资源是视频id，文档资源是文档id，图片资源是图片id||true|integer(int64)||
|&emsp;&emsp;resourceType|资源类型，VIDEO：视频，DOCUMENT：文档，IMAGE：图片||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|SectionResourceVO|


**响应参数**:


暂无


**响应示例**:
```javascript
null
```


## 移除节内资源


**接口地址**:`/api/course/sections/{sectionId}/resources/{id}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|sectionId||path|true|integer(int64)||
|id||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 调整节内资源顺序


**接口地址**:`/api/course/sections/{sectionId}/resources/order`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
[
  {
    "id": 0,
    "orderIndex": 0
  }
]
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|sectionId||path|true|integer(int64)||
|orderItems|章节排序项|body|true|array|OrderItem|
|&emsp;&emsp;id|章节id||true|integer(int64)||
|&emsp;&emsp;orderIndex|排序索引，0开始||true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


# 课程分类


## 获取分类树


**接口地址**:`/api/course/categories/`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>全量返回，前端缓存</p>



**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|CategoryVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|categoryId||integer(int64)|integer(int64)|
|name||string||
|parentId||integer(int64)|integer(int64)|
|orderIndex||integer(int32)|integer(int32)|
|children||array|CategoryVO|
|&emsp;&emsp;categoryId||integer(int64)||
|&emsp;&emsp;name||string||
|&emsp;&emsp;parentId||integer(int64)||
|&emsp;&emsp;orderIndex||integer(int32)||
|&emsp;&emsp;children||array|CategoryVO|


**响应示例**:
```javascript
[
	{
		"categoryId": 0,
		"name": "",
		"parentId": 0,
		"orderIndex": 0,
		"children": [
			{
				"categoryId": 0,
				"name": "",
				"parentId": 0,
				"orderIndex": 0,
				"children": []
			}
		]
	}
]
```


# 课程管理


## 浏览公开课程


**接口地址**:`/api/course/courses`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>分页+分类过滤+关键词搜索</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|request|课程查询请求对象，包含分页信息和过滤条件|query|true|CourseQueryRequest|CourseQueryRequest|
|&emsp;&emsp;current|||false|integer(int32)||
|&emsp;&emsp;pageSize|||false|integer(int32)||
|&emsp;&emsp;sortField|||false|string||
|&emsp;&emsp;sortOrder|||false|string||
|&emsp;&emsp;categoryId|课程分类，根据课程分类Id过滤||false|integer(int64)||
|&emsp;&emsp;courseName|课程名称，根据课程名称模糊匹配过滤||false|string||
|&emsp;&emsp;status|课程状态，根据课程状态过滤，0：草稿，1：已发布，2：已归档||false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|PageCourseVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|records||array|CourseVO|
|&emsp;&emsp;courseId||integer(int64)||
|&emsp;&emsp;courseName||string||
|&emsp;&emsp;coverUrl||string||
|&emsp;&emsp;description||string||
|&emsp;&emsp;teacherId||integer(int64)||
|&emsp;&emsp;teacherName||string||
|&emsp;&emsp;teacherAvatar||string||
|&emsp;&emsp;categoryId||integer(int64)||
|&emsp;&emsp;categoryName||string||
|&emsp;&emsp;status||integer(int32)||
|&emsp;&emsp;isPublic||integer(int32)||
|&emsp;&emsp;createdAt||string(date-time)||
|total||integer(int64)|integer(int64)|
|size||integer(int64)|integer(int64)|
|current||integer(int64)|integer(int64)|
|orders||array|OrderItem|
|&emsp;&emsp;id|章节id|integer(int64)||
|&emsp;&emsp;orderIndex|排序索引，0开始|integer(int32)||
|optimizeCountSql||PageCourseVO|PageCourseVO|
|&emsp;&emsp;records|课程信息VO|array|CourseVO|
|&emsp;&emsp;&emsp;&emsp;courseId||integer||
|&emsp;&emsp;&emsp;&emsp;courseName||string||
|&emsp;&emsp;&emsp;&emsp;coverUrl||string||
|&emsp;&emsp;&emsp;&emsp;description||string||
|&emsp;&emsp;&emsp;&emsp;teacherId||integer||
|&emsp;&emsp;&emsp;&emsp;teacherName||string||
|&emsp;&emsp;&emsp;&emsp;teacherAvatar||string||
|&emsp;&emsp;&emsp;&emsp;categoryId||integer||
|&emsp;&emsp;&emsp;&emsp;categoryName||string||
|&emsp;&emsp;&emsp;&emsp;status||integer||
|&emsp;&emsp;&emsp;&emsp;isPublic||integer||
|&emsp;&emsp;&emsp;&emsp;createdAt||string||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;size||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;orders|章节排序项|array|OrderItem|
|&emsp;&emsp;&emsp;&emsp;id|章节id|integer||
|&emsp;&emsp;&emsp;&emsp;orderIndex|排序索引，0开始|integer||
|&emsp;&emsp;optimizeCountSql||PageCourseVO|PageCourseVO|
|&emsp;&emsp;searchCount||PageCourseVO|PageCourseVO|
|&emsp;&emsp;optimizeJoinOfCountSql||boolean||
|&emsp;&emsp;maxLimit||integer(int64)||
|&emsp;&emsp;countId||string||
|&emsp;&emsp;pages||integer(int64)||
|searchCount||PageCourseVO|PageCourseVO|
|&emsp;&emsp;records|课程信息VO|array|CourseVO|
|&emsp;&emsp;&emsp;&emsp;courseId||integer||
|&emsp;&emsp;&emsp;&emsp;courseName||string||
|&emsp;&emsp;&emsp;&emsp;coverUrl||string||
|&emsp;&emsp;&emsp;&emsp;description||string||
|&emsp;&emsp;&emsp;&emsp;teacherId||integer||
|&emsp;&emsp;&emsp;&emsp;teacherName||string||
|&emsp;&emsp;&emsp;&emsp;teacherAvatar||string||
|&emsp;&emsp;&emsp;&emsp;categoryId||integer||
|&emsp;&emsp;&emsp;&emsp;categoryName||string||
|&emsp;&emsp;&emsp;&emsp;status||integer||
|&emsp;&emsp;&emsp;&emsp;isPublic||integer||
|&emsp;&emsp;&emsp;&emsp;createdAt||string||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;size||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;orders|章节排序项|array|OrderItem|
|&emsp;&emsp;&emsp;&emsp;id|章节id|integer||
|&emsp;&emsp;&emsp;&emsp;orderIndex|排序索引，0开始|integer||
|&emsp;&emsp;optimizeCountSql||PageCourseVO|PageCourseVO|
|&emsp;&emsp;searchCount||PageCourseVO|PageCourseVO|
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
			"courseId": 0,
			"courseName": "",
			"coverUrl": "",
			"description": "",
			"teacherId": 0,
			"teacherName": "",
			"teacherAvatar": "",
			"categoryId": 0,
			"categoryName": "",
			"status": 0,
			"isPublic": 0,
			"createdAt": ""
		}
	],
	"total": 0,
	"size": 0,
	"current": 0,
	"orders": [
		{
			"id": 0,
			"orderIndex": 0
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


## 创建课程


**接口地址**:`/api/course/courses`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "courseName": "Java编程入门",
  "description": "本课程适合零基础学员，系统讲解Java编程基础知识",
  "coverUrl": "https://example.com/course-cover.jpg",
  "categoryId": 12345,
  "isPublic": 1
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|courseCreateRequest|课程创建请求对象|body|true|CourseCreateRequest|CourseCreateRequest|
|&emsp;&emsp;courseName|课程名称||true|string||
|&emsp;&emsp;description|课程简介||false|string||
|&emsp;&emsp;coverUrl|课程封面URL||false|string||
|&emsp;&emsp;categoryId|课程分类ID||false|integer(int64)||
|&emsp;&emsp;isPublic|课程是否已公开，0=私有 1=公开（未入班可浏览目录）||false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|CourseVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|courseId||integer(int64)|integer(int64)|
|courseName||string||
|coverUrl||string||
|description||string||
|teacherId||integer(int64)|integer(int64)|
|teacherName||string||
|teacherAvatar||string||
|categoryId||integer(int64)|integer(int64)|
|categoryName||string||
|status||integer(int32)|integer(int32)|
|isPublic||integer(int32)|integer(int32)|
|createdAt||string(date-time)|string(date-time)|


**响应示例**:
```javascript
{
	"courseId": 0,
	"courseName": "",
	"coverUrl": "",
	"description": "",
	"teacherId": 0,
	"teacherName": "",
	"teacherAvatar": "",
	"categoryId": 0,
	"categoryName": "",
	"status": 0,
	"isPublic": 0,
	"createdAt": ""
}
```


## 课程详情


**接口地址**:`/api/course/courses/{courseId}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>含完整章节目录树</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|courseId||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|CourseDetailVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|courseId||integer(int64)|integer(int64)|
|courseName||string||
|coverUrl||string||
|description||string||
|teacherId||integer(int64)|integer(int64)|
|teacherName||string||
|teacherAvatar||string||
|categoryId||integer(int64)|integer(int64)|
|categoryName||string||
|status||integer(int32)|integer(int32)|
|isPublic||integer(int32)|integer(int32)|
|createdAt||string(date-time)|string(date-time)|
|chapters||array|ChapterVO|
|&emsp;&emsp;chapterId||integer(int64)||
|&emsp;&emsp;courseId||integer(int64)||
|&emsp;&emsp;title||string||
|&emsp;&emsp;orderIndex||integer(int32)||
|&emsp;&emsp;sections||array|SectionVO|
|&emsp;&emsp;&emsp;&emsp;sectionId||integer||
|&emsp;&emsp;&emsp;&emsp;chapterId||integer||
|&emsp;&emsp;&emsp;&emsp;title||string||
|&emsp;&emsp;&emsp;&emsp;orderIndex||integer||
|&emsp;&emsp;&emsp;&emsp;isFree||integer||
|&emsp;&emsp;&emsp;&emsp;resources||array|SectionResourceVO|


**响应示例**:
```javascript
{
	"courseId": 0,
	"courseName": "",
	"coverUrl": "",
	"description": "",
	"teacherId": 0,
	"teacherName": "",
	"teacherAvatar": "",
	"categoryId": 0,
	"categoryName": "",
	"status": 0,
	"isPublic": 0,
	"createdAt": "",
	"chapters": [
		{
			"chapterId": 0,
			"courseId": 0,
			"title": "",
			"orderIndex": 0,
			"sections": [
				{
					"sectionId": 0,
					"chapterId": 0,
					"title": "",
					"orderIndex": 0,
					"isFree": 0,
					"resources": [
						{}
					]
				}
			]
		}
	]
}
```


## 更新课程基本信息


**接口地址**:`/api/course/courses/{courseId}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "courseName": "Java编程入门",
  "description": "本课程将带你从零开始学习Java编程，涵盖基础语法、面向对象编程、常用库和框架等内容，适合初学者入门。",
  "coverUrl": "https://example.com/course-cover.jpg",
  "categoryId": 123,
  "isPublic": 1
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|courseId||path|true|integer(int64)||
|courseUpdateRequest|课程更新请求对象|body|true|CourseUpdateRequest|CourseUpdateRequest|
|&emsp;&emsp;courseName|课程名称||false|string||
|&emsp;&emsp;description|课程描述||false|string||
|&emsp;&emsp;coverUrl|课程封面url||false|string||
|&emsp;&emsp;categoryId|课程分类id||false|integer(int64)||
|&emsp;&emsp;isPublic|课程是否已公开，0=私有 1=公开（未入班可浏览目录）||false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|CourseVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|courseId||integer(int64)|integer(int64)|
|courseName||string||
|coverUrl||string||
|description||string||
|teacherId||integer(int64)|integer(int64)|
|teacherName||string||
|teacherAvatar||string||
|categoryId||integer(int64)|integer(int64)|
|categoryName||string||
|status||integer(int32)|integer(int32)|
|isPublic||integer(int32)|integer(int32)|
|createdAt||string(date-time)|string(date-time)|


**响应示例**:
```javascript
{
	"courseId": 0,
	"courseName": "",
	"coverUrl": "",
	"description": "",
	"teacherId": 0,
	"teacherName": "",
	"teacherAvatar": "",
	"categoryId": 0,
	"categoryName": "",
	"status": 0,
	"isPublic": 0,
	"createdAt": ""
}
```


## 删除课程


**接口地址**:`/api/course/courses/{courseId}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>仅草稿状态可删</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|courseId||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 归档课程


**接口地址**:`/api/course/courses/{courseId}/archive`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|courseId||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 查看课程下的所有班级


**接口地址**:`/api/course/courses/{courseId}/classes`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|courseId||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ClassVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|classId||integer(int64)|integer(int64)|
|courseId||integer(int64)|integer(int64)|
|courseName||string||
|className||string||
|teacherId||integer(int64)|integer(int64)|
|teacherName||string||
|inviteCode||string||
|maxStudents||integer(int32)|integer(int32)|
|currentStudents||integer(int32)|integer(int32)|
|startDate||string(date-time)|string(date-time)|
|endDate||string(date-time)|string(date-time)|
|status||integer(int32)|integer(int32)|
|createdAt||string(date-time)|string(date-time)|


**响应示例**:
```javascript
[
	{
		"classId": 0,
		"courseId": 0,
		"courseName": "",
		"className": "",
		"teacherId": 0,
		"teacherName": "",
		"inviteCode": "",
		"maxStudents": 0,
		"currentStudents": 0,
		"startDate": "",
		"endDate": "",
		"status": 0,
		"createdAt": ""
	}
]
```


## 发布课程


**接口地址**:`/api/course/courses/{courseId}/publish`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|courseId||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 我教的课程列表


**接口地址**:`/api/course/courses/teaching`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|request|课程查询请求对象，包含分页信息和过滤条件|query|true|CourseQueryRequest|CourseQueryRequest|
|&emsp;&emsp;current|||false|integer(int32)||
|&emsp;&emsp;pageSize|||false|integer(int32)||
|&emsp;&emsp;sortField|||false|string||
|&emsp;&emsp;sortOrder|||false|string||
|&emsp;&emsp;categoryId|课程分类，根据课程分类Id过滤||false|integer(int64)||
|&emsp;&emsp;courseName|课程名称，根据课程名称模糊匹配过滤||false|string||
|&emsp;&emsp;status|课程状态，根据课程状态过滤，0：草稿，1：已发布，2：已归档||false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|PageCourseVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|records||array|CourseVO|
|&emsp;&emsp;courseId||integer(int64)||
|&emsp;&emsp;courseName||string||
|&emsp;&emsp;coverUrl||string||
|&emsp;&emsp;description||string||
|&emsp;&emsp;teacherId||integer(int64)||
|&emsp;&emsp;teacherName||string||
|&emsp;&emsp;teacherAvatar||string||
|&emsp;&emsp;categoryId||integer(int64)||
|&emsp;&emsp;categoryName||string||
|&emsp;&emsp;status||integer(int32)||
|&emsp;&emsp;isPublic||integer(int32)||
|&emsp;&emsp;createdAt||string(date-time)||
|total||integer(int64)|integer(int64)|
|size||integer(int64)|integer(int64)|
|current||integer(int64)|integer(int64)|
|orders||array|OrderItem|
|&emsp;&emsp;id|章节id|integer(int64)||
|&emsp;&emsp;orderIndex|排序索引，0开始|integer(int32)||
|optimizeCountSql||PageCourseVO|PageCourseVO|
|&emsp;&emsp;records|课程信息VO|array|CourseVO|
|&emsp;&emsp;&emsp;&emsp;courseId||integer||
|&emsp;&emsp;&emsp;&emsp;courseName||string||
|&emsp;&emsp;&emsp;&emsp;coverUrl||string||
|&emsp;&emsp;&emsp;&emsp;description||string||
|&emsp;&emsp;&emsp;&emsp;teacherId||integer||
|&emsp;&emsp;&emsp;&emsp;teacherName||string||
|&emsp;&emsp;&emsp;&emsp;teacherAvatar||string||
|&emsp;&emsp;&emsp;&emsp;categoryId||integer||
|&emsp;&emsp;&emsp;&emsp;categoryName||string||
|&emsp;&emsp;&emsp;&emsp;status||integer||
|&emsp;&emsp;&emsp;&emsp;isPublic||integer||
|&emsp;&emsp;&emsp;&emsp;createdAt||string||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;size||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;orders|章节排序项|array|OrderItem|
|&emsp;&emsp;&emsp;&emsp;id|章节id|integer||
|&emsp;&emsp;&emsp;&emsp;orderIndex|排序索引，0开始|integer||
|&emsp;&emsp;optimizeCountSql||PageCourseVO|PageCourseVO|
|&emsp;&emsp;searchCount||PageCourseVO|PageCourseVO|
|&emsp;&emsp;optimizeJoinOfCountSql||boolean||
|&emsp;&emsp;maxLimit||integer(int64)||
|&emsp;&emsp;countId||string||
|&emsp;&emsp;pages||integer(int64)||
|searchCount||PageCourseVO|PageCourseVO|
|&emsp;&emsp;records|课程信息VO|array|CourseVO|
|&emsp;&emsp;&emsp;&emsp;courseId||integer||
|&emsp;&emsp;&emsp;&emsp;courseName||string||
|&emsp;&emsp;&emsp;&emsp;coverUrl||string||
|&emsp;&emsp;&emsp;&emsp;description||string||
|&emsp;&emsp;&emsp;&emsp;teacherId||integer||
|&emsp;&emsp;&emsp;&emsp;teacherName||string||
|&emsp;&emsp;&emsp;&emsp;teacherAvatar||string||
|&emsp;&emsp;&emsp;&emsp;categoryId||integer||
|&emsp;&emsp;&emsp;&emsp;categoryName||string||
|&emsp;&emsp;&emsp;&emsp;status||integer||
|&emsp;&emsp;&emsp;&emsp;isPublic||integer||
|&emsp;&emsp;&emsp;&emsp;createdAt||string||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;size||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;orders|章节排序项|array|OrderItem|
|&emsp;&emsp;&emsp;&emsp;id|章节id|integer||
|&emsp;&emsp;&emsp;&emsp;orderIndex|排序索引，0开始|integer||
|&emsp;&emsp;optimizeCountSql||PageCourseVO|PageCourseVO|
|&emsp;&emsp;searchCount||PageCourseVO|PageCourseVO|
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
			"courseId": 0,
			"courseName": "",
			"coverUrl": "",
			"description": "",
			"teacherId": 0,
			"teacherName": "",
			"teacherAvatar": "",
			"categoryId": 0,
			"categoryName": "",
			"status": 0,
			"isPublic": 0,
			"createdAt": ""
		}
	],
	"total": 0,
	"size": 0,
	"current": 0,
	"orders": [
		{
			"id": 0,
			"orderIndex": 0
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


# 章管理


## 更新章标题


**接口地址**:`/api/course/chapters/{chapterId}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "title": "第一章：课程介绍"
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|chapterId||path|true|integer(int64)||
|chapterUpdateRequest|章节更新请求对象|body|true|ChapterUpdateRequest|ChapterUpdateRequest|
|&emsp;&emsp;title|章节标题||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ChapterVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|chapterId||integer(int64)|integer(int64)|
|courseId||integer(int64)|integer(int64)|
|title||string||
|orderIndex||integer(int32)|integer(int32)|
|sections||array|SectionVO|
|&emsp;&emsp;sectionId||integer(int64)||
|&emsp;&emsp;chapterId||integer(int64)||
|&emsp;&emsp;title||string||
|&emsp;&emsp;orderIndex||integer(int32)||
|&emsp;&emsp;isFree||integer(int32)||
|&emsp;&emsp;resources||array|SectionResourceVO|


**响应示例**:
```javascript
{
	"chapterId": 0,
	"courseId": 0,
	"title": "",
	"orderIndex": 0,
	"sections": [
		{
			"sectionId": 0,
			"chapterId": 0,
			"title": "",
			"orderIndex": 0,
			"isFree": 0,
			"resources": [
				{}
			]
		}
	]
}
```


## 删除章


**接口地址**:`/api/course/chapters/{chapterId}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>级联删除其下所有节和节-资源关联</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|chapterId||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 添加章


**接口地址**:`/api/course/courses/{courseId}/chapters`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "title": "第一章：课程介绍"
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|courseId||path|true|integer(int64)||
|chapterCreateRequest|章节创建请求对象|body|true|ChapterCreateRequest|ChapterCreateRequest|
|&emsp;&emsp;title|章节标题||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ChapterVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|chapterId||integer(int64)|integer(int64)|
|courseId||integer(int64)|integer(int64)|
|title||string||
|orderIndex||integer(int32)|integer(int32)|
|sections||array|SectionVO|
|&emsp;&emsp;sectionId||integer(int64)||
|&emsp;&emsp;chapterId||integer(int64)||
|&emsp;&emsp;title||string||
|&emsp;&emsp;orderIndex||integer(int32)||
|&emsp;&emsp;isFree||integer(int32)||
|&emsp;&emsp;resources||array|SectionResourceVO|


**响应示例**:
```javascript
{
	"chapterId": 0,
	"courseId": 0,
	"title": "",
	"orderIndex": 0,
	"sections": [
		{
			"sectionId": 0,
			"chapterId": 0,
			"title": "",
			"orderIndex": 0,
			"isFree": 0,
			"resources": [
				{}
			]
		}
	]
}
```


## 批量调整章排序


**接口地址**:`/api/course/courses/{courseId}/chapters/order`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
[
  {
    "id": 0,
    "orderIndex": 0
  }
]
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|courseId||path|true|integer(int64)||
|orderItems|章节排序项|body|true|array|OrderItem|
|&emsp;&emsp;id|章节id||true|integer(int64)||
|&emsp;&emsp;orderIndex|排序索引，0开始||true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```