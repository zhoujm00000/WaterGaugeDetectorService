/*
Navicat MySQL Data Transfer

Source Server         : xlauncher
Source Server Version : 50720
Source Host           : localhost:3306
Source Database       : tiangong_draft

Target Server Type    : MYSQL
Target Server Version : 50720
File Encoding         : 65001

Date: 2018-10-25 13:53:47
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for configuration
-- ----------------------------
DROP TABLE IF EXISTS `configuration`;
CREATE TABLE `configuration` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `options` varchar(20) NOT NULL COMMENT '配置选项',
  `params` varchar(255) DEFAULT NULL COMMENT '参数',
  PRIMARY KEY (`id`,`options`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of configuration
-- ----------------------------
-- 192.168.6.6 1433
INSERT INTO `configuration` VALUES ('1', 'MySQL', '{\"ip\":\"db.dcxxsoft.xyz\",\"port\":\"14336\",\"name\":\"WaterResource\",\"password\":\"WaterResource\",\"database\":\"WaterResource\"}');
INSERT INTO `configuration` VALUES ('2', 'Time', '{\"data\":\"60\"}');
INSERT INTO `configuration` VALUES ('3', 'Threshold', '{\"data\":\"0.7\"}');
INSERT INTO `configuration` VALUES ('4', 'Path', '{\"data\":\"/var/tiangong_imgs\"}');
