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

 Date: 14/08/2018 02:32:36
*/


-- ----------------------------
-- Table structure for userinfo
-- ----------------------------
DROP TABLE IF EXISTS "public"."userinfo";
CREATE TABLE "public"."userinfo" (
  "userid" int4 NOT NULL DEFAULT NULL,
  "username" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL
)
;
