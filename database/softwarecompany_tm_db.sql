-- MySQL dump 10.13  Distrib 8.0.15, for Win64 (x86_64)
--
-- Host: localhost    Database: softwarecompany_tm
-- ------------------------------------------------------
-- Server version	8.0.15

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
 SET NAMES utf8 ;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `admin`
--

DROP TABLE IF EXISTS `admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `admin` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `firstName` varchar(50) NOT NULL,
  `lastName` varchar(50) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `email` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin`
--

LOCK TABLES `admin` WRITE;
/*!40000 ALTER TABLE `admin` DISABLE KEYS */;
INSERT INTO `admin` VALUES (1,'Endrit','Gjocaj','Endrit123','jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=','endritgjocaj@example.com');
/*!40000 ALTER TABLE `admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employees`
--

DROP TABLE IF EXISTS `employees`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `employees` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `firstName` varchar(50) DEFAULT NULL,
  `lastName` varchar(50) DEFAULT NULL,
  `username` varchar(50) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `gender` char(1) NOT NULL,
  `sector` varchar(50) NOT NULL,
  `position` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=164 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employees`
--

LOCK TABLES `employees` WRITE;
/*!40000 ALTER TABLE `employees` DISABLE KEYS */;
INSERT INTO `employees` VALUES (117,'Arber','Murati','Arber117','arbermurati@example.com','M','Mobile App','Back-End'),(119,'Bleona','Gjocaj','Bleona119','bleonagjocaj@example.com','F','Mobile App','Full Stack'),(120,'Dalmat','Gjoci','Dalmat120','dalmatgjoci@example.com','M','Mobile App','Front-End'),(121,'Dua','Lipa','Dua121','dualipa@example.com','F','Web App','Full Stack'),(122,'Albatros','Sherifi','Albatros122','albatrossherifi@example.com','M','Desktop App','Back-End'),(123,'Nderim','Bytyqi','Nderim123','nderimbytyqi2@example.com','M','Desktop App','Back-End'),(125,'Edin','Baxhaku','Edin125','edinbaxhaku2@example.com','M','Web App','Front-End'),(135,'Endrit','Gjocaj','Endrit135','endritgjocaj@example.com','M','Web App','Full Stack');
/*!40000 ALTER TABLE `employees` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employees_tasks`
--

DROP TABLE IF EXISTS `employees_tasks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `employees_tasks` (
  `employeeID` int(11) NOT NULL,
  `taskID` int(11) NOT NULL,
  PRIMARY KEY (`employeeID`,`taskID`),
  KEY `Constr_employees_tasks_taskID_fk` (`taskID`),
  CONSTRAINT `employees_tasks_ibfk_1` FOREIGN KEY (`taskID`) REFERENCES `tasks` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `employees_tasks_ibfk_2` FOREIGN KEY (`employeeID`) REFERENCES `employees` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=ascii;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employees_tasks`
--

LOCK TABLES `employees_tasks` WRITE;
/*!40000 ALTER TABLE `employees_tasks` DISABLE KEYS */;
INSERT INTO `employees_tasks` VALUES (135,324),(119,329),(119,331),(123,335),(121,394),(135,394),(125,417),(135,417),(122,422),(123,422),(121,425),(125,425),(135,429);
/*!40000 ALTER TABLE `employees_tasks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employees_technologies`
--

DROP TABLE IF EXISTS `employees_technologies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `employees_technologies` (
  `employeeID` int(11) NOT NULL,
  `technologyID` int(11) NOT NULL,
  PRIMARY KEY (`employeeID`,`technologyID`),
  KEY `Constr_employees_technologies_technologyID_fk` (`technologyID`),
  CONSTRAINT `employees_technologies_ibfk_1` FOREIGN KEY (`technologyID`) REFERENCES `technologies` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `employees_technologies_ibfk_2` FOREIGN KEY (`employeeID`) REFERENCES `employees` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=ascii;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employees_technologies`
--

LOCK TABLES `employees_technologies` WRITE;
/*!40000 ALTER TABLE `employees_technologies` DISABLE KEYS */;
INSERT INTO `employees_technologies` VALUES (119,124),(122,124),(123,124),(135,124),(120,127),(120,131),(125,131),(117,132),(123,132),(117,133),(122,133),(119,135),(121,135),(125,135),(1118,135),(120,137),(125,137),(123,140),(135,164),(125,169),(135,169),(121,179),(135,180);
/*!40000 ALTER TABLE `employees_technologies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `projects`
--

DROP TABLE IF EXISTS `projects`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `projects` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `projectName` varchar(50) DEFAULT NULL,
  `sector` varchar(50) NOT NULL,
  `client` varchar(50) NOT NULL,
  `startDate` varchar(50) NOT NULL,
  `dueDate` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `projectName` (`projectName`)
) ENGINE=InnoDB AUTO_INCREMENT=134 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `projects`
--

LOCK TABLES `projects` WRITE;
/*!40000 ALTER TABLE `projects` DISABLE KEYS */;
INSERT INTO `projects` VALUES (125,'ChatMe','Web App','UBT','19/07/2020','28/02/2021'),(128,'Festival Finder','Mobile App','Alpha Solutions','06/08/2020','15/02/2021'),(129,'Crazy Taxi','Desktop App','Alpha Solutions','06/08/2020','28/02/2021'),(131,'iMusic','Web App','Alpha Solutions','15/12/2020','31/03/2021');
/*!40000 ALTER TABLE `projects` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `projects_technologies`
--

DROP TABLE IF EXISTS `projects_technologies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `projects_technologies` (
  `projectID` int(11) NOT NULL,
  `technologyID` int(11) NOT NULL,
  PRIMARY KEY (`projectID`,`technologyID`),
  KEY `Constr_projects_technologies_employeeID_fk` (`technologyID`),
  CONSTRAINT `projects_technologies_ibfk_1` FOREIGN KEY (`projectID`) REFERENCES `projects` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `projects_technologies_ibfk_2` FOREIGN KEY (`technologyID`) REFERENCES `technologies` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=ascii;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `projects_technologies`
--

LOCK TABLES `projects_technologies` WRITE;
/*!40000 ALTER TABLE `projects_technologies` DISABLE KEYS */;
INSERT INTO `projects_technologies` VALUES (125,124),(129,124),(131,127),(129,132),(128,135),(131,135),(131,169),(125,178),(128,178),(125,179);
/*!40000 ALTER TABLE `projects_technologies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tasks`
--

DROP TABLE IF EXISTS `tasks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `tasks` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(50) NOT NULL,
  `project` int(11) DEFAULT NULL,
  `startDate` varchar(50) DEFAULT NULL,
  `endDate` varchar(50) DEFAULT NULL,
  `status` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `project` (`project`),
  CONSTRAINT `tasks_ibfk_2` FOREIGN KEY (`project`) REFERENCES `projects` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=437 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tasks`
--

LOCK TABLES `tasks` WRITE;
/*!40000 ALTER TABLE `tasks` DISABLE KEYS */;
INSERT INTO `tasks` VALUES (324,'Login',125,'02/08/2020','07/08/2020','Done'),(328,'Voice Communication',125,'12/08/2020',NULL,'Not Started'),(329,'Dashboard',128,'04/08/2020','06/08/2020','Done'),(331,'Festival listing',128,'16/08/2020',NULL,'In Progress'),(332,'Ticket coordination',128,'20/09/2020',NULL,'Not Started'),(335,'Traffic and map',129,'12/09/2020',NULL,'Not Started'),(394,'Chat',125,'11/12/2020',NULL,'Not Started'),(417,'Most listened artist',131,'15/12/2020','25/01/2021','Done'),(422,'Car design',129,'14/12/2020',NULL,'In Progress'),(425,'Login Page',131,'15/12/2020',NULL,'In Progress'),(429,'Location Page',131,'15/01/2021','27/01/2021','Done');
/*!40000 ALTER TABLE `tasks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `technologies`
--

DROP TABLE IF EXISTS `technologies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `technologies` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `technology` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=181 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `technologies`
--

LOCK TABLES `technologies` WRITE;
/*!40000 ALTER TABLE `technologies` DISABLE KEYS */;
INSERT INTO `technologies` VALUES (124,'Java'),(127,'JavaScript'),(131,'CSS'),(132,'PHP'),(133,'Laravel'),(135,'Angular'),(137,'HTML'),(140,'Spring Boot'),(164,'JavaFX'),(165,'C++'),(166,'Python'),(169,'React'),(170,'Node.js'),(176,'ss'),(178,'Java'),(179,'C#'),(180,'MySQL');
/*!40000 ALTER TABLE `technologies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

SET FOREIGN_KEY_CHECKS=1;
DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `user` (
  `id` int(11) NOT NULL,
  `username` varchar(50) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  `employees_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `employees_id` (`employees_id`),
  CONSTRAINT `us_emp` FOREIGN KEY (`employees_id`) REFERENCES `employees` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (117,'Arber117','jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=',117),(119,'Bleona119','jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=',119),(120,'Dalmat120','jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=',120),(121,'Dua121','jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=',121),(122,'Albatros122','jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=',122),(123,'Nderim123','jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=',123),(125,'Edin125','jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=',125),(135,'Endrit135','jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=',135);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
SET FOREIGN_KEY_CHECKS=0;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-02-04 18:09:28
