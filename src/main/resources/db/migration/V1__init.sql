CREATE TABLE `UserProfile`
(
  `id` INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
  `email` VARCHAR(50) NOT NULL UNIQUE KEY,
  `name` VARCHAR(50),
  `username` VARCHAR(50),
  `about` TEXT,
  `isAnonymous` BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE `Forum`
(
  `id` INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
  `name` VARCHAR(50) NOT NULL UNIQUE KEY,
  `short_name` VARCHAR(50) NOT NULL UNIQUE KEY,
  `user` VARCHAR(50) NOT NULL
);

CREATE TABLE `Post`
(
  `id` INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
  `forum` VARCHAR(50) NOT NULL,
  `user` VARCHAR(50) NOT NULL,
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
  `mpath` VARCHAR(255)
);

CREATE TABLE `Thread`
(
  `id` INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
  `forum` VARCHAR(50) NOT NULL,
  `user` VARCHAR(50) NOT NULL,
  `title` VARCHAR(50) NOT NULL ,
  `message` TEXT NOT NULL ,
  `slug` VARCHAR(50) NOT NULL ,
  `date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  `isClosed` BOOLEAN NOT NULL DEFAULT FALSE ,
  `isDeleted` BOOLEAN NOT NULL DEFAULT FALSE ,

  `likes` INT NOT NULL DEFAULT 0,
  `dislikes` INT NOT NULL DEFAULT 0
);

CREATE TABLE `Subscriptions`
(
  `user` VARCHAR(50) NOT NULL,
  `thread` INT NOT NULL,
  UNIQUE (`user`,`thread`)
);

CREATE TABLE `Followers`
(
  `follower` VARCHAR(50) NOT NULL,
  `followee` VARCHAR(50) NOT NULL,
  UNIQUE(`follower`, `followee`)
);

ALTER TABLE `Forum`
  ADD CONSTRAINT `fk_Forum_1`
  FOREIGN KEY (`user`)
  REFERENCES `UserProfile` (`email`)
  ON DELETE CASCADE;

ALTER TABLE `Post`
  ADD constraint `fk_Post_1`
  FOREIGN KEY (`user`) REFERENCES `UserProfile` (`email`)
  ON DELETE CASCADE;

ALTER TABLE `Post`
  ADD constraint `fk_Post_2`
  FOREIGN KEY `Post`(`forum`) REFERENCES `Forum` (`short_name`)
  ON DELETE CASCADE;

ALTER TABLE `Post`
  ADD constraint `fk_Post_3`
  FOREIGN KEY (`thread`) REFERENCES `Thread`(`id`)
  ON DELETE CASCADE;


ALTER TABLE `Thread`
  ADD constraint `fk_Thread_1`
  FOREIGN KEY (`user`) REFERENCES `UserProfile` (`email`)
  ON DELETE CASCADE;

ALTER TABLE `Thread`
  ADD constraint `fk_Thread_2`
  FOREIGN KEY (`forum`) REFERENCES `Forum` (`short_name`)
  ON DELETE CASCADE;

ALTER TABLE `Subscriptions`
  ADD constraint `fk_Subscriptions_1`
  FOREIGN KEY (`user`) REFERENCES `UserProfile` (`email`)
  ON DELETE CASCADE;

ALTER TABLE `Subscriptions`
  ADD constraint `fk_Subscriptions_2`
  FOREIGN KEY (`thread`) REFERENCES `Thread`(`id`)
  ON DELETE CASCADE;

ALTER TABLE `Followers`
  ADD constraint `fk_Followers_1`
  FOREIGN KEY (`follower`) REFERENCES `UserProfile` (`email`)
  ON DELETE CASCADE;

ALTER TABLE `Followers`
  ADD constraint `fk_Followers_2`
  FOREIGN KEY (`followee`) REFERENCES `UserProfile` (`email`)
  ON DELETE CASCADE;