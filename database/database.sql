SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

CREATE TABLE `chat_log` (
  `id` int(10) UNSIGNED NOT NULL,
  `session_id` varchar(36) COLLATE utf8_bin NOT NULL,
  `uuid` varchar(36) COLLATE utf8_bin NOT NULL,
  `username` varchar(16) COLLATE utf8_bin NOT NULL,
  `content` text COLLATE utf8_bin NOT NULL,
  `sent_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

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

CREATE TABLE `luckperms_groups` (
  `name` varchar(36) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `luckperms_groups` (`name`) VALUES
('admin'),
('advancedban_permissions'),
('commentator'),
('default'),
('full_perms'),
('host'),
('moderator'),
('owner'),
('staff'),
('staff_base_permissions');

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

INSERT INTO `luckperms_group_permissions` (`id`, `name`, `permission`, `value`, `server`, `world`, `expiry`, `contexts`) VALUES
(9, 'staff', 'displayname.Staff', 1, 'global', 'global', 0, '{}'),
(17, 'default', 'prefix.0.§1Player §r', 1, 'global', 'global', 0, '{}'),
(56, 'default', 'bukkit.command.plugins', 0, 'global', 'global', 0, '{}'),
(57, 'default', 'bukkit.command.version', 0, 'global', 'global', 0, '{}'),
(58, 'default', 'minecraft.command.me', 0, 'global', 'global', 0, '{}'),
(59, 'default', 'minecraft.command.tell', 0, 'global', 'global', 0, '{}'),
(102, 'commentator', 'group.default', 1, 'global', 'global', 0, '{}'),
(106, 'commentator', 'displayname.Commentator', 1, 'global', 'global', 0, '{}'),
(107, 'commentator', 'tournamentcore.command.ctp', 1, 'global', 'global', 0, '{}'),
(109, 'commentator', 'tournamentcore.commentator', 1, 'global', 'global', 0, '{}'),
(130, 'staff', 'weight.10', 1, 'global', 'global', 0, '{}'),
(136, 'commentator', 'weight.5', 1, 'global', 'global', 0, '{}'),
(137, 'commentator', 'prefix.5.§bCommentator ', 1, 'global', 'global', 0, '{}'),
(139, 'full_perms', 'vault.*', 1, 'global', 'global', 0, '{}'),
(140, 'full_perms', 'group.staff_base_permissions', 1, 'global', 'global', 0, '{}'),
(141, 'full_perms', 'novacore.command.*', 1, 'global', 'global', 0, '{}'),
(142, 'full_perms', 'bukkit.command.*', 1, 'global', 'global', 0, '{}'),
(143, 'full_perms', 'privatechats.chat.staff', 1, 'global', 'global', 0, '{}'),
(144, 'full_perms', 'fawe', 1, 'global', 'global', 0, '{}'),
(145, 'full_perms', 'fawe.admin', 1, 'global', 'global', 0, '{}'),
(146, 'full_perms', 'minecraft.command.*', 1, 'global', 'global', 0, '{}'),
(147, 'full_perms', 'protocol.*', 1, 'global', 'global', 0, '{}'),
(148, 'full_perms', 'novapluginmanager.*', 1, 'global', 'global', 0, '{}'),
(149, 'full_perms', 'novautils.command.*', 1, 'global', 'global', 0, '{}'),
(150, 'full_perms', 'ag', 1, 'global', 'global', 0, '{}'),
(152, 'full_perms', 'novacore.loglevel.auto.warn', 1, 'global', 'global', 0, '{}'),
(153, 'full_perms', 'sv.stacktrace', 1, 'global', 'global', 0, '{}'),
(154, 'full_perms', 'placeholderapi.*', 1, 'global', 'global', 0, '{}'),
(155, 'full_perms', 'ag.manage', 1, 'global', 'global', 0, '{}'),
(156, 'full_perms', 'holographicdisplays.*', 1, 'global', 'global', 0, '{}'),
(157, 'full_perms', 'citizens.*', 1, 'global', 'global', 0, '{}'),
(158, 'full_perms', 'sv.*', 1, 'global', 'global', 0, '{}'),
(159, 'full_perms', 'luckperms.*', 1, 'global', 'global', 0, '{}'),
(160, 'full_perms', 'worldedit.*', 1, 'global', 'global', 0, '{}'),
(161, 'staff', 'group.staff_base_permissions', 1, 'global', 'global', 0, '{}'),
(162, 'owner', 'group.full_perms', 1, 'global', 'global', 0, '{}'),
(164, 'owner', 'displayname.Owner', 1, 'global', 'global', 0, '{}'),
(165, 'owner', 'weight.1000', 1, 'global', 'global', 0, '{}'),
(167, 'host', 'displayname.Host', 1, 'global', 'global', 0, '{}'),
(168, 'host', 'group.full_perms', 1, 'global', 'global', 0, '{}'),
(169, 'host', 'weight.1000', 1, 'global', 'global', 0, '{}'),
(170, 'admin', 'displayname.Admin', 1, 'global', 'global', 0, '{}'),
(172, 'admin', 'group.full_perms', 1, 'global', 'global', 0, '{}'),
(173, 'admin', 'weight.1000', 1, 'global', 'global', 0, '{}'),
(174, 'staff_base_permissions', 'weight.5', 1, 'global', 'global', 0, '{}'),
(175, 'full_perms', 'ab.systemprefs', 1, 'global', 'global', 0, '{}'),
(176, 'full_perms', 'group.advancedban_permissions', 1, 'global', 'global', 0, '{}'),
(177, 'full_perms', 'ab.reload', 1, 'global', 'global', 0, '{}'),
(178, 'staff', 'prefix.10.§aStaff §r', 1, 'global', 'global', 0, '{}'),
(179, 'owner', 'prefix.1000.§aOwner §r', 1, 'global', 'global', 0, '{}'),
(180, 'host', 'prefix.1000.§aHost §r', 1, 'global', 'global', 0, '{}'),
(181, 'admin', 'prefix.1000.§aAdmin §r', 1, 'global', 'global', 0, '{}'),
(182, 'staff_base_permissions', 'bukkit.command.version', 1, 'global', 'global', 0, '{}'),
(183, 'staff_base_permissions', 'minecraft.command.me', 1, 'global', 'global', 0, '{}'),
(184, 'staff_base_permissions', 'novacore.loglevel.auto.warn', 1, 'global', 'global', 0, '{}'),
(185, 'staff_base_permissions', 'tournamentsystem.staff', 1, 'global', 'global', 0, '{}'),
(186, 'staff_base_permissions', 'bukkit.command.plugins', 1, 'global', 'global', 0, '{}'),
(187, 'moderator', 'weight.500', 1, 'global', 'global', 0, '{}'),
(188, 'moderator', 'displayname.Moderator', 1, 'global', 'global', 0, '{}'),
(189, 'moderator', 'group.staff_base_permissions', 1, 'global', 'global', 0, '{}'),
(190, 'moderator', 'prefix.500.§aModerator §r', 1, 'global', 'global', 0, '{}'),
(191, 'moderator', 'group.advancedban_permissions', 1, 'global', 'global', 0, '{}'),
(192, 'advancedban_permissions', 'ab.note.undo', 1, 'global', 'global', 0, '{}'),
(193, 'advancedban_permissions', 'ab.banlist', 1, 'global', 'global', 0, '{}'),
(194, 'advancedban_permissions', 'weight.20', 1, 'global', 'global', 0, '{}'),
(195, 'advancedban_permissions', 'ab.kick.use', 1, 'global', 'global', 0, '{}'),
(196, 'advancedban_permissions', 'ab.changeReason', 1, 'global', 'global', 0, '{}'),
(197, 'advancedban_permissions', 'ab.notify.note', 1, 'global', 'global', 0, '{}'),
(198, 'advancedban_permissions', 'ab.all.undo', 1, 'global', 'global', 0, '{}'),
(199, 'advancedban_permissions', 'ab.warn.perma', 1, 'global', 'global', 0, '{}'),
(200, 'advancedban_permissions', 'ab.mute.temp', 1, 'global', 'global', 0, '{}'),
(201, 'advancedban_permissions', 'ab.undoNotify.note', 1, 'global', 'global', 0, '{}'),
(202, 'advancedban_permissions', 'ab.warns.other', 1, 'global', 'global', 0, '{}'),
(203, 'advancedban_permissions', 'ab.mute.perma', 1, 'global', 'global', 0, '{}'),
(204, 'advancedban_permissions', 'ab.ban.perma', 1, 'global', 'global', 0, '{}'),
(205, 'advancedban_permissions', 'ab.ban.undo', 1, 'global', 'global', 0, '{}'),
(206, 'advancedban_permissions', 'ab.note.use', 1, 'global', 'global', 0, '{}'),
(207, 'advancedban_permissions', 'ab.notify.ban', 1, 'global', 'global', 0, '{}'),
(208, 'advancedban_permissions', 'ab.history', 1, 'global', 'global', 0, '{}'),
(209, 'advancedban_permissions', 'ab.help', 1, 'global', 'global', 0, '{}'),
(210, 'advancedban_permissions', 'ab.notify.tempipban', 1, 'global', 'global', 0, '{}'),
(211, 'advancedban_permissions', 'ab.notify.mute', 1, 'global', 'global', 0, '{}'),
(212, 'advancedban_permissions', 'ab.check.ip', 1, 'global', 'global', 0, '{}'),
(213, 'advancedban_permissions', 'ab.notify.tempmute', 1, 'global', 'global', 0, '{}'),
(214, 'advancedban_permissions', 'ab.warn.undo', 1, 'global', 'global', 0, '{}'),
(215, 'advancedban_permissions', 'ab.notes.other', 1, 'global', 'global', 0, '{}'),
(216, 'advancedban_permissions', 'ab.ban.temp', 1, 'global', 'global', 0, '{}'),
(217, 'advancedban_permissions', 'ab.notify.warn', 1, 'global', 'global', 0, '{}'),
(218, 'advancedban_permissions', 'ab.notes.own', 1, 'global', 'global', 0, '{}'),
(219, 'advancedban_permissions', 'ab.notify.ipban', 1, 'global', 'global', 0, '{}'),
(220, 'advancedban_permissions', 'ab.notify.tempwarn', 1, 'global', 'global', 0, '{}'),
(221, 'advancedban_permissions', 'ab.ipban.temp', 1, 'global', 'global', 0, '{}'),
(222, 'advancedban_permissions', 'ab.warns.own', 1, 'global', 'global', 0, '{}'),
(223, 'advancedban_permissions', 'ab.check', 1, 'global', 'global', 0, '{}'),
(224, 'advancedban_permissions', 'ab.mute.undo', 1, 'global', 'global', 0, '{}'),
(225, 'advancedban_permissions', 'ab.undoNotify.mute', 1, 'global', 'global', 0, '{}'),
(226, 'advancedban_permissions', 'ab.warn.temp', 1, 'global', 'global', 0, '{}'),
(227, 'advancedban_permissions', 'ab.undoNotify.ban', 1, 'global', 'global', 0, '{}'),
(228, 'advancedban_permissions', 'ab.notify.kick', 1, 'global', 'global', 0, '{}'),
(229, 'advancedban_permissions', 'ab.notify.tempban', 1, 'global', 'global', 0, '{}'),
(230, 'advancedban_permissions', 'ab.undoNotify.warn', 1, 'global', 'global', 0, '{}'),
(231, 'advancedban_permissions', 'ab.ipban.perma', 1, 'global', 'global', 0, '{}'),
(232, 'full_perms', 'weight.600', 1, 'global', 'global', 0, '{}'),
(233, 'full_perms', 'bungeecord.command.end', 1, 'global', 'global', 0, '{}'),
(234, 'full_perms', 'tournamentcore.command.*', 1, 'global', 'global', 0, '{}'),
(235, 'full_perms', 'bungeecord.command.alert', 1, 'global', 'global', 0, '{}'),
(236, 'full_perms', 'tournamentsystem.command.*', 1, 'global', 'global', 0, '{}'),
(237, 'full_perms', 'bungeecord.command.reload', 1, 'global', 'global', 0, '{}'),
(238, 'full_perms', 'bungeecord.command.send', 1, 'global', 'global', 0, '{}'),
(239, 'full_perms', 'bungeecord.command.ip', 1, 'global', 'global', 0, '{}'),
(240, 'staff_base_permissions', 'bungeecord.command.list', 1, 'global', 'global', 0, '{}'),
(241, 'staff_base_permissions', 'bungeecord.command.find', 1, 'global', 'global', 0, '{}'),
(242, 'staff_base_permissions', 'bungeecord.command.server', 1, 'global', 'global', 0, '{}'),
(243, 'default', 'bungeecord.command.server', 0, 'global', 'global', 0, '{}'),
(244, 'full_perms', 'spark.tps', 1, 'global', 'global', 0, '{}'),
(245, 'full_perms', 'spark.gcmonitor', 1, 'global', 'global', 0, '{}'),
(246, 'full_perms', 'spark', 1, 'global', 'global', 0, '{}'),
(247, 'full_perms', 'spark.activity', 1, 'global', 'global', 0, '{}'),
(248, 'full_perms', 'spark.ping', 1, 'global', 'global', 0, '{}'),
(249, 'full_perms', 'spark.heapsummary', 1, 'global', 'global', 0, '{}'),
(250, 'full_perms', 'spark.profiler', 1, 'global', 'global', 0, '{}'),
(251, 'full_perms', 'spark.gc', 1, 'global', 'global', 0, '{}'),
(252, 'full_perms', 'spark.heapdump', 1, 'global', 'global', 0, '{}'),
(253, 'full_perms', 'messages.command.socialspy', 1, 'global', 'global', 0, '{}'),
(254, 'full_perms', 'spark.tickmonitor', 1, 'global', 'global', 0, '{}'),
(255, 'full_perms', 'spark.healthreport', 1, 'global', 'global', 0, '{}'),
(256, 'moderator', 'messages.command.socialspy', 1, 'global', 'global', 0, '{}'),
(257, 'default', 'spark.tps', 1, 'global', 'global', 0, '{}'),
(258, 'full_perms', 'exploitfixer.notifications', 1, 'global', 'global', 0, '{}'),
(259, 'moderator', 'exploitfixer.notifications', 1, 'global', 'global', 0, '{}'),
(260, 'staff', 'exploitfixer.notifications', 1, 'global', 'global', 0, '{}'),
(261, 'full_perms', 'tournamentcore.notify.swear', 1, 'global', 'global', 0, '{}'),
(262, 'full_perms', 'tournamentsystem.commands.managedserver.kill', 1, 'global', 'global', 0, '{}'),
(263, 'full_perms', 'tournamentsystem.notify.swear', 1, 'global', 'global', 0, '{}'),
(264, 'full_perms', 'tournamentsystem.command', 1, 'global', 'global', 0, '{}'),
(265, 'full_perms', 'tournamentsystem.commands.managedserver', 1, 'global', 'global', 0, '{}'),
(266, 'full_perms', 'tournamentsystem.commands.managedserver.start', 1, 'global', 'global', 0, '{}'),
(267, 'moderator', 'tournamentsystem.notify.swear', 1, 'global', 'global', 0, '{}'),
(268, 'full_perms', 'supervanish.*', 1, 'global', 'global', 0, '{}');

CREATE TABLE `luckperms_messenger` (
  `id` int(11) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `msg` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `luckperms_players` (
  `uuid` varchar(36) NOT NULL,
  `username` varchar(16) NOT NULL,
  `primary_group` varchar(36) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `luckperms_tracks` (
  `name` varchar(36) NOT NULL,
  `groups` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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

CREATE TABLE `players` (
  `id` int(10) UNSIGNED NOT NULL,
  `uuid` varchar(36) COLLATE utf8_bin NOT NULL,
  `username` varchar(16) COLLATE utf8_bin NOT NULL,
  `team_number` int(10) UNSIGNED DEFAULT NULL,
  `kills` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `metadata` text COLLATE utf8_bin
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `player_score` (
  `id` int(10) UNSIGNED NOT NULL,
  `player_id` int(11) UNSIGNED NOT NULL,
  `server` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `reason` text COLLATE utf8_bin NOT NULL,
  `amount` int(11) NOT NULL,
  `gained_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

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

CREATE TABLE `sr_players` (
  `Nick` varchar(17) COLLATE utf8_unicode_ci NOT NULL,
  `Skin` varchar(19) COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `sr_skins` (
  `Nick` varchar(19) COLLATE utf8_unicode_ci NOT NULL,
  `Value` text COLLATE utf8_unicode_ci,
  `Signature` text COLLATE utf8_unicode_ci,
  `timestamp` text COLLATE utf8_unicode_ci
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `staff` (
  `id` int(10) UNSIGNED NOT NULL,
  `uuid` varchar(36) COLLATE utf8_bin NOT NULL,
  `role` varchar(255) COLLATE utf8_bin NOT NULL,
  `username` text COLLATE utf8_bin,
  `offline_mode` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `teams` (
  `id` int(10) UNSIGNED NOT NULL,
  `team_number` int(10) UNSIGNED NOT NULL,
  `metadata` text COLLATE utf8_bin,
  `kills` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `team_score` (
  `id` int(10) UNSIGNED NOT NULL,
  `team_id` int(10) UNSIGNED NOT NULL,
  `server` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `reason` text COLLATE utf8_bin NOT NULL,
  `amount` int(11) NOT NULL,
  `gained_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `tsdata` (
  `id` int(10) UNSIGNED NOT NULL,
  `data_key` varchar(255) COLLATE utf8_bin NOT NULL COMMENT 'This is the key. Max 255 characters long',
  `data_value` text COLLATE utf8_bin COMMENT 'This is the value. Can be as long as you want'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='This table stores the configuration';

CREATE TABLE `whitelist` (
  `id` int(10) UNSIGNED NOT NULL,
  `uuid` varchar(36) COLLATE utf8_bin NOT NULL COMMENT 'The uuid of the player',
  `username` text COLLATE utf8_bin,
  `offline_mode` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

ALTER TABLE `chat_log`
  ADD PRIMARY KEY (`id`),
  ADD KEY `session_id` (`session_id`),
  ADD KEY `uuid` (`uuid`);

ALTER TABLE `luckperms_actions`
  ADD PRIMARY KEY (`id`);

ALTER TABLE `luckperms_groups`
  ADD PRIMARY KEY (`name`);

ALTER TABLE `luckperms_group_permissions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `luckperms_group_permissions_name` (`name`);

ALTER TABLE `luckperms_messenger`
  ADD PRIMARY KEY (`id`);

ALTER TABLE `luckperms_players`
  ADD PRIMARY KEY (`uuid`),
  ADD KEY `luckperms_players_username` (`username`);

ALTER TABLE `luckperms_tracks`
  ADD PRIMARY KEY (`name`);

ALTER TABLE `luckperms_user_permissions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `luckperms_user_permissions_uuid` (`uuid`);

ALTER TABLE `players`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uuid` (`uuid`),
  ADD KEY `team_number` (`team_number`),
  ADD KEY `username` (`username`);

ALTER TABLE `player_score`
  ADD PRIMARY KEY (`id`),
  ADD KEY `player_id` (`player_id`);

ALTER TABLE `punishmenthistory`
  ADD PRIMARY KEY (`id`);

ALTER TABLE `punishments`
  ADD PRIMARY KEY (`id`);

ALTER TABLE `sr_players`
  ADD PRIMARY KEY (`Nick`);

ALTER TABLE `sr_skins`
  ADD PRIMARY KEY (`Nick`);

ALTER TABLE `staff`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uuid` (`uuid`);

ALTER TABLE `teams`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `team_number` (`team_number`);

ALTER TABLE `team_score`
  ADD PRIMARY KEY (`id`),
  ADD KEY `team_number` (`team_id`);

ALTER TABLE `tsdata`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `data_key` (`data_key`);

ALTER TABLE `whitelist`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `player` (`uuid`);

ALTER TABLE `chat_log`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=120;
  
ALTER TABLE `luckperms_actions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
  
ALTER TABLE `luckperms_group_permissions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=269;
  
ALTER TABLE `luckperms_messenger`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
  
ALTER TABLE `luckperms_user_permissions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;
  
ALTER TABLE `players`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;
  
ALTER TABLE `player_score`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;
  
ALTER TABLE `punishmenthistory`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
  
ALTER TABLE `punishments`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
  
ALTER TABLE `staff`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;
  
ALTER TABLE `teams`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;
  
ALTER TABLE `team_score`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;
  
ALTER TABLE `tsdata`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;
  
ALTER TABLE `whitelist`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

ALTER TABLE `player_score`
  ADD CONSTRAINT `link score to player` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `team_score`
ADD CONSTRAINT `link team to score` FOREIGN KEY (`team_id`) REFERENCES `teams` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;