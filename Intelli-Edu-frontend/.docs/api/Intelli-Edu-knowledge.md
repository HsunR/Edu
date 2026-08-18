# OpenAPI definition


**简介**:OpenAPI definition


**HOST**:http://192.168.101.1:8895/api/knowledge


**联系人**:


**Version**:v0


**接口路径**:/api/knowledge/v3/api-docs


[TOC]






# 知识点管理


## 创建知识点


**接口地址**:`/api/knowledge/points`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "pointName": "封装",
  "courseId": 1001,
  "parentId": 100,
  "description": "面向对象的三大特性之一",
  "orderIndex": 0
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|pointCreateRequest|知识点创建请求|body|true|PointCreateRequest|PointCreateRequest|
|&emsp;&emsp;pointName|知识点名称||true|string||
|&emsp;&emsp;courseId|所属课程ID||true|integer(int64)||
|&emsp;&emsp;parentId|父知识点ID，null表示一级知识点||false|integer(int64)||
|&emsp;&emsp;description|知识点描述||false|string||
|&emsp;&emsp;orderIndex|排序序号||false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|KnowledgePointVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|pointId|知识点ID|integer(int64)|integer(int64)|
|pointName|知识点名称|string||
|courseId|所属课程ID|integer(int64)|integer(int64)|
|parentId|父知识点ID，null表示一级知识点|integer(int64)|integer(int64)|
|description|知识点描述|string||
|orderIndex|排序序号|integer(int32)|integer(int32)|
|createdAt|创建时间|string(date-time)|string(date-time)|
|updatedAt|更新时间|string(date-time)|string(date-time)|


**响应示例**:
```javascript
{
	"pointId": 0,
	"pointName": "",
	"courseId": 0,
	"parentId": 0,
	"description": "",
	"orderIndex": 0,
	"createdAt": "",
	"updatedAt": ""
}
```


## 更新知识点


**接口地址**:`/api/knowledge/points/{pointId}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "pointName": "",
  "description": "",
  "orderIndex": 0
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|pointId||path|true|integer(int64)||
|pointUpdateRequest|知识点更新请求|body|true|PointUpdateRequest|PointUpdateRequest|
|&emsp;&emsp;pointName|知识点名称||false|string||
|&emsp;&emsp;description|知识点描述||false|string||
|&emsp;&emsp;orderIndex|排序序号||false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|KnowledgePointVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|pointId|知识点ID|integer(int64)|integer(int64)|
|pointName|知识点名称|string||
|courseId|所属课程ID|integer(int64)|integer(int64)|
|parentId|父知识点ID，null表示一级知识点|integer(int64)|integer(int64)|
|description|知识点描述|string||
|orderIndex|排序序号|integer(int32)|integer(int32)|
|createdAt|创建时间|string(date-time)|string(date-time)|
|updatedAt|更新时间|string(date-time)|string(date-time)|


**响应示例**:
```javascript
{
	"pointId": 0,
	"pointName": "",
	"courseId": 0,
	"parentId": 0,
	"description": "",
	"orderIndex": 0,
	"createdAt": "",
	"updatedAt": ""
}
```


## 删除知识点


**接口地址**:`/api/knowledge/points/{pointId}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|pointId||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 查看知识点关联的题目ID列表


**接口地址**:`/api/knowledge/points/{pointId}/questions`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|pointId||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 批量绑定题目到知识点


**接口地址**:`/api/knowledge/points/{pointId}/questions`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "questionIds": [
    201,
    202,
    203
  ]
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|pointId||path|true|integer(int64)||
|questionBindRequest|知识点绑定题目请求|body|true|QuestionBindRequest|QuestionBindRequest|
|&emsp;&emsp;questionIds|题目ID列表||true|array|integer(int64)|


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 解绑题目


