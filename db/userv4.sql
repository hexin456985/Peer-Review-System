drop database thesis;
create database thesis;
USE thesis;
CREATE TABLE `user` (
    `UID` INT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(45) NOT NULL,
    `gender` VARCHAR(45) NULL,
    `age` INT NULL,
    `password` VARCHAR(100) NULL,
    `email` VARCHAR(45) NULL,
    `institution` VARCHAR(100),
    `region` VARCHAR(50),
    PRIMARY KEY (`UID`),
    UNIQUE INDEX `UID_UNIQUE` (`UID` ASC) VISIBLE);

CREATE TABLE `role` (
                        `id` INT NOT NULL AUTO_INCREMENT COMMENT "角色ID",
                        `name` VARCHAR(45) NOT NULL COMMENT "角色名称",
                        `description` VARCHAR(255) NULL COMMENT "角色描述",
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `name`(`name`)
);

CREATE TABLE `user_role` (
                             `id` int(11) NOT NULL AUTO_INCREMENT,
                             `user_id` INT NOT NULL,
                             `role_id` INT NOT NULL,
                             PRIMARY KEY (`id`),
                             FOREIGN KEY (`user_id`) REFERENCES `user`(`UID`),
                             FOREIGN KEY (`role_id`) REFERENCES `role`(`id`)
);

CREATE TABLE `menu` (
                        `menu_id` int(11) NOT NULL AUTO_INCREMENT,
                        `component` varchar(100) DEFAULT NULL,
                        `path` varchar(100) DEFAULT NULL,
                        `redirect` varchar(100) DEFAULT NULL,
                        `name` varchar(100) DEFAULT NULL,
                        `title` varchar(100) DEFAULT NULL,
                        `icon` varchar(100) DEFAULT NULL,
                        `parent_id` int(11) DEFAULT NULL,
                        `is_leaf` varchar(1) DEFAULT NULL,
                        `hidden` tinyint(1) DEFAULT NULL,
                        PRIMARY KEY (`menu_id`)
);

CREATE TABLE `role_menu` (
                             `id` int(11) NOT NULL AUTO_INCREMENT,
                             `role_id` int(11) DEFAULT NULL,
                             `menu_id` int(11) DEFAULT NULL,
                             PRIMARY KEY (`id`)
);

CREATE TABLE `Conferences` (

                               `ConferenceId` INT NOT NULL AUTO_INCREMENT,
                               `chair_id` INT NOT NULL,
                               `short_name` VARCHAR(50) NOT NULL,
                               `full_name` VARCHAR(100) NOT NULL,
                               `event_date` DATE,
                               `event_Location` VARCHAR(100),
                               `submission_deadline` DATE,
                               `reviewresult_date` DATE,
                               `status` VARCHAR(20) NOT NULL,
                               PRIMARY KEY (`ConferenceID`)
);

CREATE TABLE `invitation` (
                         `invitationId` int(0) NOT NULL AUTO_INCREMENT COMMENT '会议邀请id',
                        `conferenceId` int(0) NOT NULL COMMENT '会议id',
                        `userId` int(0) NULL DEFAULT NULL COMMENT '用户id',
                         `status` int(0) NULL DEFAULT NULL COMMENT '状态：1-邀请待接受，2-接受，3-拒绝',
                         PRIMARY KEY (`invitationId`) USING BTREE,
                         UNIQUE INDEX `uk-userId-conferenId`(`conferenceId`, `userId`) USING BTREE
);

CREATE TABLE `Chair` (
                         `ChairId` INT NOT NULL AUTO_INCREMENT,
                         `ConferenceId` INT,
                         `user_id` INT NOT NULL,
                         PRIMARY KEY (`ChairId`),
                         FOREIGN KEY (`user_id`) REFERENCES `user`(`UID`),
                         FOREIGN KEY (`ConferenceId`) REFERENCES `Conferences`(`ConferenceId`)
);

