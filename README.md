# 校园智能打卡与活动积分平台
## 📖 项目简介

校园智能打卡与活动积分平台是一个专为学校、学院或班级设计的智能化活动打卡与积分管理系统。通过智能打卡、自动积分、数据统计、排行榜等功能，提升学生参与活动的积极性，实现活动数字化、透明化管理。
<details>
  <summary>点击查看项目截图</summary>
  <img width="290" height="202" alt="image" src="https://github.com/user-attachments/assets/6795ca62-42b4-4d2b-8258-ad77bac44739" />
  <img width="135" height="293" alt="image" src="https://github.com/user-attachments/assets/01613a78-dede-4a5b-b2e7-f6a7b4a5d961" />
  <img width="262" height="568" alt="image" src="https://github.com/user-attachments/assets/3f47ed7f-cf16-486f-bffb-288a27ea8422" />
  <img width="323" height="700" alt="image" src="https://github.com/user-attachments/assets/17ec6fb3-ab23-48da-a922-7650ba059845" />
  <img width="323" height="700" alt="image" src="https://github.com/user-attachments/assets/4efdda58-c625-490e-8ec6-11760e4ee41a" />
  <img width="197" height="428" alt="image" src="https://github.com/user-attachments/assets/e07e1926-9132-49a3-a44c-5b59c7712146" />
  <img width="198" height="428" alt="image" src="https://github.com/user-attachments/assets/b5340fd3-6552-4d2a-9b94-184a136d1e5c" />
  <img width="197" height="428" alt="image" src="https://github.com/user-attachments/assets/608f6085-68ec-495a-8783-317a9ac19985" />
  <img width="198" height="428" alt="image" src="https://github.com/user-attachments/assets/6824a5c6-ffab-4f64-b492-d01e2005151b" />
  <img width="197" height="428" alt="image" src="https://github.com/user-attachments/assets/1c779f15-5e02-4280-8813-ce84636b841d" />
  <img width="370" height="175" alt="image" src="https://github.com/user-attachments/assets/497c936e-898d-4266-9f40-14a6348ec356" />
  <img width="364" height="175" alt="image" src="https://github.com/user-attachments/assets/e12b56b3-96d1-4ade-a1e7-fed570121218" />
  <img width="354" height="201" alt="image" src="https://github.com/user-attachments/assets/3a9fed7a-db87-438f-a903-402ee206b897" />
  <img width="366" height="208" alt="image" src="https://github.com/user-attachments/assets/3c9ca381-082c-4bb7-9e34-3f15cc95246c" />
  <img width="366" height="208" alt="image" src="https://github.com/user-attachments/assets/8cc966fa-f06c-4836-96c1-550790d0ce84" />
  <img width="394" height="224" alt="image" src="https://github.com/user-attachments/assets/9d9e91bc-6b10-45c5-91b4-a99843c23ada" />
  <img width="365" height="207" alt="image" src="https://github.com/user-attachments/assets/e225110e-2972-42c8-8155-46d017baba2c" />
  <img width="388" height="220" alt="image" src="https://github.com/user-attachments/assets/a1901d63-f59a-4e08-a09c-bb43b474ee87" />
  <img width="380" height="216" alt="image" src="https://github.com/user-attachments/assets/91af78e9-9843-43c7-a425-7724b834402f" />
  <img width="390" height="221" alt="image" src="https://github.com/user-attachments/assets/c5be0846-4095-4b3f-99b0-e4eab6829d2c" />
  <img width="413" height="234" alt="image" src="https://github.com/user-attachments/assets/06d60321-2de2-46a3-81f4-6741bd5ff4a6" />

</details>

## ✨ 主要功能

### 🎯 活动管理
- **活动创建**：管理员可发布各类校园活动（讲座、志愿服务、体育锻炼,演唱会活动等）
- **活动报名**：学生可在线报名参加感兴趣的活动
- **智能打卡**：支持参加活动与打卡活动
- **活动记录**：自动记录每位学生的参与情况、签到时间

### 🎯 积分系统
- **积分规则配置**：管理员可自定义不同活动的积分获取规则
- **积分获取**：学生参与活动、按时打卡后自动获得相应积分
- **积分兑换**：支持积分兑换奖品

### 🎯 数据统计
- **活动统计**：统计活动参与人数、积分总量等数据
- **个人记录**：学生可查看历史打卡记录、积分明细、兑换历史
- **排行榜**：按积分、参与度等生成排行榜，激励参与

### 🔐 权限管理
- **多角色支持**：管理员、学生不同权限分级管理

## 🎯 技术架构

### 后端服务 (EduPoint)
- **框架**：Spring Boot 3.5.0
- **数据库**：MySQL 8.0+
- **ORM**：MyBatis Plus 3.5.5
- **缓存**：Redis
- **WebSocket**：支持实时通信
- **Java版本**：JDK 21+

