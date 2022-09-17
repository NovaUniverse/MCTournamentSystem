-- phpMyAdmin SQL Dump
-- version 4.5.4.1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Sep 17, 2022 at 12:10 PM
-- Server version: 5.7.11
-- PHP Version: 7.2.7

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `tournamentsystem`
--
CREATE DATABASE IF NOT EXISTS `tournamentsystem` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin;
USE `tournamentsystem`;

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `increment_player_score` (IN `player_uuid` VARCHAR(36), IN `score_to_add` INT)  NO SQL
BEGIN
	SELECT score INTO @old_score FROM players WHERE uuid = player_uuid LIMIT 1;
    	UPDATE players SET score = @old_score + score_to_add WHERE uuid = player_uuid;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `increment_team_score` (IN `team_id` INT, IN `score_to_add` INT)  NO SQL
BEGIN
	SELECT score INTO @old_score FROM `teams` WHERE team_number = team_id LIMIT 1;
    	UPDATE teams SET score = @old_score + score_to_add WHERE team_number = team_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `reset_data` ()  BEGIN
	UPDATE teams SET score = 0;
    
    DELETE FROM players;
    
    UPDATE tsdata SET data_value = null WHERE data_key = "active_server";
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `set_player_team` (IN `player_uuid` VARCHAR(36), IN `player_username` VARCHAR(16), IN `player_team_number` INT, IN `metadata` TEXT)  BEGIN
	IF NOT EXISTS (SELECT id FROM players WHERE uuid = player_uuid) THEN
		INSERT INTO players (uuid, username, metadata) VALUES (player_uuid, player_username, metadata);
    END IF;
    
    UPDATE players SET metadata = metadata, username = player_username, team_number = player_team_number WHERE uuid = player_uuid;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `chat_log`
--