CREATE TABLE `PCMembers` (
                             `pc_member_id` INT NOT NULL AUTO_INCREMENT,
                             `ConferenceID` INT,
                             `user_id` INT NOT NULL,
                             `InvitationStatus` VARCHAR(20) NOT NULL,
                             PRIMARY KEY (`pc_member_id`),
                             FOREIGN KEY (`user_id`) REFERENCES `user`(`UID`),
                             FOREIGN KEY (`ConferenceID`) REFERENCES `Conferences`(`ConferenceID`)

);

CREATE TABLE `Author` (
                          `author_id` INT NOT NULL AUTO_INCREMENT,
                          `author_name` VARCHAR(100) NOT NULL,
                          `author_institution` VARCHAR(100),
                          `author_region` VARCHAR(100),
                          `author_email` VARCHAR(100),
                          PRIMARY KEY (`author_id`)
);

CREATE TABLE `Topics` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `conference_id` INT NOT NULL,
  `topic_name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`));

CREATE TABLE `Papers` (
                          `paper_id` INT NOT NULL AUTO_INCREMENT,
                          `Title` VARCHAR(100) NOT NULL,
                          `paper_abstract` TEXT,
                          `pdf_path` VARCHAR(255),
                          `author_id` INT,
                          `conference_id` INT,
                          `Status` VARCHAR(20) NOT NULL,
                          PRIMARY KEY(`paper_id`)
);

CREATE TABLE `Paper_Authors` (
                         `paper_author_id` INT AUTO_INCREMENT,
                         `paper_id` INT  NOT NULL,
                         `author_id` INT NOT NULL,
                         PRIMARY KEY(`paper_author_id`),
                         FOREIGN KEY (`paper_id`) REFERENCES `Papers`(`paper_id`),
                         FOREIGN KEY (`author_id`) REFERENCES `Author`(`author_id`)
);

CREATE TABLE `Paper_Topics` (
                         `paper_topic_id` INT AUTO_INCREMENT,
                         `paper_id` INT NOT NULL,
                         `topic_id` INT NOT NULL,
                         PRIMARY KEY(`paper_topic_id`),
                         FOREIGN KEY(`paper_id`) REFERENCES `Papers`(`paper_id`),
                         FOREIGN KEY(`topic_id`) REFERENCES `Topics`(`id`)
);

CREATE TABLE `Paper_PCMember` (
                                  `id` int(11) NOT NULL AUTO_INCREMENT,
                                  `PaperID` INT,
                                  `pc_member_id` INT,
                                  `State` INT,
                                  PRIMARY KEY (`id`),
                                  FOREIGN KEY (`PaperID`) REFERENCES `Papers` (`paper_id`),
                                  FOREIGN KEY (`pc_member_id`) REFERENCES `PCMembers` (`pc_member_id`)
);

CREATE TABLE `Topic_PCMember` (
                                  `topic_pcmember_id` int(11) NOT NULL AUTO_INCREMENT,
                                  `topic_id` INT,
                                  `pc_member_id` INT,
                                  PRIMARY KEY (`topic_pcmember_id`),
                                  FOREIGN KEY (`topic_id`) REFERENCES `Topics` (`id`),
                                  FOREIGN KEY (`pc_member_id`) REFERENCES `PCMembers` (`pc_member_id`)
);

CREATE TABLE `Review` (
                          `review_id` INT NOT NULL AUTO_INCREMENT,
                          `paper_id` INT,
                          `reviewer_id` INT,
                          `review_score` INT,
                          `review_comment` VARCHAR(800),
                          `review_confidence` VARCHAR(20),
                          `rebuttal` VARCHAR(800),
                          `checked` INT default 0,
                          PRIMARY KEY(`review_id`),
                          FOREIGN KEY (`paper_id`) REFERENCES `Papers` (`paper_id`),
                          FOREIGN KEY (`reviewer_id`) REFERENCES `PCMembers` (`pc_member_id`)
);