**接口地址**:`/api/knowledge/points/{pointId}/questions/{questionId}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|pointId||path|true|integer(int64)||
|questionId||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 查看知识点关联的章节ID列表


**接口地址**:`/api/knowledge/points/{pointId}/sections`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|pointId||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 批量绑定章节到知识点


**接口地址**:`/api/knowledge/points/{pointId}/sections`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "sectionIds": [
    101,
    102,
    103
  ]
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|pointId||path|true|integer(int64)||
|sectionBindRequest|知识点绑定章节请求|body|true|SectionBindRequest|SectionBindRequest|
|&emsp;&emsp;sectionIds|章节ID列表||true|array|integer(int64)|


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 解绑章节


**接口地址**:`/api/knowledge/points/{pointId}/sections/{sectionId}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|pointId||path|true|integer(int64)||
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


## 查询知识点树


**接口地址**:`/api/knowledge/points/tree`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>返回指定课程的两级知识点树形结构</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|courseId||query|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|KnowledgeTreeVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|pointId|知识点ID|integer(int64)|integer(int64)|
|pointName|知识点名称|string||
|courseId|所属课程ID|integer(int64)|integer(int64)|
|parentId|父知识点ID|integer(int64)|integer(int64)|
|description|知识点描述|string||
|orderIndex|排序序号|integer(int32)|integer(int32)|
|children|子知识点列表|array|KnowledgeTreeVO|
|&emsp;&emsp;pointId|知识点ID|integer(int64)||
|&emsp;&emsp;pointName|知识点名称|string||
|&emsp;&emsp;courseId|所属课程ID|integer(int64)||
|&emsp;&emsp;parentId|父知识点ID|integer(int64)||
|&emsp;&emsp;description|知识点描述|string||
|&emsp;&emsp;orderIndex|排序序号|integer(int32)||
|&emsp;&emsp;children|子知识点列表|array|KnowledgeTreeVO|


**响应示例**:
```javascript
[
	{
		"pointId": 0,
		"pointName": "",
		"courseId": 0,
		"parentId": 0,
		"description": "",
		"orderIndex": 0,
		"children": [
			{
				"pointId": 0,
				"pointName": "",
				"courseId": 0,
				"parentId": 0,
				"description": "",
				"orderIndex": 0,
				"children": []
			}
		]
	}
]
```


## 查看某题关联的所有知识点


**接口地址**:`/api/knowledge/questions/{questionId}/points`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|questionId||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|KnowledgePointVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|pointId|知识点ID|integer(int64)|integer(int64)|
|pointName|知识点名称|string||
|courseId|所属课程ID|integer(int64)|integer(int64)|
|parentId|父知识点ID，null表示一级知识点|integer(int64)|integer(int64)|
|description|知识点描述|string||
|orderIndex|排序序号|integer(int32)|integer(int32)|
|createdAt|创建时间|string(date-time)|string(date-time)|
|updatedAt|更新时间|string(date-time)|string(date-time)|


**响应示例**:
```javascript
[
	{
		"pointId": 0,
		"pointName": "",
		"courseId": 0,
		"parentId": 0,
		"description": "",
		"orderIndex": 0,
		"createdAt": "",
		"updatedAt": ""
	}
]
```


## 查看某节关联的所有知识点


**接口地址**:`/api/knowledge/sections/{sectionId}/points`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|sectionId||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|KnowledgePointVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|pointId|知识点ID|integer(int64)|integer(int64)|
|pointName|知识点名称|string||
|courseId|所属课程ID|integer(int64)|integer(int64)|
|parentId|父知识点ID，null表示一级知识点|integer(int64)|integer(int64)|
|description|知识点描述|string||
|orderIndex|排序序号|integer(int32)|integer(int32)|
|createdAt|创建时间|string(date-time)|string(date-time)|
|updatedAt|更新时间|string(date-time)|string(date-time)|


**响应示例**:
```javascript
[
	{
		"pointId": 0,
		"pointName": "",
		"courseId": 0,
		"parentId": 0,
		"description": "",
		"orderIndex": 0,
		"createdAt": "",
		"updatedAt": ""
	}
]
```