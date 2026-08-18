# OpenAPI definition


**简介**:OpenAPI definition


**HOST**:http://192.168.101.1:8891/api/user


**联系人**:


**Version**:v0


**接口路径**:/api/user/v3/api-docs


[TOC]






# 用户鉴权


## 用户登录


**接口地址**:`/api/user/auth/login`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "loginType": "4",
  "username": "张三",
  "password": "1234utf-+",
  "mobile": "13800010004",
  "email": "1234567@qq.com",
  "code": "645632",
  "openId": "oTg1234567890abcdefg"
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|loginRequest|用户登录请求DTO|body|true|LoginRequest|LoginRequest|
|&emsp;&emsp;loginType|登录类型,4-用户名密码登录, 1-手机号登录, 2-邮箱登录, 3-微信登录,可用值:1,2,3,4||true|string||
|&emsp;&emsp;username|用户名,仅在用户名密码登录时必填||true|string||
|&emsp;&emsp;password|密码,仅在用户名密码登录时必填||true|string||
|&emsp;&emsp;mobile|手机号,仅在手机号登录时必填||true|string||
|&emsp;&emsp;email|邮箱,仅在邮箱登录时必填||true|string||
|&emsp;&emsp;code|验证码,仅在手机号登录和邮箱登录时必填||true|string||
|&emsp;&emsp;openId|微信/QQ OpenID,仅在微信登录时必填||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|LoginResult|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|userId||integer(int64)|integer(int64)|
|userType|可用值:Student,Teacher,Admin|string||
|accessToken||string||
|refreshToken||string||


**响应示例**:
```javascript
{
	"userId": 0,
	"userType": "",
	"accessToken": "",
	"refreshToken": ""
}
```


## 发送登录验证码


**接口地址**:`/api/user/auth/login/send-code`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "loginType": 0,
  "mobile": "",
  "email": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|sendLoginCodeRequest|SendLoginCodeRequest|body|true|SendLoginCodeRequest|SendLoginCodeRequest|
|&emsp;&emsp;loginType|||true|integer(int32)||
|&emsp;&emsp;mobile|||true|string||
|&emsp;&emsp;email|||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 用户注销


**接口地址**:`/api/user/auth/logout`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization||header|false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 刷新Access Token


**接口地址**:`/api/user/auth/refresh-token`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|refreshToken||query|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|LoginResult|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|userId||integer(int64)|integer(int64)|
|userType|可用值:Student,Teacher,Admin|string||
|accessToken||string||
|refreshToken||string||


**响应示例**:
```javascript
{
	"userId": 0,
	"userType": "",
	"accessToken": "",
	"refreshToken": ""
}
```


## 用户注册


**接口地址**:`/api/user/auth/register`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "name": "张三",
  "password": "P@ssw0rd",
  "registerType": "2",
  "mobile": "13800010004",
  "email": "12345678@qq.com",
  "code": "645632",
  "studentNo": "20210001",
  "grade": "2021级",
  "major": "计算机科学与技术",
  "enrollmentYear": 2021,
  "school": "广东技术师范大学"
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|registerRequest|用户注册请求DTO|body|true|RegisterRequest|RegisterRequest|
|&emsp;&emsp;name|姓名，长度在2到20之间||true|string||
|&emsp;&emsp;password|密码必须包含字母、数字和特殊字符且长度在6到20之间||true|string||
|&emsp;&emsp;registerType|注册类型：1-手机号注册，2-邮箱验证码注册,3-微信OpenID注册,可用值:手机验证码注册,邮箱验证码注册,微信OpenID注册||true|string||
|&emsp;&emsp;mobile|手机号，必须是中国大陆的手机号||true|string||
|&emsp;&emsp;email|邮箱地址，必须符合邮箱格式||true|string||
|&emsp;&emsp;code|验证码，注册类型为MOBILE_CODE时是短信验证码，EMAIL_CODE时是邮箱验证码||true|string||
|&emsp;&emsp;studentNo|学号，长度在8到12之间||true|string||
|&emsp;&emsp;grade|年级，如2021级||false|string||
|&emsp;&emsp;major|专业，如计算机科学与技术||false|string||
|&emsp;&emsp;enrollmentYear|入学年份，如2021||false|integer(int32)||
|&emsp;&emsp;school|学校名称，如XX大学||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 发送注册验证码


**接口地址**:`/api/user/auth/register/send-code`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "registerType": "2",
  "mobile": "13800010004",
  "email": "12345@qq.com"
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|sendRegisterCodeRequest|SendRegisterCodeRequest|body|true|SendRegisterCodeRequest|SendRegisterCodeRequest|
|&emsp;&emsp;registerType|注册类型，1-手机号注册，2-邮箱注册,3-微信注册,可用值:手机验证码注册,邮箱验证码注册,微信OpenID注册||true|string||
|&emsp;&emsp;mobile|手机号，registerType为MOBILE_CODE时必填||true|string||
|&emsp;&emsp;email|邮箱，registerType为EMAIL_CODE时必填||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


# 用户相关接口


## 多条件查询


