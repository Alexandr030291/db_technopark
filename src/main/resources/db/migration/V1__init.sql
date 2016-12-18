CREATE TABLE `UserProfile`
(
  `id` INT NOT NULL UNIQUE KEY PRIMARY KEY,
  `name` VARCHAR(50),
  `username` VARCHAR(50),
  `about` TEXT,
  `isAnonymous` BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE `Users`
(
  `id` INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
  `email` VARCHAR(50) NOT NULL UNIQUE KEY
);

CREATE TABLE `ForumDetail`
(
  `id` INT NOT NULL UNIQUE KEY PRIMARY KEY,
  `name` VARCHAR(50) NOT NULL UNIQUE KEY,
  `user` INT NOT NULL
);

CREATE TABLE `Forums`
(
  `id` INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
  `short_name` VARCHAR(50) NOT NULL UNIQUE KEY
);

CREATE TABLE `UsersOfForum`
(
  `user` INT NOT NULL,
  `forum` INT NOT NULL,
  `user_name` VARCHAR(50),
  UNIQUE (`user`,`forum`,`user_name`)
);

CREATE TABLE `Post`
(
  `id` INT NOT NULL PRIMARY KEY UNIQUE KEY ,
  `forum` INT NOT NULL,
  `user` INT NOT NULL,
  `thread` INT NOT NULL ,
  `message` TEXT NOT NULL ,
  `date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  `parent` INT NULL DEFAULT NULL ,
  `isApproved` BOOLEAN NOT NULL DEFAULT FALSE ,
  `isHighlighted` BOOLEAN NOT NULL DEFAULT FALSE ,
  `isEdited` BOOLEAN NOT NULL DEFAULT FALSE ,
  `isSpam` BOOLEAN NOT NULL DEFAULT FALSE ,
  `isDeleted` BOOLEAN NOT NULL DEFAULT FALSE ,

  `likes` INT NOT NULL DEFAULT 0,
  `dislikes` INT NOT NULL DEFAULT 0,
  `mpath` VARCHAR(255),
  `root` INT NOT NULL DEFAULT 0
);

CREATE TABLE `Thread`
(
  `id` INT NOT NULL PRIMARY KEY UNIQUE KEY ,
  `forum` INT NOT NULL,
  `user` INT NOT NULL,
  `title` VARCHAR(50) NOT NULL ,
  `message` TEXT NOT NULL ,
  `slug` VARCHAR(50) NOT NULL ,
  `date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  `isClosed` BOOLEAN NOT NULL DEFAULT FALSE ,
  `isDeleted` BOOLEAN NOT NULL DEFAULT FALSE ,

  `likes` INT NOT NULL DEFAULT 0,
  `dislikes` INT NOT NULL DEFAULT 0,
  `posts` INT NOT NULL DEFAULT 0
);

CREATE TABLE `Subscriptions`
(
  `user` INT NOT NULL,
  `thread` INT NOT NULL,
  UNIQUE (`user`,`thread`)
);

CREATE TABLE `Followers`
(
  `follower` INT NOT NULL,
  `followee` INT NOT NULL,
  UNIQUE(`follower`, `followee`)
);

CREATE TABLE `LastId`
(
  `table` VARCHAR(50) NOT NULL UNIQUE KEY ,
  `count` INT NOT NULL DEFAULT 0
);
INSERT INTO `LastId` (`table`, `count`) VALUES ('thread', '0');
INSERT INTO `LastId` (`table`, `count`) VALUES ('post', '0');
INSERT INTO `LastId` (`table`, `count`) VALUES ('forum', '0');
INSERT INTO `LastId` (`table`, `count`) VALUES ('user', '0');

ALTER TABLE `Post` engine = MyISAM;

CREATE INDEX `usersForum` ON UsersOfForum(`forum`,`user`,`user_name`);
CREATE INDEX `postUserAndData` ON `Post`(`user`,`date`);
create index `postRootAndTreadAndData` on `Post`(`thread`,`date`,`root`);
CREATE INDEX `postForumAndDate` ON `Post`(`forum`,`date`);
CREATE INDEX `threadForumAndDate` ON `Thread`(`forum`,`date`);