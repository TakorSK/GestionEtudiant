-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : sam. 05 avr. 2025 à 01:44
-- Version du serveur : 10.4.32-MariaDB
-- Version de PHP : 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `prj`
--

-- --------------------------------------------------------

--
-- Structure de la table `club`
--

CREATE TABLE `club` (
  `id` int(3) NOT NULL,
  `name` varchar(30) NOT NULL,
  `description` text DEFAULT NULL,
  `founded_date` date DEFAULT NULL,
  `uni_id` int(3) DEFAULT NULL
) ;

-- --------------------------------------------------------

--
-- Structure de la table `section`
--

CREATE TABLE `section` (
  `id` int(5) NOT NULL,
  `name` varchar(10) NOT NULL,
  `group_name` varchar(10) NOT NULL,
  `uni_id` int(3) DEFAULT NULL,
  `academic_year` varchar(9) DEFAULT NULL
) ;

-- --------------------------------------------------------

--
-- Structure de la table `student`
--

CREATE TABLE `student` (
  `id` int(8) NOT NULL,
  `section_id` int(5) DEFAULT NULL,
  `email` varchar(50) NOT NULL,
  `full_name` varchar(50) NOT NULL,
  `uni_id` int(3) DEFAULT NULL,
  `club_id` int(3) DEFAULT NULL,
  `is_online` tinyint(1) DEFAULT 0,
  `registration_date` date DEFAULT curdate(),
  `last_login` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ;

--
-- Déchargement des données de la table `student`
--

INSERT INTO `student` (`id`, `section_id`, `email`, `full_name`, `uni_id`, `club_id`, `is_online`, `registration_date`, `last_login`) VALUES
(10003, NULL, 'cs101@student.edu', 'Charlie Brown', 1, NULL, 0, '2025-04-05', '2025-04-04 23:37:06'),
(10005, NULL, 'techclub@student.edu', 'Emily Davis', 1, NULL, 0, '2025-04-05', '2025-04-04 23:39:48');

-- --------------------------------------------------------

--
-- Structure de la table `timetable`
--

CREATE TABLE `timetable` (
  `id` int(5) NOT NULL,
  `section_id` int(5) NOT NULL,
  `day_of_week` enum('Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday') NOT NULL,
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  `subject` varchar(50) NOT NULL,
  `room` varchar(20) DEFAULT NULL,
  `professor` varchar(50) DEFAULT NULL
) ;

-- --------------------------------------------------------

--
-- Structure de la table `uni`
--

CREATE TABLE `uni` (
  `id` int(3) NOT NULL,
  `name` varchar(30) NOT NULL,
  `location` varchar(50) DEFAULT NULL,
  `established_year` year(4) DEFAULT NULL,
  `website` varchar(100) DEFAULT NULL
) ;

--
-- Déchargement des données de la table `uni`
--

INSERT INTO `uni` (`id`, `name`, `location`, `established_year`, `website`) VALUES
(1, 'jlj', 'jlj', '2004', 'm');

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `club`
--
ALTER TABLE `club`
  ADD PRIMARY KEY (`id`),
  ADD KEY `uni_id` (`uni_id`);

--
-- Index pour la table `section`
--
ALTER TABLE `section`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_section_group` (`name`,`group_name`,`uni_id`),
  ADD KEY `uni_id` (`uni_id`);

--
-- Index pour la table `student`
--
ALTER TABLE `student`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `section_id` (`section_id`),
  ADD KEY `uni_id` (`uni_id`),
  ADD KEY `club_id` (`club_id`);

--
-- Index pour la table `timetable`
--
ALTER TABLE `timetable`
  ADD PRIMARY KEY (`id`),
  ADD KEY `section_id` (`section_id`);

--
-- Index pour la table `uni`
--
ALTER TABLE `uni`
  ADD PRIMARY KEY (`id`);

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `club`
--
ALTER TABLE `club`
  ADD CONSTRAINT `club_ibfk_1` FOREIGN KEY (`uni_id`) REFERENCES `uni` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `section`
--
ALTER TABLE `section`
  ADD CONSTRAINT `section_ibfk_1` FOREIGN KEY (`uni_id`) REFERENCES `uni` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `student`
--
ALTER TABLE `student`
  ADD CONSTRAINT `student_ibfk_1` FOREIGN KEY (`section_id`) REFERENCES `section` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `student_ibfk_2` FOREIGN KEY (`uni_id`) REFERENCES `uni` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `student_ibfk_3` FOREIGN KEY (`club_id`) REFERENCES `club` (`id`) ON DELETE SET NULL;

--
-- Contraintes pour la table `timetable`
--
ALTER TABLE `timetable`
  ADD CONSTRAINT `timetable_ibfk_1` FOREIGN KEY (`section_id`) REFERENCES `section` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
