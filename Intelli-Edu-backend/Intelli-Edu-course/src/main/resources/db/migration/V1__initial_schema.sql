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
-- Name: co_category; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.co_category (
    category_id bigint NOT NULL,
    name character varying(50) NOT NULL,
    parent_id bigint,
    order_index integer DEFAULT 0 NOT NULL,
    is_deleted smallint DEFAULT 0 NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: TABLE co_category; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.co_category IS '课程分类表';


--
-- Name: COLUMN co_category.category_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_category.category_id IS '分类ID';


--
-- Name: COLUMN co_category.name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_category.name IS '分类名称';


--
-- Name: COLUMN co_category.parent_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_category.parent_id IS '父分类ID，NULL表示顶级分类';


--
-- Name: COLUMN co_category.order_index; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_category.order_index IS '同级排序序号';


--
-- Name: co_chapter; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.co_chapter (
    chapter_id bigint NOT NULL,
    course_id bigint NOT NULL,
    title character varying(200) NOT NULL,
    order_index integer DEFAULT 0 NOT NULL,
    version integer DEFAULT 0 NOT NULL,
    is_deleted smallint DEFAULT 0 NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: TABLE co_chapter; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.co_chapter IS '课程章表';


--
-- Name: COLUMN co_chapter.chapter_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_chapter.chapter_id IS '章ID';


--
-- Name: COLUMN co_chapter.course_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_chapter.course_id IS '所属课程ID';


--
-- Name: COLUMN co_chapter.title; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_chapter.title IS '章标题';


--
-- Name: COLUMN co_chapter.order_index; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_chapter.order_index IS '章排序序号';


--
-- Name: co_class; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.co_class (
    class_id bigint NOT NULL,
    course_id bigint NOT NULL,
    class_name character varying(100) NOT NULL,
    teacher_id bigint NOT NULL,
    invite_code character varying(8) NOT NULL,
    max_students integer,
    start_date timestamp with time zone,
    end_date timestamp with time zone,
    status smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0 NOT NULL,
    is_deleted smallint DEFAULT 0 NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: TABLE co_class; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.co_class IS '班级表（课程开课实例）';


--
-- Name: COLUMN co_class.class_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_class.class_id IS '班级ID';


--
-- Name: COLUMN co_class.course_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_class.course_id IS '所属课程ID';


--
-- Name: COLUMN co_class.class_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_class.class_name IS '班级名称';


--
-- Name: COLUMN co_class.teacher_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_class.teacher_id IS '班级管理教师userId';


--
-- Name: COLUMN co_class.invite_code; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_class.invite_code IS '邀请码（8位大写字母+数字）';


--
-- Name: COLUMN co_class.max_students; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_class.max_students IS '最大学生数（NULL=不限制）';


--
-- Name: COLUMN co_class.start_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_class.start_date IS '开课日期';


--
-- Name: COLUMN co_class.end_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_class.end_date IS '结课日期';


--
-- Name: COLUMN co_class.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_class.status IS '班级状态：0=招生中 1=进行中 2=已结束';


--
-- Name: co_class_member; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.co_class_member (
    id bigint NOT NULL,
    class_id bigint NOT NULL,
    student_id bigint NOT NULL,
    status smallint DEFAULT 0 NOT NULL,
    joined_at timestamp with time zone DEFAULT now() NOT NULL,
    is_deleted smallint DEFAULT 0 NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: TABLE co_class_member; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.co_class_member IS '班级成员表';


--
-- Name: COLUMN co_class_member.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_class_member.id IS '主键ID';


--
-- Name: COLUMN co_class_member.class_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_class_member.class_id IS '班级ID';


--
-- Name: COLUMN co_class_member.student_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_class_member.student_id IS '学生userId';


--
-- Name: COLUMN co_class_member.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_class_member.status IS '成员状态：0=正常 1=已退出';


--
-- Name: COLUMN co_class_member.joined_at; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_class_member.joined_at IS '加入时间';


--
-- Name: co_course; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.co_course (
    course_id bigint NOT NULL,
    course_name character varying(100) NOT NULL,
    description text,
    cover_url character varying(500),
    teacher_id bigint NOT NULL,
    category_id bigint,
    status smallint DEFAULT 0 NOT NULL,
    is_public smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0 NOT NULL,
    is_deleted smallint DEFAULT 0 NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: TABLE co_course; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.co_course IS '课程表';


--
-- Name: COLUMN co_course.course_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_course.course_id IS '课程ID';


--
-- Name: COLUMN co_course.course_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_course.course_name IS '课程名称';


--
-- Name: COLUMN co_course.description; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_course.description IS '课程简介';


--
-- Name: COLUMN co_course.cover_url; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_course.cover_url IS '课程封面图URL';


--
-- Name: COLUMN co_course.teacher_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_course.teacher_id IS '授课教师userId';


--
-- Name: COLUMN co_course.category_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_course.category_id IS '所属分类ID';


--
-- Name: COLUMN co_course.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_course.status IS '课程状态：0=草稿 1=已发布 2=已归档';


--
-- Name: COLUMN co_course.is_public; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_course.is_public IS '是否公开：0=私有 1=公开（未入班可浏览目录）';


--
-- Name: co_section; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.co_section (
    section_id bigint NOT NULL,
    chapter_id bigint NOT NULL,
    course_id bigint NOT NULL,
    title character varying(200) NOT NULL,
    order_index integer DEFAULT 0 NOT NULL,
    is_free smallint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0 NOT NULL,
    is_deleted smallint DEFAULT 0 NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    parent_section_id bigint,
    level integer DEFAULT 1 NOT NULL,
    CONSTRAINT ck_co_section_level_positive CHECK ((level >= 1))
);


--
-- Name: TABLE co_section; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.co_section IS '课程节表';


--
-- Name: COLUMN co_section.section_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_section.section_id IS '节ID';


--
-- Name: COLUMN co_section.chapter_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_section.chapter_id IS '所属章ID';


--
-- Name: COLUMN co_section.course_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_section.course_id IS '所属课程ID（冗余，便于按课程直接查所有节）';


--
-- Name: COLUMN co_section.title; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_section.title IS '节标题';


--
-- Name: COLUMN co_section.order_index; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_section.order_index IS '节排序序号';


--
-- Name: COLUMN co_section.is_free; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_section.is_free IS '是否免费预览：0=否 1=是（公开课程中未入班也可访问）';


--
-- Name: COLUMN co_section.parent_section_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_section.parent_section_id IS '父节ID；为空表示章下一级节';


--
-- Name: COLUMN co_section.level; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_section.level IS '节层级；章下一级节从1开始';


--
-- Name: co_section_resource; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.co_section_resource (
    id bigint NOT NULL,
    section_id bigint NOT NULL,
    resource_id bigint NOT NULL,
    resource_type character varying(20) NOT NULL,
    order_index integer DEFAULT 0 NOT NULL,
    is_deleted smallint DEFAULT 0 NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: TABLE co_section_resource; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.co_section_resource IS '节-资源关联表';


--
-- Name: COLUMN co_section_resource.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_section_resource.id IS '主键ID';


--
-- Name: COLUMN co_section_resource.section_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_section_resource.section_id IS '节ID';


--
-- Name: COLUMN co_section_resource.resource_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_section_resource.resource_id IS '资源ID（Resource服务）';


--
-- Name: COLUMN co_section_resource.resource_type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_section_resource.resource_type IS '资源大类（冗余）：VIDEO / DOCUMENT / IMAGE';


--
-- Name: COLUMN co_section_resource.order_index; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.co_section_resource.order_index IS '节内资源排序序号';


--
-- Name: co_category co_category_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.co_category
    ADD CONSTRAINT co_category_pkey PRIMARY KEY (category_id);


--
-- Name: co_chapter co_chapter_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.co_chapter
    ADD CONSTRAINT co_chapter_pkey PRIMARY KEY (chapter_id);


--
-- Name: co_class_member co_class_member_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.co_class_member
    ADD CONSTRAINT co_class_member_pkey PRIMARY KEY (id);


--
-- Name: co_class co_class_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.co_class
    ADD CONSTRAINT co_class_pkey PRIMARY KEY (class_id);


--
-- Name: co_course co_course_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.co_course
    ADD CONSTRAINT co_course_pkey PRIMARY KEY (course_id);


--
-- Name: co_section co_section_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.co_section
    ADD CONSTRAINT co_section_pkey PRIMARY KEY (section_id);


--
-- Name: co_section_resource co_section_resource_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.co_section_resource
    ADD CONSTRAINT co_section_resource_pkey PRIMARY KEY (id);


--
-- Name: idx_category_parent; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_category_parent ON public.co_category USING btree (parent_id) WHERE (is_deleted = 0);


--
-- Name: idx_chapter_course; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_chapter_course ON public.co_chapter USING btree (course_id) WHERE (is_deleted = 0);


--
-- Name: idx_class_course; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_class_course ON public.co_class USING btree (course_id) WHERE (is_deleted = 0);


--
-- Name: idx_class_teacher; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_class_teacher ON public.co_class USING btree (teacher_id) WHERE (is_deleted = 0);


--
-- Name: idx_course_category; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_course_category ON public.co_course USING btree (category_id) WHERE (is_deleted = 0);


--
-- Name: idx_course_status; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_course_status ON public.co_course USING btree (status) WHERE (is_deleted = 0);


--
-- Name: idx_course_teacher; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_course_teacher ON public.co_course USING btree (teacher_id) WHERE (is_deleted = 0);


--
-- Name: idx_member_class; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_member_class ON public.co_class_member USING btree (class_id) WHERE (is_deleted = 0);


--
-- Name: idx_member_student; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_member_student ON public.co_class_member USING btree (student_id) WHERE (is_deleted = 0);


--
-- Name: idx_section_chapter; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_section_chapter ON public.co_section USING btree (chapter_id) WHERE (is_deleted = 0);


--
-- Name: idx_section_course; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_section_course ON public.co_section USING btree (course_id) WHERE (is_deleted = 0);


--
-- Name: idx_section_parent_order; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_section_parent_order ON public.co_section USING btree (chapter_id, parent_section_id, order_index) WHERE (is_deleted = 0);


--
-- Name: idx_sr_section; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_sr_section ON public.co_section_resource USING btree (section_id) WHERE (is_deleted = 0);


--
-- Name: uq_class_invite_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX uq_class_invite_code ON public.co_class USING btree (invite_code) WHERE (is_deleted = 0);


--
-- Name: uq_member_class_student; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX uq_member_class_student ON public.co_class_member USING btree (class_id, student_id) WHERE (is_deleted = 0);


--
-- Name: uq_sr_section_resource; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX uq_sr_section_resource ON public.co_section_resource USING btree (section_id, resource_id) WHERE (is_deleted = 0);


--
-- Name: co_category co_category_parent_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.co_category
    ADD CONSTRAINT co_category_parent_id_fkey FOREIGN KEY (parent_id) REFERENCES public.co_category(category_id);


--
-- Name: co_course co_course_category_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.co_course
    ADD CONSTRAINT co_course_category_id_fkey FOREIGN KEY (category_id) REFERENCES public.co_category(category_id);


--
-- Name: co_section fk_co_section_parent_section; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.co_section
    ADD CONSTRAINT fk_co_section_parent_section FOREIGN KEY (parent_section_id) REFERENCES public.co_section(section_id);


--
-- PostgreSQL database dump complete
--
