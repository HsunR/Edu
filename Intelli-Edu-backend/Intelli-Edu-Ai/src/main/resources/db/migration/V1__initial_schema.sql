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
-- Name: ai_conversation; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ai_conversation (
    conversation_id bigint NOT NULL,
    user_id bigint NOT NULL,
    title character varying(100) DEFAULT '新对话'::character varying NOT NULL,
    agent_type character varying(30) DEFAULT 'chat'::character varying NOT NULL,
    last_message_id bigint,
    last_message_at timestamp with time zone,
    message_count integer DEFAULT 0 NOT NULL,
    is_pinned integer DEFAULT 0 NOT NULL,
    is_deleted integer DEFAULT 0 NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: TABLE ai_conversation; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.ai_conversation IS 'AI 对话会话列表';


--
-- Name: COLUMN ai_conversation.conversation_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ai_conversation.conversation_id IS '会话ID（雪花）';


--
-- Name: COLUMN ai_conversation.user_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ai_conversation.user_id IS '所属用户ID';


--
-- Name: COLUMN ai_conversation.title; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ai_conversation.title IS '会话标题：首轮对话后 LLM 异步生成';


--
-- Name: COLUMN ai_conversation.agent_type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ai_conversation.agent_type IS 'Agent 类型：chat/attribution/recommend/ppt_author';


--
-- Name: COLUMN ai_conversation.last_message_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ai_conversation.last_message_id IS '最后一条消息ID（指向 MongoDB messageId）';


--
-- Name: COLUMN ai_conversation.last_message_at; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ai_conversation.last_message_at IS '最后消息时间（会话列表排序用）';


--
-- Name: COLUMN ai_conversation.message_count; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ai_conversation.message_count IS '消息总数';


--
-- Name: COLUMN ai_conversation.is_pinned; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ai_conversation.is_pinned IS '是否置顶：0否 1是';


--
-- Name: ai_conversation_summary; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ai_conversation_summary (
    summary_id bigint NOT NULL,
    conversation_id bigint NOT NULL,
    summary text DEFAULT ''::text NOT NULL,
    covered_to_msg_id bigint,
    covered_msg_count integer DEFAULT 0 NOT NULL,
    est_tokens integer DEFAULT 0 NOT NULL,
    version integer DEFAULT 0 NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: TABLE ai_conversation_summary; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.ai_conversation_summary IS 'CHAT_TUTOR 会话历史摘要表（滚动压缩存储，一会话一行，用于维持长期记忆）';


--
-- Name: COLUMN ai_conversation_summary.summary_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ai_conversation_summary.summary_id IS '摘要记录唯一ID（雪花算法生成）';


--
-- Name: COLUMN ai_conversation_summary.conversation_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ai_conversation_summary.conversation_id IS '会话ID，关联会话主表，一会话对应一行摘要';


--
-- Name: COLUMN ai_conversation_summary.summary; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ai_conversation_summary.summary IS '压缩后的历史摘要文本（滚动更新：旧摘要+新消息→新摘要）';


--
-- Name: COLUMN ai_conversation_summary.covered_to_msg_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ai_conversation_summary.covered_to_msg_id IS '摘要已覆盖的最后一条消息ID（增量判断依据，NULL表示尚无消息被覆盖）';


--
-- Name: COLUMN ai_conversation_summary.covered_msg_count; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ai_conversation_summary.covered_msg_count IS '已被压缩进摘要的消息总条数（用于触发压缩阈值判断）';


--
-- Name: COLUMN ai_conversation_summary.est_tokens; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ai_conversation_summary.est_tokens IS '摘要文本的估算token数（监控体积，触发裁剪）';


--
-- Name: COLUMN ai_conversation_summary.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ai_conversation_summary.version IS '乐观锁版本号（并发更新时校验，防止覆盖）';


--
-- Name: COLUMN ai_conversation_summary.created_at; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ai_conversation_summary.created_at IS '记录创建时间（带时区的时间戳）';


--
-- Name: COLUMN ai_conversation_summary.updated_at; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ai_conversation_summary.updated_at IS '记录最后更新时间（每次压缩刷新）';


--
-- Name: ai_task; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ai_task (
    task_id bigint NOT NULL,
    task_type character varying(30) NOT NULL,
    biz_id bigint NOT NULL,
    biz_type character varying(30) NOT NULL,
    user_id bigint,
    requester_id bigint,
    status character varying(20) DEFAULT 'PENDING'::character varying NOT NULL,
    retry_count integer DEFAULT 0 NOT NULL,
    max_retry integer DEFAULT 3 NOT NULL,
    started_at timestamp with time zone,
    finished_at timestamp with time zone,
    duration_ms integer,
    model_name character varying(50),
    prompt_tokens integer,
    completion_tokens integer,
    total_tokens integer,
    error_code character varying(50),
    error_msg character varying(1000),
    request_id character varying(64),
    message_id character varying(64),
    is_deleted integer DEFAULT 0 NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    sandbox_task_id bigint,
    result_bucket character varying(128),
    result_object_key character varying(500),
    result_content_type character varying(128),
    result_size_bytes bigint,
    result_sha256 character varying(64)
);


--
-- Name: TABLE ai_task; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.ai_task IS 'AI 异步任务执行记录';


--
-- Name: COLUMN ai_task.task_type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ai_task.task_type IS 'grading=批改 ocr=识别 ppt=课件生成';


--
-- Name: COLUMN ai_task.biz_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ai_task.biz_id IS '业务对象ID，grading 时是 answer_sheet.sheet_id';


--
-- Name: COLUMN ai_task.biz_type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ai_task.biz_type IS 'answer_sheet/resource 等';


--
-- Name: COLUMN ai_task.user_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ai_task.user_id IS '业务关联用户（grading 任务是答卷的学生）';


--
-- Name: COLUMN ai_task.requester_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ai_task.requester_id IS '任务发起者（grading 任务是教师）';


--
-- Name: COLUMN ai_task.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ai_task.status IS 'PENDING/PROCESSING/SUCCEEDED/FAILED/DEAD';


--
-- Name: COLUMN ai_task.request_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ai_task.request_id IS '关联 MQ 消息的 requestId，便于日志链路追踪';


--
-- Name: COLUMN ai_task.message_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ai_task.message_id IS 'MQ messageId，幂等用';


--
-- Name: COLUMN ai_task.sandbox_task_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ai_task.sandbox_task_id IS 'Sandbox模块任务ID';


--
-- Name: COLUMN ai_task.result_bucket; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ai_task.result_bucket IS '课件产物所在MinIO Bucket';


--
-- Name: COLUMN ai_task.result_object_key; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ai_task.result_object_key IS '课件产物MinIO对象Key';


--
-- Name: COLUMN ai_task.result_content_type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ai_task.result_content_type IS '课件产物MIME类型';


--
-- Name: COLUMN ai_task.result_size_bytes; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ai_task.result_size_bytes IS '课件产物大小，单位字节';


--
-- Name: COLUMN ai_task.result_sha256; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.ai_task.result_sha256 IS '课件产物SHA-256摘要';


--
-- Name: chat_list; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.chat_list (
    conversation_id character varying(64) NOT NULL,
    user_id bigint,
    conversation_title character varying(64),
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    version integer DEFAULT 0,
    is_delete smallint DEFAULT 0
);


--
-- Name: TABLE chat_list; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.chat_list IS '聊天会话表';


--
-- Name: COLUMN chat_list.conversation_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.chat_list.conversation_id IS '会话id';


--
-- Name: COLUMN chat_list.user_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.chat_list.user_id IS '用户id';


--
-- Name: COLUMN chat_list.conversation_title; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.chat_list.conversation_title IS '会话标题';


--
-- Name: COLUMN chat_list.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.chat_list.create_time IS '创建时间';


--
-- Name: COLUMN chat_list.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.chat_list.update_time IS '更新时间';


--
-- Name: COLUMN chat_list.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.chat_list.version IS '乐观锁';


--
-- Name: COLUMN chat_list.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.chat_list.is_delete IS '逻辑删除标志';


--
-- Name: chat_message; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.chat_message (
    id bigint NOT NULL,
    conversation_id character varying(64) NOT NULL,
    content text NOT NULL,
    role character varying(32) NOT NULL,
    tokens integer DEFAULT 0,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    version integer DEFAULT 0,
    is_delete smallint DEFAULT 0 NOT NULL
);


--
-- Name: TABLE chat_message; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.chat_message IS '聊天消息表';


--
-- Name: COLUMN chat_message.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.chat_message.id IS '主键ID';


--
-- Name: COLUMN chat_message.conversation_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.chat_message.conversation_id IS '会话ID';


--
-- Name: COLUMN chat_message.content; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.chat_message.content IS '消息内容';


--
-- Name: COLUMN chat_message.role; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.chat_message.role IS '角色：user/assistant/system';


--
-- Name: COLUMN chat_message.tokens; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.chat_message.tokens IS '消息token数';


--
-- Name: COLUMN chat_message.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.chat_message.create_time IS '创建时间';


--
-- Name: COLUMN chat_message.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.chat_message.update_time IS '更新时间';


--
-- Name: COLUMN chat_message.version; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.chat_message.version IS '乐观锁';


--
-- Name: COLUMN chat_message.is_delete; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.chat_message.is_delete IS '是否删除';


--
-- Name: chat_message_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.chat_message_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: chat_message_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.chat_message_id_seq OWNED BY public.chat_message.id;


--
-- Name: chat_message id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.chat_message ALTER COLUMN id SET DEFAULT nextval('public.chat_message_id_seq'::regclass);


--
-- Name: ai_conversation ai_conversation_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ai_conversation
    ADD CONSTRAINT ai_conversation_pkey PRIMARY KEY (conversation_id);


--
-- Name: ai_conversation_summary ai_conversation_summary_conversation_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ai_conversation_summary
    ADD CONSTRAINT ai_conversation_summary_conversation_id_key UNIQUE (conversation_id);


--
-- Name: ai_conversation_summary ai_conversation_summary_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ai_conversation_summary
    ADD CONSTRAINT ai_conversation_summary_pkey PRIMARY KEY (summary_id);


--
-- Name: ai_task ai_task_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ai_task
    ADD CONSTRAINT ai_task_pkey PRIMARY KEY (task_id);


--
-- Name: chat_list chat_list_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.chat_list
    ADD CONSTRAINT chat_list_pkey PRIMARY KEY (conversation_id);


--
-- Name: chat_message chat_message_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.chat_message
    ADD CONSTRAINT chat_message_pkey PRIMARY KEY (id);


--
-- Name: idx_conv_user; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_conv_user ON public.ai_conversation USING btree (user_id, is_pinned DESC, last_message_at DESC) WHERE (is_deleted = 0);


--
-- Name: idx_task_biz; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_task_biz ON public.ai_task USING btree (biz_type, biz_id, task_type, created_at DESC) WHERE (is_deleted = 0);


--
-- Name: idx_task_dead; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_task_dead ON public.ai_task USING btree (status, created_at DESC) WHERE ((is_deleted = 0) AND ((status)::text = ANY ((ARRAY['FAILED'::character varying, 'DEAD'::character varying])::text[])));


--
-- Name: idx_user_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_user_id ON public.chat_list USING btree (user_id);


--
-- Name: uk_ai_task_sandbox_task_id; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX uk_ai_task_sandbox_task_id ON public.ai_task USING btree (sandbox_task_id) WHERE ((sandbox_task_id IS NOT NULL) AND (is_deleted = 0));


--
-- Name: uq_task_message; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX uq_task_message ON public.ai_task USING btree (message_id) WHERE ((is_deleted = 0) AND (message_id IS NOT NULL));


--
-- PostgreSQL database dump complete
--
