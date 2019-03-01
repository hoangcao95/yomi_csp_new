-- phpMyAdmin SQL Dump
-- version 4.4.8
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Jun 15, 2017 at 04:02 AM
-- Server version: 5.7.18-log
-- PHP Version: 5.5.38

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `iloto`
--

-- --------------------------------------------------------

--
-- Table structure for table `auth_module`
--

CREATE TABLE IF NOT EXISTS `auth_module` (
  `id` bigint(20) NOT NULL,
  `name` varchar(256) NOT NULL DEFAULT '',
  `class` varchar(256) DEFAULT NULL,
  `desc` varchar(512) NOT NULL,
  `status` int(1) NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `auth_perm`
--

CREATE TABLE IF NOT EXISTS `auth_perm` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL DEFAULT '',
  `display_name` varchar(256) DEFAULT NULL,
  `description` varchar(256) DEFAULT NULL,
  `module_id` bigint(20) DEFAULT NULL,
  `status` int(1) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `auth_perm`
--

INSERT INTO `auth_perm` (`id`, `name`, `display_name`, `description`, `module_id`, `status`) VALUES
(1, 'CREATE', NULL, NULL, NULL, NULL),
(2, 'READ', NULL, NULL, NULL, NULL),
(3, 'UPDATE', NULL, NULL, NULL, NULL),
(4, 'DELETE', NULL, NULL, NULL, NULL),
(5, 'EXECUTE', NULL, NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `auth_role`
--

CREATE TABLE IF NOT EXISTS `auth_role` (
  `id` bigint(20) NOT NULL,
  `name` varchar(50) NOT NULL,
  `display_name` varchar(256) DEFAULT NULL,
  `description` varchar(256) DEFAULT NULL,
  `status` int(1) DEFAULT '1'
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `auth_role`
--

INSERT INTO `auth_role` (`id`, `name`, `display_name`, `description`, `status`) VALUES
(1, 'Administrators', NULL, NULL, 1),
(2, 'Managers', NULL, NULL, 1),
(3, 'Users', NULL, NULL, 1);

-- --------------------------------------------------------

--
-- Table structure for table `auth_role_perm`
--

CREATE TABLE IF NOT EXISTS `auth_role_perm` (
  `role_id` bigint(20) NOT NULL,
  `perm_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `auth_user`
--

CREATE TABLE IF NOT EXISTS `auth_user` (
  `id` bigint(20) NOT NULL,
  `user_name` varchar(45) DEFAULT NULL,
  `first_name` varchar(15) DEFAULT NULL,
  `middle_name` varchar(15) DEFAULT NULL,
  `last_name` varchar(15) DEFAULT NULL,
  `full_name` varchar(50) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `gender` varchar(1) DEFAULT '0',
  `salt` varchar(45) DEFAULT NULL,
  `password` varchar(128) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `modified_date` datetime DEFAULT NULL,
  `is_verified` tinyint(4) NOT NULL DEFAULT '1',
  `status` tinyint(4) NOT NULL DEFAULT '1',
  `user_type` tinyint(4) DEFAULT '1' COMMENT '0 -  system user; 1 - ; 2- affiliate; 4-sale staff'
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `auth_user`
--

INSERT INTO `auth_user` (`id`, `user_name`, `first_name`, `middle_name`, `last_name`, `full_name`, `email`, `gender`, `salt`, `password`, `created_date`, `modified_date`, `is_verified`, `status`, `user_type`) VALUES
(1, 'admin', 'Admin', NULL, NULL, NULL, 'admin@yo.com', '1', '5876695f8e4e1811', '$2a$10$9zhxxKf12qX2CZC8/VP.I.MOFWJTX18T3Hn6NtWH8q8Zkf6lVyHEy', '2014-07-03 22:21:33', NULL, 1, 1, 1);

-- --------------------------------------------------------

--
-- Table structure for table `auth_usermeta`
--

CREATE TABLE IF NOT EXISTS `auth_usermeta` (
  `id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `meta_key` varchar(255) DEFAULT NULL,
  `meta_value` longtext
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `auth_user_role`
--

CREATE TABLE IF NOT EXISTS `auth_user_role` (
  `user_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `auth_user_role`
--

INSERT INTO `auth_user_role` (`user_id`, `role_id`) VALUES
(1, 1),
(1, 2),
(1, 3);

-- --------------------------------------------------------

--
-- Table structure for table `core_charge_log`
--

CREATE TABLE IF NOT EXISTS `core_charge_log` (
  `id` bigint(20) NOT NULL,
  `trans_date` datetime DEFAULT NULL,
  `type` varchar(20) DEFAULT NULL,
  `msisdn` varchar(45) DEFAULT '',
  `amount` float DEFAULT '0',
  `call_status` int(1) DEFAULT '0',
  `result_status` int(1) DEFAULT '0',
  `req_data` text,
  `resp_data` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `core_detect_number_log`
--

CREATE TABLE IF NOT EXISTS `core_detect_number_log` (
  `id` bigint(20) NOT NULL,
  `trans_date` datetime DEFAULT NULL,
  `caller_ip` varchar(32) DEFAULT NULL,
  `source_ip` varchar(32) DEFAULT NULL,
  `msisdn` varchar(20) DEFAULT NULL,
  `result_status` int(1) DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `core_kpi_daily`
--

CREATE TABLE IF NOT EXISTS `core_kpi_daily` (
  `id` bigint(20) NOT NULL,
  `datetime` date DEFAULT NULL,
  `summary_at` datetime DEFAULT NULL,
  `total` bigint(11) DEFAULT NULL,
  `success` bigint(11) DEFAULT NULL,
  `kpi_type` int(4) DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `core_mo_sms`
--

CREATE TABLE IF NOT EXISTS `core_mo_sms` (
  `id` bigint(20) NOT NULL,
  `msisdn` varchar(125) DEFAULT NULL,
  `short_code` varchar(15) DEFAULT NULL,
  `message` varchar(512) DEFAULT NULL,
  `service_code` varchar(45) DEFAULT NULL,
  `keyword` varchar(45) DEFAULT NULL,
  `sms_id` varchar(45) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `process_status` int(11) DEFAULT '200'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `core_mt_sms`
--

CREATE TABLE IF NOT EXISTS `core_mt_sms` (
  `id` bigint(20) NOT NULL,
  `short_code` varchar(15) DEFAULT NULL,
  `msisdn` varchar(125) DEFAULT NULL,
  `message` varchar(512) DEFAULT NULL,
  `service_code` varchar(45) DEFAULT NULL,
  `keyword` varchar(45) DEFAULT NULL,
  `sms_id` varchar(45) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `request_id` int(11) DEFAULT NULL,
  `sent_status` int(1) DEFAULT '1',
  `channel` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `core_sms_syntax`
--

CREATE TABLE IF NOT EXISTS `core_sms_syntax` (
  `id` bigint(11) NOT NULL,
  `operator` varchar(45) DEFAULT NULL,
  `short_code` varchar(15) DEFAULT NULL,
  `syntax` varchar(45) DEFAULT NULL,
  `regex` varchar(45) DEFAULT NULL,
  `command` varchar(45) DEFAULT NULL,
  `description` varchar(512) DEFAULT NULL,
  `product_id` bigint(20) DEFAULT NULL,
  `package_id` bigint(20) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `status` int(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `core_subscriber`
--

CREATE TABLE IF NOT EXISTS `core_subscriber` (
  `id` bigint(20) NOT NULL,
  `msisdn` varchar(125) DEFAULT NULL,
  `mpin` varchar(15) NOT NULL,
  `product_id` bigint(20) DEFAULT NULL,
  `package_id` bigint(20) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `modified_date` datetime DEFAULT NULL,
  `register_date` datetime DEFAULT NULL,
  `unregister_date` datetime DEFAULT NULL,
  `last_renew` datetime DEFAULT NULL,
  `last_retry` datetime DEFAULT NULL,
  `expired_date` datetime DEFAULT NULL,
  `status` int(1) DEFAULT '0' COMMENT '0 - Inactive; 1 - active',
  `channel` varchar(32) DEFAULT '0' COMMENT '0 - sms; 1 - wap; 2 - ivr',
  `reg_new` int(1) DEFAULT '1',
  `charged_count` int(11) NOT NULL DEFAULT '0',
  `last_charged` datetime DEFAULT NULL,
  `charge_failed_count` int(11) DEFAULT '0',
  `charge_count_inday` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `core_subscriber`
--

INSERT INTO `core_subscriber` (`id`, `msisdn`, `mpin`, `product_id`, `package_id`, `created_date`, `modified_date`, `register_date`, `unregister_date`, `last_renew`, `last_retry`, `expired_date`, `status`, `channel`, `reg_new`, `charged_count`, `last_charged`, `charge_failed_count`, `charge_count_inday`) VALUES
(1, '904596082', '8832', 1, 5, '2017-06-09 16:13:39', '2017-06-11 20:37:29', '2017-06-11 20:37:41', '2017-06-11 20:26:41', NULL, NULL, '2017-06-12 20:37:40', 0, 'WAP', 0, 0, NULL, 0, 0),
(5, '904596082', '0382', 1, 1, '2017-06-09 17:18:58', '2017-06-14 17:34:37', '2017-06-14 17:34:36', NULL, NULL, NULL, '2017-06-15 17:34:35', 1, 'WAP', 0, 0, NULL, 0, 0),
(7, '904596082', '6517', 1, 19, '2017-06-12 13:56:25', '2017-06-14 17:31:34', '2017-06-14 17:31:32', '2017-06-14 17:31:13', NULL, NULL, '2017-06-21 17:31:31', 0, 'SMS', 0, 0, NULL, 0, 0),
(9, '1205090702', '8467', 1, 1, '2017-06-14 14:15:47', '2017-06-14 14:15:47', '2017-06-14 14:15:45', NULL, NULL, NULL, '2017-06-15 14:15:44', 1, 'SMS', 1, 0, NULL, 0, 0),
(11, '904596082', '9853', 1, 9, '2017-06-14 17:41:47', '2017-06-14 17:41:47', '2017-06-14 17:41:45', NULL, NULL, NULL, '2017-06-21 17:41:44', 1, 'SMS', 1, 0, NULL, 0, 0),
(13, '1205090702', '7601', 1, 9, '2017-06-14 17:42:00', '2017-06-14 17:50:17', '2017-06-14 17:41:59', '2017-06-14 17:50:15', NULL, NULL, '2017-06-21 17:41:58', 0, 'SMS', 1, 0, NULL, 0, 0),
(15, '904596082', '2251', 1, 11, '2017-06-14 17:46:57', '2017-06-14 17:46:57', '2017-06-14 17:46:56', NULL, NULL, NULL, '2017-06-15 17:46:55', 1, 'WAP', 1, 0, NULL, 0, 0),
(17, '904596082', '5750', 1, 23, '2017-06-15 10:01:44', '2017-06-15 10:01:44', '2017-06-15 10:01:43', NULL, NULL, NULL, '2017-06-16 10:01:42', 1, 'WAP', 1, 0, NULL, 0, 0),
(19, '904596082', '9990', 1, 31, '2017-06-15 10:10:13', '2017-06-15 10:10:13', '2017-06-15 10:10:12', NULL, NULL, NULL, '2017-06-22 10:10:11', 1, 'WAP', 1, 0, NULL, 0, 0);

-- --------------------------------------------------------

--
-- Table structure for table `core_subscriber_attribute`
--

CREATE TABLE IF NOT EXISTS `core_subscriber_attribute` (
  `id` bigint(20) NOT NULL,
  `attr_key` varchar(45) DEFAULT NULL,
  `attr_value` varchar(45) DEFAULT NULL,
  `msisdn` varchar(125) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `core_subs_blacklist`
--

CREATE TABLE IF NOT EXISTS `core_subs_blacklist` (
  `id` bigint(11) NOT NULL,
  `type` int(1) DEFAULT '0',
  `msisdn` varchar(45) DEFAULT NULL,
  `regex` varchar(45) DEFAULT NULL,
  `added_date` datetime DEFAULT NULL,
  `modified_date` datetime DEFAULT NULL,
  `status` int(1) DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `core_subs_request`
--

CREATE TABLE IF NOT EXISTS `core_subs_request` (
  `id` bigint(20) NOT NULL,
  `trans_id` varchar(45) DEFAULT NULL,
  `subs_id` bigint(20) NOT NULL,
  `msisdn` varchar(20) DEFAULT NULL,
  `req_datetime` datetime DEFAULT NULL,
  `req_status` int(1) DEFAULT NULL,
  `amount` float DEFAULT NULL,
  `command` varchar(160) DEFAULT NULL,
  `process_status` int(11) DEFAULT NULL,
  `data` text,
  `data_resp` text
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `core_subs_request`
--

INSERT INTO `core_subs_request` (`id`, `trans_id`, `subs_id`, `msisdn`, `req_datetime`, `req_status`, `amount`, `command`, `process_status`, `data`, `data_resp`) VALUES
(1, '20170609161339109810', 1, '904596082', '2017-06-09 16:13:40', NULL, NULL, 'REG', 200, '{"amount":0,"providerCode":"","errorDesc":"","transId":"20170609161339109810","expiredDate":"2017/06/10 16:13:45","channel":"WAP","regDate":"2017/06/09 16:13:39","errorCode":"0000","regNew":true,"subsPackageCode":"KQMT_GOINGAY","mpin":"8832","msisdn":"904596082","originalSms":"DK XSMT","refCode":"","status":true}', '{"pageInfo":null,"message":"OK","content":null,"errors":null,"status":200,"timestamp":1496999628142}'),
(3, '20170609161625474660', 1, '904596082', '2017-06-09 16:16:26', NULL, NULL, 'CAN', 200, '{"cancelDate":"2017/06/09 16:16:25","subsPackageCode":"KQMT_GOINGAY","errorDesc":"","transId":"20170609161625474660","channel":"SMS","errorCode":"0000","msisdn":"904596082","originalSms":"HUY XSMT","status":true}', '{"pageInfo":null,"message":"OK","content":null,"errors":null,"status":200,"timestamp":1496999793858}'),
(5, '20170609171858009252', 5, '904596082', '2017-06-09 17:18:59', NULL, NULL, 'REG', 200, '{"amount":0,"providerCode":"","errorDesc":"","transId":"20170609171858009252","expiredDate":"2017/06/10 17:19:05","channel":"WAP","regDate":"2017/06/09 17:18:58","errorCode":"0000","regNew":true,"subsPackageCode":"KQMB_GOINGAY","mpin":"0382","msisdn":"904596082","originalSms":"DK XSMB","refCode":"","status":true}', '{"pageInfo":null,"message":"OK","content":null,"errors":null,"status":200,"timestamp":1497003546713}'),
(7, '20170611202607083504', 1, '904596082', '2017-06-11 20:26:07', NULL, NULL, 'REG', 200, '{"amount":0,"providerCode":"","errorDesc":"","transId":"20170611202607083504","expiredDate":"2017/06/12 20:26:18","channel":"WAP","regDate":"2017/06/11 20:26:07","errorCode":"0000","regNew":true,"subsPackageCode":"KQMT_GOINGAY","mpin":"2011","msisdn":"904596082","originalSms":"DK XSMT","refCode":"","status":true}', '{"pageInfo":null,"message":"OK","content":null,"errors":null,"status":200,"timestamp":1497187580439}'),
(9, '20170611202629690290', 1, '904596082', '2017-06-11 20:26:30', NULL, NULL, 'CAN', 200, '{"cancelDate":"2017/06/11 20:26:29","subsPackageCode":"KQMT_GOINGAY","errorDesc":"","transId":"20170611202629690290","channel":"SMS","errorCode":"0000","msisdn":"904596082","originalSms":"HUY XSMT","status":true}', '{"pageInfo":null,"message":"OK","content":null,"errors":null,"status":200,"timestamp":1497187602989}'),
(11, '20170611203728711860', 1, '904596082', '2017-06-11 20:37:29', NULL, NULL, 'REG', 200, '{"amount":0,"providerCode":"","errorDesc":"","transId":"20170611203728711860","expiredDate":"2017/06/12 20:37:40","channel":"WAP","regDate":"2017/06/11 20:37:28","errorCode":"0000","regNew":true,"subsPackageCode":"KQMT_GOINGAY","mpin":"5824","msisdn":"904596082","originalSms":"DK XSMT","refCode":"","status":true}', '{"pageInfo":null,"message":"OK","content":null,"errors":null,"status":200,"timestamp":1497188262016}'),
(13, '20170612135624919009', 7, '904596082', '2017-06-12 13:56:26', NULL, NULL, 'REG', 200, '{"amount":0,"providerCode":"","errorDesc":"","transId":"20170612135624919009","expiredDate":"2017/06/19 13:56:37","channel":"SMS","regDate":"2017/06/12 13:56:24","errorCode":"0000","regNew":true,"subsPackageCode":"TKMT_GOITUAN","mpin":"6517","msisdn":"904596082","originalSms":"DK TKMT7","refCode":"","status":true}', '{"pageInfo":null,"message":"OK","content":null,"errors":null,"status":200,"timestamp":1497250600333}'),
(15, '20170614141547229434', 9, '1205090702', '2017-06-14 14:15:48', NULL, NULL, 'REG', 200, '{"amount":1000,"providerCode":"","errorDesc":"","transId":"20170614141547229434","expiredDate":"2017/06/15 14:15:44","channel":"SMS","regDate":"2017/06/14 14:15:47","errorCode":"0000","regNew":true,"subsPackageCode":"KQMB_GOINGAY","mpin":"8467","msisdn":"1205090702","originalSms":"DK XSMB","refCode":"","status":true}', '{"pageInfo":null,"message":"OK","content":null,"errors":null,"status":200,"timestamp":1497424547808}'),
(17, '20170614173114874272', 7, '904596082', '2017-06-14 17:31:15', NULL, NULL, 'CAN', 200, '{"cancelDate":"2017/06/14 17:31:14","subsPackageCode":"TKMT_GOITUAN","errorDesc":"","transId":"20170614173114874272","channel":"SMS","errorCode":"0000","msisdn":"904596082","originalSms":"HUY TKMT7","status":true}', '{"pageInfo":null,"message":"OK","content":null,"errors":null,"status":200,"timestamp":1497436274993}'),
(19, '20170614173134092584', 7, '904596082', '2017-06-14 17:31:34', NULL, NULL, 'REG', 200, '{"amount":1000,"providerCode":"","errorDesc":"","transId":"20170614173134092584","expiredDate":"2017/06/21 17:31:31","channel":"SMS","regDate":"2017/06/14 17:31:34","errorCode":"0000","regNew":true,"subsPackageCode":"TKMT_GOITUAN","mpin":"0767","msisdn":"904596082","originalSms":"DK TKMT7","refCode":"","status":true}', '{"pageInfo":null,"message":"OK","content":null,"errors":null,"status":200,"timestamp":1497436294129}'),
(21, '20170614173437049059', 5, '904596082', '2017-06-14 17:34:37', NULL, NULL, 'REG', 200, '{"amount":1000,"providerCode":"","errorDesc":"","transId":"20170614173437049059","expiredDate":"2017/06/15 17:34:35","channel":"WAP","regDate":"2017/06/14 17:34:37","errorCode":"0000","regNew":true,"subsPackageCode":"KQMB_GOINGAY","mpin":"7576","msisdn":"904596082","originalSms":"DK XSMB","refCode":"","status":true}', '{"pageInfo":null,"message":"OK","content":null,"errors":null,"status":200,"timestamp":1497436477089}'),
(23, '20170614174146850202', 11, '904596082', '2017-06-14 17:41:47', NULL, NULL, 'REG', 200, '{"amount":0,"providerCode":"","errorDesc":"","transId":"20170614174146850202","expiredDate":"2017/06/21 17:41:44","channel":"SMS","regDate":"2017/06/14 17:41:46","errorCode":"0000","regNew":true,"subsPackageCode":"KQMT_GOITUAN","mpin":"9853","msisdn":"904596082","originalSms":"DK XSMT7","refCode":"","status":true}', '{"pageInfo":null,"message":"OK","content":null,"errors":null,"status":200,"timestamp":1497436906893}'),
(25, '20170614174159502716', 13, '1205090702', '2017-06-14 17:42:00', NULL, NULL, 'REG', 200, '{"amount":0,"providerCode":"","errorDesc":"","transId":"20170614174159502716","expiredDate":"2017/06/21 17:41:58","channel":"SMS","regDate":"2017/06/14 17:41:59","errorCode":"0000","regNew":true,"subsPackageCode":"KQMT_GOITUAN","mpin":"7601","msisdn":"1205090702","originalSms":"DK XSMT7","refCode":"","status":true}', '{"pageInfo":null,"message":"OK","content":null,"errors":null,"status":200,"timestamp":1497436919533}'),
(27, '20170614174656596995', 15, '904596082', '2017-06-14 17:46:57', NULL, NULL, 'REG', 200, '{"amount":1000,"providerCode":"","errorDesc":"","transId":"20170614174656596995","expiredDate":"2017/06/15 17:46:55","channel":"WAP","regDate":"2017/06/14 17:46:56","errorCode":"0000","regNew":true,"subsPackageCode":"TKMB_GOINGAY","mpin":"2251","msisdn":"904596082","originalSms":"DK TKMB","refCode":"","status":true}', '{"pageInfo":null,"message":"OK","content":null,"errors":null,"status":200,"timestamp":1497437216630}'),
(29, '20170614175016943419', 13, '1205090702', '2017-06-14 17:50:17', NULL, NULL, 'CAN', 200, '{"cancelDate":"2017/06/14 17:50:16","subsPackageCode":"KQMT_GOITUAN","errorDesc":"","transId":"20170614175016943419","channel":"SMS","errorCode":"0000","msisdn":"1205090702","originalSms":"HUY XSMT7","status":true}', '{"pageInfo":null,"message":"OK","content":null,"errors":null,"status":200,"timestamp":1497437416975}'),
(31, '20170615100144194613', 17, '904596082', '2017-06-15 10:01:45', NULL, NULL, 'REG', 200, '{"amount":0,"providerCode":"","errorDesc":"","transId":"20170615100144194613","expiredDate":"2017/06/16 10:01:42","channel":"WAP","regDate":"2017/06/15 10:01:44","errorCode":"0000","regNew":true,"subsPackageCode":"VIPMN_GOINGAY","mpin":"5750","msisdn":"904596082","originalSms":"DK VIPMN VIPMN_GOINGAY","refCode":"","status":true}', '{"pageInfo":null,"message":"OK","content":null,"errors":null,"status":200,"timestamp":1497495704727}'),
(33, '20170615101012849062', 19, '904596082', '2017-06-15 10:10:13', NULL, NULL, 'REG', 200, '{"amount":0,"providerCode":"","errorDesc":"","transId":"20170615101012849062","expiredDate":"2017/06/22 10:10:11","channel":"WAP","regDate":"2017/06/15 10:10:12","errorCode":"0000","regNew":true,"subsPackageCode":"MG_GOITUAN","mpin":"9990","msisdn":"904596082","originalSms":"DK MG MG_GOITUAN","refCode":"","status":true}', '{"pageInfo":null,"message":"OK","content":null,"errors":null,"status":200,"timestamp":1497496212889}');

-- --------------------------------------------------------

--
-- Table structure for table `core_vas_package`
--

CREATE TABLE IF NOT EXISTS `core_vas_package` (
  `id` bigint(20) NOT NULL,
  `product_id` bigint(20) DEFAULT NULL,
  `name` varchar(45) DEFAULT NULL,
  `desc` varchar(512) DEFAULT NULL,
  `type` int(1) DEFAULT '0',
  `price` float DEFAULT '0',
  `duration` int(11) DEFAULT '1',
  `free_duration` int(11) DEFAULT '0',
  `created_date` datetime DEFAULT NULL,
  `modified_date` datetime DEFAULT NULL,
  `status` int(1) DEFAULT '1'
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `core_vas_package`
--

INSERT INTO `core_vas_package` (`id`, `product_id`, `name`, `desc`, `type`, `price`, `duration`, `free_duration`, `created_date`, `modified_date`, `status`) VALUES
(1, 1, 'KQMB_GOINGAY', 'KQMB_GOINGAY', 0, 1000, 1, 0, '2017-06-09 16:12:33', '2017-06-09 16:12:39', 1),
(3, 1, 'KQMN_GOINGAY', 'KQMN_GOINGAY', 0, 1000, 1, 0, '2017-06-09 16:12:33', '2017-06-09 16:12:39', 1),
(5, 1, 'KQMT_GOINGAY', 'KQMB_GOINGAY', 0, 1000, 1, 0, '2017-06-09 16:12:33', '2017-06-09 16:12:39', 1),
(7, 1, 'KQMN_GOITUAN', 'KQMB_GOINGAY', 0, 1000, 1, 0, '2017-06-09 16:12:33', '2017-06-09 16:12:39', 1),
(9, 1, 'KQMT_GOITUAN', 'KQMB_GOINGAY', 0, 1000, 1, 0, '2017-06-09 16:12:33', '2017-06-09 16:12:39', 1),
(11, 1, 'TKMB_GOINGAY', 'KQMB_GOINGAY', 0, 1000, 1, 0, '2017-06-09 16:12:33', '2017-06-09 16:12:39', 1),
(13, 1, 'TKMN_GOINGAY', 'KQMB_GOINGAY', 0, 1000, 1, 0, '2017-06-09 16:12:33', '2017-06-09 16:12:39', 1),
(15, 1, 'TKMT_GOINGAY', 'KQMB_GOINGAY', 0, 1000, 1, 0, '2017-06-09 16:12:33', '2017-06-09 16:12:39', 1),
(17, 1, 'TKMN_GOITUAN', 'KQMB_GOINGAY', 0, 1000, 1, 0, '2017-06-09 16:12:33', '2017-06-09 16:12:39', 1),
(19, 1, 'TKMT_GOITUAN', 'KQMB_GOINGAY', 0, 1000, 1, 0, '2017-06-09 16:12:33', '2017-06-09 16:12:39', 1),
(21, 1, 'VIPMB_GOINGAY', 'KQMB_GOINGAY', 0, 1000, 1, 0, '2017-06-09 16:12:33', '2017-06-09 16:12:39', 1),
(23, 1, 'VIPMN_GOINGAY', 'KQMB_GOINGAY', 0, 1000, 1, 0, '2017-06-09 16:12:33', '2017-06-09 16:12:39', 1),
(25, 1, 'VIPMT_GOINGAY', 'KQMB_GOINGAY', 0, 1000, 1, 0, '2017-06-09 16:12:33', '2017-06-09 16:12:39', 1),
(27, 1, 'VIPMN_GOITUAN', 'KQMB_GOINGAY', 0, 1000, 1, 0, '2017-06-09 16:12:33', '2017-06-09 16:12:39', 1),
(29, 1, 'VIPMT_GOITUAN', 'KQMB_GOINGAY', 0, 1000, 1, 0, '2017-06-09 16:12:33', '2017-06-09 16:12:39', 1),
(31, 1, 'MG_GOITUAN', 'KQMB_GOINGAY', 0, 1000, 1, 0, '2017-06-09 16:12:33', '2017-06-09 16:12:39', 1),
(33, 1, '4D_GOITUAN', 'KQMB_GOINGAY', 0, 1000, 1, 0, '2017-06-09 16:12:33', '2017-06-09 16:12:39', 1),
(35, 1, 'DT_GOITUAN', 'KQMB_GOINGAY', 0, 1000, 1, 0, '2017-06-09 16:12:33', '2017-06-09 16:12:39', 1),
(37, 1, 'KQ_TAILE', 'KQMB_GOINGAY', 0, 1000, 1, 0, '2017-06-09 16:12:33', '2017-06-09 16:12:39', 1),
(39, 1, 'TK_TAILE', 'KQMB_GOINGAY', 0, 1000, 1, 0, '2017-06-09 16:12:33', '2017-06-09 16:12:39', 1),
(41, 1, 'VIP_TAILE', 'KQMB_GOINGAY', 0, 1000, 1, 0, '2017-06-09 16:12:33', '2017-06-09 16:12:39', 1),
(43, 1, 'MG_TAILE', 'KQMB_GOINGAY', 0, 1000, 1, 0, '2017-06-09 16:12:33', '2017-06-09 16:12:39', 1),
(45, 1, '4D_TAILE', 'KQMB_GOINGAY', 0, 1000, 1, 0, '2017-06-09 16:12:33', '2017-06-09 16:12:39', 1),
(47, 1, 'DT_TAILE', 'KQMB_GOINGAY', 0, 1000, 1, 0, '2017-06-09 16:12:33', '2017-06-09 16:12:39', 1);

-- --------------------------------------------------------

--
-- Table structure for table `core_vas_product`
--

CREATE TABLE IF NOT EXISTS `core_vas_product` (
  `id` bigint(20) NOT NULL,
  `code` varchar(45) DEFAULT NULL,
  `name` varchar(128) DEFAULT NULL,
  `status` int(1) DEFAULT '1'
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `core_vas_product`
--

INSERT INTO `core_vas_product` (`id`, `code`, `name`, `status`) VALUES
(1, 'iLoto', 'iLoto', 1);

-- --------------------------------------------------------

--
-- Table structure for table `schedule`
--

CREATE TABLE IF NOT EXISTS `schedule` (
  `id` bigint(11) NOT NULL,
  `name` varchar(128) NOT NULL,
  `type` varchar(45) NOT NULL DEFAULT '0',
  `status` tinyint(4) NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `schedule_detail`
--

CREATE TABLE IF NOT EXISTS `schedule_detail` (
  `id` bigint(11) NOT NULL,
  `schedule_id` bigint(11) NOT NULL,
  `day_id` tinyint(4) NOT NULL DEFAULT '0',
  `start_time` time DEFAULT NULL,
  `end_time` time DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `auth_module`
--
ALTER TABLE `auth_module`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `auth_perm`
--
ALTER TABLE `auth_perm`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `auth_role`
--
ALTER TABLE `auth_role`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `auth_role_perm`
--
ALTER TABLE `auth_role_perm`
  ADD PRIMARY KEY (`role_id`,`perm_id`),
  ADD KEY `FK_RP_PERM` (`perm_id`);

--
-- Indexes for table `auth_user`
--
ALTER TABLE `auth_user`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username_UNIQUE` (`user_name`),
  ADD UNIQUE KEY `email_UNIQUE` (`email`);

--
-- Indexes for table `auth_usermeta`
--
ALTER TABLE `auth_usermeta`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_UM_USER_idx` (`user_id`);

--
-- Indexes for table `auth_user_role`
--
ALTER TABLE `auth_user_role`
  ADD PRIMARY KEY (`user_id`,`role_id`),
  ADD KEY `FK_UR_ROLE_idx` (`role_id`);

--
-- Indexes for table `core_charge_log`
--
ALTER TABLE `core_charge_log`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `core_detect_number_log`
--
ALTER TABLE `core_detect_number_log`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `core_kpi_daily`
--
ALTER TABLE `core_kpi_daily`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `core_mo_sms`
--
ALTER TABLE `core_mo_sms`
  ADD PRIMARY KEY (`id`),
  ADD KEY `DATETIME_INDX` (`created_date`),
  ADD KEY `MSISDN_INDX` (`msisdn`);

--
-- Indexes for table `core_mt_sms`
--
ALTER TABLE `core_mt_sms`
  ADD PRIMARY KEY (`id`),
  ADD KEY `DATETIME_INDX` (`created_date`),
  ADD KEY `MSISDN_INDX` (`msisdn`);

--
-- Indexes for table `core_sms_syntax`
--
ALTER TABLE `core_sms_syntax`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `core_subscriber`
--
ALTER TABLE `core_subscriber`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `msisdn_service_UNIQUE` (`msisdn`,`package_id`) USING BTREE,
  ADD KEY `REG_DATE_INDX` (`register_date`),
  ADD KEY `RECHARGE_IDX` (`status`) USING BTREE,
  ADD KEY `msisdn` (`msisdn`);

--
-- Indexes for table `core_subscriber_attribute`
--
ALTER TABLE `core_subscriber_attribute`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `MSISDN_IDX` (`attr_key`,`msisdn`);

--
-- Indexes for table `core_subs_blacklist`
--
ALTER TABLE `core_subs_blacklist`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `core_subs_request`
--
ALTER TABLE `core_subs_request`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `core_vas_package`
--
ALTER TABLE `core_vas_package`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name_UNIQUE` (`name`);

--
-- Indexes for table `core_vas_product`
--
ALTER TABLE `core_vas_product`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `schedule`
--
ALTER TABLE `schedule`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `schedule_detail`
--
ALTER TABLE `schedule_detail`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_SD_SCHEDULE_idx` (`schedule_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `auth_module`
--
ALTER TABLE `auth_module`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `auth_perm`
--
ALTER TABLE `auth_perm`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=6;
--
-- AUTO_INCREMENT for table `auth_role`
--
ALTER TABLE `auth_role`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT for table `auth_user`
--
ALTER TABLE `auth_user`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=2;
--
-- AUTO_INCREMENT for table `auth_usermeta`
--
ALTER TABLE `auth_usermeta`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `core_charge_log`
--
ALTER TABLE `core_charge_log`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `core_detect_number_log`
--
ALTER TABLE `core_detect_number_log`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `core_kpi_daily`
--
ALTER TABLE `core_kpi_daily`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `core_mo_sms`
--
ALTER TABLE `core_mo_sms`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `core_mt_sms`
--
ALTER TABLE `core_mt_sms`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `core_sms_syntax`
--
ALTER TABLE `core_sms_syntax`
  MODIFY `id` bigint(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `core_subscriber`
--
ALTER TABLE `core_subscriber`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=21;
--
-- AUTO_INCREMENT for table `core_subscriber_attribute`
--
ALTER TABLE `core_subscriber_attribute`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `core_subs_blacklist`
--
ALTER TABLE `core_subs_blacklist`
  MODIFY `id` bigint(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `core_subs_request`
--
ALTER TABLE `core_subs_request`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=35;
--
-- AUTO_INCREMENT for table `core_vas_package`
--
ALTER TABLE `core_vas_package`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=49;
--
-- AUTO_INCREMENT for table `core_vas_product`
--
ALTER TABLE `core_vas_product`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT for table `schedule`
--
ALTER TABLE `schedule`
  MODIFY `id` bigint(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `schedule_detail`
--
ALTER TABLE `schedule_detail`
  MODIFY `id` bigint(11) NOT NULL AUTO_INCREMENT;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `auth_role_perm`
--
ALTER TABLE `auth_role_perm`
  ADD CONSTRAINT `FK_RP_PERM` FOREIGN KEY (`perm_id`) REFERENCES `auth_perm` (`id`),
  ADD CONSTRAINT `FK_RP_ROLE` FOREIGN KEY (`role_id`) REFERENCES `auth_role` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `auth_usermeta`
--
ALTER TABLE `auth_usermeta`
  ADD CONSTRAINT `FK_UM_USER` FOREIGN KEY (`user_id`) REFERENCES `auth_user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `auth_user_role`
--
ALTER TABLE `auth_user_role`
  ADD CONSTRAINT `FK_UR_ROLE` FOREIGN KEY (`role_id`) REFERENCES `auth_role` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `FK_UR_USER` FOREIGN KEY (`user_id`) REFERENCES `auth_user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `schedule_detail`
--
ALTER TABLE `schedule_detail`
  ADD CONSTRAINT `FK_SD_SCHEDULE` FOREIGN KEY (`schedule_id`) REFERENCES `schedule` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
