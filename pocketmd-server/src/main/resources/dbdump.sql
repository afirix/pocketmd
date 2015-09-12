CREATE DATABASE  IF NOT EXISTS `pocketmd` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `pocketmd`;
-- MySQL dump 10.13  Distrib 5.6.19, for osx10.7 (i386)
--
-- Host: pocketmd.cyrbuzuswrh6.us-east-1.rds.amazonaws.com    Database: pocketmd
-- ------------------------------------------------------
-- Server version	5.6.19-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `authorities`
--

DROP TABLE IF EXISTS `authorities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `authorities` (
  `username` varchar(50) NOT NULL,
  `authority` varchar(50) NOT NULL,
  UNIQUE KEY `ix_auth_username` (`username`,`authority`),
  CONSTRAINT `fk_authorities_users` FOREIGN KEY (`username`) REFERENCES `users` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `authorities`
--

LOCK TABLES `authorities` WRITE;
/*!40000 ALTER TABLE `authorities` DISABLE KEYS */;
INSERT INTO `authorities` VALUES ('djterry','ROLE_PATIENT'),('drbrown','ROLE_DOCTOR'),('drgreen','ROLE_DOCTOR'),('drwhite','ROLE_DOCTOR'),('kathyz','ROLE_PATIENT'),('laguna','ROLE_PATIENT'),('larson','ROLE_PATIENT'),('michelle','ROLE_PATIENT'),('rodney','ROLE_PATIENT'),('toddbear','ROLE_PATIENT');
/*!40000 ALTER TABLE `authorities` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `checkin_medications`
--

DROP TABLE IF EXISTS `checkin_medications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `checkin_medications` (
  `checkin_medication_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `checkin_id` bigint(20) NOT NULL,
  `medication_name` varchar(50) NOT NULL,
  `medication_taken` bit(1) NOT NULL,
  `intake_time` datetime DEFAULT NULL,
  PRIMARY KEY (`checkin_medication_id`),
  UNIQUE KEY `ix_checkin_id_medication_name` (`checkin_id`,`medication_name`),
  KEY `fk_checkinmeds_checkins_idx` (`checkin_id`),
  CONSTRAINT `fk_checkinmeds_checkins` FOREIGN KEY (`checkin_id`) REFERENCES `checkins` (`checkin_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=111 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `checkin_medications`
--

LOCK TABLES `checkin_medications` WRITE;
/*!40000 ALTER TABLE `checkin_medications` DISABLE KEYS */;
INSERT INTO `checkin_medications` VALUES (37,13,'Mortobidone','\0',NULL),(38,13,'Naflon','\0',NULL),(39,13,'Oxycodone','\0',NULL),(40,13,'Vicodin','\0',NULL),(77,23,'Mortobidone','\0',NULL),(78,23,'Naflon','\0',NULL),(79,23,'Oxycodone','\0',NULL),(80,23,'Vicodin','\0',NULL),(85,26,'Mortobidone','\0',NULL),(86,26,'Naflon','\0',NULL),(87,26,'Oxycodone','\0',NULL),(88,26,'Vicodin','\0',NULL),(89,27,'Mortobidone','\0',NULL),(90,27,'Naflon','\0',NULL),(91,27,'Oxycodone','\0',NULL),(92,27,'Vicodin','\0',NULL),(93,28,'Mortobidone','\0',NULL),(94,28,'Naflon','\0',NULL),(95,28,'Oxycodone','\0',NULL),(96,28,'Vicodin','\0',NULL),(97,29,'Mortobidone','\0',NULL),(98,29,'Oxycodone','\0',NULL),(99,29,'Vicodin','\0',NULL),(100,29,'Vordone','\0',NULL),(101,30,'Mortobidone','\0',NULL),(102,30,'Oxycodone','\0',NULL),(103,30,'Vicodin','\0',NULL),(104,30,'Vordone','\0',NULL),(105,31,'Oxycodone','','2014-11-23 22:41:16'),(106,31,'Vicodin','\0',NULL),(107,32,'Oxycodone','','2014-11-26 20:40:28'),(108,32,'Vicodin','','2014-11-26 19:35:37'),(109,33,'Acetaminophen','\0',NULL),(110,33,'Vicodin','\0',NULL);
/*!40000 ALTER TABLE `checkin_medications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `checkins`
--

DROP TABLE IF EXISTS `checkins`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `checkins` (
  `checkin_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `patient_id` bigint(20) NOT NULL,
  `pain_severity` enum('WELL_CONTROLLED','MODERATE','SEVERE') NOT NULL,
  `eating_problems` enum('NO','SOME','CANT_EAT') NOT NULL,
  `time` datetime NOT NULL,
  `file_s3_url` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`checkin_id`),
  KEY `fk_checkins_patients_idx` (`patient_id`),
  KEY `ix_patient_id` (`patient_id`),
  CONSTRAINT `fk_checkins_patients` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`patient_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `checkins`
--

LOCK TABLES `checkins` WRITE;
/*!40000 ALTER TABLE `checkins` DISABLE KEYS */;
INSERT INTO `checkins` VALUES (13,1,'SEVERE','CANT_EAT','2014-11-19 07:39:42',NULL),(23,1,'SEVERE','CANT_EAT','2014-11-19 20:45:06',NULL),(26,1,'SEVERE','CANT_EAT','2014-11-19 20:47:24',NULL),(27,1,'WELL_CONTROLLED','NO','2014-11-22 20:30:39',NULL),(28,1,'WELL_CONTROLLED','NO','2014-11-22 20:36:47',NULL),(29,1,'WELL_CONTROLLED','NO','2014-11-22 23:28:55',NULL),(30,1,'WELL_CONTROLLED','NO','2014-11-22 23:32:35',NULL),(31,1,'WELL_CONTROLLED','NO','2014-11-23 22:41:19',NULL),(32,1,'MODERATE','SOME','2014-11-26 21:41:48',NULL),(33,1,'SEVERE','CANT_EAT','2014-11-26 21:45:04',NULL);
/*!40000 ALTER TABLE `checkins` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doctors`
--

DROP TABLE IF EXISTS `doctors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `doctors` (
  `doctor_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  PRIMARY KEY (`doctor_id`),
  UNIQUE KEY `ix_username` (`username`),
  KEY `fk_doctors_users_idx` (`username`),
  CONSTRAINT `fk_doctors_users` FOREIGN KEY (`username`) REFERENCES `users` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `doctors`
--

LOCK TABLES `doctors` WRITE;
/*!40000 ALTER TABLE `doctors` DISABLE KEYS */;
INSERT INTO `doctors` VALUES (1,'drwhite','Sean','White'),(2,'drbrown','Morgan','Brown'),(3,'drgreen','Helen','Green');
/*!40000 ALTER TABLE `doctors` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gcm_registrations`
--

DROP TABLE IF EXISTS `gcm_registrations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gcm_registrations` (
  `record_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `registration_id` varbinary(4096) NOT NULL,
  `registration_id_hash` binary(32) NOT NULL,
  PRIMARY KEY (`record_id`),
  KEY `ix_registration_id_hash` (`registration_id_hash`),
  KEY `ix_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gcm_registrations`
--

LOCK TABLES `gcm_registrations` WRITE;
/*!40000 ALTER TABLE `gcm_registrations` DISABLE KEYS */;
INSERT INTO `gcm_registrations` VALUES (1,1,'APA91bHxLsvdk9DL0JQ9j_aqYbdRHc89ocr0VPx6RWzwi7hw9x-cDXKuuFZ-6QMpuRrws0oGI4_VAZqMSIGIboViipvYqJhblLg6a7DaxElRiIz1r_6i5TIohEVl-6tj1Zb1v8NqVJSOmT2WprECh2KC-BtjsmvawEXpbEEou1VTpF60VQOxOUs','\\ÉWnqhÚÅJ›^}|b’nP“ã⁄y%T}Ò»§ü3');
/*!40000 ALTER TABLE `gcm_registrations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `patients`
--

DROP TABLE IF EXISTS `patients`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `patients` (
  `patient_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `date_of_birth` date NOT NULL,
  `record_number` bigint(20) NOT NULL,
  `doctor_id` bigint(20) NOT NULL,
  PRIMARY KEY (`patient_id`),
  UNIQUE KEY `ix_username` (`username`),
  KEY `fk_patients_users_idx` (`username`),
  KEY `ix_fullname` (`first_name`,`last_name`),
  KEY `fk_patients_doctors_idx` (`doctor_id`),
  CONSTRAINT `fk_patients_doctors` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`doctor_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_patients_users` FOREIGN KEY (`username`) REFERENCES `users` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `patients`
--

LOCK TABLES `patients` WRITE;
/*!40000 ALTER TABLE `patients` DISABLE KEYS */;
INSERT INTO `patients` VALUES (1,'toddbear','Todd','Bear','1978-03-23',17445,1),(2,'michelle','Michelle','Reyes','1953-04-04',33440,2),(3,'rodney','Rod','Palawen','1947-02-21',98243,1),(4,'larson','Larry','Sonnenheim','1962-07-11',58827,3),(5,'laguna','Tracy','Smith','1980-11-22',14663,3),(12,'djterry','Terrence','Spock','1974-01-05',47727,3),(13,'kathyz','Kathy','Zimmerman','1954-09-12',73872,2);
/*!40000 ALTER TABLE `patients` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prescriptions`
--

DROP TABLE IF EXISTS `prescriptions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `prescriptions` (
  `prescription_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `patient_id` bigint(20) NOT NULL,
  `medication_name` varchar(50) NOT NULL,
  PRIMARY KEY (`prescription_id`),
  KEY `fk_prescriptions_patients_idx` (`patient_id`),
  CONSTRAINT `fk_prescriptions_patients` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`patient_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prescriptions`
--

LOCK TABLES `prescriptions` WRITE;
/*!40000 ALTER TABLE `prescriptions` DISABLE KEYS */;
INSERT INTO `prescriptions` VALUES (2,2,'Lortab'),(3,3,'Acetaminophen'),(4,3,'Oxycodone'),(5,4,'Ibuprofen'),(6,4,'Naflon'),(7,4,'Tapentadol'),(8,5,'Oxycodone'),(18,1,'Vicodin'),(27,1,'Acetaminophen');
/*!40000 ALTER TABLE `prescriptions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `username` varchar(50) NOT NULL,
  `password` varchar(70) NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('djterry','$2a$12$pnSTsLxPB9Zl0xRD6cWwGuHxUoo0TIYIh2HnmkKV1Z3/woVO6MEEa',1),('drbrown','$2a$12$Kjjqxdz9Kp0WD7Y6Mwn5huKRw8Bi.sqXpUFVUDbof48CrlGummiRO',1),('drgreen','$2a$12$qpQxBgGndszosY0VZSPjZu/MXXRItXRw5RuxXMm9B8MsUFfxk1E.a',1),('drwhite','$2a$12$O4DILgze5s1neXCrgBdNrO/TsnO0Kyubedt2XCa02rPa1ueIr1ZCO',1),('kathyz','$2a$12$B4w..rx9GngvoYFSYiIacuYj8Bac1GHdibAZRYiSJu8xQGyexnm8G',1),('laguna','$2a$12$NgqcE..86hX2JaGcz8uegOL3qDXf0ani8xtYQz5pPsTECN1SSBQYu',1),('larson','$2a$12$ZNMwWrWLuhqU7OMyN6tjm.FNa9xYH0Vm1Pj5QEBAM/Pw2rfdxaQ3y',1),('michelle','$2a$12$zCC0YkI8DCaAYtLH0D404.IroU329K4zR5QBuHEi.RyHSwBbh1T6C',1),('rodney','$2a$12$HbsOxCjj9M9/i3dbeFTWdOrAMh0Ra90Ms1BsEUDCgQTz4V2hrX2MO',1),('toddbear','$2a$12$dJkGFhI4oYs4xskoIopKJ.UWVrJHxooRdzNI73B3/Et5LyBKC1SqS',1);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-09-11 19:21:15