**接口地址**:`/api/user/users`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>多条件查询</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|queryRequest||query|true|UserQueryRequest|UserQueryRequest|
|&emsp;&emsp;current|||false|integer(int32)||
|&emsp;&emsp;pageSize|||false|integer(int32)||
|&emsp;&emsp;sortField|||false|string||
|&emsp;&emsp;sortOrder|||false|string||
|&emsp;&emsp;userId|||false|integer(int64)||
|&emsp;&emsp;name|||false|string||
|&emsp;&emsp;userType|||false|string||
|&emsp;&emsp;sex|||false|integer(int32)||
|&emsp;&emsp;email|||false|string||
|&emsp;&emsp;mobile|||false|string||
|&emsp;&emsp;school|||false|string||
|&emsp;&emsp;status|||false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|PageUserVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|records||array|UserVO|
|&emsp;&emsp;userId|用户id|integer(int64)||
|&emsp;&emsp;name|用户姓名|string||
|&emsp;&emsp;type|用户身份，1学生，2教师,可用值:Student,Teacher,Admin|string||
|&emsp;&emsp;sex|用户性别，1男，0女,可用值:未知,男,女|string||
|&emsp;&emsp;avatarUrl|用户头像URL地址|string||
|&emsp;&emsp;personalSignature|用户个性签名|string||
|&emsp;&emsp;school|用户学校|string||
|total||integer(int64)|integer(int64)|
|size||integer(int64)|integer(int64)|
|current||integer(int64)|integer(int64)|
|orders||array|OrderItem|
|&emsp;&emsp;column||string||
|&emsp;&emsp;asc||boolean||
|optimizeCountSql||PageUserVO|PageUserVO|
|&emsp;&emsp;records|用户信息VO对象|array|UserVO|
|&emsp;&emsp;&emsp;&emsp;userId|用户id|integer||
|&emsp;&emsp;&emsp;&emsp;name|用户姓名|string||
|&emsp;&emsp;&emsp;&emsp;type|用户身份，1学生，2教师,可用值:Student,Teacher,Admin|string||
|&emsp;&emsp;&emsp;&emsp;sex|用户性别，1男，0女,可用值:未知,男,女|string||
|&emsp;&emsp;&emsp;&emsp;avatarUrl|用户头像URL地址|string||
|&emsp;&emsp;&emsp;&emsp;personalSignature|用户个性签名|string||
|&emsp;&emsp;&emsp;&emsp;school|用户学校|string||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;size||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;orders||array|OrderItem|
|&emsp;&emsp;&emsp;&emsp;column||string||
|&emsp;&emsp;&emsp;&emsp;asc||boolean||
|&emsp;&emsp;optimizeCountSql||PageUserVO|PageUserVO|
|&emsp;&emsp;searchCount||PageUserVO|PageUserVO|
|&emsp;&emsp;optimizeJoinOfCountSql||boolean||
|&emsp;&emsp;maxLimit||integer(int64)||
|&emsp;&emsp;countId||string||
|&emsp;&emsp;pages||integer(int64)||
|searchCount||PageUserVO|PageUserVO|
|&emsp;&emsp;records|用户信息VO对象|array|UserVO|
|&emsp;&emsp;&emsp;&emsp;userId|用户id|integer||
|&emsp;&emsp;&emsp;&emsp;name|用户姓名|string||
|&emsp;&emsp;&emsp;&emsp;type|用户身份，1学生，2教师,可用值:Student,Teacher,Admin|string||
|&emsp;&emsp;&emsp;&emsp;sex|用户性别，1男，0女,可用值:未知,男,女|string||
|&emsp;&emsp;&emsp;&emsp;avatarUrl|用户头像URL地址|string||
|&emsp;&emsp;&emsp;&emsp;personalSignature|用户个性签名|string||
|&emsp;&emsp;&emsp;&emsp;school|用户学校|string||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;size||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;orders||array|OrderItem|
|&emsp;&emsp;&emsp;&emsp;column||string||
|&emsp;&emsp;&emsp;&emsp;asc||boolean||
|&emsp;&emsp;optimizeCountSql||PageUserVO|PageUserVO|
|&emsp;&emsp;searchCount||PageUserVO|PageUserVO|
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
			"userId": 12345,
			"name": "张三",
			"type": "1",
			"sex": "1",
			"avatarUrl": "http://example.com/avatar.jpg",
			"personalSignature": "热爱编程，喜欢分享",
			"school": "广东技术师范大学"
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


## 获取用户信息


**接口地址**:`/api/user/users/{userId}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>根据用户ID获取用户信息</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|userId||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|UserVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|userId|用户id|integer(int64)|integer(int64)|
|name|用户姓名|string||
|type|用户身份，1学生，2教师,可用值:Student,Teacher,Admin|string||
|sex|用户性别，1男，0女,可用值:未知,男,女|string||
|avatarUrl|用户头像URL地址|string||
|personalSignature|用户个性签名|string||
|school|用户学校|string||


**响应示例**:
```javascript
{
	"userId": 12345,
	"name": "张三",
	"type": "1",
	"sex": "1",
	"avatarUrl": "http://example.com/avatar.jpg",
	"personalSignature": "热爱编程，喜欢分享",
	"school": "广东技术师范大学"
}
```


## 删除用户


**接口地址**:`/api/user/users/{userId}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>根据用户ID删除用户</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|userId||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 分配教师角色