CREATE TABLE `chat_log` (
  `id` int(10) UNSIGNED NOT NULL,
  `session_id` varchar(36) COLLATE utf8_bin NOT NULL,
  `uuid` varchar(36) COLLATE utf8_bin NOT NULL,
  `username` varchar(16) COLLATE utf8_bin NOT NULL,
  `content` text COLLATE utf8_bin NOT NULL,
  `sent_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `luckperms_actions`
--

CREATE TABLE `luckperms_actions` (
  `id` int(11) NOT NULL,
  `time` bigint(20) NOT NULL,
  `actor_uuid` varchar(36) NOT NULL,
  `actor_name` varchar(100) NOT NULL,
  `type` char(1) NOT NULL,
  `acted_uuid` varchar(36) NOT NULL,
  `acted_name` varchar(36) NOT NULL,
  `action` varchar(300) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `luckperms_groups`
--

CREATE TABLE `luckperms_groups` (
  `name` varchar(36) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `luckperms_groups`
--

INSERT INTO `luckperms_groups` (`name`) VALUES
('commentator'),
('default'),
('helper'),
('host'),
('moderator'),
('staff');

-- --------------------------------------------------------

--
-- Table structure for table `luckperms_group_permissions`
--

CREATE TABLE `luckperms_group_permissions` (
  `id` int(11) NOT NULL,
  `name` varchar(36) NOT NULL,
  `permission` varchar(200) NOT NULL,
  `value` tinyint(1) NOT NULL,
  `server` varchar(36) NOT NULL,
  `world` varchar(36) NOT NULL,
  `expiry` int(11) NOT NULL,
  `contexts` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `luckperms_group_permissions`
--

INSERT INTO `luckperms_group_permissions` (`id`, `name`, `permission`, `value`, `server`, `world`, `expiry`, `contexts`) VALUES
(1, 'moderator', 'group.helper', 1, 'global', 'global', 0, '{}'),
(2, 'moderator', 'displayname.Moderator', 1, 'global', 'global', 0, '{}'),
(4, 'moderator', 'weight.100', 1, 'global', 'global', 0, '{}'),
(5, 'helper', 'group.default', 1, 'global', 'global', 0, '{}'),
(6, 'helper', 'weight.10', 1, 'global', 'global', 0, '{}'),
(8, 'helper', 'displayname.Helper', 1, 'global', 'global', 0, '{}'),
(9, 'staff', 'displayname.Staff', 1, 'global', 'global', 0, '{}'),
(10, 'staff', 'group.moderator', 1, 'global', 'global', 0, '{}'),
(12, 'staff', 'weight.1000', 1, 'global', 'global', 0, '{}'),
(13, 'host', 'weight.10000', 1, 'global', 'global', 0, '{}'),
(14, 'host', 'displayname.Host', 1, 'global', 'global', 0, '{}'),
(16, 'host', 'group.staff', 1, 'global', 'global', 0, '{}'),
(17, 'default', 'prefix.0.§1Player §r', 1, 'global', 'global', 0, '{}'),
(20, 'helper', 'novacore.loglevel.auto.warn', 1, 'global', 'global', 0, '{}'),
(21, 'staff', 'holograms.*', 1, 'global', 'global', 0, '{}'),
(22, 'staff', 'bukkit.*', 1, 'global', 'global', 0, '{}'),
(23, 'staff', 'novacore.command.*', 1, 'global', 'global', 0, '{}'),
(24, 'staff', 'fawe.*', 1, 'global', 'global', 0, '{}'),
(25, 'staff', 'tournamentcore.command.*', 1, 'global', 'global', 0, '{}'),
(26, 'staff', 'vault.update', 1, 'global', 'global', 0, '{}'),
(28, 'staff', 'vault.admin', 1, 'global', 'global', 0, '{}'),
(29, 'staff', 'minecraft.command.*', 1, 'global', 'global', 0, '{}'),
(30, 'staff', 'protocol.*', 1, 'global', 'global', 0, '{}'),
(31, 'staff', 'citizens.citizens.*', 1, 'global', 'global', 0, '{}'),
(32, 'staff', 'supervanish.*', 1, 'global', 'global', 0, '{}'),
(33, 'staff', 'signedit.admin', 1, 'global', 'global', 0, '{}'),
(34, 'staff', 'luckperms.*', 1, 'global', 'global', 0, '{}'),
(35, 'staff', 'worldedit.*', 1, 'global', 'global', 0, '{}'),
(37, 'moderator', 'prefix.100.§e§lModerator §r', 1, 'global', 'global', 0, '{}'),
(38, 'helper', 'prefix.10.§d§lHelper §r', 1, 'global', 'global', 0, '{}'),
(39, 'staff', 'prefix.1000.§b§lStaff §r', 1, 'global', 'global', 0, '{}'),
(40, 'host', 'prefix.10000.§a§lHost §r', 1, 'global', 'global', 0, '{}'),
(41, 'staff', 'bungeecord.command.*', 1, 'global', 'global', 0, '{}'),
(42, 'moderator', 'bungeecord.command.list', 1, 'global', 'global', 0, '{}'),
(43, 'moderator', 'bungeecord.command.find', 1, 'global', 'global', 0, '{}'),
(44, 'moderator', 'bungeecord.command.send', 1, 'global', 'global', 0, '{}'),
(45, 'moderator', 'bungeecord.command.ip', 1, 'global', 'global', 0, '{}'),
(46, 'moderator', 'bungeecord.command.server', 1, 'global', 'global', 0, '{}'),
(47, 'moderator', 'messages.command.socialspy', 1, 'global', 'global', 0, '{}'),
(49, 'staff', 'messages.command.socialspy', 1, 'global', 'global', 0, '{}'),
(50, 'host', 'bungeemessages.command.socialspy', 1, 'global', 'global', 0, '{}'),
(51, 'moderator', 'bungeemessages.command.socialspy', 1, 'global', 'global', 0, '{}'),
(52, 'moderator', 'tournamentcore.autosocialspy', 1, 'global', 'global', 0, '{}'),
(53, 'helper', 'bukkit.command.version', 1, 'global', 'global', 0, '{}'),
(54, 'helper', 'minecraft.command.me', 1, 'global', 'global', 0, '{}'),
(55, 'helper', 'bukkit.command.plugins', 1, 'global', 'global', 0, '{}'),
(56, 'default', 'bukkit.command.plugins', 0, 'global', 'global', 0, '{}'),
(57, 'default', 'bukkit.command.version', 0, 'global', 'global', 0, '{}'),
(58, 'default', 'minecraft.command.me', 0, 'global', 'global', 0, '{}'),
(59, 'default', 'minecraft.command.tell', 0, 'global', 'global', 0, '{}'),
(60, 'moderator', 'tournamentcore.command.yborder', 1, 'global', 'global', 0, '{}'),
(61, 'moderator', 'ab.changeReason', 1, 'global', 'global', 0, '{}'),
(62, 'moderator', 'ab.notify.note', 1, 'global', 'global', 0, '{}'),
(63, 'moderator', 'ab.undoNotify.note', 1, 'global', 'global', 0, '{}'),
(64, 'moderator', 'ab.warns.other', 1, 'global', 'global', 0, '{}'),
(65, 'moderator', 'ab.ban.perma', 1, 'global', 'global', 0, '{}'),
(66, 'moderator', 'ab.ban.undo', 1, 'global', 'global', 0, '{}'),
(67, 'moderator', 'ab.note.use', 1, 'global', 'global', 0, '{}'),
(68, 'moderator', 'ab.check.ip', 1, 'global', 'global', 0, '{}'),
(69, 'moderator', 'ab.notify.tempmute', 1, 'global', 'global', 0, '{}'),
(70, 'moderator', 'ab.ban.temp', 1, 'global', 'global', 0, '{}'),
(71, 'moderator', 'ab.notify.warn', 1, 'global', 'global', 0, '{}'),
(72, 'moderator', 'ab.reload', 1, 'global', 'global', 0, '{}'),
(73, 'moderator', 'ab.ipban.temp', 1, 'global', 'global', 0, '{}'),
(74, 'moderator', 'ab.check', 1, 'global', 'global', 0, '{}'),
(75, 'moderator', 'ab.mute.undo', 1, 'global', 'global', 0, '{}'),
(76, 'moderator', 'ab.undoNotify.mute', 1, 'global', 'global', 0, '{}'),
(77, 'moderator', 'ab.warn.temp', 1, 'global', 'global', 0, '{}'),
(78, 'moderator', 'ab.notify.kick', 1, 'global', 'global', 0, '{}'),
(79, 'moderator', 'ab.undoNotify.warn', 1, 'global', 'global', 0, '{}'),
(80, 'moderator', 'ab.ipban.perma', 1, 'global', 'global', 0, '{}'),
(81, 'moderator', 'ab.note.undo', 1, 'global', 'global', 0, '{}'),
(82, 'moderator', 'ab.banlist', 1, 'global', 'global', 0, '{}'),
(83, 'moderator', 'ab.kick.use', 1, 'global', 'global', 0, '{}'),
(84, 'moderator', 'ab.all.undo', 1, 'global', 'global', 0, '{}'),
(85, 'moderator', 'ab.warn.perma', 1, 'global', 'global', 0, '{}'),
(86, 'moderator', 'ab.systemprefs', 1, 'global', 'global', 0, '{}'),
(87, 'moderator', 'ab.mute.temp', 1, 'global', 'global', 0, '{}'),
(88, 'moderator', 'ab.mute.perma', 1, 'global', 'global', 0, '{}'),
(89, 'moderator', 'ab.notify.ban', 1, 'global', 'global', 0, '{}'),
(90, 'moderator', 'ab.history', 1, 'global', 'global', 0, '{}'),
(91, 'moderator', 'ab.help', 1, 'global', 'global', 0, '{}'),
(92, 'moderator', 'ab.notify.tempipban', 1, 'global', 'global', 0, '{}'),
(93, 'moderator', 'ab.notify.mute', 1, 'global', 'global', 0, '{}'),
(94, 'moderator', 'ab.warn.undo', 1, 'global', 'global', 0, '{}'),
(95, 'moderator', 'ab.notes.other', 1, 'global', 'global', 0, '{}'),
(96, 'moderator', 'ab.notes.own', 1, 'global', 'global', 0, '{}'),
(97, 'moderator', 'ab.notify.ipban', 1, 'global', 'global', 0, '{}'),
(98, 'moderator', 'ab.notify.tempwarn', 1, 'global', 'global', 0, '{}'),
(99, 'moderator', 'ab.warns.own', 1, 'global', 'global', 0, '{}'),
(100, 'moderator', 'ab.undoNotify.ban', 1, 'global', 'global', 0, '{}'),
(101, 'moderator', 'ab.notify.tempban', 1, 'global', 'global', 0, '{}'),
(102, 'commentator', 'group.default', 1, 'global', 'global', 0, '{}'),
(103, 'commentator', 'weight.10', 1, 'global', 'global', 0, '{}'),
(105, 'commentator', 'tournamentcore.command.csp', 1, 'global', 'global', 0, '{}'),
(106, 'commentator', 'displayname.Commentator', 1, 'global', 'global', 0, '{}'),
(107, 'commentator', 'tournamentcore.command.ctp', 1, 'global', 'global', 0, '{}'),
(108, 'commentator', 'prefix.10.§b§lCommentator §r', 1, 'global', 'global', 0, '{}'),
(109, 'commentator', 'tournamentcore.commentator', 1, 'global', 'global', 0, '{}'),
(110, 'moderator', 'novauniverse.tntrun.aggressivedecay', 1, 'global', 'global', 0, '{}'),
(111, 'moderator', 'tournamentcore.command.respawnplayer', 1, 'global', 'global', 0, '{}'),
(112, 'moderator', 'mcf.restoredeathmessages', 1, 'global', 'global', 0, '{}'),
(113, 'moderator', 'tournamentcore.notify.swear', 1, 'global', 'global', 0, '{}'),
(114, 'moderator', 'tournamentsystem.command.timeout', 1, 'global', 'global', 0, '{}');

-- --------------------------------------------------------

--
-- Table structure for table `luckperms_messenger`
--

CREATE TABLE `luckperms_messenger` (
  `id` int(11) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `msg` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `luckperms_players`
--

CREATE TABLE `luckperms_players` (
  `uuid` varchar(36) NOT NULL,
  `username` varchar(16) NOT NULL,
  `primary_group` varchar(36) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `luckperms_tracks`
--

CREATE TABLE `luckperms_tracks` (
  `name` varchar(36) NOT NULL,
  `groups` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `luckperms_user_permissions`
--

CREATE TABLE `luckperms_user_permissions` (
  `id` int(11) NOT NULL,
  `uuid` varchar(36) NOT NULL,
  `permission` varchar(200) NOT NULL,
  `value` tinyint(1) NOT NULL,
  `server` varchar(36) NOT NULL,
  `world` varchar(36) NOT NULL,
  `expiry` int(11) NOT NULL,
  `contexts` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `players`
--

CREATE TABLE `players` (
  `id` int(10) UNSIGNED NOT NULL,
  `uuid` varchar(36) COLLATE utf8_bin NOT NULL,
  `username` varchar(16) COLLATE utf8_bin NOT NULL,
  `score` int(11) NOT NULL DEFAULT '0',
  `team_number` int(10) UNSIGNED DEFAULT NULL,
  `kills` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `metadata` text COLLATE utf8_bin
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `punishmenthistory`
--

CREATE TABLE `punishmenthistory` (
  `id` int(11) NOT NULL,
  `name` varchar(16) COLLATE utf8_bin DEFAULT NULL,
  `uuid` varchar(35) COLLATE utf8_bin DEFAULT NULL,
  `reason` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `operator` varchar(16) COLLATE utf8_bin DEFAULT NULL,
  `punishmentType` varchar(16) COLLATE utf8_bin DEFAULT NULL,
  `start` mediumtext COLLATE utf8_bin,
  `end` mediumtext COLLATE utf8_bin,
  `calculation` varchar(50) COLLATE utf8_bin DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `punishments`
--

CREATE TABLE `punishments` (
  `id` int(11) NOT NULL,
  `name` varchar(16) COLLATE utf8_bin DEFAULT NULL,
  `uuid` varchar(35) COLLATE utf8_bin DEFAULT NULL,
  `reason` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `operator` varchar(16) COLLATE utf8_bin DEFAULT NULL,
  `punishmentType` varchar(16) COLLATE utf8_bin DEFAULT NULL,
  `start` mediumtext COLLATE utf8_bin,
  `end` mediumtext COLLATE utf8_bin,
  `calculation` varchar(50) COLLATE utf8_bin DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `staff`
--

CREATE TABLE `staff` (
  `id` int(10) UNSIGNED NOT NULL,
  `uuid` varchar(36) COLLATE utf8_bin NOT NULL,
  `role` varchar(255) COLLATE utf8_bin NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `teams`
--

CREATE TABLE `teams` (
  `id` int(10) UNSIGNED NOT NULL,
  `team_number` int(10) UNSIGNED NOT NULL,
  `score` int(11) NOT NULL DEFAULT '0',
  `metadata` text COLLATE utf8_bin
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `tsdata`
--

CREATE TABLE `tsdata` (
  `id` int(10) UNSIGNED NOT NULL,
  `data_key` varchar(255) COLLATE utf8_bin NOT NULL COMMENT 'This is the key. Max 255 characters long',
  `data_value` text COLLATE utf8_bin COMMENT 'This is the value. Can be as long as you want'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='This table stores the configuration';

--
-- Dumping data for table `tsdata`
--

INSERT INTO `tsdata` (`id`, `data_key`, `data_value`) VALUES
(1, 'tournament_name', 'Tournament'),
(2, 'scoreboard_url', 'https://novauniverse.net'),
(3, 'active_server', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `whitelist`
--

CREATE TABLE `whitelist` (
  `id` int(10) UNSIGNED NOT NULL,
  `uuid` varchar(36) COLLATE utf8_bin NOT NULL COMMENT 'The uuid of the player'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `chat_log`
--
ALTER TABLE `chat_log`
  ADD PRIMARY KEY (`id`),
  ADD KEY `session_id` (`session_id`),
  ADD KEY `uuid` (`uuid`);

--
-- Indexes for table `luckperms_actions`
--
ALTER TABLE `luckperms_actions`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `luckperms_groups`
--
ALTER TABLE `luckperms_groups`
  ADD PRIMARY KEY (`name`);

--
-- Indexes for table `luckperms_group_permissions`
--
ALTER TABLE `luckperms_group_permissions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `luckperms_group_permissions_name` (`name`);

--
-- Indexes for table `luckperms_messenger`
--
ALTER TABLE `luckperms_messenger`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `luckperms_players`
--
ALTER TABLE `luckperms_players`
  ADD PRIMARY KEY (`uuid`),
  ADD KEY `luckperms_players_username` (`username`);

--
-- Indexes for table `luckperms_tracks`
--
ALTER TABLE `luckperms_tracks`
  ADD PRIMARY KEY (`name`);

--
-- Indexes for table `luckperms_user_permissions`
--
ALTER TABLE `luckperms_user_permissions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `luckperms_user_permissions_uuid` (`uuid`);

--
-- Indexes for table `players`
--
ALTER TABLE `players`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uuid` (`uuid`),
  ADD KEY `team_number` (`team_number`),
  ADD KEY `username` (`username`);

--
-- Indexes for table `punishmenthistory`
--
ALTER TABLE `punishmenthistory`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `punishments`
--
ALTER TABLE `punishments`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `staff`
--
ALTER TABLE `staff`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uuid` (`uuid`);

--
-- Indexes for table `teams`
--
ALTER TABLE `teams`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `team_number` (`team_number`);

--
-- Indexes for table `tsdata`
--
ALTER TABLE `tsdata`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `data_key` (`data_key`);

--
-- Indexes for table `whitelist`
--
ALTER TABLE `whitelist`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `player` (`uuid`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `chat_log`
--
ALTER TABLE `chat_log`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=65;
--
-- AUTO_INCREMENT for table `luckperms_actions`
--
ALTER TABLE `luckperms_actions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=56;
--
-- AUTO_INCREMENT for table `luckperms_group_permissions`
--
ALTER TABLE `luckperms_group_permissions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=115;
--
-- AUTO_INCREMENT for table `luckperms_messenger`
--
ALTER TABLE `luckperms_messenger`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `luckperms_user_permissions`
--
ALTER TABLE `luckperms_user_permissions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;
--
-- AUTO_INCREMENT for table `players`
--
ALTER TABLE `players`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=39;
--
-- AUTO_INCREMENT for table `punishmenthistory`
--
ALTER TABLE `punishmenthistory`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
--
-- AUTO_INCREMENT for table `punishments`
--
ALTER TABLE `punishments`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `staff`
--
ALTER TABLE `staff`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT for table `teams`
--
ALTER TABLE `teams`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=61;
--
-- AUTO_INCREMENT for table `tsdata`
--
ALTER TABLE `tsdata`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT for table `whitelist`
--
ALTER TABLE `whitelist`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
