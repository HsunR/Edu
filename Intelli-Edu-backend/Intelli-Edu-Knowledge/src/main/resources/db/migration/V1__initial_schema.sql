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
-- Name: kn_knowledge_point; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.kn_knowledge_point (
    point_id bigint NOT NULL,
    point_name character varying(100) NOT NULL,
    course_id bigint NOT NULL,
    parent_id bigint,
    description character varying(500),
    order_index integer DEFAULT 0 NOT NULL,
    is_deleted integer DEFAULT 0 NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: TABLE kn_knowledge_point; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.kn_knowledge_point IS '知识点';


--
-- Name: COLUMN kn_knowledge_point.point_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.kn_knowledge_point.point_id IS '知识点ID';


--
-- Name: COLUMN kn_knowledge_point.point_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.kn_knowledge_point.point_name IS '知识点名称';


--
-- Name: COLUMN kn_knowledge_point.course_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.kn_knowledge_point.course_id IS '所属课程ID';


--
-- Name: COLUMN kn_knowledge_point.parent_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.kn_knowledge_point.parent_id IS '父知识点ID，null=一级知识点';


--
-- Name: COLUMN kn_knowledge_point.description; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.kn_knowledge_point.description IS '知识点描述';


--
-- Name: COLUMN kn_knowledge_point.order_index; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.kn_knowledge_point.order_index IS '排序';


--
-- Name: kn_knowledge_relation; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.kn_knowledge_relation (
    id bigint NOT NULL,
    source_point_id bigint NOT NULL,
    target_point_id bigint NOT NULL,
    relation_type integer NOT NULL,
    course_id bigint NOT NULL,
    weight integer DEFAULT 1 NOT NULL,
    is_deleted integer DEFAULT 0 NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: TABLE kn_knowledge_relation; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.kn_knowledge_relation IS '知识点间关系（预留）';


--
-- Name: COLUMN kn_knowledge_relation.source_point_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.kn_knowledge_relation.source_point_id IS '源知识点ID';


--
-- Name: COLUMN kn_knowledge_relation.target_point_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.kn_knowledge_relation.target_point_id IS '目标知识点ID';


--
-- Name: COLUMN kn_knowledge_relation.relation_type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.kn_knowledge_relation.relation_type IS '关系类型：prerequisite=前置依赖 related=关联 contains=包含';


--
-- Name: COLUMN kn_knowledge_relation.weight; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.kn_knowledge_relation.weight IS '关系权重（图谱可视化用）';


--
-- Name: kn_knowledge_section; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.kn_knowledge_section (
    id bigint NOT NULL,
    point_id bigint NOT NULL,
    section_id bigint NOT NULL,
    course_id bigint NOT NULL,
    is_deleted integer DEFAULT 0 NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: TABLE kn_knowledge_section; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.kn_knowledge_section IS '知识点-章节关联';


--
-- Name: COLUMN kn_knowledge_section.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.kn_knowledge_section.id IS '关联ID';


--
-- Name: COLUMN kn_knowledge_section.point_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.kn_knowledge_section.point_id IS '知识点ID（二级）';


--
-- Name: COLUMN kn_knowledge_section.section_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.kn_knowledge_section.section_id IS '章节ID（co_section.section_id）';


--
-- Name: COLUMN kn_knowledge_section.course_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.kn_knowledge_section.course_id IS '冗余课程ID（用于校验一致性和按课程查询）';


--
-- Name: kn_question_knowledge; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.kn_question_knowledge (
    id bigint NOT NULL,
    point_id bigint NOT NULL,
    question_id bigint NOT NULL,
    course_id bigint NOT NULL,
    is_deleted integer DEFAULT 0 NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: TABLE kn_question_knowledge; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.kn_question_knowledge IS '知识点-题目关联';


--
-- Name: COLUMN kn_question_knowledge.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.kn_question_knowledge.id IS '关联ID';


--
-- Name: COLUMN kn_question_knowledge.point_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.kn_question_knowledge.point_id IS '知识点ID（二级）';


--
-- Name: COLUMN kn_question_knowledge.question_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.kn_question_knowledge.question_id IS '题目ID（ex_question.question_id）';


--
-- Name: COLUMN kn_question_knowledge.course_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.kn_question_knowledge.course_id IS '冗余课程ID';


--
-- Name: kn_knowledge_point kn_knowledge_point_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.kn_knowledge_point
    ADD CONSTRAINT kn_knowledge_point_pkey PRIMARY KEY (point_id);


--
-- Name: kn_knowledge_relation kn_knowledge_relation_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.kn_knowledge_relation
    ADD CONSTRAINT kn_knowledge_relation_pkey PRIMARY KEY (id);


--
-- Name: kn_knowledge_section kn_knowledge_section_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.kn_knowledge_section
    ADD CONSTRAINT kn_knowledge_section_pkey PRIMARY KEY (id);


--
-- Name: kn_question_knowledge kn_question_knowledge_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.kn_question_knowledge
    ADD CONSTRAINT kn_question_knowledge_pkey PRIMARY KEY (id);


--
-- Name: idx_kp_course; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_kp_course ON public.kn_knowledge_point USING btree (course_id) WHERE (is_deleted = 0);


--
-- Name: idx_kp_parent; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_kp_parent ON public.kn_knowledge_point USING btree (parent_id) WHERE ((is_deleted = 0) AND (parent_id IS NOT NULL));


--
-- Name: idx_kr_course; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_kr_course ON public.kn_knowledge_relation USING btree (course_id) WHERE (is_deleted = 0);


--
-- Name: idx_ks_course; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_ks_course ON public.kn_knowledge_section USING btree (course_id) WHERE (is_deleted = 0);


--
-- Name: idx_ks_section; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_ks_section ON public.kn_knowledge_section USING btree (section_id) WHERE (is_deleted = 0);


--
-- Name: idx_qk_course; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qk_course ON public.kn_question_knowledge USING btree (course_id) WHERE (is_deleted = 0);


--
-- Name: idx_qk_question; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qk_question ON public.kn_question_knowledge USING btree (question_id) WHERE (is_deleted = 0);


--
-- Name: uq_kp_name; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX uq_kp_name ON public.kn_knowledge_point USING btree (course_id, parent_id, point_name) WHERE (is_deleted = 0);


--
-- Name: uq_kp_name_root; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX uq_kp_name_root ON public.kn_knowledge_point USING btree (course_id, point_name) WHERE ((is_deleted = 0) AND (parent_id IS NULL));


--
-- Name: uq_kr_source_target; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX uq_kr_source_target ON public.kn_knowledge_relation USING btree (source_point_id, target_point_id, relation_type) WHERE (is_deleted = 0);


--
-- Name: uq_ks_point_section; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX uq_ks_point_section ON public.kn_knowledge_section USING btree (point_id, section_id) WHERE (is_deleted = 0);


--
-- Name: uq_qk_point_question; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX uq_qk_point_question ON public.kn_question_knowledge USING btree (point_id, question_id) WHERE (is_deleted = 0);


--
-- PostgreSQL database dump complete
--
