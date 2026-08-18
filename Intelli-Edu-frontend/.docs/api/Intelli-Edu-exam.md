# OpenAPI definition


**简介**:OpenAPI definition


**HOST**:http://192.168.101.1:8894/api/exam


**联系人**:


**Version**:v0


**接口路径**:/api/exam/v3/api-docs


[TOC]






# 答题模块


## 进入考试


**接口地址**:`/api/exam/answers/exams/{examId}/enter`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>创建答卷，计算个人截止时间</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|examId||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|AnswerSheetVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|sheetId|答卷ID|integer(int64)|integer(int64)|
|examId|考试ID|integer(int64)|integer(int64)|
|studentId|学生ID|integer(int64)|integer(int64)|
|studentName|学生姓名|string||
|status|答卷状态，0=未开始 1=进行中 2=已结束 3-已批阅完成|integer(int32)|integer(int32)|
|totalScore|总分|number||
|objectiveScore|客观题分数|number||
|subjectiveScore|主观题分数|number||
|submitCount|提交次数|integer(int32)|integer(int32)|
|startAnswerTime|开始答题时间|string(date-time)|string(date-time)|
|submitTime|最后一次提交时间|string(date-time)|string(date-time)|
|deadline|答题截止时间|string(date-time)|string(date-time)|


**响应示例**:
```javascript
{
	"sheetId": 0,
	"examId": 0,
	"studentId": 0,
	"studentName": "",
	"status": 0,
	"totalScore": 0,
	"objectiveScore": 0,
	"subjectiveScore": 0,
	"submitCount": 0,
	"startAnswerTime": "",
	"submitTime": "",
	"deadline": ""
}
```


## 查看我的答卷


**接口地址**:`/api/exam/answers/exams/{examId}/my-sheet`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>含答题记录和题目快照</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|examId||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|AnswerSheetDetailVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|sheetId|答卷ID|integer(int64)|integer(int64)|
|examId|考试ID|integer(int64)|integer(int64)|
|examName|考试名称|string||
|studentId|学生ID|integer(int64)|integer(int64)|
|status|考试状态|integer(int32)|integer(int32)|
|totalScore|总分|number||
|objectiveScore|客观题得分|number||
|subjectiveScore|主观题得分|number||
|submitCount|提交次数|integer(int32)|integer(int32)|
|startAnswerTime|开始答题时间|string(date-time)|string(date-time)|
|submitTime|最后一次提交时间|string(date-time)|string(date-time)|
|deadline|截止时间|string(date-time)|string(date-time)|
|records|答题记录列表|array|AnswerRecordVO|
|&emsp;&emsp;recordId|答题记录ID|integer(int64)||
|&emsp;&emsp;questionId|问题ID|integer(int64)||
|&emsp;&emsp;answerContent|学生的答案内容|string||
|&emsp;&emsp;score|得分|number||
|&emsp;&emsp;isCorrect|是否正确，客观题会自动判定|boolean||
|&emsp;&emsp;gradingStatus|批改状态，0=未批改 1=已批改 2-AI批改中|integer(int32)||
|&emsp;&emsp;graderId|批改教师ID，null表示未批改或AI批改中|integer(int64)||
|&emsp;&emsp;comment|评语|string||
|&emsp;&emsp;questionType|题目类型，0=单选 1=多选 2=判断 3=填空 4=简答|integer(int32)||
|&emsp;&emsp;stem|题目内容|string||
|&emsp;&emsp;questionScore|题目分值|number||
|&emsp;&emsp;correctAnswer|正确答案，客观题有值|string||


**响应示例**:
```javascript
{
	"sheetId": 0,
	"examId": 0,
	"examName": "",
	"studentId": 0,
	"status": 0,
	"totalScore": 0,
	"objectiveScore": 0,
	"subjectiveScore": 0,
	"submitCount": 0,
	"startAnswerTime": "",
	"submitTime": "",
	"deadline": "",
	"records": [
		{
			"recordId": 0,
			"questionId": 0,
			"answerContent": "",
			"score": 0,
			"isCorrect": true,
			"gradingStatus": 0,
			"graderId": 0,
			"comment": "",
			"questionType": 0,
			"stem": "",
			"questionScore": 0,
			"correctAnswer": ""
		}
	]
}
```


## 保存单题答案


