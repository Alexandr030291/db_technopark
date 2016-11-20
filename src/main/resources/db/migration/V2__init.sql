CREATE TABLE `LastId`
(
  `table` VARCHAR(50) NOT NULL UNIQUE KEY ,
  `count` INT NOT NULL DEFAULT 0
);
INSERT INTO `LastId` (`table`, `count`) VALUES ('thread', '0');
INSERT INTO `LastId` (`table`, `count`) VALUES ('post', '0');