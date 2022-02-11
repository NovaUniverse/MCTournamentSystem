-- phpMyAdmin SQL Dump
-- version 4.5.4.1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Feb 11, 2022 at 09:46 AM
-- Server version: 5.7.11
-- PHP Version: 5.6.18

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
CREATE DEFINER=`root`@`localhost` PROCEDURE `reset_data` ()  BEGIN
	UPDATE teams SET score = 0;
    
    DELETE FROM players;
    
    UPDATE tsdata SET data_value = null WHERE data_key = "active_server";
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `set_player_team` (`player_uuid` VARCHAR(36), `player_username` VARCHAR(16), `player_team_number` INT)  BEGIN
	IF NOT EXISTS (SELECT id FROM players WHERE uuid = player_uuid) THEN
		INSERT INTO players (uuid, username) VALUES (player_uuid, player_username);
    END IF;
    
    UPDATE players SET username = player_username, team_number = player_team_number WHERE uuid = player_uuid;
END$$

DELIMITER ;

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

--
-- Dumping data for table `luckperms_actions`
--

INSERT INTO `luckperms_actions` (`id`, `time`, `actor_uuid`, `actor_name`, `type`, `acted_uuid`, `acted_name`, `action`) VALUES
(1, 1633954140, '00000000-0000-0000-0000-000000000000', 'Console', 'G', 'null', 'moderator', 'webeditor add group.helper true'),
(2, 1633954140, '00000000-0000-0000-0000-000000000000', 'Console', 'G', 'null', 'moderator', 'webeditor add weight.100 true'),
(3, 1633954140, '00000000-0000-0000-0000-000000000000', 'Console', 'G', 'null', 'moderator', 'webeditor add prefix.100.moderator true'),
(4, 1633954140, '00000000-0000-0000-0000-000000000000', 'Console', 'G', 'null', 'moderator', 'webeditor add displayname.Moderator true'),
(5, 1633954140, '00000000-0000-0000-0000-000000000000', 'Console', 'G', 'null', 'helper', 'webeditor add weight.10 true'),
(6, 1633954140, '00000000-0000-0000-0000-000000000000', 'Console', 'G', 'null', 'helper', 'webeditor add group.default true'),
(7, 1633954140, '00000000-0000-0000-0000-000000000000', 'Console', 'G', 'null', 'helper', 'webeditor add prefix.10.Helper true'),
(8, 1633954140, '00000000-0000-0000-0000-000000000000', 'Console', 'G', 'null', 'helper', 'webeditor add displayname.Helper true'),
(9, 1633954140, '00000000-0000-0000-0000-000000000000', 'Console', 'G', 'null', 'staff', 'webeditor add group.moderator true'),
(10, 1633954140, '00000000-0000-0000-0000-000000000000', 'Console', 'G', 'null', 'staff', 'webeditor add prefix.1000.staff true'),
(11, 1633954140, '00000000-0000-0000-0000-000000000000', 'Console', 'G', 'null', 'staff', 'webeditor add weight.1000 true'),
(12, 1633954140, '00000000-0000-0000-0000-000000000000', 'Console', 'G', 'null', 'staff', 'webeditor add displayname.Staff true'),
(13, 1633954140, '00000000-0000-0000-0000-000000000000', 'Console', 'G', 'null', 'host', 'webeditor add weight.10000 true'),
(14, 1633954140, '00000000-0000-0000-0000-000000000000', 'Console', 'G', 'null', 'host', 'webeditor add displayname.Host true'),
(15, 1633954140, '00000000-0000-0000-0000-000000000000', 'Console', 'G', 'null', 'host', 'webeditor add prefix.10000.Host true'),
(16, 1633954140, '00000000-0000-0000-0000-000000000000', 'Console', 'G', 'null', 'host', 'webeditor add group.staff true'),
(17, 1633955242, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'default', 'webeditor add prefix.0.§1Player §r true'),
(18, 1633955242, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'moderator', 'webeditor add prefix.100.§l§eModerator §r true'),
(19, 1633955242, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'moderator', 'webeditor remove prefix.100.moderator true'),
(20, 1633955242, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'helper', 'webeditor add prefix.10.§l§dHelper §r true'),
(21, 1633955242, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'helper', 'webeditor remove prefix.10.Helper true'),
(22, 1633955242, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'helper', 'webeditor add novacore.loglevel.auto.warn true'),
(23, 1633955242, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'staff', 'webeditor add bukkit.* true'),
(24, 1633955242, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'staff', 'webeditor add holograms.* true'),
(25, 1633955242, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'staff', 'webeditor add novacore.command.* true'),
(26, 1633955242, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'staff', 'webeditor add tournamentcore.command.* true'),
(27, 1633955242, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'staff', 'webeditor add vault.update true'),
(28, 1633955242, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'staff', 'webeditor add fawe.* true'),
(29, 1633955242, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'staff', 'webeditor add prefix.1000.§l§bStaff §r true'),
(30, 1633955243, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'staff', 'webeditor add vault.admin true'),
(31, 1633955243, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'staff', 'webeditor add minecraft.command.* true'),
(32, 1633955243, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'staff', 'webeditor add protocol.* true'),
(33, 1633955243, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'staff', 'webeditor add citizens.citizens.* true'),
(34, 1633955243, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'staff', 'webeditor add supervanish.* true'),
(35, 1633955243, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'staff', 'webeditor add luckperms.* true'),
(36, 1633955243, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'staff', 'webeditor add signedit.admin true'),
(37, 1633955243, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'staff', 'webeditor add worldedit.* true'),
(38, 1633955243, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'staff', 'webeditor remove prefix.1000.staff true'),
(39, 1633955243, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'host', 'webeditor add prefix.10000.§l§aHost §r true'),
(40, 1633955243, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'host', 'webeditor remove prefix.10000.Host true'),
(41, 1633955289, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'moderator', 'webeditor remove prefix.100.§l§eModerator §r true'),
(42, 1633955289, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'moderator', 'webeditor add prefix.100.§e§lModerator §r true'),
(43, 1633955289, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'helper', 'webeditor add prefix.10.§d§lHelper §r true'),
(44, 1633955289, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'helper', 'webeditor remove prefix.10.§l§dHelper §r true'),
(45, 1633955289, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'staff', 'webeditor add prefix.1000.§b§lStaff §r true'),
(46, 1633955289, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'staff', 'webeditor remove prefix.1000.§l§bStaff §r true'),
(47, 1633955289, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'host', 'webeditor add prefix.10000.§a§lHost §r true'),
(48, 1633955289, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'host', 'webeditor remove prefix.10000.§l§aHost §r true'),
(49, 1634893105, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'staff', 'webeditor add bungeecord.command.* true'),
(50, 1634893105, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'moderator', 'webeditor add bungeecord.command.list true'),
(51, 1634893105, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'moderator', 'webeditor add bungeecord.command.find true'),
(52, 1634893105, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'moderator', 'webeditor add bungeecord.command.send true'),
(53, 1634893105, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'moderator', 'webeditor add bungeecord.command.ip true'),
(54, 1634893105, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'moderator', 'webeditor add bungeecord.command.server true'),
(55, 1635410160, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'moderator', 'webeditor add messages.command.socialspy true'),
(56, 1635410213, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'host', 'webeditor add messages.command.socialspy true'),
(57, 1635410213, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'staff', 'webeditor add messages.command.socialspy true'),
(58, 1635410322, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01@bungee', 'G', 'null', 'host', 'webeditor add bungeemessages.command.socialspy true'),
(59, 1635410322, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01@bungee', 'G', 'null', 'host', 'webeditor remove messages.command.socialspy true'),
(60, 1635410322, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01@bungee', 'G', 'null', 'moderator', 'webeditor add bungeemessages.command.socialspy true'),
(61, 1635416693, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'moderator', 'webeditor add tournamentcore.autosocialspy true'),
(62, 1642152409, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'helper', 'webeditor add bukkit.command.version true'),
(63, 1642152409, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'helper', 'webeditor add minecraft.command.me true'),
(64, 1642152409, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'helper', 'webeditor add bukkit.command.plugins true'),
(65, 1642152409, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'default', 'webeditor add bukkit.command.plugins false'),
(66, 1642152409, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'default', 'webeditor add minecraft.command.me false'),
(67, 1642152409, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'default', 'webeditor add bukkit.command.version false'),
(68, 1642152409, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'default', 'webeditor add minecraft.command.tell false'),
(69, 1642153872, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01', 'G', 'null', 'moderator', 'webeditor add tournamentcore.command.yborder true'),
(70, 1642185873, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'Zeeraa01@bungee', 'U', 'cb128b6b-400d-4e5b-baf3-3a15c9792ccd', 'vlc_mediaplayer', 'webeditor add * true');

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
(60, 'moderator', 'tournamentcore.command.yborder', 1, 'global', 'global', 0, '{}');

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

--
-- Dumping data for table `luckperms_players`
--

INSERT INTO `luckperms_players` (`uuid`, `username`, `primary_group`) VALUES
('0f3157cf-99a4-4bf9-beed-ce83096ebfcf', 'afteryesterday', 'default'),
('22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'zeeraa01', 'default'),
('31370aa2-2978-4072-9c2d-dbd7dba00ff1', 'smarttortoise', 'default'),
('37dc6e39-2ef9-47ad-ba8a-1f1a162800ba', 'footi_', 'default'),
('45c49c88-950c-4f4b-afe0-55ca5d0593d8', 'aleksa445', 'default'),
('5203face-89ca-49b7-a5a0-f2cf0fe230e7', 'woltry', 'default'),
('5457678e-c69d-4438-be87-a986f351f6d0', '_certifiedrat', 'default'),
('7b63903d-397a-4876-80a5-e63786195b40', 'weneedsnow', 'default'),
('83c3d18d-3b17-4e9e-ba7b-8887d5fc5183', 'killjo12323', 'default'),
('866a6931-a503-48b1-9d6f-0dde92c05918', 'nissemosserud', 'default'),
('8ec663e7-9a3d-4014-9bc6-a6915e629a56', 'debianbtw', 'default'),
('93eb833c-c09b-42c7-8776-7b7cfa6dfebe', 'two_towers', 'default'),
('980dbf7d-0904-426f-9c02-d9af3c099fb2', 'istromus', 'default'),
('9bb529cc-8681-4698-8c3a-e25d802bb1ca', 'penguinslippers', 'default'),
('ac7fd064-b4e7-43d0-85de-a9e701019afc', 'youhomehugg', 'default'),
('c1c832fe-e522-47a4-8de3-d197a81b0ec9', 'mangoplayz', 'default'),
('c53c296d-c8ba-4cca-9b0b-44565efc9d84', 'notander', 'default'),
('c68b842c-7092-466f-b2ea-cdf6f6f8f011', 'huefiho', 'default'),
('ca2e347b-025a-4e7b-8019-752b83661f7f', 'i_got_your_ip', 'default'),
('cb128b6b-400d-4e5b-baf3-3a15c9792ccd', 'vlc_mediaplayer', 'default'),
('cceec271-0a6c-422b-a2b4-bc3e4b6ca666', 'llizzybeth', 'default'),
('f0602181-c6b7-49ff-a541-2c7960adb85a', 'zerlandgamer', 'default'),
('f19a7ebb-5ab7-4866-a320-c46869732101', 'awre_', 'default'),
('f4ca2a40-c926-4b37-9a41-46bc1613152f', 'entity_666', 'default');

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

--
-- Dumping data for table `luckperms_user_permissions`
--

INSERT INTO `luckperms_user_permissions` (`id`, `uuid`, `permission`, `value`, `server`, `world`, `expiry`, `contexts`) VALUES
(1, '22a9eca8-2221-4bc9-b463-de0f3a0cc652', 'group.host', 1, 'global', 'global', 0, '{}'),
(4, '866a6931-a503-48b1-9d6f-0dde92c05918', 'group.default', 1, 'global', 'global', 0, '{}'),
(5, 'cb128b6b-400d-4e5b-baf3-3a15c9792ccd', 'group.moderator', 1, 'global', 'global', 0, '{}'),
(6, '37dc6e39-2ef9-47ad-ba8a-1f1a162800ba', 'group.helper', 1, 'global', 'global', 0, '{}'),
(7, 'cb128b6b-400d-4e5b-baf3-3a15c9792ccd', '*', 1, 'global', 'global', 0, '{}');

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
  `kills` int(10) UNSIGNED NOT NULL DEFAULT '0'
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
  `score` int(11) NOT NULL DEFAULT '0'
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
-- AUTO_INCREMENT for table `luckperms_actions`
--
ALTER TABLE `luckperms_actions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=71;
--
-- AUTO_INCREMENT for table `luckperms_group_permissions`
--
ALTER TABLE `luckperms_group_permissions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=61;
--
-- AUTO_INCREMENT for table `luckperms_messenger`
--
ALTER TABLE `luckperms_messenger`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `luckperms_user_permissions`
--
ALTER TABLE `luckperms_user_permissions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;
--
-- AUTO_INCREMENT for table `players`
--
ALTER TABLE `players`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=43;
--
-- AUTO_INCREMENT for table `staff`
--
ALTER TABLE `staff`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;
--
-- AUTO_INCREMENT for table `teams`
--
ALTER TABLE `teams`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=49;
--
-- AUTO_INCREMENT for table `tsdata`
--
ALTER TABLE `tsdata`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT for table `whitelist`
--
ALTER TABLE `whitelist`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
