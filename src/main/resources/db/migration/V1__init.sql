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

CREATE TABLE `Post`
(
  `id` INT NOT NULL PRIMARY KEY UNIQUE KEY ,
  `forum` INT NOT NULL,
  `user` INT NOT NULL,
  `thread` INT NOT NULL ,
  `message` TEXT NOT NULL ,
  `date` DATETIME NOT NULL,

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
  `date` DATETIME NOT NULL,

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

ALTER TABLE `UserProfile`
  ADD CONSTRAINT `fk_UserProfile_1`
FOREIGN KEY (`id`)
REFERENCES `Users` (`id`)
  ON DELETE CASCADE;

ALTER TABLE `ForumDetail`
  ADD CONSTRAINT `fk_ForumDetail_1`
FOREIGN KEY (`id`)
REFERENCES `Forums` (`id`)
  ON DELETE CASCADE;

ALTER TABLE `ForumDetail`
  ADD CONSTRAINT `fk_ForumDetail_2`
  FOREIGN KEY (`user`)
  REFERENCES `Users` (`id`)
  ON DELETE CASCADE;

ALTER TABLE `Post`
  ADD constraint `fk_Post_1`
  FOREIGN KEY (`user`) REFERENCES `Users` (`id`)
  ON DELETE CASCADE;

ALTER TABLE `Post`
  ADD constraint `fk_Post_2`
  FOREIGN KEY `Post`(`forum`) REFERENCES `Forums` (`id`)
  ON DELETE CASCADE;

ALTER TABLE `Post`
  ADD constraint `fk_Post_3`
  FOREIGN KEY (`thread`) REFERENCES `Thread`(`id`)
  ON DELETE CASCADE;


ALTER TABLE `Thread`
  ADD constraint `fk_Thread_1`
  FOREIGN KEY (`user`) REFERENCES `Users` (`id`)
  ON DELETE CASCADE;

ALTER TABLE `Thread`
  ADD constraint `fk_Thread_2`
  FOREIGN KEY (`forum`) REFERENCES `Forums` (`id`)
  ON DELETE CASCADE;

ALTER TABLE `Subscriptions`
  ADD constraint `fk_Subscriptions_1`
  FOREIGN KEY (`user`) REFERENCES `Users` (`id`)
  ON DELETE CASCADE;

ALTER TABLE `Subscriptions`
  ADD constraint `fk_Subscriptions_2`
  FOREIGN KEY (`thread`) REFERENCES `Thread`(`id`)
  ON DELETE CASCADE;

ALTER TABLE `Followers`
  ADD constraint `fk_Followers_1`
  FOREIGN KEY (`follower`) REFERENCES `Users` (`id`)
  ON DELETE CASCADE;

ALTER TABLE `Followers`
  ADD constraint `fk_Followers_2`
  FOREIGN KEY (`followee`) REFERENCES `Users` (`id`)
  ON DELETE CASCADE;