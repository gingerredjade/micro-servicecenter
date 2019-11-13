/*
 Navicat MySQL Data Transfer

 Source Server         : 192.168.56.221-3306
 Source Server Type    : MySQL
 Source Server Version : 80017
 Source Host           : 192.168.56.221:3306
 Source Schema         : service_center

 Target Server Type    : MySQL
 Target Server Version : 80017
 File Encoding         : 65001

 Date: 28/10/2019 15:11:40
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for organization
-- ----------------------------
DROP TABLE IF EXISTS `organization`;
CREATE TABLE `organization`  (
  `organization_id` int(11) NOT NULL AUTO_INCREMENT,
  `organization_address` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `organization_contact` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `organization_email` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `organization_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `organization_phone_number` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `organization_website` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `special_identity` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`organization_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of organization
-- ----------------------------
INSERT INTO `organization` VALUES (1, '北京市海淀区北四环中路211号（100083）', '工作人员', 'xxhtjb@cetc.com.cn', '中国电子科技集团公司第十五研究所', '010-89055775', 'http://www.nci.ac.cn/', 'cetc15');
INSERT INTO `organization` VALUES (2, '北京市朝阳区酒仙桥北路甲10号院电子城IT产业园107楼（100015）', '工作人员', 'support@supermap.com', '北京超图软件股份有限公司', '+86-10-59896655', 'https://www.supermap.com/', 'sm');
INSERT INTO `organization` VALUES (3, '北京市朝阳区安翔北里甲11号北京创业大厦A座东门3层（100101）', '工作人员', '无', '国遥新天地', '010-64876655', 'http://www.ev-image.com/', 'ev');
INSERT INTO `organization` VALUES (4, '北京市海淀区花园北路14号66幢2层226号（213022）', '工作人员', 'public@mapscloud.com', '北京星球时空科技有限公司', '010-52861878', 'http://www.mapscloud.cn', 'mc');
INSERT INTO `organization` VALUES (5, '北京市海淀区永丰路与北清路交汇处东南四维图新大厦（100094）', '工作人员', 'info@naviinfo.com', '四维图新', '010-82306399', 'https://www.navinfo.com/', 'nav');
INSERT INTO `organization` VALUES (6, '北京市中关村科技装备创新创业基地3层303（100085）', '工作人员', 'gttech@gtgis.cn', '北京庚图科技有限公司', '010-61199626', 'http://www.gtgis.cn/', 'gt');

SET FOREIGN_KEY_CHECKS = 1;