### 管理端 (admin-ui)
- **框架**：Vue 3.4.38 + TypeScript
- **UI组件**：Element Plus 2.8.0
- **状态管理**：Pinia 2.2.2
- **路由**：Vue Router 4.4.3
- **构建工具**：Vite 5.4.1
- **包管理**：pnpm

### 移动端 (H5)
- **框架**：UniApp
- **UI组件**：uview-plus 3.4.43
- **动画**：animate.css 4.1.1
- **时间处理**：dayjs 1.11.13

## 🚀 快速开始

### 环境要求
- Node.js >= 18.0.0
- Java JDK 21+
- MySQL 8.0+
- Redis


### 1.数据库配置
```sql
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
create database edu_point;
use edu_point;
CREATE TABLE `t_activity` (
                              `id` int NOT NULL AUTO_INCREMENT COMMENT '活动ID',
                              `title` varchar(100) NOT NULL COMMENT '活动标题',
                              `description` text COMMENT '活动描述',
                              `start_time` varchar(100) NOT NULL COMMENT '开始时间',
                              `end_time` varchar(100) NOT NULL COMMENT '结束时间',
                              `point` int DEFAULT NULL COMMENT '积分数值',
                              `poster` varchar(100) DEFAULT NULL COMMENT '海报',
                              `rule_description` varchar(255) DEFAULT NULL COMMENT '积分规则描述',
                              `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              `create_by` varchar(64) DEFAULT NULL,
                              `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              `update_by` varchar(64) DEFAULT NULL,
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='活动及积分规则表';
INSERT INTO `t_activity` VALUES
                             (1,'五月天「好好好想见到你」演唱会-上海站','五月天乐队时隔3年重返上海体育场，带来3小时音乐盛宴','2023-10-21 18:00','2024-10-21 21:30',45,'/upload/e2b1426b-5cf4-49f0-bf99-0218a9fd4477.jpg','参与互动环节额外+5积分','2025-06-20 14:05:16','1','2025-06-21 18:23:50','1'),
                             (4,'Taylor Swift「The Eras Tour」亚洲巡演-东京站','国际巨星Taylor Swift首次在日本举办大型体育场巡演','2024-02-10 18:30','2025-09-10 23:00',80,'/upload/e4a31451-ee3a-4c73-b6bf-1c29742eea95.png','VIP座位可获得双倍积分','2025-06-20 14:05:16','1','2025-06-21 18:12:28','1'),
                             (5,'周杰伦「嘉年华」世界巡回演唱会-北京站','华语乐坛天王周杰伦2023全新巡回演唱会，包含经典歌曲和新专辑曲目','2023-09-15 19:30','2025-09-15 22:00',50,'/upload/f0397dba-fd1b-49b7-a6ca-f6555dab018e.jpg','观看完整场演唱会可获得50积分','2025-06-20 14:05:16','1','2025-06-21 18:23:40','1'),
                             (6,'张惠妹「ASMR」世界巡回演唱会-广州站','aMEI张惠妹全新主题演唱会，沉浸式音乐体验','2024-01-12 20:00','2025-07-12 23:00',55,'/upload/21932d55-2e22-4e0e-847a-5e489a4da6ee.jpg','参与合唱环节可获得额外积分','2025-06-20 14:05:16','1','2025-06-20 14:12:50','1');
CREATE TABLE `t_gift` (
                          `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                          `name` varchar(100) NOT NULL COMMENT '礼物名称',
                          `point` int NOT NULL COMMENT '花费积分',
                          `description` varchar(255) DEFAULT NULL COMMENT '礼物描述',
                          `image_url` varchar(255) DEFAULT NULL COMMENT '礼物图片',
                          `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          `create_by` varchar(64) DEFAULT NULL,
                          `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          `update_by` varchar(64) DEFAULT NULL,
                          `stock` int DEFAULT NULL COMMENT '库存数量',
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='礼物表';
INSERT INTO `t_gift` VALUES
                         (1,'周杰伦签名CD',500,'周杰伦「嘉年华」世界巡回演唱会限量签名版CD','/upload/4940cdb3-7f13-4a43-a5d5-76e4d19d7cee.webp','2025-06-18 13:08:57','admin','2025-06-21 15:56:52','admin',0),
                         (2,'演唱会荧光棒套装',300,'五月天官方应援荧光棒套装（含电池）','/upload/f794609c-72ac-4f1c-80ec-5c3a49de2cf4.jpg','2025-06-18 13:08:57','admin','2025-06-21 15:58:48',NULL,80),
                         (3,'Taylor Swift纪念T恤',450,'「The Eras Tour」亚洲巡演限量纪念T恤','/upload/2f20a713-f58d-4475-80a6-048a7d21a0ae.jpg','2025-06-18 13:08:57','user1','2025-06-21 15:59:28','user1',96),
                         (4,'演唱会主题保温杯',250,'张惠妹「ASMR」演唱会主题保温杯','/upload/7d71ae95-8555-4d1f-ae77-763ffe68f27f.avif','2025-06-18 13:08:57','system','2025-06-21 16:00:46',NULL,99),
                         (5,'演唱会门票收藏册',180,'专业设计的演唱会门票收藏册（可存放20张）','/upload/9121156a-d5fa-4cb0-bd9a-43356ffe9df4.avif','2025-06-18 13:08:57','admin','2025-06-21 16:01:57','admin',99),
                         (7,'演唱会主题夜灯',350,'「嘉年华」演唱会主题3D造型夜灯','/upload/d1b2c85e-1e11-4805-8a28-e4636954c886.avif','2025-06-18 13:08:57','admin','2025-06-21 16:02:14','user3',99),
                         (8,'签名海报',120,'艺人亲笔签名演唱会宣传海报','/upload/d304821e-c6d6-4899-bef8-b59f5671db90.jpg','2025-06-18 13:08:57','user4','2025-06-20 18:14:46',NULL,99),
                         (9,'应援手环',280,'官方应援LED发光手环（可编程）','/upload/dfcc98d1-f20b-464b-93f4-5e265db8ac00.avif','2025-06-18 13:08:57','system','2025-06-21 16:03:01','admin',99);