**接口地址**:`/api/exam/answers/sheets/{sheetId}/questions/{questionId}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>写入Redis，定时刷入DB</p>



**请求示例**:


```javascript
{
  "answerContent": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|sheetId||path|true|integer(int64)||
|questionId||path|true|integer(int64)||
|answerSaveRequest|保存答案请求|body|true|AnswerSaveRequest|AnswerSaveRequest|
|&emsp;&emsp;answerContent|学生答案内容||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 交卷


**接口地址**:`/api/exam/answers/sheets/{sheetId}/submit`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>刷Redis→自动判分→更新答卷</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|sheetId||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


# 考试管理


## 查询考试列表


**接口地址**:`/api/exam/exams`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>支持按班级、课程、类型、状态过滤</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|request|考试查询请求|query|true|ExamQueryRequest|ExamQueryRequest|
|&emsp;&emsp;current|||false|integer(int32)||
|&emsp;&emsp;pageSize|||false|integer(int32)||
|&emsp;&emsp;sortField|||false|string||
|&emsp;&emsp;sortOrder|||false|string||
|&emsp;&emsp;classId|班级ID||false|integer(int64)||
|&emsp;&emsp;courseId|课程ID||false|integer(int64)||
|&emsp;&emsp;examType|考试类型：0=考试 1=练习 2=作业||false|integer(int32)||
|&emsp;&emsp;status|状态：0=未开始 1=进行中 2=已结束 3=已批阅完成||false|integer(int32)||
|&emsp;&emsp;keyword|关键词||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|PageExamVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|records||array|ExamVO|
|&emsp;&emsp;examId||integer(int64)||
|&emsp;&emsp;examName||string||
|&emsp;&emsp;paperId||integer(int64)||
|&emsp;&emsp;paperName||string||
|&emsp;&emsp;classId||integer(int64)||
|&emsp;&emsp;courseId||integer(int64)||
|&emsp;&emsp;teacherId||integer(int64)||
|&emsp;&emsp;examType||integer(int32)||
|&emsp;&emsp;startTime||string(date-time)||
|&emsp;&emsp;endTime||string(date-time)||
|&emsp;&emsp;durationMinutes||integer(int32)||
|&emsp;&emsp;allowLateSubmit||boolean||
|&emsp;&emsp;status||integer(int32)||
|&emsp;&emsp;createdAt||string(date-time)||
|total||integer(int64)|integer(int64)|
|size||integer(int64)|integer(int64)|
|current||integer(int64)|integer(int64)|
|orders||array|OrderItem|
|&emsp;&emsp;id|试卷-题目关联ID|integer(int64)||
|&emsp;&emsp;orderIndex|新的排序值|integer(int32)||
|&emsp;&emsp;sectionIndex|新的节号|integer(int32)||
|optimizeCountSql||PageExamVO|PageExamVO|
|&emsp;&emsp;records|考试VO|array|ExamVO|
|&emsp;&emsp;&emsp;&emsp;examId||integer||
|&emsp;&emsp;&emsp;&emsp;examName||string||
|&emsp;&emsp;&emsp;&emsp;paperId||integer||
|&emsp;&emsp;&emsp;&emsp;paperName||string||
|&emsp;&emsp;&emsp;&emsp;classId||integer||
|&emsp;&emsp;&emsp;&emsp;courseId||integer||
|&emsp;&emsp;&emsp;&emsp;teacherId||integer||
|&emsp;&emsp;&emsp;&emsp;examType||integer||
|&emsp;&emsp;&emsp;&emsp;startTime||string||
|&emsp;&emsp;&emsp;&emsp;endTime||string||
|&emsp;&emsp;&emsp;&emsp;durationMinutes||integer||
|&emsp;&emsp;&emsp;&emsp;allowLateSubmit||boolean||
|&emsp;&emsp;&emsp;&emsp;status||integer||
|&emsp;&emsp;&emsp;&emsp;createdAt||string||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;size||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;orders|排序项列表|array|OrderItem|
|&emsp;&emsp;&emsp;&emsp;id|试卷-题目关联ID|integer||
|&emsp;&emsp;&emsp;&emsp;orderIndex|新的排序值|integer||
|&emsp;&emsp;&emsp;&emsp;sectionIndex|新的节号|integer||
|&emsp;&emsp;optimizeCountSql||PageExamVO|PageExamVO|
|&emsp;&emsp;searchCount||PageExamVO|PageExamVO|
|&emsp;&emsp;optimizeJoinOfCountSql||boolean||
|&emsp;&emsp;maxLimit||integer(int64)||
|&emsp;&emsp;countId||string||
|&emsp;&emsp;pages||integer(int64)||
|searchCount||PageExamVO|PageExamVO|
|&emsp;&emsp;records|考试VO|array|ExamVO|
|&emsp;&emsp;&emsp;&emsp;examId||integer||
|&emsp;&emsp;&emsp;&emsp;examName||string||
|&emsp;&emsp;&emsp;&emsp;paperId||integer||
|&emsp;&emsp;&emsp;&emsp;paperName||string||
|&emsp;&emsp;&emsp;&emsp;classId||integer||
|&emsp;&emsp;&emsp;&emsp;courseId||integer||
|&emsp;&emsp;&emsp;&emsp;teacherId||integer||
|&emsp;&emsp;&emsp;&emsp;examType||integer||
|&emsp;&emsp;&emsp;&emsp;startTime||string||
|&emsp;&emsp;&emsp;&emsp;endTime||string||
|&emsp;&emsp;&emsp;&emsp;durationMinutes||integer||
|&emsp;&emsp;&emsp;&emsp;allowLateSubmit||boolean||
|&emsp;&emsp;&emsp;&emsp;status||integer||
|&emsp;&emsp;&emsp;&emsp;createdAt||string||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;size||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;orders|排序项列表|array|OrderItem|
|&emsp;&emsp;&emsp;&emsp;id|试卷-题目关联ID|integer||
|&emsp;&emsp;&emsp;&emsp;orderIndex|新的排序值|integer||
|&emsp;&emsp;&emsp;&emsp;sectionIndex|新的节号|integer||
|&emsp;&emsp;optimizeCountSql||PageExamVO|PageExamVO|
|&emsp;&emsp;searchCount||PageExamVO|PageExamVO|
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
			"examId": 0,
			"examName": "",
			"paperId": 0,
			"paperName": "",
			"classId": 0,
			"courseId": 0,
			"teacherId": 0,
			"examType": 0,
			"startTime": "",
			"endTime": "",
			"durationMinutes": 0,
			"allowLateSubmit": true,
			"status": 0,
			"createdAt": ""
		}
	],
	"total": 0,
	"size": 0,
	"current": 0,
	"orders": [
		{
			"id": 0,
			"orderIndex": 0,
			"sectionIndex": 0
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


## 发布考试


**接口地址**:`/api/exam/exams`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "examName": "",
  "paperId": 0,
  "classId": 0,
  "examType": 0,
  "startTime": "",
  "endTime": "",
  "durationMinutes": 0,
  "allowLateSubmit": false
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|examCreateRequest|考试创建请求|body|true|ExamCreateRequest|ExamCreateRequest|
|&emsp;&emsp;examName|考试名称||true|string||
|&emsp;&emsp;paperId|试卷ID||true|integer(int64)||
|&emsp;&emsp;classId|班级ID||true|integer(int64)||
|&emsp;&emsp;examType|类型：0=考试 1=练习 2=作业||true|integer(int32)||
|&emsp;&emsp;startTime|开放窗口开始时间||true|string(date-time)||
|&emsp;&emsp;endTime|开放窗口结束时间||true|string(date-time)||
|&emsp;&emsp;durationMinutes|答题时长（分钟），null=不限时||false|integer(int32)||
|&emsp;&emsp;allowLateSubmit|是否允许迟交||false|boolean||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ExamVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|examId||integer(int64)|integer(int64)|
|examName||string||
|paperId||integer(int64)|integer(int64)|
|paperName||string||
|classId||integer(int64)|integer(int64)|
|courseId||integer(int64)|integer(int64)|
|teacherId||integer(int64)|integer(int64)|
|examType||integer(int32)|integer(int32)|
|startTime||string(date-time)|string(date-time)|
|endTime||string(date-time)|string(date-time)|
|durationMinutes||integer(int32)|integer(int32)|
|allowLateSubmit||boolean||
|status||integer(int32)|integer(int32)|
|createdAt||string(date-time)|string(date-time)|


**响应示例**:
```javascript
{
	"examId": 0,
	"examName": "",
	"paperId": 0,
	"paperName": "",
	"classId": 0,
	"courseId": 0,
	"teacherId": 0,
	"examType": 0,
	"startTime": "",
	"endTime": "",
	"durationMinutes": 0,
	"allowLateSubmit": true,
	"status": 0,
	"createdAt": ""
}
```


## 更新考试


**接口地址**:`/api/exam/exams/{examId}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>仅未开始的考试可修改</p>



**请求示例**:


```javascript
{
  "examName": "",
  "startTime": "",
  "endTime": "",
  "durationMinutes": 0,
  "allowLateSubmit": true
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|examId||path|true|integer(int64)||
|examUpdateRequest|考试更新请求|body|true|ExamUpdateRequest|ExamUpdateRequest|
|&emsp;&emsp;examName|考试名称||false|string||
|&emsp;&emsp;startTime|开放窗口开始时间||false|string(date-time)||
|&emsp;&emsp;endTime|开放窗口结束时间||false|string(date-time)||
|&emsp;&emsp;durationMinutes|答题时长（分钟）||false|integer(int32)||
|&emsp;&emsp;allowLateSubmit|是否允许迟交||false|boolean||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ExamVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|examId||integer(int64)|integer(int64)|
|examName||string||
|paperId||integer(int64)|integer(int64)|
|paperName||string||
|classId||integer(int64)|integer(int64)|
|courseId||integer(int64)|integer(int64)|
|teacherId||integer(int64)|integer(int64)|
|examType||integer(int32)|integer(int32)|
|startTime||string(date-time)|string(date-time)|
|endTime||string(date-time)|string(date-time)|
|durationMinutes||integer(int32)|integer(int32)|
|allowLateSubmit||boolean||
|status||integer(int32)|integer(int32)|
|createdAt||string(date-time)|string(date-time)|


**响应示例**:
```javascript
{
	"examId": 0,
	"examName": "",
	"paperId": 0,
	"paperName": "",
	"classId": 0,
	"courseId": 0,
	"teacherId": 0,
	"examType": 0,
	"startTime": "",
	"endTime": "",
	"durationMinutes": 0,
	"allowLateSubmit": true,
	"status": 0,
	"createdAt": ""
}
```


## 删除考试


**接口地址**:`/api/exam/exams/{examId}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>仅未开始的考试可删除</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|examId||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 查看所有答卷列表


**接口地址**:`/api/exam/exams/{examId}/sheets`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|examId||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|AnswerSheetVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|sheetId|答卷ID|integer(int64)|integer(int64)|
|examId|考试ID|integer(int64)|integer(int64)|
|studentId|学生ID|integer(int64)|integer(int64)|
|studentName|学生姓名|string||
|status|答卷状态，0=未开始 1=进行中 2=已结束 3-已批阅完成|integer(int32)|integer(int32)|
|totalScore|总分|number||
|objectiveScore|客观题分数|number||
|subjectiveScore|主观题分数|number||
|submitCount|提交次数|integer(int32)|integer(int32)|
|startAnswerTime|开始答题时间|string(date-time)|string(date-time)|
|submitTime|最后一次提交时间|string(date-time)|string(date-time)|
|deadline|答题截止时间|string(date-time)|string(date-time)|


**响应示例**:
```javascript
[
	{
		"sheetId": 0,
		"examId": 0,
		"studentId": 0,
		"studentName": "",
		"status": 0,
		"totalScore": 0,
		"objectiveScore": 0,
		"subjectiveScore": 0,
		"submitCount": 0,
		"startAnswerTime": "",
		"submitTime": "",
		"deadline": ""
	}
]
```


## 考试统计


**接口地址**:`/api/exam/exams/{examId}/stats`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>已交卷人数、平均分、分数段等</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|examId||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ExamStatsVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|totalStudents|应考人数|integer(int32)|integer(int32)|
|submittedCount|已交卷人数|integer(int32)|integer(int32)|
|answeringCount|答题中人数|integer(int32)|integer(int32)|
|gradedCount|已批阅人数|integer(int32)|integer(int32)|
|maxScore|最高分|number||
|minScore|最低分|number||
|avgScore|平均分|number||


**响应示例**:
```javascript
{
	"totalStudents": 0,
	"submittedCount": 0,
	"answeringCount": 0,
	"gradedCount": 0,
	"maxScore": 0,
	"minScore": 0,
	"avgScore": 0
}
```


## 批阅单道题


**接口地址**:`/api/exam/exams/records/{recordId}/grade`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "score": 0,
  "comment": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|recordId||path|true|integer(int64)||
|gradeRequest|批阅请求|body|true|GradeRequest|GradeRequest|
|&emsp;&emsp;score|得分||true|number||
|&emsp;&emsp;comment|评语||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 查看学生答卷详情


**接口地址**:`/api/exam/exams/sheets/{sheetId}/detail`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>教师批阅时查看</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|sheetId||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|AnswerSheetDetailVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|sheetId|答卷ID|integer(int64)|integer(int64)|
|examId|考试ID|integer(int64)|integer(int64)|
|examName|考试名称|string||
|studentId|学生ID|integer(int64)|integer(int64)|
|status|考试状态|integer(int32)|integer(int32)|
|totalScore|总分|number||
|objectiveScore|客观题得分|number||
|subjectiveScore|主观题得分|number||
|submitCount|提交次数|integer(int32)|integer(int32)|
|startAnswerTime|开始答题时间|string(date-time)|string(date-time)|
|submitTime|最后一次提交时间|string(date-time)|string(date-time)|
|deadline|截止时间|string(date-time)|string(date-time)|
|records|答题记录列表|array|AnswerRecordVO|
|&emsp;&emsp;recordId|答题记录ID|integer(int64)||
|&emsp;&emsp;questionId|问题ID|integer(int64)||
|&emsp;&emsp;answerContent|学生的答案内容|string||
|&emsp;&emsp;score|得分|number||
|&emsp;&emsp;isCorrect|是否正确，客观题会自动判定|boolean||
|&emsp;&emsp;gradingStatus|批改状态，0=未批改 1=已批改 2-AI批改中|integer(int32)||
|&emsp;&emsp;graderId|批改教师ID，null表示未批改或AI批改中|integer(int64)||
|&emsp;&emsp;comment|评语|string||
|&emsp;&emsp;questionType|题目类型，0=单选 1=多选 2=判断 3=填空 4=简答|integer(int32)||
|&emsp;&emsp;stem|题目内容|string||
|&emsp;&emsp;questionScore|题目分值|number||
|&emsp;&emsp;correctAnswer|正确答案，客观题有值|string||


**响应示例**:
```javascript
{
	"sheetId": 0,
	"examId": 0,
	"examName": "",
	"studentId": 0,
	"status": 0,
	"totalScore": 0,
	"objectiveScore": 0,
	"subjectiveScore": 0,
	"submitCount": 0,
	"startAnswerTime": "",
	"submitTime": "",
	"deadline": "",
	"records": [
		{
			"recordId": 0,
			"questionId": 0,
			"answerContent": "",
			"score": 0,
			"isCorrect": true,
			"gradingStatus": 0,
			"graderId": 0,
			"comment": "",
			"questionType": 0,
			"stem": "",
			"questionScore": 0,
			"correctAnswer": ""
		}
	]
}
```


## 完成批阅


**接口地址**:`/api/exam/exams/sheets/{sheetId}/finish-grading`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>汇总主观题得分，更新答卷状态</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|sheetId||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


# 试卷管理


## 查询试卷列表


**接口地址**:`/api/exam/papers`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|request|试卷查询请求|query|true|PaperQueryRequest|PaperQueryRequest|
|&emsp;&emsp;current|||false|integer(int32)||
|&emsp;&emsp;pageSize|||false|integer(int32)||
|&emsp;&emsp;sortField|||false|string||
|&emsp;&emsp;sortOrder|||false|string||
|&emsp;&emsp;courseId|课程ID||false|integer(int64)||
|&emsp;&emsp;status|状态：0=草稿 1=已发布||false|integer(int32)||
|&emsp;&emsp;keyword|关键词||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|PagePaperVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|records||array|PaperVO|
|&emsp;&emsp;paperId||integer(int64)||
|&emsp;&emsp;paperName||string||
|&emsp;&emsp;courseId||integer(int64)||
|&emsp;&emsp;teacherId||integer(int64)||
|&emsp;&emsp;totalScore||number||
|&emsp;&emsp;sections||array|object|
|&emsp;&emsp;status||integer(int32)||
|&emsp;&emsp;questionCount||integer(int32)||
|&emsp;&emsp;createdAt||string(date-time)||
|&emsp;&emsp;updatedAt||string(date-time)||
|total||integer(int64)|integer(int64)|
|size||integer(int64)|integer(int64)|
|current||integer(int64)|integer(int64)|
|orders||array|OrderItem|
|&emsp;&emsp;id|试卷-题目关联ID|integer(int64)||
|&emsp;&emsp;orderIndex|新的排序值|integer(int32)||
|&emsp;&emsp;sectionIndex|新的节号|integer(int32)||
|optimizeCountSql||PagePaperVO|PagePaperVO|
|&emsp;&emsp;records|试卷VO|array|PaperVO|
|&emsp;&emsp;&emsp;&emsp;paperId||integer||
|&emsp;&emsp;&emsp;&emsp;paperName||string||
|&emsp;&emsp;&emsp;&emsp;courseId||integer||
|&emsp;&emsp;&emsp;&emsp;teacherId||integer||
|&emsp;&emsp;&emsp;&emsp;totalScore||number||
|&emsp;&emsp;&emsp;&emsp;sections||array|object|
|&emsp;&emsp;&emsp;&emsp;status||integer||
|&emsp;&emsp;&emsp;&emsp;questionCount||integer||
|&emsp;&emsp;&emsp;&emsp;createdAt||string||
|&emsp;&emsp;&emsp;&emsp;updatedAt||string||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;size||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;orders|排序项列表|array|OrderItem|
|&emsp;&emsp;&emsp;&emsp;id|试卷-题目关联ID|integer||
|&emsp;&emsp;&emsp;&emsp;orderIndex|新的排序值|integer||
|&emsp;&emsp;&emsp;&emsp;sectionIndex|新的节号|integer||
|&emsp;&emsp;optimizeCountSql||PagePaperVO|PagePaperVO|
|&emsp;&emsp;searchCount||PagePaperVO|PagePaperVO|
|&emsp;&emsp;optimizeJoinOfCountSql||boolean||
|&emsp;&emsp;maxLimit||integer(int64)||
|&emsp;&emsp;countId||string||
|&emsp;&emsp;pages||integer(int64)||
|searchCount||PagePaperVO|PagePaperVO|
|&emsp;&emsp;records|试卷VO|array|PaperVO|
|&emsp;&emsp;&emsp;&emsp;paperId||integer||
|&emsp;&emsp;&emsp;&emsp;paperName||string||
|&emsp;&emsp;&emsp;&emsp;courseId||integer||
|&emsp;&emsp;&emsp;&emsp;teacherId||integer||
|&emsp;&emsp;&emsp;&emsp;totalScore||number||
|&emsp;&emsp;&emsp;&emsp;sections||array|object|
|&emsp;&emsp;&emsp;&emsp;status||integer||
|&emsp;&emsp;&emsp;&emsp;questionCount||integer||
|&emsp;&emsp;&emsp;&emsp;createdAt||string||
|&emsp;&emsp;&emsp;&emsp;updatedAt||string||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;size||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;orders|排序项列表|array|OrderItem|
|&emsp;&emsp;&emsp;&emsp;id|试卷-题目关联ID|integer||
|&emsp;&emsp;&emsp;&emsp;orderIndex|新的排序值|integer||
|&emsp;&emsp;&emsp;&emsp;sectionIndex|新的节号|integer||
|&emsp;&emsp;optimizeCountSql||PagePaperVO|PagePaperVO|
|&emsp;&emsp;searchCount||PagePaperVO|PagePaperVO|
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
			"paperId": 0,
			"paperName": "",
			"courseId": 0,
			"teacherId": 0,
			"totalScore": 0,
			"sections": [],
			"status": 0,
			"questionCount": 0,
			"createdAt": "",
			"updatedAt": ""
		}
	],
	"total": 0,
	"size": 0,
	"current": 0,
	"orders": [
		{
			"id": 0,
			"orderIndex": 0,
			"sectionIndex": 0
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


## 创建试卷


**接口地址**:`/api/exam/papers`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "paperName": "",
  "courseId": 0,
  "sections": [
    {
      "index": 1,
      "title": "选择题"
    }
  ]
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|paperCreateRequest|试卷创建请求|body|true|PaperCreateRequest|PaperCreateRequest|
|&emsp;&emsp;paperName|试卷名称||true|string||
|&emsp;&emsp;courseId|课程ID||true|integer(int64)||
|&emsp;&emsp;sections|分节标题||false|array|object|


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|PaperVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|paperId||integer(int64)|integer(int64)|
|paperName||string||
|courseId||integer(int64)|integer(int64)|
|teacherId||integer(int64)|integer(int64)|
|totalScore||number||
|sections||array||
|status||integer(int32)|integer(int32)|
|questionCount||integer(int32)|integer(int32)|
|createdAt||string(date-time)|string(date-time)|
|updatedAt||string(date-time)|string(date-time)|


**响应示例**:
```javascript
{
	"paperId": 0,
	"paperName": "",
	"courseId": 0,
	"teacherId": 0,
	"totalScore": 0,
	"sections": [],
	"status": 0,
	"questionCount": 0,
	"createdAt": "",
	"updatedAt": ""
}
```


## 试卷详情


**接口地址**:`/api/exam/papers/{paperId}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>含完整题目列表</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|paperId||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|PaperDetailVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|paperId||integer(int64)|integer(int64)|
|paperName||string||
|courseId||integer(int64)|integer(int64)|
|teacherId||integer(int64)|integer(int64)|
|totalScore||number||
|sections||array||
|status||integer(int32)|integer(int32)|
|questions||array|PaperQuestionVO|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;paperId||integer(int64)||
|&emsp;&emsp;questionId||integer(int64)||
|&emsp;&emsp;orderIndex||integer(int32)||
|&emsp;&emsp;score||number||
|&emsp;&emsp;sectionIndex||integer(int32)||
|&emsp;&emsp;question|题目VO|QuestionVO|QuestionVO|
|&emsp;&emsp;&emsp;&emsp;questionId||integer||
|&emsp;&emsp;&emsp;&emsp;bankId||integer||
|&emsp;&emsp;&emsp;&emsp;questionType||integer||
|&emsp;&emsp;&emsp;&emsp;stem||string||
|&emsp;&emsp;&emsp;&emsp;analysis||string||
|&emsp;&emsp;&emsp;&emsp;answer||string||
|&emsp;&emsp;&emsp;&emsp;score||number||
|&emsp;&emsp;&emsp;&emsp;difficulty||integer||
|&emsp;&emsp;&emsp;&emsp;options|选项VO|array|QuestionOptionVO|
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;optionId||integer||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;label||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;content||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;isCorrect||boolean||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;orderIndex||integer||
|&emsp;&emsp;&emsp;&emsp;createdAt||string||
|&emsp;&emsp;&emsp;&emsp;updatedAt||string||
|&emsp;&emsp;questionSnapshot||object||
|createdAt||string(date-time)|string(date-time)|
|updatedAt||string(date-time)|string(date-time)|


**响应示例**:
```javascript
{
	"paperId": 0,
	"paperName": "",
	"courseId": 0,
	"teacherId": 0,
	"totalScore": 0,
	"sections": [],
	"status": 0,
	"questions": [
		{
			"id": 0,
			"paperId": 0,
			"questionId": 0,
			"orderIndex": 0,
			"score": 0,
			"sectionIndex": 0,
			"question": {
				"questionId": 0,
				"bankId": 0,
				"questionType": 0,
				"stem": "",
				"analysis": "",
				"answer": "",
				"score": 0,
				"difficulty": 0,
				"options": [
					{
						"optionId": 0,
						"label": "",
						"content": "",
						"isCorrect": true,
						"orderIndex": 0
					}
				],
				"createdAt": "",
				"updatedAt": ""
			},
			"questionSnapshot": {}
		}
	],
	"createdAt": "",
	"updatedAt": ""
}
```


## 更新试卷基本信息


**接口地址**:`/api/exam/papers/{paperId}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>仅草稿状态可修改</p>



**请求示例**:


```javascript
{
  "paperName": "",
  "sections": []
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|paperId||path|true|integer(int64)||
|paperUpdateRequest|试卷更新请求|body|true|PaperUpdateRequest|PaperUpdateRequest|
|&emsp;&emsp;paperName|试卷名称||false|string||
|&emsp;&emsp;sections|分节标题||false|array|object|


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|PaperVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|paperId||integer(int64)|integer(int64)|
|paperName||string||
|courseId||integer(int64)|integer(int64)|
|teacherId||integer(int64)|integer(int64)|
|totalScore||number||
|sections||array||
|status||integer(int32)|integer(int32)|
|questionCount||integer(int32)|integer(int32)|
|createdAt||string(date-time)|string(date-time)|
|updatedAt||string(date-time)|string(date-time)|


**响应示例**:
```javascript
{
	"paperId": 0,
	"paperName": "",
	"courseId": 0,
	"teacherId": 0,
	"totalScore": 0,
	"sections": [],
	"status": 0,
	"questionCount": 0,
	"createdAt": "",
	"updatedAt": ""
}
```


## 删除试卷


**接口地址**:`/api/exam/papers/{paperId}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>仅草稿状态可删除</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|paperId||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 发布试卷


**接口地址**:`/api/exam/papers/{paperId}/publish`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>冻结题目快照，状态变为已发布</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|paperId||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 向试卷添加题目


**接口地址**:`/api/exam/papers/{paperId}/questions`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "questions": [
    {
      "questionId": 0,
      "score": 0,
      "sectionIndex": 1
    }
  ]
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|paperId||path|true|integer(int64)||
|paperQuestionAddRequest|试卷添加题目请求|body|true|PaperQuestionAddRequest|PaperQuestionAddRequest|
|&emsp;&emsp;questions|单个题目项||true|array|QuestionItem|
|&emsp;&emsp;&emsp;&emsp;questionId|题目ID||true|integer||
|&emsp;&emsp;&emsp;&emsp;score|在本卷中的分值||true|number||
|&emsp;&emsp;&emsp;&emsp;sectionIndex|归属节号||false|integer||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 从试卷移除题目


**接口地址**:`/api/exam/papers/{paperId}/questions/{questionId}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|paperId||path|true|integer(int64)||
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


## 调整试卷题目排序


**接口地址**:`/api/exam/papers/{paperId}/questions/order`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "items": [
    {
      "id": 0,
      "orderIndex": 0,
      "sectionIndex": 0
    }
  ]
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|paperId||path|true|integer(int64)||
|paperQuestionOrderRequest|试卷题目排序请求|body|true|PaperQuestionOrderRequest|PaperQuestionOrderRequest|
|&emsp;&emsp;items|排序项列表||true|array|OrderItem|
|&emsp;&emsp;&emsp;&emsp;id|试卷-题目关联ID||true|integer||
|&emsp;&emsp;&emsp;&emsp;orderIndex|新的排序值||true|integer||
|&emsp;&emsp;&emsp;&emsp;sectionIndex|新的节号||false|integer||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


# 题库管理


## 查询题库列表


**接口地址**:`/api/exam/question-banks`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|request|题库查询请求|query|true|QuestionBankQueryRequest|QuestionBankQueryRequest|
|&emsp;&emsp;current|||false|integer(int32)||
|&emsp;&emsp;pageSize|||false|integer(int32)||
|&emsp;&emsp;sortField|||false|string||
|&emsp;&emsp;sortOrder|||false|string||
|&emsp;&emsp;courseId|课程ID||false|integer(int64)||
|&emsp;&emsp;keyword|关键词搜索||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|PageQuestionBankVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|records||array|QuestionBankVO|
|&emsp;&emsp;bankId||integer(int64)||
|&emsp;&emsp;bankName||string||
|&emsp;&emsp;courseId||integer(int64)||
|&emsp;&emsp;teacherId||integer(int64)||
|&emsp;&emsp;description||string||
|&emsp;&emsp;questionCount||integer(int32)||
|&emsp;&emsp;createdAt||string(date-time)||
|&emsp;&emsp;updatedAt||string(date-time)||
|total||integer(int64)|integer(int64)|
|size||integer(int64)|integer(int64)|
|current||integer(int64)|integer(int64)|
|orders||array|OrderItem|
|&emsp;&emsp;id|试卷-题目关联ID|integer(int64)||
|&emsp;&emsp;orderIndex|新的排序值|integer(int32)||
|&emsp;&emsp;sectionIndex|新的节号|integer(int32)||
|optimizeCountSql||PageQuestionBankVO|PageQuestionBankVO|
|&emsp;&emsp;records|题库VO|array|QuestionBankVO|
|&emsp;&emsp;&emsp;&emsp;bankId||integer||
|&emsp;&emsp;&emsp;&emsp;bankName||string||
|&emsp;&emsp;&emsp;&emsp;courseId||integer||
|&emsp;&emsp;&emsp;&emsp;teacherId||integer||
|&emsp;&emsp;&emsp;&emsp;description||string||
|&emsp;&emsp;&emsp;&emsp;questionCount||integer||
|&emsp;&emsp;&emsp;&emsp;createdAt||string||
|&emsp;&emsp;&emsp;&emsp;updatedAt||string||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;size||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;orders|排序项列表|array|OrderItem|
|&emsp;&emsp;&emsp;&emsp;id|试卷-题目关联ID|integer||
|&emsp;&emsp;&emsp;&emsp;orderIndex|新的排序值|integer||
|&emsp;&emsp;&emsp;&emsp;sectionIndex|新的节号|integer||
|&emsp;&emsp;optimizeCountSql||PageQuestionBankVO|PageQuestionBankVO|
|&emsp;&emsp;searchCount||PageQuestionBankVO|PageQuestionBankVO|
|&emsp;&emsp;optimizeJoinOfCountSql||boolean||
|&emsp;&emsp;maxLimit||integer(int64)||
|&emsp;&emsp;countId||string||
|&emsp;&emsp;pages||integer(int64)||
|searchCount||PageQuestionBankVO|PageQuestionBankVO|
|&emsp;&emsp;records|题库VO|array|QuestionBankVO|
|&emsp;&emsp;&emsp;&emsp;bankId||integer||
|&emsp;&emsp;&emsp;&emsp;bankName||string||
|&emsp;&emsp;&emsp;&emsp;courseId||integer||
|&emsp;&emsp;&emsp;&emsp;teacherId||integer||
|&emsp;&emsp;&emsp;&emsp;description||string||
|&emsp;&emsp;&emsp;&emsp;questionCount||integer||
|&emsp;&emsp;&emsp;&emsp;createdAt||string||
|&emsp;&emsp;&emsp;&emsp;updatedAt||string||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;size||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;orders|排序项列表|array|OrderItem|
|&emsp;&emsp;&emsp;&emsp;id|试卷-题目关联ID|integer||
|&emsp;&emsp;&emsp;&emsp;orderIndex|新的排序值|integer||
|&emsp;&emsp;&emsp;&emsp;sectionIndex|新的节号|integer||
|&emsp;&emsp;optimizeCountSql||PageQuestionBankVO|PageQuestionBankVO|
|&emsp;&emsp;searchCount||PageQuestionBankVO|PageQuestionBankVO|
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
			"bankId": 0,
			"bankName": "",
			"courseId": 0,
			"teacherId": 0,
			"description": "",
			"questionCount": 0,
			"createdAt": "",
			"updatedAt": ""
		}
	],
	"total": 0,
	"size": 0,
	"current": 0,
	"orders": [
		{
			"id": 0,
			"orderIndex": 0,
			"sectionIndex": 0
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


## 创建题库


**接口地址**:`/api/exam/question-banks`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "bankName": "Java基础单选题库",
  "courseId": 0,
  "description": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|questionBankCreateRequest|题库创建请求|body|true|QuestionBankCreateRequest|QuestionBankCreateRequest|
|&emsp;&emsp;bankName|题库名称||true|string||
|&emsp;&emsp;courseId|所属课程ID||true|integer(int64)||
|&emsp;&emsp;description|题库描述||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|QuestionBankVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|bankId||integer(int64)|integer(int64)|
|bankName||string||
|courseId||integer(int64)|integer(int64)|
|teacherId||integer(int64)|integer(int64)|
|description||string||
|questionCount||integer(int32)|integer(int32)|
|createdAt||string(date-time)|string(date-time)|
|updatedAt||string(date-time)|string(date-time)|


**响应示例**:
```javascript
{
	"bankId": 0,
	"bankName": "",
	"courseId": 0,
	"teacherId": 0,
	"description": "",
	"questionCount": 0,
	"createdAt": "",
	"updatedAt": ""
}
```


## 更新题库


**接口地址**:`/api/exam/question-banks/{bankId}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "bankName": "",
  "description": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|bankId||path|true|integer(int64)||
|questionBankUpdateRequest|题库更新请求|body|true|QuestionBankUpdateRequest|QuestionBankUpdateRequest|
|&emsp;&emsp;bankName|题库名称||false|string||
|&emsp;&emsp;description|题库描述||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|QuestionBankVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|bankId||integer(int64)|integer(int64)|
|bankName||string||
|courseId||integer(int64)|integer(int64)|
|teacherId||integer(int64)|integer(int64)|
|description||string||
|questionCount||integer(int32)|integer(int32)|
|createdAt||string(date-time)|string(date-time)|
|updatedAt||string(date-time)|string(date-time)|


**响应示例**:
```javascript
{
	"bankId": 0,
	"bankName": "",
	"courseId": 0,
	"teacherId": 0,
	"description": "",
	"questionCount": 0,
	"createdAt": "",
	"updatedAt": ""
}
```


## 删除题库


**接口地址**:`/api/exam/question-banks/{bankId}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>题库中有题目时不可删除</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|bankId||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


# 题目管理


## 分页查询题目


**接口地址**:`/api/exam/questions`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>支持按题库、题型、难度、关键词过滤</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|request|题目查询请求|query|true|QuestionQueryRequest|QuestionQueryRequest|
|&emsp;&emsp;current|||false|integer(int32)||
|&emsp;&emsp;pageSize|||false|integer(int32)||
|&emsp;&emsp;sortField|||false|string||
|&emsp;&emsp;sortOrder|||false|string||
|&emsp;&emsp;bankId|题库ID||false|integer(int64)||
|&emsp;&emsp;questionType|题目类型：0=单选 1=多选 2=判断 3=填空 4=简答||false|integer(int32)||
|&emsp;&emsp;difficulty|难度：1-5||false|integer(int32)||
|&emsp;&emsp;keyword|关键词搜索（题干）||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|PageQuestionVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|records||array|QuestionVO|
|&emsp;&emsp;questionId||integer(int64)||
|&emsp;&emsp;bankId||integer(int64)||
|&emsp;&emsp;questionType||integer(int32)||
|&emsp;&emsp;stem||string||
|&emsp;&emsp;analysis||string||
|&emsp;&emsp;answer||string||
|&emsp;&emsp;score||number||
|&emsp;&emsp;difficulty||integer(int32)||
|&emsp;&emsp;options|选项VO|array|QuestionOptionVO|
|&emsp;&emsp;&emsp;&emsp;optionId||integer||
|&emsp;&emsp;&emsp;&emsp;label||string||
|&emsp;&emsp;&emsp;&emsp;content||string||
|&emsp;&emsp;&emsp;&emsp;isCorrect||boolean||
|&emsp;&emsp;&emsp;&emsp;orderIndex||integer||
|&emsp;&emsp;createdAt||string(date-time)||
|&emsp;&emsp;updatedAt||string(date-time)||
|total||integer(int64)|integer(int64)|
|size||integer(int64)|integer(int64)|
|current||integer(int64)|integer(int64)|
|orders||array|OrderItem|
|&emsp;&emsp;id|试卷-题目关联ID|integer(int64)||
|&emsp;&emsp;orderIndex|新的排序值|integer(int32)||
|&emsp;&emsp;sectionIndex|新的节号|integer(int32)||
|optimizeCountSql||PageQuestionVO|PageQuestionVO|
|&emsp;&emsp;records|题目VO|array|QuestionVO|
|&emsp;&emsp;&emsp;&emsp;questionId||integer||
|&emsp;&emsp;&emsp;&emsp;bankId||integer||
|&emsp;&emsp;&emsp;&emsp;questionType||integer||
|&emsp;&emsp;&emsp;&emsp;stem||string||
|&emsp;&emsp;&emsp;&emsp;analysis||string||
|&emsp;&emsp;&emsp;&emsp;answer||string||
|&emsp;&emsp;&emsp;&emsp;score||number||
|&emsp;&emsp;&emsp;&emsp;difficulty||integer||
|&emsp;&emsp;&emsp;&emsp;options|选项VO|array|QuestionOptionVO|
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;optionId||integer||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;label||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;content||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;isCorrect||boolean||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;orderIndex||integer||
|&emsp;&emsp;&emsp;&emsp;createdAt||string||
|&emsp;&emsp;&emsp;&emsp;updatedAt||string||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;size||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;orders|排序项列表|array|OrderItem|
|&emsp;&emsp;&emsp;&emsp;id|试卷-题目关联ID|integer||
|&emsp;&emsp;&emsp;&emsp;orderIndex|新的排序值|integer||
|&emsp;&emsp;&emsp;&emsp;sectionIndex|新的节号|integer||
|&emsp;&emsp;optimizeCountSql||PageQuestionVO|PageQuestionVO|
|&emsp;&emsp;searchCount||PageQuestionVO|PageQuestionVO|
|&emsp;&emsp;optimizeJoinOfCountSql||boolean||
|&emsp;&emsp;maxLimit||integer(int64)||
|&emsp;&emsp;countId||string||
|&emsp;&emsp;pages||integer(int64)||
|searchCount||PageQuestionVO|PageQuestionVO|
|&emsp;&emsp;records|题目VO|array|QuestionVO|
|&emsp;&emsp;&emsp;&emsp;questionId||integer||
|&emsp;&emsp;&emsp;&emsp;bankId||integer||
|&emsp;&emsp;&emsp;&emsp;questionType||integer||
|&emsp;&emsp;&emsp;&emsp;stem||string||
|&emsp;&emsp;&emsp;&emsp;analysis||string||
|&emsp;&emsp;&emsp;&emsp;answer||string||
|&emsp;&emsp;&emsp;&emsp;score||number||
|&emsp;&emsp;&emsp;&emsp;difficulty||integer||
|&emsp;&emsp;&emsp;&emsp;options|选项VO|array|QuestionOptionVO|
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;optionId||integer||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;label||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;content||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;isCorrect||boolean||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;orderIndex||integer||
|&emsp;&emsp;&emsp;&emsp;createdAt||string||
|&emsp;&emsp;&emsp;&emsp;updatedAt||string||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;size||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;orders|排序项列表|array|OrderItem|
|&emsp;&emsp;&emsp;&emsp;id|试卷-题目关联ID|integer||
|&emsp;&emsp;&emsp;&emsp;orderIndex|新的排序值|integer||
|&emsp;&emsp;&emsp;&emsp;sectionIndex|新的节号|integer||
|&emsp;&emsp;optimizeCountSql||PageQuestionVO|PageQuestionVO|
|&emsp;&emsp;searchCount||PageQuestionVO|PageQuestionVO|
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
			"questionId": 0,
			"bankId": 0,
			"questionType": 0,
			"stem": "",
			"analysis": "",
			"answer": "",
			"score": 0,
			"difficulty": 0,
			"options": [
				{
					"optionId": 0,
					"label": "",
					"content": "",
					"isCorrect": true,
					"orderIndex": 0
				}
			],
			"createdAt": "",
			"updatedAt": ""
		}
	],
	"total": 0,
	"size": 0,
	"current": 0,
	"orders": [
		{
			"id": 0,
			"orderIndex": 0,
			"sectionIndex": 0
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


## 查看题目详情


**接口地址**:`/api/exam/questions/{questionId}`


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
|200|OK|QuestionVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|questionId||integer(int64)|integer(int64)|
|bankId||integer(int64)|integer(int64)|
|questionType||integer(int32)|integer(int32)|
|stem||string||
|analysis||string||
|answer||string||
|score||number||
|difficulty||integer(int32)|integer(int32)|
|options||array|QuestionOptionVO|
|&emsp;&emsp;optionId||integer(int64)||
|&emsp;&emsp;label||string||
|&emsp;&emsp;content||string||
|&emsp;&emsp;isCorrect||boolean||
|&emsp;&emsp;orderIndex||integer(int32)||
|createdAt||string(date-time)|string(date-time)|
|updatedAt||string(date-time)|string(date-time)|


**响应示例**:
```javascript
{
	"questionId": 0,
	"bankId": 0,
	"questionType": 0,
	"stem": "",
	"analysis": "",
	"answer": "",
	"score": 0,
	"difficulty": 0,
	"options": [
		{
			"optionId": 0,
			"label": "",
			"content": "",
			"isCorrect": true,
			"orderIndex": 0
		}
	],
	"createdAt": "",
	"updatedAt": ""
}
```


## 更新题目


**接口地址**:`/api/exam/questions/{questionId}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "stem": "",
  "analysis": "",
  "answer": "",
  "score": 0,
  "difficulty": 0,
  "options": [
    {
      "label": "A",
      "content": "",
      "isCorrect": false,
      "orderIndex": 0
    }
  ]
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|questionId||path|true|integer(int64)||
|questionUpdateRequest|题目更新请求|body|true|QuestionUpdateRequest|QuestionUpdateRequest|
|&emsp;&emsp;stem|题干||false|string||
|&emsp;&emsp;analysis|解析||false|string||
|&emsp;&emsp;answer|标准答案||false|string||
|&emsp;&emsp;score|默认分值||false|number||
|&emsp;&emsp;difficulty|难度：1-5||false|integer(int32)||
|&emsp;&emsp;options|题目选项DTO||false|array|QuestionOptionDTO|
|&emsp;&emsp;&emsp;&emsp;label|选项标号||true|string||
|&emsp;&emsp;&emsp;&emsp;content|选项内容||true|string||
|&emsp;&emsp;&emsp;&emsp;isCorrect|是否正确答案||false|boolean||
|&emsp;&emsp;&emsp;&emsp;orderIndex|排序||false|integer||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|QuestionVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|questionId||integer(int64)|integer(int64)|
|bankId||integer(int64)|integer(int64)|
|questionType||integer(int32)|integer(int32)|
|stem||string||
|analysis||string||
|answer||string||
|score||number||
|difficulty||integer(int32)|integer(int32)|
|options||array|QuestionOptionVO|
|&emsp;&emsp;optionId||integer(int64)||
|&emsp;&emsp;label||string||
|&emsp;&emsp;content||string||
|&emsp;&emsp;isCorrect||boolean||
|&emsp;&emsp;orderIndex||integer(int32)||
|createdAt||string(date-time)|string(date-time)|
|updatedAt||string(date-time)|string(date-time)|


**响应示例**:
```javascript
{
	"questionId": 0,
	"bankId": 0,
	"questionType": 0,
	"stem": "",
	"analysis": "",
	"answer": "",
	"score": 0,
	"difficulty": 0,
	"options": [
		{
			"optionId": 0,
			"label": "",
			"content": "",
			"isCorrect": true,
			"orderIndex": 0
		}
	],
	"createdAt": "",
	"updatedAt": ""
}
```


## 删除题目


**接口地址**:`/api/exam/questions/{questionId}`


**请求方式**:`DELETE`


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
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 在题库中创建题目


**接口地址**:`/api/exam/questions/banks/{bankId}`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "questionType": 0,
  "stem": "",
  "analysis": "",
  "answer": "",
  "score": 5,
  "difficulty": 3,
  "options": [
    {
      "label": "A",
      "content": "",
      "isCorrect": false,
      "orderIndex": 0
    }
  ]
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|bankId||path|true|integer(int64)||
|questionCreateRequest|题目创建请求|body|true|QuestionCreateRequest|QuestionCreateRequest|
|&emsp;&emsp;questionType|题目类型：0=单选 1=多选 2=判断 3=填空 4=简答||true|integer(int32)||
|&emsp;&emsp;stem|题干||true|string||
|&emsp;&emsp;analysis|解析||false|string||
|&emsp;&emsp;answer|标准答案||false|string||
|&emsp;&emsp;score|默认分值||true|number||
|&emsp;&emsp;difficulty|难度：1-5||false|integer(int32)||
|&emsp;&emsp;options|题目选项DTO||false|array|QuestionOptionDTO|
|&emsp;&emsp;&emsp;&emsp;label|选项标号||true|string||
|&emsp;&emsp;&emsp;&emsp;content|选项内容||true|string||
|&emsp;&emsp;&emsp;&emsp;isCorrect|是否正确答案||false|boolean||
|&emsp;&emsp;&emsp;&emsp;orderIndex|排序||false|integer||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|QuestionVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|questionId||integer(int64)|integer(int64)|
|bankId||integer(int64)|integer(int64)|
|questionType||integer(int32)|integer(int32)|
|stem||string||
|analysis||string||
|answer||string||
|score||number||
|difficulty||integer(int32)|integer(int32)|
|options||array|QuestionOptionVO|
|&emsp;&emsp;optionId||integer(int64)||
|&emsp;&emsp;label||string||
|&emsp;&emsp;content||string||
|&emsp;&emsp;isCorrect||boolean||
|&emsp;&emsp;orderIndex||integer(int32)||
|createdAt||string(date-time)|string(date-time)|
|updatedAt||string(date-time)|string(date-time)|


**响应示例**:
```javascript
{
	"questionId": 0,
	"bankId": 0,
	"questionType": 0,
	"stem": "",
	"analysis": "",
	"answer": "",
	"score": 0,
	"difficulty": 0,
	"options": [
		{
			"optionId": 0,
			"label": "",
			"content": "",
			"isCorrect": true,
			"orderIndex": 0
		}
	],
	"createdAt": "",
	"updatedAt": ""
}
```