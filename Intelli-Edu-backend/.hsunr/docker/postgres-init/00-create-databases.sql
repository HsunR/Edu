-- Docker bootstrap only: create empty databases. Service-owned schemas are
-- created and evolved by Flyway migrations packaged with each service.
CREATE DATABASE intelli_edu;
CREATE DATABASE intelli_edu_ai;
CREATE DATABASE intelli_edu_course;
CREATE DATABASE intelli_edu_exam;
CREATE DATABASE intelli_edu_knowledge;
CREATE DATABASE intelli_edu_learning;
CREATE DATABASE intelli_edu_rag;
CREATE DATABASE intelli_edu_resource;
CREATE DATABASE intelli_edu_sandbox;
CREATE DATABASE intelli_edu_user;

\connect intelli_edu_rag
CREATE EXTENSION IF NOT EXISTS vector WITH SCHEMA public;
