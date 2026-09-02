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
-- Name: us_student_profile; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.us_student_profile (
    user_id bigint NOT NULL,
    student_no character varying(30),
    grade character varying(20),
    major character varying(50),
    enrollment_year smallint,
    created_at timestamp with time zone DEFAULT now(),
    updated_at timestamp with time zone DEFAULT now()
);


--
-- Name: COLUMN us_student_profile.student_no; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.us_student_profile.student_no IS '学号';


--
-- Name: COLUMN us_student_profile.grade; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.us_student_profile.grade IS '年级';


--
-- Name: COLUMN us_student_profile.major; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.us_student_profile.major IS '专业';


--
-- Name: COLUMN us_student_profile.enrollment_year; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.us_student_profile.enrollment_year IS '入学年份';


--
-- Name: us_teacher_profile; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.us_teacher_profile (
    user_id bigint NOT NULL,
    teacher_no character varying(30),
    title character varying(30),
    department character varying(50),
    bio text,
    created_at timestamp with time zone DEFAULT now(),
    updated_at timestamp with time zone DEFAULT now()
);


--
-- Name: COLUMN us_teacher_profile.teacher_no; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.us_teacher_profile.teacher_no IS '工号';


--
-- Name: COLUMN us_teacher_profile.title; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.us_teacher_profile.title IS '职称';


--
-- Name: COLUMN us_teacher_profile.department; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.us_teacher_profile.department IS '院系';


--
-- Name: COLUMN us_teacher_profile.bio; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.us_teacher_profile.bio IS '教师简介';


--
-- Name: us_user; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.us_user (
    user_id bigint NOT NULL,
    name character varying(50) NOT NULL,
    password character varying(255) NOT NULL,
    user_type smallint DEFAULT 1 NOT NULL,
    avatar_url character varying(512),
    email character varying(100),
    mobile character(11),
    open_id character varying(128),
    sex smallint DEFAULT 0,
    school character varying(100),
    personal_signature character varying(255),
    status smallint DEFAULT 1,
    version integer DEFAULT 0,
    is_deleted smallint DEFAULT 0,
    created_at timestamp with time zone DEFAULT now(),
    updated_at timestamp with time zone DEFAULT now()
);


--
-- Name: TABLE us_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.us_user IS '用户基础表';


--
-- Name: COLUMN us_user.name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.us_user.name IS '姓名';


--
-- Name: COLUMN us_user.password; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.us_user.password IS '密码（加密后）';


--
-- Name: COLUMN us_user.user_type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.us_user.user_type IS '1=学生 2=教师 3=管理员';


--
-- Name: COLUMN us_user.avatar_url; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.us_user.avatar_url IS '头像URL';


--
-- Name: COLUMN us_user.email; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.us_user.email IS '邮箱';


--
-- Name: COLUMN us_user.mobile; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.us_user.mobile IS '手机号';


--
-- Name: COLUMN us_user.open_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.us_user.open_id IS '第三方登录唯一标识（如微信OpenID）';


--
-- Name: COLUMN us_user.sex; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.us_user.sex IS '0=未知 1=男 2=女';


--
-- Name: COLUMN us_user.school; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.us_user.school IS '学校';


--
-- Name: COLUMN us_user.personal_signature; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.us_user.personal_signature IS '个人签名';


--
-- Name: COLUMN us_user.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.us_user.status IS '1=正常 0=禁用';


--
-- Name: us_student_profile us_student_profile_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.us_student_profile
    ADD CONSTRAINT us_student_profile_pkey PRIMARY KEY (user_id);


--
-- Name: us_teacher_profile us_teacher_profile_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.us_teacher_profile
    ADD CONSTRAINT us_teacher_profile_pkey PRIMARY KEY (user_id);


--
-- Name: us_user us_user_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.us_user
    ADD CONSTRAINT us_user_pkey PRIMARY KEY (user_id);


--
-- Name: uk_user_email; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX uk_user_email ON public.us_user USING btree (email) WHERE ((is_deleted = 0) AND (email IS NOT NULL));


--
-- Name: uk_user_mobile; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX uk_user_mobile ON public.us_user USING btree (mobile) WHERE ((is_deleted = 0) AND (mobile IS NOT NULL));


--
-- PostgreSQL database dump complete
--
