CREATE TABLE User
(
  id INT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(50) NOT NULL,
  name VARCHAR(50),
  user_name VARCHAR(50),
  about VARCHAR(50),
  isAnonymous BOOLEAN
);
CREATE UNIQUE INDEX User_email_uindex ON User (email);

CREATE TABLE Forum
(
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  short_name VARCHAR(50) NOT NULL,
  user VARCHAR(50) NOT NULL,
  CONSTRAINT Forum_User_email_fk FOREIGN KEY (user) REFERENCES User (email)
);
CREATE UNIQUE INDEX Forum_name_uindex ON Forum (name);
CREATE UNIQUE INDEX Forum_short_name_uindex ON Forum (short_name);

CREATE TABLE Post
(
  id INT PRIMARY KEY AUTO_INCREMENT,
  date DATETIME,
  thread INT,
  message TEXT,
  user VARCHAR(50) NOT NULL,
  CONSTRAINT Post_User_email_fk FOREIGN KEY (user) REFERENCES User (email),
  forum VARCHAR(50) NOT NULL,
  CONSTRAINT Post_Forum_short_name_fk FOREIGN KEY (forum) REFERENCES Forum (short_name),

  parent INT,
  isApproved BOOLEAN,
  isHighlighted BOOLEAN,
  isEdited BOOLEAN,
  isSpam BOOLEAN,
  isDelete BOOLEAN
);

CREATE TABLE Thread
(
  id INT PRIMARY KEY AUTO_INCREMENT,
  forum VARCHAR(50) NOT NULL,
  CONSTRAINT Thread_Forum_short_name_fk FOREIGN KEY (forum) REFERENCES Forum (short_name),
  title VARCHAR(50),
  isClosed BOOLEAN,
  user VARCHAR(50) NOT NULL,
  CONSTRAINT Thread_User_email_fk FOREIGN KEY (user) REFERENCES User (email),
  date DATETIME,
  message TEXT,
  slug VARCHAR(50),

  isDelete BOOLEAN
);

CREATE TABLE Subscriptions
(
  user VARCHAR(50) NOT NULL,
  CONSTRAINT Subscriptions_User_email_fk FOREIGN KEY (user) REFERENCES User(email),
  thread INT NOT NULL,
  CONSTRAINT Subscriptions_Thread_id_fk FOREIGN KEY (thread) REFERENCES Thread(id)
);

CREATE TABLE Followers
(
  id INT NOT NULL,
  CONSTRAINT Followers_User_id_fk FOREIGN KEY (id) REFERENCES User (id),
  followee VARCHAR(50) NOT NULL,
  CONSTRAINT Followers_User_email_fk FOREIGN KEY (followee) REFERENCES User (email)
);