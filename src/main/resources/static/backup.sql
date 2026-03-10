-- BookVillage Database Backup
-- Generated: 2024-01-15 03:00:00
-- Server: localhost:3407
-- Database: bookvillage_mock

CREATE DATABASE IF NOT EXISTS `bookvillage_mock`;
USE `bookvillage_mock`;

-- Table: users
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `role` varchar(20) DEFAULT 'USER',
  `status` varchar(20) DEFAULT 'ACTIVE',
  PRIMARY KEY (`id`)
);

-- Sample admin account (password: admin1234)
INSERT INTO `users` (`id`, `username`, `email`, `password`, `name`, `role`, `status`)
VALUES (1, 'admin', 'admin@bookvillage.com', '7c4a8d09ca3762af61e59520943dc26494f8941b', 'Administrator', 'ADMIN', 'ACTIVE');

-- Sample user accounts
INSERT INTO `users` (`id`, `username`, `email`, `password`, `name`, `role`, `status`)
VALUES
(2, 'user001', 'user001@example.com', 'e38ad214943daad1d64c102faec29de4afe9da3d', 'Hong Gil-dong', 'USER', 'ACTIVE'),
(3, 'user002', 'user002@example.com', 'e38ad214943daad1d64c102faec29de4afe9da3d', 'Kim Cheol-su', 'USER', 'ACTIVE');
