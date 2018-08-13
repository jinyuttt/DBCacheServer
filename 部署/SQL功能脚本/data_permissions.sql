/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : PostgreSQL
 Source Server Version : 90514
 Source Host           : localhost:5432
 Source Catalog        : postgres
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 90514
 File Encoding         : 65001

 Date: 14/08/2018 02:32:12
*/


-- ----------------------------
-- Table structure for data_permissions
-- ----------------------------
DROP TABLE IF EXISTS "public"."data_permissions";
CREATE TABLE "public"."data_permissions" (
  "userid" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL,
  "tablename" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL,
  "insert" bool DEFAULT NULL,
  "delete" bool DEFAULT NULL,
  "update" bool DEFAULT NULL,
  "select" bool DEFAULT NULL,
  "create" bool DEFAULT NULL,
  "drop" bool DEFAULT NULL,
  "truncate" bool DEFAULT NULL,
  "id" int8 NOT NULL DEFAULT nextval('data_permissions_id_seq'::regclass)
)
;

-- ----------------------------
-- Primary Key structure for table data_permissions
-- ----------------------------
ALTER TABLE "public"."data_permissions" ADD CONSTRAINT "data_permissions_pkey" PRIMARY KEY ("id");