**接口地址**:`/api/user/users/{userId}/assign-teacher`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>管理员将用户提升为教师</p>



**请求示例**:


```javascript
{
  "userId": 123456789,
  "teacherNo": "T12345",
  "title": "教授",
  "department": "计算机科学与技术学院",
  "bio": "具有10年教学经验，专注于人工智能领域的研究和教学。"
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|assignTeacherRequest|注册教师请求对象|body|true|AssignTeacherRequest|AssignTeacherRequest|
|&emsp;&emsp;userId|将要分配教师的用户ID||true|integer(int64)||
|&emsp;&emsp;teacherNo|教师工号||true|string||
|&emsp;&emsp;title|教师职称||false|string||
|&emsp;&emsp;department|教师所属院系||false|string||
|&emsp;&emsp;bio|教师个人简介||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 获取当前登录用户信息


**接口地址**:`/api/user/users/me`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|UserDetailVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|userId|用户id|integer(int64)|integer(int64)|
|name|用户姓名|string||
|type|用户身份，1学生，2教师,可用值:Student,Teacher,Admin|string||
|sex|用户性别，1男，0女,可用值:未知,男,女|string||
|avatarUrl|用户头像URL地址|string||
|personalSignature|用户个性签名|string||
|school|用户学校|string||
|email||string||
|mobile||string||
|status|可用值:正常,禁止|string||
|studentProfile||StudentProfileVO|StudentProfileVO|
|&emsp;&emsp;studentNo||string||
|&emsp;&emsp;grade||string||
|&emsp;&emsp;major||string||
|&emsp;&emsp;enrollmentYear||integer(int32)||
|teacherProfile||TeacherProfileVO|TeacherProfileVO|
|&emsp;&emsp;teacherNo||string||
|&emsp;&emsp;title||string||
|&emsp;&emsp;department||string||
|&emsp;&emsp;bio||string||


**响应示例**:
```javascript
{
	"userId": 12345,
	"name": "张三",
	"type": "1",
	"sex": "1",
	"avatarUrl": "http://example.com/avatar.jpg",
	"personalSignature": "热爱编程，喜欢分享",
	"school": "广东技术师范大学",
	"email": "",
	"mobile": "",
	"status": "",
	"studentProfile": {
		"studentNo": "",
		"grade": "",
		"major": "",
		"enrollmentYear": 0
	},
	"teacherProfile": {
		"teacherNo": "",
		"title": "",
		"department": "",
		"bio": ""
	}
}
```


## 更新用户基本信息


**接口地址**:`/api/user/users/me`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>根据用户ID更新用户信息</p>



**请求示例**:


```javascript
{
  "name": "张三",
  "sex": "0",
  "school": "广东技术师范大学",
  "personalSignature": "热爱学习，热爱生活"
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|userUpdateRequest|UserUpdateRequest|body|true|UserUpdateRequest|UserUpdateRequest|
|&emsp;&emsp;name|姓名||false|string||
|&emsp;&emsp;sex|0-未知，1-男,2-女,可用值:未知,男,女||false|string||
|&emsp;&emsp;school|学校||false|string||
|&emsp;&emsp;personalSignature|个性签名||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 更新用户头像


**接口地址**:`/api/user/users/me/avatar`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>根据用户ID更新用户头像</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|avatarUrl||query|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 修改密码


**接口地址**:`/api/user/users/me/password`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>根据用户ID修改用户密码</p>



**请求示例**:


```javascript
{
  "oldPassword": "OldP@ssw0rd",
  "newPassword": "NewP@ssw0rd"
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|passwordUpdateRequest|PasswordUpdateRequest|body|true|PasswordUpdateRequest|PasswordUpdateRequest|
|&emsp;&emsp;oldPassword|旧密码，不能为空||true|string||
|&emsp;&emsp;newPassword|新密码，必须包含字母、数字和特殊字符且长度在6到20之间||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 更新当前用户档案信息


**接口地址**:`/api/user/users/me/profile`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "studentNo": "20230001",
  "grade": "2023",
  "major": "计算机科学与技术",
  "enrollmentYear": 2023,
  "teacherNo": "T2023001",
  "title": "副教授",
  "department": "计算机学院",
  "bio": "我是一名计算机科学与技术专业的教师，主要研究方向是人工智能和大数据。"
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|profileUpdateRequest|用户档案更新请求|body|true|ProfileUpdateRequest|ProfileUpdateRequest|
|&emsp;&emsp;studentNo|学号||false|string||
|&emsp;&emsp;grade|年级||false|string||
|&emsp;&emsp;major|专业||false|string||
|&emsp;&emsp;enrollmentYear|入学年份||false|integer(int32)||
|&emsp;&emsp;teacherNo|教师工号||false|string||
|&emsp;&emsp;title|职称||false|string||
|&emsp;&emsp;department|所属院系||false|string||
|&emsp;&emsp;bio|个人简介||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```