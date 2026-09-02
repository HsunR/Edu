-- Generated from the previous schema-only PostgreSQL export.
-- Subsequent changes must be added as new versioned Flyway migrations.


SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: ex_answer_record; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ex_answer_record (
    record_id bigint NOT NULL,
    sheet_id bigint NOT NULL,
    question_id bigint NOT NULL,
    answer_content text,
    score numeric(5,1) DEFAULT 0 NOT NULL,
    is_correct boolean,
    grading_status integer DEFAULT 0 NOT NULL,
    grader_id bigint,
    comment text,
    is_deleted integer DEFAULT 0 NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    ai_score numeric(5,1),
    ai_comment text,
    ai_wrong_type character varying(30),
    ai_confidence numeric(3,2)
);


--
-- Name: TABLE ex_answer_record; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.ex_answer_record IS '答题记录';


--
-- Name: COLUMN ex_answer_record.record_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_answer_record.record_id IS '记录ID';


--
-- Name: COLUMN ex_answer_record.sheet_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_answer_record.sheet_id IS '所属答卷ID';


--
-- Name: COLUMN ex_answer_record.question_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_answer_record.question_id IS '题目ID';


--
-- Name: COLUMN ex_answer_record.answer_content; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_answer_record.answer_content IS '学生答案';


--
-- Name: COLUMN ex_answer_record.score; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_answer_record.score IS '得分（批阅后回填，默认0）';


--
-- Name: COLUMN ex_answer_record.is_correct; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_answer_record.is_correct IS '是否正确（客观题自动判定）';


--
-- Name: COLUMN ex_answer_record.grading_status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_answer_record.grading_status IS '批阅状态：0=待批阅 1=已批阅 2=AI批阅中';


--
-- Name: COLUMN ex_answer_record.grader_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_answer_record.grader_id IS '批阅人ID（null表示自动批阅）';


--
-- Name: COLUMN ex_answer_record.comment; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_answer_record.comment IS '批阅评语';


--
-- Name: COLUMN ex_answer_record.ai_score; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_answer_record.ai_score IS 'AI 批改建议分（非最终分，教师确认后才进 score）';


--
-- Name: COLUMN ex_answer_record.ai_comment; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_answer_record.ai_comment IS 'AI 评语';


--
-- Name: COLUMN ex_answer_record.ai_wrong_type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_answer_record.ai_wrong_type IS 'AI判定的错误类型';


--
-- Name: COLUMN ex_answer_record.ai_confidence; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_answer_record.ai_confidence IS 'AI 置信度 0-1';