CREATE TABLE `t_student` (
                             `id` int NOT NULL AUTO_INCREMENT,
                             `username` varchar(255) NOT NULL,
                             `nickname` varchar(255) DEFAULT '',
                             `password` varchar(255) NOT NULL,
                             `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             `create_by` varchar(64) DEFAULT NULL,
                             `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             `update_by` varchar(64) DEFAULT NULL,
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1166090242 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
INSERT INTO `t_student` VALUES (2,'1','学生','1','2025-06-10 11:26:04',NULL,'2025-06-20 09:02:45','1');
CREATE TABLE `t_student_activity_history` (
                                              `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                              `student_id` bigint NOT NULL COMMENT '学生ID',
                                              `student_name` varchar(128) NOT NULL COMMENT '学生名称',
                                              `activity_id` bigint DEFAULT NULL COMMENT '活动ID，可选',
                                              `status` int DEFAULT NULL COMMENT '活动状态 1 申请中，2 已完成，3 未完成',
                                              `activity_name` varchar(128) NOT NULL COMMENT '活动名称',
                                              `poster` varchar(100) DEFAULT NULL COMMENT '海报',
                                              `activity_time` varchar(128) NOT NULL COMMENT '活动时间',
                                              `point` int DEFAULT NULL COMMENT '得分',
                                              `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                              `create_by` varchar(64) DEFAULT NULL,
                                              `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                              `update_by` varchar(64) DEFAULT NULL,
                                              PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=155 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生活动历史记录表';
INSERT INTO `t_student_activity_history` VALUES (154,2,'1',4,2,'Taylor Swift「The Eras Tour」亚洲巡演-东京站','/upload/e4a31451-ee3a-4c73-b6bf-1c29742eea95.png','2024-02-10 ~ 2025-09-10',80,'2025-06-21 19:03:52','2','2025-06-21 19:03:55','2');
CREATE TABLE `t_student_gift` (
                                  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                  `student_id` int NOT NULL COMMENT '学生ID，关联学生表',
                                  `student_name` varchar(128) NOT NULL COMMENT '学生名称',
                                  `gift_id` int NOT NULL COMMENT '礼物ID，关联gift表',
                                  `gift_name` varchar(128) DEFAULT NULL COMMENT '礼物',
                                  `image_url` varchar(255) DEFAULT NULL COMMENT '礼物图片',
                                  `point` int NOT NULL COMMENT '本次兑换花费积分',
                                  `status` int NOT NULL DEFAULT '1' COMMENT '兑换状态（1-成功 2-取消 3-待发放等）',
                                  `receive_name` varchar(128) DEFAULT NULL COMMENT '收件人',
                                  `receive_phone` varchar(128) DEFAULT NULL COMMENT '收件人电话',
                                  `address` varchar(128) DEFAULT NULL COMMENT '地址',
                                  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  `create_by` varchar(64) DEFAULT NULL,
                                  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                  `update_by` varchar(64) DEFAULT NULL,
                                  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=86 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生兑换礼物表';
INSERT INTO `t_student_gift` VALUES
                                 (64,2,'1',1,'周杰伦签名CD','/upload/e4a31451-ee3a-4c73-b6bf-1c29742eea95.png',500,3,'柳金秋','13849504938','福建省厦门市集美区厦门理工学院','2025-06-20 14:39:43','1','2025-06-20 15:11:07',NULL),
                                 (65,2,'1',2,'演唱会荧光棒套装','/upload/d304821e-c6d6-4899-bef8-b59f5671db90.jpg',300,1,'李思琪','13764579467','福建省福州市仓山区','2025-06-20 14:55:41','1','2025-06-20 15:11:07',NULL),
                                 (66,2,'1',3,'Taylor Swift纪念T恤','/upload/e4a31451-ee3a-4c73-b6bf-1c29742eea95.png',450,2,'黄雨洁','13457689467','福建省南平市延平区','2025-06-20 14:57:29','1','2025-06-20 15:11:07',NULL);
CREATE TABLE `t_student_score` (
                                   `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                   `student_id` int NOT NULL COMMENT '学生ID，关联学生表',
                                   `student_name` varchar(128) NOT NULL COMMENT '学生名称',
                                   `total_point` int NOT NULL DEFAULT '0' COMMENT '当前累计积分',
                                   `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   `create_by` varchar(64) DEFAULT NULL,
                                   `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                   `update_by` varchar(64) DEFAULT NULL,
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生累计积分表';
INSERT INTO `t_student_score` VALUES
                                  (8,2,'1',300,'2025-06-18 16:30:20','2','2025-06-21 19:04:30','system'),
                                  (20,1001,'张家铭',88,'2025-06-20 14:02:17','system','2025-06-20 14:02:17','teacher_li'),
                                  (21,1002,'王若曦',93,'2025-06-20 14:02:17','system','2025-06-20 14:02:17','teacher_wang'),
                                  (22,1003,'李泽谦',82,'2025-06-20 14:02:17','admin','2025-06-20 14:02:17','admin'),
                                  (23,1004,'刘昕雨',95,'2025-06-20 14:02:17','system','2025-06-20 14:02:17','teacher_zhao'),
                                  (24,1005,'陈沐阳',79,'2025-06-20 14:02:17','admin','2025-06-20 14:02:17','teacher_liu'),
                                  (25,1006,'杨诗涵',91,'2025-06-20 14:02:17','system','2025-06-20 14:02:17','system'),
                                  (26,1007,'赵砚书',86,'2025-06-20 14:02:17','admin','2025-06-20 14:02:17','admin'),
                                  (27,1008,'周予安',90,'2025-06-20 14:02:17','system','2025-06-20 14:02:17','teacher_wang'),
                                  (28,1009,'吴瑾萱',84,'2025-06-20 14:02:17','admin','2025-06-20 14:02:17','teacher_zhang'),
                                  (29,1010,'郑云舟',87,'2025-06-20 14:02:17','system','2025-06-20 14:02:17','system');
CREATE TABLE `t_user` (
                          `id` int NOT NULL AUTO_INCREMENT,
                          `username` varchar(255) NOT NULL,
                          `password` varchar(255) NOT NULL,
                          `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          `create_by` varchar(64) DEFAULT NULL,
                          `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          `update_by` varchar(64) DEFAULT NULL,
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1539395587 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
INSERT INTO `t_user` VALUES (1,'admin','123456','2025-06-10 10:58:27',NULL,'2025-06-10 10:58:27',NULL);
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

```

### 2.启动后端服务
```bash
cd EduPoint
# 配置数据库连接信息
# 修改 application.yml 中的数据库配置
#修改pom.xml中的JAVA 版本
mvn spring-boot:run
```

### 3.启动管理端
## Change directory
- cd vue-admin

## Install pnpm
- npm install pnpm -g

## Install dependencies
- pnpm install

## Start the project
- pnpm run dev

### 5.启动H5端
## 在HBuilderX中打开
- npm install

##  🎯数据库设计

### 核心表结构
- `t_user` - 管理员用户表
- `t_student` - 学生用户表
- `t_activity` - 活动表
- `t_student_activity_history` - 学生活动历史记录表
- `t_student_score` - 学生累计积分表
- `t_gift` - 礼物表
- `t_student_gift` - 学生兑换礼物表

## 🔧 开发指南

### 后端开发
- 使用Spring Boot 3.x + MyBatis Plus
- 遵循RESTful API设计规范
- 统一异常处理和响应格式

### 前端开发
- 管理端：Vue3 + Element Plus + TypeScript
- 移动端：UniApp + uview-plus
- 统一的代码规范和提交规范

### 数据库开发
- 使用MySQL 8.0+
- 表名统一使用`t_`前缀
- 包含标准的审计字段（create_time, update_time等）

## 📝 API文档
-参考EduPoint中的各Controller层

## 🎯 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 🎯 联系方式

- 项目维护者：Jeffern
- 邮箱：Jeffern1030@gmail.com
- 切勿用于商业用途，会追究版权

---

⭐ 如果这个项目对您有帮助，请给我们一个星标！