INSERT INTO `user` (`UID`, `name`, `gender`, `age`, `password`) VALUES ('1', 'admin', 'male', '20', '123456');
INSERT INTO `user` (`name`, `gender`, `age`, `password`, `institution`, `region`) VALUES ('b', 'male', '30', '123456', 'Fudan University', 'China');
INSERT INTO `user` (`name`, `gender`, `age`, `password`, `institution`, `region`) VALUES ('c', 'female', '20', '123456', 'Fudan University', 'China');
INSERT INTO `user` (`name`, `gender`, `age`, `password`, `institution`, `region`) VALUES ('user101', 'male', '30', '123456', 'TT Company', 'Japan');
INSERT INTO `user` (`name`, `gender`, `age`, `password`, `institution`, `region`) VALUES ('user102', 'female', '25', '123456', 'Micro Company', 'America');
INSERT INTO `user` (`name`, `gender`, `age`, `password`, `institution`, `region`) VALUES ('user103', 'male', '35', '123456', 'Beijing University', 'China');
INSERT INTO `user` (`name`, `gender`, `age`, `password`, `institution`, `region`) VALUES ('user104', 'female', '55', '123456', 'Tokyo University', 'Japan');
INSERT INTO `user` (`name`, `gender`, `age`, `password`, `institution`, `region`) VALUES ('user105', 'female', '24', '123456', 'London University', 'England');

INSERT INTO `role` (`name`, `description`) VALUES ('admin', '超级管理员');
INSERT INTO `role` (`name`, `description`) VALUES ('normal', '一般用户');
INSERT INTO `role` (`name`, `description`) VALUES ('chair', '会议主席');
INSERT INTO `role` (`name`, `description`) VALUES ('pc_member', '会议审稿人');


INSERT INTO `user_role` (`id`, `user_id`, `role_id`) VALUES (1, 1, 1);
INSERT INTO `user_role` (`user_id`, `role_id`) VALUES (2, 2);
INSERT INTO `user_role` (`user_id`, `role_id`) VALUES (3, 2);

INSERT INTO `menu` (`menu_id`, `component`, `path`, `redirect`, `name`, `title`, `icon`, `parent_id`, `is_leaf`, `hidden`)
VALUES
(1, 'Layout', '/sys', '/sys/user', 'sysManage', '系统管理', 'userManage', 0, 'N', 0),
(2, 'sys/user', 'user', '', 'userList', '用户列表', 'user', 1, 'Y', 0),
(3, 'sys/role', 'role', '', 'roleList', '角色列表', 'roleManage', 1, 'Y', 0),
(4, 'Layout', '/conference', '/conference/conferenceList', 'conference', '会议列表', 'form', 0, 'N', 0),
(5, 'conference/review', 'review', '', 'review', '会议审核', 'confcheck', 4, 'Y', 0),
(6, 'conference/conferenceList', 'conferenceList', '', 'conferenceList', '开放会议', 'conflist', 4, 'Y', 0),
(7, 'conference/myConference', 'myConference', '', 'myConference', '我的会议', 'myconf', 4, 'Y', 0),
(8, 'Layout', '/invitation', '/invitation/invitationList', 'invitation', '邀请列表', 'invitation', 0, 'N', 0),
(9, 'invitation/invitationList', 'invitationList', '', 'invitationList', '我的审稿人邀请', 'myInvitation', 8, 'Y', 0),
(10, 'invitation/state', 'state', '', 'state', '审稿人邀请状态', 'invitationState', 8, 'Y', 0),
(11, 'Layout', '/paper', '/paper/mypaper', 'paper', '稿件列表', 'file', 0, 'N', 0),
(12, 'paper/mypaper', 'mypaper', '', 'mypaper', '我的稿件', 'fileupload', 11, 'Y', 0),
(13, 'paper/paperreview', 'paperreview', '', 'paperreview', '审核稿件', 'form', 11, 'Y', 0);


