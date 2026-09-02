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
-- Name: lp_mastery; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.lp_mastery (
    mastery_id bigint NOT NULL,
    student_id bigint NOT NULL,
    class_id bigint NOT NULL,
    course_id bigint NOT NULL,
    point_id bigint NOT NULL,
    mastery_level smallint DEFAULT 0 NOT NULL,
    total_score numeric(10,1) DEFAULT 0 NOT NULL,
    earned_score numeric(10,1) DEFAULT 0 NOT NULL,
    answer_count integer DEFAULT 0 NOT NULL,
    correct_count integer DEFAULT 0 NOT NULL,
    last_practice_at timestamp with time zone,
    is_deleted integer DEFAULT 0 NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: TABLE lp_mastery; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.lp_mastery IS '学生-知识点掌握度';


--
-- Name: COLUMN lp_mastery.mastery_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_mastery.mastery_id IS '掌握度记录ID';


--
-- Name: COLUMN lp_mastery.student_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_mastery.student_id IS '学生ID';


--
-- Name: COLUMN lp_mastery.class_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_mastery.class_id IS '班级ID';


--
-- Name: COLUMN lp_mastery.course_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_mastery.course_id IS '课程ID';


--
-- Name: COLUMN lp_mastery.point_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_mastery.point_id IS '知识点ID';


--
-- Name: COLUMN lp_mastery.mastery_level; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_mastery.mastery_level IS '掌握度 0-100';


--
-- Name: COLUMN lp_mastery.total_score; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_mastery.total_score IS '该知识点累计应得总分（相关题目满分之和）';


--
-- Name: COLUMN lp_mastery.earned_score; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_mastery.earned_score IS '该知识点累计实际得分';


--
-- Name: COLUMN lp_mastery.answer_count; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_mastery.answer_count IS '该知识点累计作答题数';


--
-- Name: COLUMN lp_mastery.correct_count; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_mastery.correct_count IS '该知识点累计答对题数';


--
-- Name: COLUMN lp_mastery.last_practice_at; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_mastery.last_practice_at IS '最近一次涉及该知识点的作答时间';


--
-- Name: lp_sheet_graded_log; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.lp_sheet_graded_log (
    log_id bigint NOT NULL,
    sheet_id bigint NOT NULL,
    submit_count integer NOT NULL,
    student_id bigint NOT NULL,
    class_id bigint NOT NULL,
    course_id bigint NOT NULL,
    contribution_json text,
    is_deleted integer DEFAULT 0 NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: TABLE lp_sheet_graded_log; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.lp_sheet_graded_log IS '答卷批改处理日志（幂等+回滚）';


--
-- Name: COLUMN lp_sheet_graded_log.log_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_sheet_graded_log.log_id IS '日志ID';


--
-- Name: COLUMN lp_sheet_graded_log.sheet_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_sheet_graded_log.sheet_id IS '答卷ID';


--
-- Name: COLUMN lp_sheet_graded_log.submit_count; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_sheet_graded_log.submit_count IS '提交次数（用于重复提交判断）';


--
-- Name: COLUMN lp_sheet_graded_log.student_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_sheet_graded_log.student_id IS '学生ID';


--
-- Name: COLUMN lp_sheet_graded_log.class_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_sheet_graded_log.class_id IS '班级ID';


--
-- Name: COLUMN lp_sheet_graded_log.course_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_sheet_graded_log.course_id IS '课程ID';


--
-- Name: COLUMN lp_sheet_graded_log.contribution_json; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_sheet_graded_log.contribution_json IS '贡献快照JSON';


--
-- Name: lp_wrong_point; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.lp_wrong_point (
    id bigint NOT NULL,
    wrong_id bigint NOT NULL,
    student_id bigint NOT NULL,
    class_id bigint NOT NULL,
    course_id bigint NOT NULL,
    point_id bigint NOT NULL,
    is_deleted integer DEFAULT 0 NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: TABLE lp_wrong_point; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.lp_wrong_point IS '错题-知识点关联';


--
-- Name: COLUMN lp_wrong_point.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_wrong_point.id IS '关联ID';


--
-- Name: COLUMN lp_wrong_point.wrong_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_wrong_point.wrong_id IS '错题记录ID（lp_wrong_record.wrong_id）';


--
-- Name: COLUMN lp_wrong_point.student_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_wrong_point.student_id IS '学生ID';


--
-- Name: COLUMN lp_wrong_point.class_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_wrong_point.class_id IS '班级ID';


--
-- Name: COLUMN lp_wrong_point.course_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_wrong_point.course_id IS '课程ID';


--
-- Name: COLUMN lp_wrong_point.point_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_wrong_point.point_id IS '知识点ID';


--
-- Name: lp_wrong_record; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.lp_wrong_record (
    wrong_id bigint NOT NULL,
    student_id bigint NOT NULL,
    class_id bigint NOT NULL,
    course_id bigint NOT NULL,
    question_id bigint NOT NULL,
    record_id bigint,
    exam_id bigint,
    question_type smallint,
    full_score numeric(5,1),
    earned_score numeric(5,1),
    wrong_type character varying(30),
    is_resolved integer DEFAULT 0 NOT NULL,
    resolved_at timestamp with time zone,
    wrong_count integer DEFAULT 1 NOT NULL,
    last_wrong_at timestamp with time zone DEFAULT now() NOT NULL,
    is_deleted integer DEFAULT 0 NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: TABLE lp_wrong_record; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.lp_wrong_record IS '学生错题记录';


--
-- Name: COLUMN lp_wrong_record.wrong_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_wrong_record.wrong_id IS '错题记录ID';


--
-- Name: COLUMN lp_wrong_record.student_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_wrong_record.student_id IS '学生ID';


--
-- Name: COLUMN lp_wrong_record.class_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_wrong_record.class_id IS '班级ID';


--
-- Name: COLUMN lp_wrong_record.course_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_wrong_record.course_id IS '课程ID';


--
-- Name: COLUMN lp_wrong_record.question_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_wrong_record.question_id IS '题目ID（ex_question.question_id）';


--
-- Name: COLUMN lp_wrong_record.record_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_wrong_record.record_id IS '来源答题记录ID（最近一次）';


--
-- Name: COLUMN lp_wrong_record.exam_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_wrong_record.exam_id IS '来源考试ID（最近一次）';


--
-- Name: COLUMN lp_wrong_record.question_type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_wrong_record.question_type IS '题目类型：0单选 1多选 2判断 3填空 4简答';


--
-- Name: COLUMN lp_wrong_record.full_score; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_wrong_record.full_score IS '该题满分（最近一次）';


--
-- Name: COLUMN lp_wrong_record.earned_score; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_wrong_record.earned_score IS '该题得分（最近一次）';


--
-- Name: COLUMN lp_wrong_record.wrong_type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_wrong_record.wrong_type IS 'AI认定的错误类型，来自 ex_answer_record.ai_wrong_type，无则 null';


--
-- Name: COLUMN lp_wrong_record.is_resolved; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_wrong_record.is_resolved IS '是否已解决：0未解决 1已解决（学生手动标记）';


--
-- Name: COLUMN lp_wrong_record.resolved_at; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_wrong_record.resolved_at IS '标记解决的时间';


--
-- Name: COLUMN lp_wrong_record.wrong_count; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_wrong_record.wrong_count IS '该题累计答错次数';


--
-- Name: COLUMN lp_wrong_record.last_wrong_at; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.lp_wrong_record.last_wrong_at IS '最近一次答错时间';


--
-- Name: lp_mastery lp_mastery_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lp_mastery
    ADD CONSTRAINT lp_mastery_pkey PRIMARY KEY (mastery_id);


--
-- Name: lp_sheet_graded_log lp_sheet_graded_log_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lp_sheet_graded_log
    ADD CONSTRAINT lp_sheet_graded_log_pkey PRIMARY KEY (log_id);


--
-- Name: lp_wrong_point lp_wrong_point_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lp_wrong_point
    ADD CONSTRAINT lp_wrong_point_pkey PRIMARY KEY (id);


--
-- Name: lp_wrong_record lp_wrong_record_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lp_wrong_record
    ADD CONSTRAINT lp_wrong_record_pkey PRIMARY KEY (wrong_id);


--
-- Name: idx_lpm_class_point; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_lpm_class_point ON public.lp_mastery USING btree (class_id, point_id) WHERE (is_deleted = 0);


--
-- Name: idx_lpm_course; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_lpm_course ON public.lp_mastery USING btree (course_id) WHERE (is_deleted = 0);


--
-- Name: idx_lpm_student_class; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_lpm_student_class ON public.lp_mastery USING btree (student_id, class_id) WHERE (is_deleted = 0);


--
-- Name: idx_lpsgl_student; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_lpsgl_student ON public.lp_sheet_graded_log USING btree (student_id) WHERE (is_deleted = 0);


--
-- Name: idx_lpw_class_question; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_lpw_class_question ON public.lp_wrong_record USING btree (class_id, question_id) WHERE (is_deleted = 0);


--
-- Name: idx_lpw_student_class; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_lpw_student_class ON public.lp_wrong_record USING btree (student_id, class_id, is_resolved) WHERE (is_deleted = 0);


--
-- Name: idx_lpwp_class_point; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_lpwp_class_point ON public.lp_wrong_point USING btree (class_id, point_id) WHERE (is_deleted = 0);


--
-- Name: idx_lpwp_student_point; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_lpwp_student_point ON public.lp_wrong_point USING btree (student_id, point_id) WHERE (is_deleted = 0);


--
-- Name: uq_lpm_student_class_point; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX uq_lpm_student_class_point ON public.lp_mastery USING btree (student_id, class_id, point_id) WHERE (is_deleted = 0);


--
-- Name: uq_lpsg_sheet; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX uq_lpsg_sheet ON public.lp_sheet_graded_log USING btree (sheet_id) WHERE (is_deleted = 0);


--
-- Name: uq_lpsgl_sheet; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX uq_lpsgl_sheet ON public.lp_sheet_graded_log USING btree (sheet_id) WHERE (is_deleted = 0);


--
-- Name: uq_lpw_student_class_question; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX uq_lpw_student_class_question ON public.lp_wrong_record USING btree (student_id, class_id, question_id) WHERE (is_deleted = 0);


--
-- Name: uq_lpwp_wrong_point; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX uq_lpwp_wrong_point ON public.lp_wrong_point USING btree (wrong_id, point_id) WHERE (is_deleted = 0);


--
-- PostgreSQL database dump complete
--
