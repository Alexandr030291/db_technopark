CREATE TABLE technopark.User
(
  id INT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(50) CHARACTER SET 'utf8' NOT NULL,
  name VARCHAR(50) CHARACTER SET 'utf8',
  username VARCHAR(50) CHARACTER SET 'utf8',
  about VARCHAR(50) CHARACTER SET 'utf8',
  isAnonymous BOOLEAN
);
CREATE UNIQUE INDEX User_email_uindex ON technopark.User (email);

CREATE TABLE technopark.Form
(
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) CHARACTER SET 'utf8' NOT NULL,
  short_name VARCHAR(50) CHARACTER SET 'utf8' NOT NULL,
  user VARCHAR(50) CHARACTER SET 'utf8' NOT NULL,
  CONSTRAINT Forum_User_email_fk FOREIGN KEY (user) REFERENCES User (email)
);
CREATE UNIQUE INDEX Forum_name_uindex ON technopark.Forum (name);
CREATE UNIQUE INDEX Forum_short_name_uindex ON technopark.Forum (short_name);

CREATE TABLE technopark.Post
(
  id INT PRIMARY KEY AUTO_INCREMENT,
  date DATETIME,
  thread INT,
  message TEXT CHARACTER SET 'utf8',
  user VARCHAR(50) CHARACTER SET 'utf8' NOT NULL,
  CONSTRAINT Post_User_email_fk FOREIGN KEY (user) REFERENCES User (email),
  forum VARCHAR(50) CHARACTER SET 'utf8' NOT NULL,
  CONSTRAINT Post_Forum_short_name_fk FOREIGN KEY (forum) REFERENCES Forum (short_name),

  parent INT,
  isApproved BOOLEAN,
  isHighlighted BOOLEAN,
  isEdited BOOLEAN,
  isSpam BOOLEAN,
  isDelete BOOLEAN
);

CREATE TABLE technopark.Thread
(
  id INT PRIMARY KEY AUTO_INCREMENT,
  forum VARCHAR(50) CHARACTER SET 'utf8' NOT NULL,
  CONSTRAINT Thread_Forum_short_name_fk FOREIGN KEY (forum) REFERENCES Forum (short_name),
  title VARCHAR(50) CHARACTER SET 'utf8',
  isClosed BOOLEAN,
  user VARCHAR(50) CHARACTER SET 'utf8' NOT NULL,
  CONSTRAINT Thread_User_email_fk FOREIGN KEY (user) REFERENCES User (email),
  date DATETIME,
  message TEXT CHARACTER SET 'utf8',
  slug VARCHAR(50) CHARACTER SET 'utf8',

  isDelete BOOLEAN
)