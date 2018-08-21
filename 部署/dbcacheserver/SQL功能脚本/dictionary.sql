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

 Date: 14/08/2018 02:32:24
*/


-- ----------------------------
-- Table structure for dictionary
-- ----------------------------
DROP TABLE IF EXISTS "public"."dictionary";
CREATE TABLE "public"."dictionary" (
  "enname" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL,
  "zid" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL,
  "zname" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL
)
;
