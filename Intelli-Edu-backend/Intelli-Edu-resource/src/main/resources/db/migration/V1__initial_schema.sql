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
-- Name: rs_resource; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.rs_resource (
    resource_id bigint NOT NULL,
    resource_name character varying(255) NOT NULL,
    resource_type smallint NOT NULL,
    file_format character varying(10),
    file_size bigint,
    storage_key character varying(512),
    access_url character varying(512),
    uploader_id bigint NOT NULL,
    upload_status smallint DEFAULT 0,
    version integer DEFAULT 0,
    is_deleted smallint DEFAULT 0,
    created_at timestamp with time zone DEFAULT now(),
    updated_at timestamp with time zone DEFAULT now()
);


--
-- Name: TABLE rs_resource; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.rs_resource IS '资源表';


--
-- Name: COLUMN rs_resource.resource_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.rs_resource.resource_id IS '主键ID';


--
-- Name: COLUMN rs_resource.resource_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.rs_resource.resource_name IS '原始文件名';


--
-- Name: COLUMN rs_resource.resource_type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.rs_resource.resource_type IS '大类：1=视频 2=文档 3=图片';


--
-- Name: COLUMN rs_resource.file_format; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.rs_resource.file_format IS '具体格式：pdf/docx/mp4';


--
-- Name: COLUMN rs_resource.file_size; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.rs_resource.file_size IS '文件大小（字节）';


--
-- Name: COLUMN rs_resource.storage_key; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.rs_resource.storage_key IS 'COS key 或 VOD FileId';


--
-- Name: COLUMN rs_resource.access_url; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.rs_resource.access_url IS '访问地址';


--
-- Name: COLUMN rs_resource.uploader_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.rs_resource.uploader_id IS '上传者 userId';


--
-- Name: COLUMN rs_resource.upload_status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.rs_resource.upload_status IS '0=待确认 1=成功 2=失败';


--
-- Name: COLUMN rs_resource.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.rs_resource.version IS '版本号';


--
-- Name: COLUMN rs_resource.is_deleted; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.rs_resource.is_deleted IS '逻辑删除标识';


--
-- Name: COLUMN rs_resource.created_at; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.rs_resource.created_at IS '创建时间';


--
-- Name: COLUMN rs_resource.updated_at; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.rs_resource.updated_at IS '更新时间';


--
-- Name: rs_video_meta; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.rs_video_meta (
    resource_id bigint NOT NULL,
    duration integer,
    cover_url character varying(512),
    definition character varying(20),
    transcode_status smallint DEFAULT 0,
    vod_file_id character varying(128),
    created_at timestamp with time zone DEFAULT now(),
    updated_at timestamp with time zone DEFAULT now()
);


--
-- Name: TABLE rs_video_meta; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.rs_video_meta IS '视频扩展信息表';


--
-- Name: COLUMN rs_video_meta.resource_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.rs_video_meta.resource_id IS '关联资源ID (外键)';


--
-- Name: COLUMN rs_video_meta.duration; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.rs_video_meta.duration IS '时长（秒）';


--
-- Name: COLUMN rs_video_meta.cover_url; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.rs_video_meta.cover_url IS '视频封面';


--
-- Name: COLUMN rs_video_meta.definition; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.rs_video_meta.definition IS '清晰度';


--
-- Name: COLUMN rs_video_meta.transcode_status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.rs_video_meta.transcode_status IS '0=待处理 1=完成 2=失败';


--
-- Name: COLUMN rs_video_meta.vod_file_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.rs_video_meta.vod_file_id IS '腾讯云 VOD FileId';


--
-- Name: COLUMN rs_video_meta.created_at; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.rs_video_meta.created_at IS '创建时间';


--
-- Name: COLUMN rs_video_meta.updated_at; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.rs_video_meta.updated_at IS '更新时间';


--
-- Name: rs_resource rs_resource_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.rs_resource
    ADD CONSTRAINT rs_resource_pkey PRIMARY KEY (resource_id);


--
-- Name: rs_video_meta rs_video_meta_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.rs_video_meta
    ADD CONSTRAINT rs_video_meta_pkey PRIMARY KEY (resource_id);


--
-- Name: idx_resource_uploader; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_resource_uploader ON public.rs_resource USING btree (uploader_id) WHERE (is_deleted = 0);


--
-- PostgreSQL database dump complete
--
