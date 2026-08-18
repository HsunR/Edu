-- ============================================================
-- Intelli-Edu Knowledge Module DDL
-- Database: intelli_edu_knowledge
-- PostgreSQL 16+
-- ============================================================

-- 1. kn_knowledge_point
CREATE TABLE IF NOT EXISTS kn_knowledge_point (
    point_id        BIGINT          PRIMARY KEY,
    point_name      VARCHAR(100)    NOT NULL,
    course_id       BIGINT          NOT NULL,
    parent_id       BIGINT,
    description     VARCHAR(500),
    order_index     INT             NOT NULL DEFAULT 0,
    is_deleted      INT             NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT now()
);

COMMENT ON TABLE  kn_knowledge_point IS '知识点';
COMMENT ON COLUMN kn_knowledge_point.point_id    IS '知识点ID';
COMMENT ON COLUMN kn_knowledge_point.point_name  IS '知识点名称';
COMMENT ON COLUMN kn_knowledge_point.course_id   IS '所属课程ID';
COMMENT ON COLUMN kn_knowledge_point.parent_id   IS '父知识点ID，null=一级知识点';
COMMENT ON COLUMN kn_knowledge_point.description IS '知识点描述';
COMMENT ON COLUMN kn_knowledge_point.order_index IS '排序';

CREATE INDEX IF NOT EXISTS idx_kp_course ON kn_knowledge_point (course_id) WHERE is_deleted = 0;
CREATE INDEX IF NOT EXISTS idx_kp_parent ON kn_knowledge_point (parent_id) WHERE is_deleted = 0 AND parent_id IS NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS uq_kp_name ON kn_knowledge_point (course_id, parent_id, point_name) WHERE is_deleted = 0;
CREATE UNIQUE INDEX IF NOT EXISTS uq_kp_name_root ON kn_knowledge_point (course_id, point_name) WHERE is_deleted = 0 AND parent_id IS NULL;

-- 2. kn_knowledge_section
CREATE TABLE IF NOT EXISTS kn_knowledge_section (
    id              BIGINT          PRIMARY KEY,
    point_id        BIGINT          NOT NULL,
    section_id      BIGINT          NOT NULL,
    course_id       BIGINT          NOT NULL,
    is_deleted      INT             NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT now()
);

COMMENT ON TABLE  kn_knowledge_section IS '知识点-章节关联';
COMMENT ON COLUMN kn_knowledge_section.id         IS '关联ID';
COMMENT ON COLUMN kn_knowledge_section.point_id   IS '知识点ID（二级）';
COMMENT ON COLUMN kn_knowledge_section.section_id IS '章节ID';
COMMENT ON COLUMN kn_knowledge_section.course_id  IS '冗余课程ID';

CREATE UNIQUE INDEX IF NOT EXISTS uq_ks_point_section ON kn_knowledge_section (point_id, section_id) WHERE is_deleted = 0;
CREATE INDEX IF NOT EXISTS idx_ks_section ON kn_knowledge_section (section_id) WHERE is_deleted = 0;
CREATE INDEX IF NOT EXISTS idx_ks_course  ON kn_knowledge_section (course_id)  WHERE is_deleted = 0;

-- 3. kn_question_knowledge
CREATE TABLE IF NOT EXISTS kn_question_knowledge (
    id              BIGINT          PRIMARY KEY,
    point_id        BIGINT          NOT NULL,
    question_id     BIGINT          NOT NULL,
    course_id       BIGINT          NOT NULL,
    is_deleted      INT             NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT now()
);

COMMENT ON TABLE  kn_question_knowledge IS '知识点-题目关联';
COMMENT ON COLUMN kn_question_knowledge.id          IS '关联ID';
COMMENT ON COLUMN kn_question_knowledge.point_id    IS '知识点ID（二级）';
COMMENT ON COLUMN kn_question_knowledge.question_id IS '题目ID';
COMMENT ON COLUMN kn_question_knowledge.course_id   IS '冗余课程ID';

CREATE UNIQUE INDEX IF NOT EXISTS uq_qk_point_question ON kn_question_knowledge (point_id, question_id) WHERE is_deleted = 0;
CREATE INDEX IF NOT EXISTS idx_qk_question ON kn_question_knowledge (question_id) WHERE is_deleted = 0;
CREATE INDEX IF NOT EXISTS idx_qk_course   ON kn_question_knowledge (course_id)   WHERE is_deleted = 0;

-- 4. kn_knowledge_relation (预留)
CREATE TABLE IF NOT EXISTS kn_knowledge_relation (
    id                  BIGINT          PRIMARY KEY,
    source_point_id     BIGINT          NOT NULL,
    target_point_id     BIGINT          NOT NULL,
    relation_type       VARCHAR(30)     NOT NULL,
    course_id           BIGINT          NOT NULL,
    weight              INT             NOT NULL DEFAULT 1,
    is_deleted          INT             NOT NULL DEFAULT 0,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT now()
);

COMMENT ON TABLE  kn_knowledge_relation IS '知识点间关系（预留）';
COMMENT ON COLUMN kn_knowledge_relation.source_point_id IS '源知识点ID';
COMMENT ON COLUMN kn_knowledge_relation.target_point_id IS '目标知识点ID';
COMMENT ON COLUMN kn_knowledge_relation.relation_type   IS '关系类型：prerequisite/related/contains';
COMMENT ON COLUMN kn_knowledge_relation.weight          IS '关系权重';

CREATE UNIQUE INDEX IF NOT EXISTS uq_kr_source_target ON kn_knowledge_relation (source_point_id, target_point_id, relation_type) WHERE is_deleted = 0;
CREATE INDEX IF NOT EXISTS idx_kr_course ON kn_knowledge_relation (course_id) WHERE is_deleted = 0;
