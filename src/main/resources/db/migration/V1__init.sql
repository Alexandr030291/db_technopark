CREATE TABLE `User`
(
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `email` VARCHAR(50) NOT NULL,
  `name` VARCHAR(50),
  `user_name` VARCHAR(50),
  `about` VARCHAR(50),
  `isAnonymous` BOOLEAN
);
CREATE UNIQUE INDEX `User_email_uindex` ON `User` (`email`);

CREATE TABLE `Forum`
(
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `short_name` VARCHAR(50) NOT NULL,
  `userService` VARCHAR(50) NOT NULL,
  FOREIGN KEY (`userService`) REFERENCES `User`(`email`)
  ON DELETE CASCADE
);
CREATE UNIQUE INDEX `Forum_name_uindex` ON `Forum` (`name`);
CREATE UNIQUE INDEX `Forum_short_name_uindex` ON `Forum` (`short_name`);

CREATE TABLE `Post`
(
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `forumService` VARCHAR(50) NOT NULL,
  `userService` VARCHAR(50) NOT NULL,
  `thread` INT,
  `message` TEXT,
  `date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  `parent` INT,
  `isApproved` BOOLEAN,
  `isHighlighted` BOOLEAN,
  `isEdited` BOOLEAN,
  `isSpam` BOOLEAN,
  `isDelete` BOOLEAN,

  likes INT NOT NULL DEFAULT 0,
  dislikes INT NOT NULL DEFAULT 0,
  mpath VARCHAR(255),

  FOREIGN KEY (`userService`) REFERENCES `User` (`email`)
  ON DELETE CASCADE,
  FOREIGN KEY (`forumService`) REFERENCES `Forum` (`short_name`)
  ON DELETE CASCADE
);

CREATE TABLE `Thread`
(
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `forumService` VARCHAR(50) NOT NULL,
  `userService` VARCHAR(50) NOT NULL,
  `title` VARCHAR(50),
  `message` TEXT,
  `slug` VARCHAR(50),
  `date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  `isClosed` BOOLEAN,
  `isDelete` BOOLEAN,

  likes INT NOT NULL DEFAULT 0,
  dislikes INT NOT NULL DEFAULT 0,

  FOREIGN KEY (`userService`) REFERENCES `User` (`email`)
  ON DELETE CASCADE,
  FOREIGN KEY (`forumService`) REFERENCES `Forum` (`short_name`)
  ON DELETE CASCADE
);

CREATE TABLE `Subscriptions`
(
  `userService` VARCHAR(50) NOT NULL,
  `thread` INT NOT NULL,
  UNIQUE (`userService`,`thread`),

  FOREIGN KEY (`userService`) REFERENCES `User` (`email`)
  ON DELETE CASCADE,
  FOREIGN KEY (`thread`) REFERENCES `Thread`(`id`)
  ON DELETE CASCADE
);

CREATE TABLE `Followers`
(
  `follower` VARCHAR(50) NOT NULL,
  `followee` VARCHAR(50) NOT NULL,
  UNIQUE(`follower`, `followee`),

  FOREIGN KEY (`follower`) REFERENCES `User` (`email`)
  ON DELETE CASCADE,
  FOREIGN KEY (`followee`) REFERENCES `User` (`email`)
  ON DELETE CASCADE
);
