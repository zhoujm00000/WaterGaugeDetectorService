/*
Navicat MySQL Data Transfer

Source Server         : xlauncher
Source Server Version : 50720
Source Host           : localhost:3306
Source Database       : tiangong_draft

Target Server Type    : MYSQL
Target Server Version : 50720
File Encoding         : 65001

Date: 2018-10-23 10:01:29
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
INSERT INTO `configuration` VALUES ('1', 'MySQL', '{\"ip\":\"127.0.0.1\",\"port\":\"3306\",\"name\":\"\",\"password\":\"\",\"database\":\"\"}');
INSERT INTO `configuration` VALUES ('2', 'Time', '{\"data\":\"10\"}');
INSERT INTO `configuration` VALUES ('3', 'Threshold', '{\"data\":\"0.5\"}');
INSERT INTO `configuration` VALUES ('4', 'Path', '{\"data\":\"/opt/tiangong_imgs\"}');

-- ----------------------------
-- Table structure for consume
-- ----------------------------
DROP TABLE IF EXISTS `consume`;
CREATE TABLE `consume` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `start_time` datetime DEFAULT NULL COMMENT '读取数据的开始时间',
  `consume_time` int(50) DEFAULT NULL COMMENT '读取数据的消耗时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for predict
-- ----------------------------
DROP TABLE IF EXISTS `predict`;
CREATE TABLE `predict` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `collect_time` datetime NOT NULL COMMENT '采集时间',
  `channel` int(4) DEFAULT NULL COMMENT '设备',
  `SID` varchar(20) DEFAULT NULL COMMENT '站点编号',
  `identify` float(8,0) DEFAULT NULL COMMENT '预测数据',
  `check` float(8,0) DEFAULT NULL COMMENT '反馈数据',
  `source` mediumblob COMMENT '图片数据',
  `status` int(4) NOT NULL DEFAULT '0' COMMENT '图片是否有遮挡，1有遮挡，0无遮挡',
  `picture` mediumblob COMMENT '存储缩略图',
  `identify_description` varchar(32) DEFAULT NULL COMMENT '预测异常图片信息描述(正常、无水尺异常、水尺异常、刻度异常)',
  `check_description` varchar(32) DEFAULT NULL COMMENT '反馈异常图片信息描述(正常、无水尺异常、水尺异常、刻度异常)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `account` varchar(20) NOT NULL COMMENT '账号',
  `password` varchar(50) NOT NULL COMMENT '密码',
  `token` varchar(255) DEFAULT NULL COMMENT '令牌',
  PRIMARY KEY (`account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for length
-- ----------------------------
DROP TABLE IF EXISTS `length`;
CREATE TABLE `length` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `SID` varchar(20) DEFAULT NULL,
  `channel` int(4) DEFAULT NULL,
  `height` int(8) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