INSERT INTO `role_menu` (`role_id`, `menu_id`) VALUES (1, 1);
INSERT INTO `role_menu` (`role_id`, `menu_id`) VALUES (1, 2);
INSERT INTO `role_menu` (`role_id`, `menu_id`) VALUES (1, 3);
INSERT INTO `role_menu` (`role_id`, `menu_id`) VALUES (1, 4);
INSERT INTO `role_menu` (`role_id`, `menu_id`) VALUES (1, 5);
INSERT INTO `role_menu` (`role_id`, `menu_id`) VALUES (1, 6);
INSERT INTO `role_menu` (`role_id`, `menu_id`) VALUES (1, 7);
INSERT INTO `role_menu` (`role_id`, `menu_id`) VALUES (1, 8);
INSERT INTO `role_menu` (`role_id`, `menu_id`) VALUES (1, 9);
INSERT INTO `role_menu` (`role_id`, `menu_id`) VALUES (1, 10);
INSERT INTO `role_menu` (`role_id`, `menu_id`) VALUES (1, 11);
INSERT INTO `role_menu` (`role_id`, `menu_id`) VALUES (1, 12);
INSERT INTO `role_menu` (`role_id`, `menu_id`) VALUES (1, 13);
INSERT INTO `role_menu` (`role_id`, `menu_id`) VALUES (2, 4);
INSERT INTO `role_menu` (`role_id`, `menu_id`) VALUES (2, 6);
INSERT INTO `role_menu` (`role_id`, `menu_id`) VALUES (2, 7);
INSERT INTO `role_menu` (`role_id`, `menu_id`) VALUES (2, 8);
INSERT INTO `role_menu` (`role_id`, `menu_id`) VALUES (2, 9);
INSERT INTO `role_menu` (`role_id`, `menu_id`) VALUES (2, 11);
INSERT INTO `role_menu` (`role_id`, `menu_id`) VALUES (2, 12);
INSERT INTO `role_menu` (`role_id`, `menu_id`) VALUES (3, 4);
INSERT INTO `role_menu` (`role_id`, `menu_id`) VALUES (3, 6);
INSERT INTO `role_menu` (`role_id`, `menu_id`) VALUES (3, 7);
INSERT INTO `role_menu` (`role_id`, `menu_id`) VALUES (3, 8);
INSERT INTO `role_menu` (`role_id`, `menu_id`) VALUES (3, 9);
INSERT INTO `role_menu` (`role_id`, `menu_id`) VALUES (3, 10);
INSERT INTO `role_menu` (`role_id`, `menu_id`) VALUES (3, 11);
INSERT INTO `role_menu` (`role_id`, `menu_id`) VALUES (3, 12);
INSERT INTO `role_menu` (`role_id`, `menu_id`) VALUES (4, 13);


INSERT INTO `Conferences` (ConferenceId, chair_id, short_name, full_name, event_date, event_Location, submission_deadline, reviewresult_date, status) VALUES (1, 1, 'a', 'aaa', '2023-10-31', 'SHANGHAI', '2023-11-01', '2023-11-01', '开放投稿');
INSERT INTO `Conferences` (ConferenceId, chair_id, short_name, full_name, event_date, event_Location, submission_deadline, reviewresult_date, status) VALUES (2, 1, 'b', 'bbb', '2023-08-31', 'SHANGHAI', '2023-10-01', '2023-11-15', '接受');
INSERT INTO `Conferences` (ConferenceId, chair_id, short_name, full_name, event_date, event_Location, submission_deadline, reviewresult_date, status) VALUES (3, 1, 'c', 'ccc', '2023-05-30', 'BEIJING', '2023-12-01', '2024-02-15', '拒绝');

INSERT INTO `Topics` (`id`, `conference_id`, `topic_name`) VALUES ('1', '1', 'topic1');
INSERT INTO `Topics` (`id`, `conference_id`, `topic_name`) VALUES ('2', '1', 'topic2');
INSERT INTO `Topics` (`id`, `conference_id`, `topic_name`) VALUES ('3', '1', 'topic3');
INSERT INTO `Topics` (`id`, `conference_id`, `topic_name`) VALUES ('4', '2', 'topic1');
INSERT INTO `Topics` (`id`, `conference_id`, `topic_name`) VALUES ('5', '3', 'topic3');