--
-- Name: ex_answer_sheet; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ex_answer_sheet (
    sheet_id bigint NOT NULL,
    exam_id bigint NOT NULL,
    student_id bigint NOT NULL,
    status integer DEFAULT 0 NOT NULL,
    total_score numeric(6,1) DEFAULT 0 NOT NULL,
    objective_score numeric(6,1) DEFAULT 0 NOT NULL,
    subjective_score numeric(6,1) DEFAULT 0 NOT NULL,
    submit_count integer DEFAULT 0 NOT NULL,
    start_answer_time timestamp with time zone,
    submit_time timestamp with time zone,
    deadline timestamp with time zone,
    version integer DEFAULT 0 NOT NULL,
    is_deleted integer DEFAULT 0 NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: TABLE ex_answer_sheet; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.ex_answer_sheet IS '答卷';


--
-- Name: COLUMN ex_answer_sheet.sheet_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_answer_sheet.sheet_id IS '答卷ID';


--
-- Name: COLUMN ex_answer_sheet.exam_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_answer_sheet.exam_id IS '关联考试ID';


--
-- Name: COLUMN ex_answer_sheet.student_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_answer_sheet.student_id IS '学生ID';


--
-- Name: COLUMN ex_answer_sheet.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_answer_sheet.status IS '状态：0=答题中 1=已提交 2=已批阅';


--
-- Name: COLUMN ex_answer_sheet.total_score; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_answer_sheet.total_score IS '总得分（= objective + subjective）';


--
-- Name: COLUMN ex_answer_sheet.objective_score; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_answer_sheet.objective_score IS '客观题得分（交卷时自动计算）';


--
-- Name: COLUMN ex_answer_sheet.subjective_score; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_answer_sheet.subjective_score IS '主观题得分（教师批阅后回填）';


--
-- Name: COLUMN ex_answer_sheet.submit_count; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_answer_sheet.submit_count IS '提交次数（作业模式可多次提交覆盖）';


--
-- Name: COLUMN ex_answer_sheet.start_answer_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_answer_sheet.start_answer_time IS '开始作答时间';


--
-- Name: COLUMN ex_answer_sheet.submit_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_answer_sheet.submit_time IS '最后一次提交时间';


--
-- Name: COLUMN ex_answer_sheet.deadline; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_answer_sheet.deadline IS '个人截止时间：duration!=null → min(start+duration, end_time)；duration=null → end_time；allow_late_submit=true → null（不自动交卷）';


--
-- Name: COLUMN ex_answer_sheet.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_answer_sheet.version IS '乐观锁版本号';


--
-- Name: ex_exam; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ex_exam (
    exam_id bigint NOT NULL,
    exam_name character varying(200) NOT NULL,
    paper_id bigint NOT NULL,
    class_id bigint NOT NULL,
    course_id bigint NOT NULL,
    teacher_id bigint NOT NULL,
    exam_type integer DEFAULT 0 NOT NULL,
    start_time timestamp with time zone NOT NULL,
    end_time timestamp with time zone NOT NULL,
    duration_minutes integer,
    allow_late_submit boolean DEFAULT false NOT NULL,
    status integer DEFAULT 0 NOT NULL,
    version integer DEFAULT 0 NOT NULL,
    is_deleted integer DEFAULT 0 NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: TABLE ex_exam; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.ex_exam IS '考试（发布实例）';


--
-- Name: COLUMN ex_exam.exam_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_exam.exam_id IS '考试ID';


--
-- Name: COLUMN ex_exam.exam_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_exam.exam_name IS '考试名称';


--
-- Name: COLUMN ex_exam.paper_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_exam.paper_id IS '关联试卷ID';


--
-- Name: COLUMN ex_exam.class_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_exam.class_id IS '关联班级ID（Feign查Course服务）';


--
-- Name: COLUMN ex_exam.course_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_exam.course_id IS '冗余课程ID（发布时校验：paper.course_id == class所属course_id）';


--
-- Name: COLUMN ex_exam.teacher_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_exam.teacher_id IS '发布教师ID（发布时校验：== 当前用户 == paper.teacher_id）';


--
-- Name: COLUMN ex_exam.exam_type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_exam.exam_type IS '类型：0=考试 1=练习 2=作业';


--
-- Name: COLUMN ex_exam.start_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_exam.start_time IS '开放窗口开始时间';


--
-- Name: COLUMN ex_exam.end_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_exam.end_time IS '开放窗口结束时间';


--
-- Name: COLUMN ex_exam.duration_minutes; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_exam.duration_minutes IS '答题时长（分钟），null=不限时，截止退化为end_time';


--
-- Name: COLUMN ex_exam.allow_late_submit; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_exam.allow_late_submit IS '是否允许迟交（true时自动交卷跳过该考试的答卷）';


--
-- Name: COLUMN ex_exam.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_exam.status IS '状态：0=未开始 1=进行中 2=已结束 3=已批阅完成';


--
-- Name: COLUMN ex_exam.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_exam.version IS '乐观锁版本号';


--
-- Name: ex_paper; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ex_paper (
    paper_id bigint NOT NULL,
    paper_name character varying(200) NOT NULL,
    course_id bigint NOT NULL,
    teacher_id bigint NOT NULL,
    total_score numeric(6,1) DEFAULT 0 NOT NULL,
    sections jsonb,
    status integer DEFAULT 0 NOT NULL,
    version integer DEFAULT 0 NOT NULL,
    is_deleted integer DEFAULT 0 NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: TABLE ex_paper; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.ex_paper IS '试卷';


--
-- Name: COLUMN ex_paper.paper_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_paper.paper_id IS '试卷ID';


--
-- Name: COLUMN ex_paper.paper_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_paper.paper_name IS '试卷名称';


--
-- Name: COLUMN ex_paper.course_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_paper.course_id IS '所属课程ID';


--
-- Name: COLUMN ex_paper.teacher_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_paper.teacher_id IS '出卷教师ID';


--
-- Name: COLUMN ex_paper.total_score; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_paper.total_score IS '总分（冗余，组卷时计算）';


--
-- Name: COLUMN ex_paper.sections; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_paper.sections IS '分节标题 [{"index":1,"title":"选择题"},...]';


--
-- Name: COLUMN ex_paper.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_paper.status IS '状态：0=草稿 1=已发布';


--
-- Name: COLUMN ex_paper.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_paper.version IS '乐观锁版本号';


--
-- Name: ex_paper_question; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ex_paper_question (
    id bigint NOT NULL,
    paper_id bigint NOT NULL,
    question_id bigint NOT NULL,
    order_index integer DEFAULT 0 NOT NULL,
    score numeric(5,1) NOT NULL,
    section_index integer DEFAULT 1 NOT NULL,
    question_snapshot jsonb,
    is_deleted integer DEFAULT 0 NOT NULL
);


--
-- Name: TABLE ex_paper_question; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.ex_paper_question IS '试卷-题目关联';


--
-- Name: COLUMN ex_paper_question.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_paper_question.id IS '关联ID';


--
-- Name: COLUMN ex_paper_question.paper_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_paper_question.paper_id IS '试卷ID';


--
-- Name: COLUMN ex_paper_question.question_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_paper_question.question_id IS '题目ID（保留引用，用于统计追溯）';


--
-- Name: COLUMN ex_paper_question.order_index; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_paper_question.order_index IS '题号排序';


--
-- Name: COLUMN ex_paper_question.score; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_paper_question.score IS '该题在本卷中的分值';


--
-- Name: COLUMN ex_paper_question.section_index; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_paper_question.section_index IS '归属第几节（对应paper.sections的index）';


--
-- Name: COLUMN ex_paper_question.question_snapshot; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_paper_question.question_snapshot IS '题目快照（试卷发布时冻结）：{"stem":"...","question_type":0,"options":[...],"answer":"B","analysis":"..."}，草稿阶段为null';


--
-- Name: ex_question; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ex_question (
    question_id bigint NOT NULL,
    bank_id bigint NOT NULL,
    question_type integer NOT NULL,
    stem text NOT NULL,
    analysis text,
    answer text,
    score numeric(5,1) DEFAULT 0 NOT NULL,
    difficulty integer DEFAULT 3 NOT NULL,
    is_deleted integer DEFAULT 0 NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    grading_criteria text
);


--
-- Name: TABLE ex_question; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.ex_question IS '题目';


--
-- Name: COLUMN ex_question.question_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_question.question_id IS '题目ID';


--
-- Name: COLUMN ex_question.bank_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_question.bank_id IS '所属题库ID';


--
-- Name: COLUMN ex_question.question_type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_question.question_type IS '题目类型：0=单选 1=多选 2=判断 3=填空 4=简答';


--
-- Name: COLUMN ex_question.stem; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_question.stem IS '题干（支持富文本）';


--
-- Name: COLUMN ex_question.analysis; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_question.analysis IS '解析';


--
-- Name: COLUMN ex_question.answer; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_question.answer IS '标准答案（客观题存选项标号如A或A,B，主观题存参考答案）';


--
-- Name: COLUMN ex_question.score; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_question.score IS '默认分值';


--
-- Name: COLUMN ex_question.difficulty; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_question.difficulty IS '难度：1-5';


--
-- Name: COLUMN ex_question.grading_criteria; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_question.grading_criteria IS '主观题评分标准，教师填写。NULL 时 AI 走降级批改';


--
-- Name: ex_question_bank; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ex_question_bank (
    bank_id bigint NOT NULL,
    bank_name character varying(100) NOT NULL,
    course_id bigint NOT NULL,
    teacher_id bigint NOT NULL,
    description character varying(500),
    question_count integer DEFAULT 0 NOT NULL,
    is_deleted integer DEFAULT 0 NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: TABLE ex_question_bank; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.ex_question_bank IS '题库';


--
-- Name: COLUMN ex_question_bank.bank_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_question_bank.bank_id IS '题库ID';


--
-- Name: COLUMN ex_question_bank.bank_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_question_bank.bank_name IS '题库名称';


--
-- Name: COLUMN ex_question_bank.course_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_question_bank.course_id IS '所属课程ID';


--
-- Name: COLUMN ex_question_bank.teacher_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_question_bank.teacher_id IS '创建教师ID';


--
-- Name: COLUMN ex_question_bank.description; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_question_bank.description IS '题库描述';


--
-- Name: COLUMN ex_question_bank.question_count; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_question_bank.question_count IS '题目数量（冗余计数）';


--
-- Name: ex_question_option; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ex_question_option (
    option_id bigint NOT NULL,
    question_id bigint NOT NULL,
    label character varying(10) NOT NULL,
    content text NOT NULL,
    is_correct boolean DEFAULT false NOT NULL,
    order_index integer DEFAULT 0 NOT NULL,
    is_deleted integer DEFAULT 0 NOT NULL
);


--
-- Name: TABLE ex_question_option; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.ex_question_option IS '题目选项';


--
-- Name: COLUMN ex_question_option.option_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_question_option.option_id IS '选项ID';


--
-- Name: COLUMN ex_question_option.question_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_question_option.question_id IS '所属题目ID';


--
-- Name: COLUMN ex_question_option.label; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_question_option.label IS '选项标号（A/B/C/D 或 对/错）';


--
-- Name: COLUMN ex_question_option.content; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_question_option.content IS '选项内容';


--
-- Name: COLUMN ex_question_option.is_correct; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_question_option.is_correct IS '是否正确答案';


--
-- Name: COLUMN ex_question_option.order_index; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ex_question_option.order_index IS '排序';


--
-- Name: ex_answer_record ex_answer_record_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ex_answer_record
    ADD CONSTRAINT ex_answer_record_pkey PRIMARY KEY (record_id);


--
-- Name: ex_answer_sheet ex_answer_sheet_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ex_answer_sheet
    ADD CONSTRAINT ex_answer_sheet_pkey PRIMARY KEY (sheet_id);


--
-- Name: ex_exam ex_exam_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ex_exam
    ADD CONSTRAINT ex_exam_pkey PRIMARY KEY (exam_id);


--
-- Name: ex_paper ex_paper_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ex_paper
    ADD CONSTRAINT ex_paper_pkey PRIMARY KEY (paper_id);


--
-- Name: ex_paper_question ex_paper_question_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ex_paper_question
    ADD CONSTRAINT ex_paper_question_pkey PRIMARY KEY (id);


--
-- Name: ex_question_bank ex_question_bank_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ex_question_bank
    ADD CONSTRAINT ex_question_bank_pkey PRIMARY KEY (bank_id);


--
-- Name: ex_question_option ex_question_option_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ex_question_option
    ADD CONSTRAINT ex_question_option_pkey PRIMARY KEY (option_id);


--
-- Name: ex_question ex_question_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ex_question
    ADD CONSTRAINT ex_question_pkey PRIMARY KEY (question_id);


--
-- Name: idx_ar_grading; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_ar_grading ON public.ex_answer_record USING btree (sheet_id, grading_status) WHERE (is_deleted = 0);


--
-- Name: idx_ar_sheet; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_ar_sheet ON public.ex_answer_record USING btree (sheet_id) WHERE (is_deleted = 0);


--
-- Name: idx_as_auto_submit; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_as_auto_submit ON public.ex_answer_sheet USING btree (deadline) WHERE ((is_deleted = 0) AND (status = 0) AND (deadline IS NOT NULL));


--
-- Name: idx_as_exam; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_as_exam ON public.ex_answer_sheet USING btree (exam_id) WHERE (is_deleted = 0);


--
-- Name: idx_as_student; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_as_student ON public.ex_answer_sheet USING btree (student_id) WHERE (is_deleted = 0);


--
-- Name: idx_e_class; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_e_class ON public.ex_exam USING btree (class_id) WHERE (is_deleted = 0);


--
-- Name: idx_e_course; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_e_course ON public.ex_exam USING btree (course_id) WHERE (is_deleted = 0);


--
-- Name: idx_e_paper; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_e_paper ON public.ex_exam USING btree (paper_id) WHERE (is_deleted = 0);


--
-- Name: idx_e_status; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_e_status ON public.ex_exam USING btree (status, end_time) WHERE (is_deleted = 0);


--
-- Name: idx_e_teacher; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_e_teacher ON public.ex_exam USING btree (teacher_id) WHERE (is_deleted = 0);


--
-- Name: idx_p_course; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_p_course ON public.ex_paper USING btree (course_id) WHERE (is_deleted = 0);


--
-- Name: idx_p_teacher; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_p_teacher ON public.ex_paper USING btree (teacher_id) WHERE (is_deleted = 0);


--
-- Name: idx_pq_paper; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_pq_paper ON public.ex_paper_question USING btree (paper_id) WHERE (is_deleted = 0);


--
-- Name: idx_q_bank; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_q_bank ON public.ex_question USING btree (bank_id) WHERE (is_deleted = 0);


--
-- Name: idx_q_type; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_q_type ON public.ex_question USING btree (bank_id, question_type) WHERE (is_deleted = 0);


--
-- Name: idx_qb_course; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qb_course ON public.ex_question_bank USING btree (course_id) WHERE (is_deleted = 0);


--
-- Name: idx_qb_teacher; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qb_teacher ON public.ex_question_bank USING btree (teacher_id) WHERE (is_deleted = 0);


--
-- Name: idx_qo_question; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qo_question ON public.ex_question_option USING btree (question_id) WHERE (is_deleted = 0);


--
-- Name: uq_ar_sheet_question; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX uq_ar_sheet_question ON public.ex_answer_record USING btree (sheet_id, question_id) WHERE (is_deleted = 0);


--
-- Name: uq_as_exam_student; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX uq_as_exam_student ON public.ex_answer_sheet USING btree (exam_id, student_id) WHERE (is_deleted = 0);


--
-- Name: uq_pq_paper_question; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX uq_pq_paper_question ON public.ex_paper_question USING btree (paper_id, question_id) WHERE (is_deleted = 0);


--
-- PostgreSQL database dump complete
--
