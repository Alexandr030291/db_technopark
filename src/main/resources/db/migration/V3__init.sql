ALTER TABLE `Post` engine = MyISAM;

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

ALTER TABLE `UsersOfForum`
  ADD CONSTRAINT `fk_UsersOfForum_1`
FOREIGN KEY (`user`)
REFERENCES `Users` (`id`)
  ON DELETE CASCADE ;

ALTER TABLE `UsersOfForum`
  ADD CONSTRAINT `fk_UsersOfForum_2`
FOREIGN KEY (`forum`)
REFERENCES `Forums` (`id`)
  ON DELETE CASCADE ;

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