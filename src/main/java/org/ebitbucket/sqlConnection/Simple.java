/*
package ru.mail.park.main;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mysql.fabric.jdbc.FabricMySQLDriver;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.model.Forum;
import ru.mail.park.model.ForumThread;
import ru.mail.park.model.Post;
import ru.mail.park.model.UserProfile;

import java.sql.*;


@RestController
public class RegistrationController{

    static final String URL = "jdbc:mysql://localhost:3306/dbtest";
    static final String USERNAME = "someroot";
    static final String PASSWORD = "somepassword";


    @RequestMapping(path = "db/api/clear", method = RequestMethod.GET)
    public ResponseEntity clear() {
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );

            final String postsDrop = "Drop table if exists `posts`;";
            PreparedStatement preparedStatementDropTablePosts = connection.prepareStatement(postsDrop);
            preparedStatementDropTablePosts.executeUpdate();
            final String subscribeDrop = "drop table if exists `subscribe`";
            PreparedStatement preparedStatementDropTableSubscribe = connection.prepareStatement(subscribeDrop);
            preparedStatementDropTableSubscribe.executeUpdate();
            final String threadsDrop = "Drop table if exists `threads`;";
            PreparedStatement preparedStatementDropTableThreads = connection.prepareStatement(threadsDrop);
            preparedStatementDropTableThreads.executeUpdate();
            final String forumsDrop = "Drop table if exists `forums`;";
            PreparedStatement preparedStatementDropTableForums = connection.prepareStatement(forumsDrop);
            preparedStatementDropTableForums.executeUpdate();
            final String followersDrop = "Drop table if exists `followers`;";
            PreparedStatement preparedStatementDropTableFollowers = connection.prepareStatement(followersDrop);
            preparedStatementDropTableFollowers.executeUpdate();
            final String usersDrop = "Drop table if exists `users`;";
            PreparedStatement preparedStatementDropTableUsers = connection.prepareStatement(usersDrop);
            preparedStatementDropTableUsers.executeUpdate();

            final String usersCreate = "CREATE TABLE `dbtest`.`users` (" +
                    "  `id` INT NOT NULL AUTO_INCREMENT," +
                    "  `isAnonymos` TINYINT(1) NOT NULL," +
                    "  `name` VARCHAR(45) CHARACTER SET 'utf8' NOT NULL," +
                    "  `about` VARCHAR(60) CHARACTER SET 'utf8' NOT NULL," +
                    "  `email` VARCHAR(60) CHARACTER SET 'utf8' NOT NULL," +
                    "  `username` VARCHAR(45) CHARACTER SET 'utf8' NOT NULL," +
                    "UNIQUE key(email)," +
                    "  PRIMARY KEY (`id`));";
            PreparedStatement preparedStatementCreateTableUsers = connection.prepareStatement(usersCreate);
            preparedStatementCreateTableUsers.executeUpdate();
            final String followCreate = "CREATE TABLE `dbtest`.`followers` (" +
                    "  `id` INT NOT NULL AUTO_INCREMENT," +
                    "  `follower_id` INT NOT NULL," +
                    "  `following_id` INT NULL," +
                    "  foreign key (`follower_id`) references `users`(`id`) on delete cascade," +
                    "  foreign key (`following_id`) references `users`(`id`) on delete cascade," +
                    "  UNIQUE key(`follower_id`, `following_id`)," +
                    "  PRIMARY KEY (`id`)" +
                    "  );";
            PreparedStatement preparedStatementCreateTableFollow = connection.prepareStatement(followCreate);
            preparedStatementCreateTableFollow.executeUpdate();
            final String forumsCreate = "  CREATE TABLE `dbtest`.`forums` (" +
                    "  `id` INT NOT NULL AUTO_INCREMENT," +
                    "  `name` VARCHAR(60) CHARACTER SET 'utf8' NOT NULL," +
                    "  `short_name` VARCHAR(60) CHARACTER SET 'utf8' NOT NULL," +
                    "  `user_id` INT NOT NULL," +
                    "  foreign key (`user_id`) references `users`(`id`) on delete cascade," +
                    "  PRIMARY KEY (`id`)," +
                    "  UNIQUE INDEX `name_UNIQUE` (`name` ASC)," +
                    "  UNIQUE INDEX `short_name_UNIQUE` (`short_name` ASC)" +
                    "  );";
            PreparedStatement preparedStatementCreateTableForums = connection.prepareStatement(forumsCreate);
            preparedStatementCreateTableForums.executeUpdate();
            final String threadsCreate = "CREATE TABLE `dbtest`.`threads` (" +
                    "  `id` INT NOT NULL AUTO_INCREMENT," +
                    "  `user_id` INT NOT NULL," +
                    "  `forum_id` INT NOT NULL," +
                    "  `title` VARCHAR(45) CHARACTER SET 'utf8' NOT NULL," +
                    "  `isClosed` INT(1) NOT NULL," +
                    "  `isDeleted` INT(1) NOT NULL," +
                    "  `date` DATETIME NOT NULL," +
                    "  `message` MEDIUMTEXT CHARACTER SET 'utf8' NOT NULL," +
                    "  `slug` VARCHAR(45) CHARACTER SET 'utf8' NOT NULL," +
                    "  `likes` INT NOT NULL DEFAULT 0," +
                    "  `dislikes` INT NOT NULL DEFAULT 0," +
                    "  `points` INT NOT NULL DEFAULT 0," +
                    "  foreign key (`user_id`) references `users`(`id`) on delete cascade," +
                    "  foreign key (`forum_id`) references `forums`(`id`) on delete cascade," +
                    "  PRIMARY KEY (`id`));";
            PreparedStatement preparedStatementCreateTableThreads = connection.prepareStatement(threadsCreate);
            preparedStatementCreateTableThreads.executeUpdate();
            final String subscribeCreate = "CREATE TABLE `dbtest`.`subscribe` (" +
                    "  `id` INT NOT NULL AUTO_INCREMENT," +
                    "  `user_id` INT NOT NULL," +
                    "  `thread_id` INT NOT NULL," +
                    "  foreign key (`user_id`) references `users`(`id`) on delete cascade," +
                    "  foreign key (`thread_id`) references `threads`(`id`) on delete cascade," +
                    "  PRIMARY KEY (`id`));";
            PreparedStatement preparedStatementCreateTableSubscribe = connection.prepareStatement(subscribeCreate);
            preparedStatementCreateTableSubscribe.executeUpdate();
            final String postsCreate = "  CREATE TABLE `dbtest`.`posts` (" +
                    "  `id` INT NOT NULL AUTO_INCREMENT," +
                    "  `thread_id` INT NOT NULL," +
                    "  `message` MEDIUMTEXT CHARACTER SET 'utf8' NOT NULL," +
                    "  `date` DATETIME NOT NULL," +
                    "  `user_id` INT NOT NULL," +
                    "  `forum_id` INT NOT NULL," +
                    "  `parent_id` INT NULL," +
                    "  `isApproved` TINYINT(1) NOT NULL," +
                    "  `isHighlighted` TINYINT(1) NOT NULL," +
                    "  `isEdited` TINYINT(1) NOT NULL," +
                    "  `isSpam` TINYINT(1) NOT NULL," +
                    "  `isDeleted` TINYINT(1) NOT NULL DEFAULT 0," +
                    "  `likes` INT NOT NULL DEFAULT 0," +
                    "  `dislikes` INT NOT NULL DEFAULT 0," +
                    "  `points` INT NOT NULL DEFAULT 0," +
                    "  foreign key (`user_id`) references `users`(`id`) on delete cascade," +
                    "  foreign key (`forum_id`) references `forums`(`id`) on delete cascade," +
                    "  foreign key (`thread_id`) references `threads`(`id`) on delete cascade," +
                    "  foreign key (`parent_id`) references `posts`(`id`) on delete cascade," +
                    "  PRIMARY KEY (`id`));";
            PreparedStatement preparedStatementCreateTablePosts = connection.prepareStatement(postsCreate);
            preparedStatementCreateTablePosts.executeUpdate();

            connection.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        SuccessResponse response = new SuccessResponse(0,"\"OK\"");
        return ResponseEntity.ok(response.createJSONResponce());
    }

    @RequestMapping(path = "db/api/status/", method = RequestMethod.GET)
    public ResponseEntity status() {
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );

            String response = "{\"user\": ";
            final String usersCount = "Select count(*) from users;";
            ResultSet resultSet = connection.createStatement().executeQuery(usersCount);
            while(resultSet.next()) {
                response = response + resultSet.getInt(1);
            }
            response = response + ", \"thread\": ";
            final String threadsCount = "Select count(*) from threads;";
            resultSet = connection.createStatement().executeQuery(threadsCount);
            while(resultSet.next()) {
                response = response + resultSet.getInt(1);
            }
            response = response + ", \"forum\": ";
            final String forumsCount = "Select count(*) from forums;";
            resultSet = connection.createStatement().executeQuery(forumsCount);
            while(resultSet.next()) {
                response = response + resultSet.getInt(1);
            }
            response = response + ", \"post\": ";
            final String postsCount = "Select count(*) from posts;";
            resultSet = connection.createStatement().executeQuery(postsCount);
            while(resultSet.next()) {
                response = response + resultSet.getInt(1);
            }
            response = response + " }";
            connection.close();
            SuccessResponse successResponse = new SuccessResponse(0,response);
            return ResponseEntity.ok(successResponse.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        SuccessResponse response = new SuccessResponse(0,"Something go wrong");
        return ResponseEntity.ok(response.createJSONResponce());
    }
    @RequestMapping(path = "/db/api/user/create/", method=RequestMethod.POST)
    public ResponseEntity createUser(@RequestBody CreateUserRequest body){
        final String username = body.getUsername();
        final String about = body.getAbout();
        Boolean isAnonymos = body.getAnonymos();
        if(isAnonymos==null) {
            isAnonymos = false;
        }
        final String name = body.getName();
        final String email = body.getEmail();
        UserProfile user = new UserProfile(isAnonymos, email, name, about, username);
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );
            final String createUser = user.incert("users");
            PreparedStatement preparedStatementCreateUser = connection.prepareStatement(createUser);
            preparedStatementCreateUser.executeUpdate();
            final String getUserId = "Select id from users where email = '" + email + "';";
            ResultSet resultSet = connection.createStatement().executeQuery(getUserId);
            while(resultSet.next()) {
                user.setId(resultSet.getInt(1));
            }
            connection.close();
            SuccessResponse response = new SuccessResponse(0,user.toJSON());
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/forum/create/", method=RequestMethod.POST)
    public ResponseEntity forumCreate(@RequestBody CreateForumRequest body) {
        final String name = body.getName();
        final String short_name = body.getShort_name();
        final String user_email = body.getUser_email();
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );
            Forum forum = new Forum(name, short_name, user_email);
            final String getUserId = "Select id from users where email = '" + user_email + "';";
            ResultSet resultSet = connection.createStatement().executeQuery(getUserId);
            while(resultSet.next()) {
                forum.setUser_id(resultSet.getInt(1));
            }
            final String createForum = forum.incert();
            PreparedStatement preparedStatementCreateForum = connection.prepareStatement(createForum);
            preparedStatementCreateForum.executeUpdate();
            final String getForumId = "Select id from forums where name = '" + name + "';";
            resultSet = connection.createStatement().executeQuery(getForumId);
            while(resultSet.next()) {
                forum.setId(resultSet.getInt(1));
            }
            connection.close();
            SuccessResponse response = new SuccessResponse(0,forum.toJSON());
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/thread/create/", method = RequestMethod.POST)
    public ResponseEntity threadCreate(@RequestBody CreateThreadRequest body) {
        final String date = body.getDate();
        final String forumName = body.getForum_name();
        final Boolean isClosed = body.isClosed();
        Boolean isDeleted = body.isDeleted();
        if(isDeleted==null){
            isDeleted = false;
        }
        final String message = body.getMessage();
        final String slug = body.getSlug();
        final String title = body.getTitle();
        final String userEmail = body.getUser_email();

        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );
            ForumThread forumThread = new ForumThread(date, isClosed, isDeleted, message,
                    slug, title, userEmail, forumName);

            final String getUserId = "Select id from users where email = '" + userEmail + "';";
            ResultSet resultSet = connection.createStatement().executeQuery(getUserId);
            while(resultSet.next()) {
                forumThread.setUser_id(resultSet.getInt(1));
            }

            final String getForumId = "Select id from forums where name = '" + forumName + "';";
            resultSet = connection.createStatement().executeQuery(getForumId);
            while(resultSet.next()) {
                forumThread.setForum_id(resultSet.getInt(1));
            }

            final String createThread = forumThread.insert();
            PreparedStatement preparedStatementCreateThread = connection.prepareStatement(createThread);
            preparedStatementCreateThread.executeUpdate();

            StringBuilder builder = new StringBuilder();
            builder.append("Select id from threads where title = '");
            builder.append(title);
            builder.append("' and message = '");
            builder.append(message);
            builder.append("' and slug = '");
            builder.append(slug);
            builder.append("' and date = '");
            builder.append(date);
            builder.append("'");
            final String getThreadId = builder.toString();//"Select id from threads where title = '" + title + "' and;"; //title - не уникальный
            resultSet = connection.createStatement().executeQuery(getThreadId);
            while(resultSet.next()) {
                forumThread.setId(resultSet.getInt(1));
            }
            connection.close();
            SuccessResponse response = new SuccessResponse(0,forumThread.toJSON());
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/thread/details/", method = RequestMethod.GET)
    public ResponseEntity threadDetails(@RequestBody ThreadStatusChangeRequest body) {
        Integer id = body.getThreadId();
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );

            StringBuilder builder = new StringBuilder();
            builder.append("Select * from threads where id = ");
            builder.append(id);
            String getPost = builder.toString();
            ResultSet resultSet = connection.createStatement().executeQuery(getPost);
            Integer userId = -1;
            Integer forumId = -1;
            String title = "";
            Boolean isClosed = true;
            Boolean isDeleted = false;
            String date = "";
            String message = "";
            String slug = "";
            Integer likes = 0;
            Integer dislikes = 0;
            Integer points = 0;

            while(resultSet.next()) {
                userId = resultSet.getInt("user_id");
                forumId = resultSet.getInt("forum_id");
                title = resultSet.getString("title");

                isDeleted = resultSet.getBoolean("isDeleted");
                date = resultSet.getString("date");
                message = resultSet.getString("message");
                slug = resultSet.getString("slug");
                likes = resultSet.getInt("likes");
                dislikes = resultSet.getInt("dislikes");
                points = resultSet.getInt("points");
            }

            ForumThread forumThread = new ForumThread(id, userId, forumId, title,
                    isClosed, isDeleted, date, message, slug, likes, dislikes, points);

            builder.setLength(0);
            builder.append("Select email from users where id = ");
            builder.append(userId);
            builder.append(";");
            final String getUserEmail = builder.toString();
            resultSet = connection.createStatement().executeQuery(getUserEmail);
            while(resultSet.next()) {
                forumThread.setUserEmail(resultSet.getString(1));
            }

            builder.setLength(0);
            builder.append("Select name from forums where id =");
            builder.append(forumId);
            builder.append(";");
            final String getForumName = builder.toString();
            resultSet = connection.createStatement().executeQuery(getForumName);
            while(resultSet.next()) {
                forumThread.setForumName(resultSet.getString(1));
            }

            builder.setLength(0);
            builder.append("Select count(*) from posts where thread_id =");
            builder.append(id);
            builder.append(";");
            final String getPostsCount = builder.toString();
            resultSet = connection.createStatement().executeQuery(getPostsCount);
            while(resultSet.next()) {
                forumThread.setPosts(resultSet.getInt(1));
            }

            connection.close();
            SuccessResponse response = new SuccessResponse(0,forumThread.toJSONDetails());
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/thread/update/", method = RequestMethod.POST)
    public ResponseEntity threadUpdate(@RequestBody UpdateThreadRequest body) {
        String message = body.getMessage();
        String slug = body.getSlug();
        Integer id = body.getThreadId();
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );

            StringBuilder builder = new StringBuilder();
            builder.append("Update threads set message = \"");
            builder.append(message);
            builder.append("\", slug = \"");
            builder.append(slug);
            builder.append("\"  where id = ");
            builder.append(id);
            String UpdateThread = builder.toString();
            PreparedStatement preparedStatementUpdateThread = connection.prepareStatement(UpdateThread);
            preparedStatementUpdateThread.executeUpdate();

            builder.setLength(0);
            builder.append("Select * from threads where id = ");
            builder.append(id);
            String getPost = builder.toString();
            ResultSet resultSet = connection.createStatement().executeQuery(getPost);
            Integer userId = -1;
            Integer forumId = -1;
            String title = "";
            Boolean isClosed = true;
            Boolean isDeleted = false;
            String date = "";
            Integer likes = 0;
            Integer dislikes = 0;
            Integer points = 0;

            while(resultSet.next()) {
                userId = resultSet.getInt("user_id");
                forumId = resultSet.getInt("forum_id");
                title = resultSet.getString("title");

                isDeleted = resultSet.getBoolean("isDeleted");
                date = resultSet.getString("date");


                likes = resultSet.getInt("likes");
                dislikes = resultSet.getInt("dislikes");
                points = resultSet.getInt("points");
            }

            ForumThread forumThread = new ForumThread(id, userId, forumId, title,
                    isClosed, isDeleted, date, message, slug, likes, dislikes, points);

            builder.setLength(0);
            builder.append("Select email from users where id = ");
            builder.append(userId);
            builder.append(";");
            final String getUserEmail = builder.toString();
            resultSet = connection.createStatement().executeQuery(getUserEmail);
            while(resultSet.next()) {
                forumThread.setUserEmail(resultSet.getString(1));
            }

            builder.setLength(0);
            builder.append("Select name from forums where id =");
            builder.append(forumId);
            builder.append(";");
            final String getForumName = builder.toString();
            resultSet = connection.createStatement().executeQuery(getForumName);
            while(resultSet.next()) {
                forumThread.setForumName(resultSet.getString(1));
            }

            builder.setLength(0);
            builder.append("Select count(*) from posts where thread_id =");
            builder.append(id);
            builder.append(";");
            final String getPostsCount = builder.toString();
            resultSet = connection.createStatement().executeQuery(getPostsCount);
            while(resultSet.next()) {
                forumThread.setPosts(resultSet.getInt(1));
            }

            connection.close();
            SuccessResponse response = new SuccessResponse(0,forumThread.toJSONDetails());
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/thread/close/", method = RequestMethod.POST)
    public ResponseEntity threadClose(@RequestBody ThreadStatusChangeRequest body) {
        Integer id = body.getThreadId();
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );

            StringBuilder builder = new StringBuilder();

            builder.setLength(0);
            builder.append("Update threads set isClosed = ");
            builder.append(true);
            builder.append("  where id = ");
            builder.append(id);
            String closeThread = builder.toString();
            PreparedStatement preparedStatementCloseThread = connection.prepareStatement(closeThread);
            preparedStatementCloseThread.executeUpdate();

            connection.close();
            builder.setLength(0);
            builder.append("{ \"thread\": ");
            builder.append(id);
            builder.append(" }");
            final String responseBody = builder.toString();
            SuccessResponse response = new SuccessResponse(0,responseBody);
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/thread/open/", method = RequestMethod.POST)
    public ResponseEntity threadOpen(@RequestBody ThreadStatusChangeRequest body) {
        Integer id = body.getThreadId();
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );

            StringBuilder builder = new StringBuilder();

            builder.setLength(0);
            builder.append("Update threads set isClosed = ");
            builder.append(false);
            builder.append("  where id = ");
            builder.append(id);
            String closeThread = builder.toString();
            PreparedStatement preparedStatementOpenThread = connection.prepareStatement(closeThread);
            preparedStatementOpenThread.executeUpdate();

            connection.close();
            builder.setLength(0);
            builder.append("{ \"thread\": ");
            builder.append(id);
            builder.append(" }");
            final String responseBody = builder.toString();
            SuccessResponse response = new SuccessResponse(0,responseBody);
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/thread/remove/", method = RequestMethod.POST)
    public ResponseEntity threadRemove(@RequestBody ThreadStatusChangeRequest body) {
        Integer id = body.getThreadId();
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );

            StringBuilder builder = new StringBuilder();

            builder.setLength(0);
            builder.append("Update threads set isDeleted = ");
            builder.append(true);
            builder.append("  where id = ");
            builder.append(id);
            String closeThread = builder.toString();
            PreparedStatement preparedStatementRemoveThread = connection.prepareStatement(closeThread);
            preparedStatementRemoveThread.executeUpdate();

            builder.setLength(0);
            builder.append("Update posts set isDeleted = ");
            builder.append(true);
            builder.append("  where thread_id = ");
            builder.append(id);
            String deleteThreadPosts = builder.toString();
            PreparedStatement preparedStatementRemoveThreadPosts = connection.prepareStatement(deleteThreadPosts);
            preparedStatementRemoveThreadPosts.executeUpdate();


            connection.close();
            builder.setLength(0);
            builder.append("{ \"thread\": ");
            builder.append(id);
            builder.append(" }");
            final String responseBody = builder.toString();
            SuccessResponse response = new SuccessResponse(0,responseBody);
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }
    @RequestMapping(path = "/db/api/thread/restore/", method = RequestMethod.POST)
    public ResponseEntity threadRestore(@RequestBody ThreadStatusChangeRequest body) {
        Integer id = body.getThreadId();
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );

            StringBuilder builder = new StringBuilder();

            builder.setLength(0);
            builder.append("Update threads set isDeleted = ");
            builder.append(false);
            builder.append("  where id = ");
            builder.append(id);
            String closeThread = builder.toString();
            PreparedStatement preparedStatementRestoreThread = connection.prepareStatement(closeThread);
            preparedStatementRestoreThread.executeUpdate();

            connection.close();
            builder.setLength(0);
            builder.append("{ \"thread\": ");
            builder.append(id);
            builder.append(" }");
            final String responseBody = builder.toString();
            SuccessResponse response = new SuccessResponse(0,responseBody);
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/post/create/", method = RequestMethod.POST)
    public ResponseEntity dbtest(@RequestBody CreatePostRequest body) {
        final String date = body.getDate();
        final Integer threadId = body.getThreadId();
        final String message = body.getMessage();
        final String userEmail = body.getUserEmail();
        final String forumShortName = body.getForumShortName();
        final Integer parentId = body.getParentId();
        Boolean isApproved = body.getApproved();
        if(isApproved==null){
            isApproved = false;
        }
        Boolean isHighlighted = body.getHoghlighted();
        if(isHighlighted==null){
            isHighlighted = false;
        }
        Boolean isEdited = body.getEdited();
        if(isEdited==null){
            isEdited = false;
        }
        Boolean isSpam = body.getSpam();
        if(isSpam==null){
            isSpam = false;
        }
        Boolean isDeleted = body.getDeleted();
        if(isDeleted==null){
            isDeleted = false;
        }
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );

            Post post = new Post(date, threadId, message, userEmail, forumShortName,
                    parentId, isApproved,isHighlighted,isEdited, isSpam, isDeleted);

            final String getUserId = "Select id from users where email = '" + userEmail + "';";
            ResultSet resultSet = connection.createStatement().executeQuery(getUserId);
            while(resultSet.next()) {
                post.setUserId(resultSet.getInt(1));
            }

            final String getForumId = "Select id from forums where short_name = '" + forumShortName + "';";
            resultSet = connection.createStatement().executeQuery(getForumId);
            while(resultSet.next()) {
                post.setForumId(resultSet.getInt(1));
            }

            final String createPost = post.insert();
            System.out.println(createPost);
            PreparedStatement preparedStatementCreatePost = connection.prepareStatement(createPost);
            preparedStatementCreatePost.executeUpdate();

            StringBuilder builder = new StringBuilder();
            builder.append("Select id from posts where date = '");
            builder.append(date);
            builder.append("' and message = '");
            builder.append(message);
            builder.append("' and user_id = '");
            builder.append(post.getUserId());
            builder.append("'");
            final String getPostId = builder.toString();//"Select id from threads where title = '" + title + "' and;"; //title - не уникальный
            resultSet = connection.createStatement().executeQuery(getPostId);
            while(resultSet.next()) {
                post.setId(resultSet.getInt(1));
            }
            connection.close();
            SuccessResponse response = new SuccessResponse(0,post.toJSON());
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/post/vote/", method = RequestMethod.POST)
    public ResponseEntity postVote(@RequestBody VotePostRequest body) {
        Integer postId = body.getPostId();
        Integer vote = body.getVote(); //здесь надо разворачивать, если вот больше +-1
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );

            StringBuilder builder = new StringBuilder();
            builder.append("Select * from posts where id = ");
            builder.append(postId);
            String getPost = builder.toString();
            ResultSet resultSet = connection.createStatement().executeQuery(getPost);
            String responseBody = "";
            Integer id = -1;
            Integer threadId = -1;
            String message = "";
            String date = "";
            Integer userId = -1;
            Integer forumId = -1;
            Integer parentId = -1;
            Boolean isSpam = false;
            Boolean isApproved = false;
            Boolean isHighlighted = false;
            Boolean isEdited = false;
            Boolean isDeleted = false;
            Integer likes = 0;
            Integer dislikes = 0;
            Integer points = 0;

            while(resultSet.next()) {
                id = resultSet.getInt("id");
                threadId = resultSet.getInt("thread_id");
                message = resultSet.getString("message");
                date = resultSet.getString("date");
                userId = resultSet.getInt("user_id");
                forumId = resultSet.getInt("forum_id");
                parentId = resultSet.getInt("parent_id");
                isSpam = resultSet.getBoolean("isSpam");
                isApproved = resultSet.getBoolean("isApproved");
                isHighlighted = resultSet.getBoolean("isHighlighted");
                isEdited = resultSet.getBoolean("isEdited");
                isDeleted = resultSet.getBoolean("isDeleted");
                likes = resultSet.getInt("likes");
                dislikes = resultSet.getInt("dislikes");
                points = resultSet.getInt("points");
            }

            Post post = new Post(id, threadId, message, date, userId, forumId, parentId, isSpam,
                    isApproved, isHighlighted, isEdited, isDeleted, likes, dislikes, points);

            final String getUserEmail = "Select email from users where id = '" + userId + "';";
            resultSet = connection.createStatement().executeQuery(getUserEmail);
            while(resultSet.next()) {
                post.setUserEmail(resultSet.getString(1));
            }

            final String getForumShortName = "Select short_name from forums where id = '" + forumId + "';";
            resultSet = connection.createStatement().executeQuery(getForumShortName);
            while(resultSet.next()) {
                post.setForumShortName(resultSet.getString(1));
            }

            if(vote>0) {
                builder.setLength(0);
                builder.append("Update posts set likes = ");
                builder.append(post.like());
                builder.append(", points = ");
                builder.append(post.getPoints());
                builder.append(" where id = ");
                builder.append(id);
                String setLikes = builder.toString();
                PreparedStatement preparedStatementSetLikes = connection.prepareStatement(setLikes);
                preparedStatementSetLikes.executeUpdate();
            } else {
                builder.setLength(0);
                builder.append("Update posts set dislikes = ");
                builder.append(post.dislike());
                builder.append(", points = ");
                builder.append(post.getPoints());
                builder.append(" where id = ");
                builder.append(id);
                String setDislikes = builder.toString();
                PreparedStatement preparedStatementSetDislikes = connection.prepareStatement(setDislikes);
                preparedStatementSetDislikes.executeUpdate();
            }

            responseBody = post.toJSONDetails();

            connection.close();
            SuccessResponse response = new SuccessResponse(0,responseBody);
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/post/details/", method = RequestMethod.GET)
    public ResponseEntity postDetails(@RequestParam Integer post) {
        Integer id = post;
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );

            StringBuilder builder = new StringBuilder();
            builder.append("Select * from posts where id = ");
            builder.append(id);
            String getPost = builder.toString();
            ResultSet resultSet = connection.createStatement().executeQuery(getPost);
            String responseBody = "";
            Integer threadId = -1;
            String message = "";
            String date = "";
            Integer userId = -1;
            Integer forumId = -1;
            Integer parentId = -1;
            Boolean isSpam = false;
            Boolean isApproved = false;
            Boolean isHighlighted = false;
            Boolean isEdited = false;
            Boolean isDeleted = false;
            Integer likes = 0;
            Integer dislikes = 0;
            Integer points = 0;

            while(resultSet.next()) {
                threadId = resultSet.getInt("thread_id");
                message = resultSet.getString("message");
                date = resultSet.getString("date");
                userId = resultSet.getInt("user_id");
                forumId = resultSet.getInt("forum_id");
                parentId = resultSet.getInt("parent_id");
                isSpam = resultSet.getBoolean("isSpam");
                isApproved = resultSet.getBoolean("isApproved");
                isHighlighted = resultSet.getBoolean("isHighlighted");
                isEdited = resultSet.getBoolean("isEdited");
                isDeleted = resultSet.getBoolean("isDeleted");
                likes = resultSet.getInt("likes");
                dislikes = resultSet.getInt("dislikes");
                points = resultSet.getInt("points");
            }

            Post tempPost = new Post(id, threadId, message, date, userId, forumId, parentId, isSpam,
                    isApproved, isHighlighted, isEdited, isDeleted, likes, dislikes, points);

            final String getUserEmail = "Select email from users where id = '" + userId + "';";
            resultSet = connection.createStatement().executeQuery(getUserEmail);
            while(resultSet.next()) {
                tempPost.setUserEmail(resultSet.getString(1));
            }

            final String getForumShortName = "Select short_name from forums where id = '" + forumId + "';";
            resultSet = connection.createStatement().executeQuery(getForumShortName);
            while(resultSet.next()) {
                tempPost.setForumShortName(resultSet.getString(1));
            }

            responseBody = tempPost.toJSONDetails();

            connection.close();
            SuccessResponse response = new SuccessResponse(0,responseBody);
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/post/remove/", method = RequestMethod.POST)
    public ResponseEntity postRemove(@RequestBody RemoveRestorePostRequest body) {
        Integer id = body.getPostId();
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );

            StringBuilder builder = new StringBuilder();
            builder.append("Update posts set isDeleted = ");
            builder.append(true);
            builder.append("  where id = ");
            builder.append(id);
            String removePost = builder.toString();
            PreparedStatement preparedStatementSetDislikes = connection.prepareStatement(removePost);
            preparedStatementSetDislikes.executeUpdate();

            builder.setLength(0);
            builder.append("{\"post\":");
            builder.append(id);
            builder.append("}");
            String responseBody = builder.toString();

            connection.close();
            SuccessResponse response = new SuccessResponse(0,responseBody);
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/post/restore/", method = RequestMethod.POST)
    public ResponseEntity postRestore(@RequestBody RemoveRestorePostRequest body) {
        Integer id = body.getPostId();
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );

            StringBuilder builder = new StringBuilder();
            builder.append("Update posts set isDeleted = ");
            builder.append(false);
            builder.append("  where id = ");
            builder.append(id);

            String removePost = builder.toString();
            PreparedStatement preparedStatementSetDislikes = connection.prepareStatement(removePost);
            preparedStatementSetDislikes.executeUpdate();

            builder.setLength(0);
            builder.append("{\"post\":");
            builder.append(id);
            builder.append("}");
            String responseBody = builder.toString();

            connection.close();
            SuccessResponse response = new SuccessResponse(0,responseBody);
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/post/update/", method = RequestMethod.POST)
    public ResponseEntity postUpdate(@RequestBody UpdatePostRequest body) {
        Integer postId = body.getPostId();
        String message = body.getMessage(); //здесь надо разворачивать, если вот больше +-1
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );

            StringBuilder builder = new StringBuilder();
            builder.append("Select * from posts where id = ");
            builder.append(postId);
            String getPost = builder.toString();
            ResultSet resultSet = connection.createStatement().executeQuery(getPost);
            String responseBody = "";
            Integer id = -1;
            Integer threadId = -1;
            String date = "";
            Integer userId = -1;
            Integer forumId = -1;
            Integer parentId = -1;
            Boolean isSpam = false;
            Boolean isApproved = false;
            Boolean isHighlighted = false;
            Boolean isEdited = false;
            Boolean isDeleted = false;
            Integer likes = 0;
            Integer dislikes = 0;
            Integer points = 0;

            while(resultSet.next()) {
                id = resultSet.getInt("id");
                threadId = resultSet.getInt("thread_id");
                date = resultSet.getString("date");
                userId = resultSet.getInt("user_id");
                forumId = resultSet.getInt("forum_id");
                parentId = resultSet.getInt("parent_id");
                isSpam = resultSet.getBoolean("isSpam");
                isApproved = resultSet.getBoolean("isApproved");
                isHighlighted = resultSet.getBoolean("isHighlighted");
                isEdited = resultSet.getBoolean("isEdited");
                isDeleted = resultSet.getBoolean("isDeleted");
                likes = resultSet.getInt("likes");
                dislikes = resultSet.getInt("dislikes");
                points = resultSet.getInt("points");
            }

            Post post = new Post(id, threadId, message, date, userId, forumId, parentId, isSpam,
                    isApproved, isHighlighted, isEdited, isDeleted, likes, dislikes, points);

            final String getUserEmail = "Select email from users where id = '" + userId + "';";
            resultSet = connection.createStatement().executeQuery(getUserEmail);
            while(resultSet.next()) {
                post.setUserEmail(resultSet.getString(1));
            }

            final String getForumShortName = "Select short_name from forums where id = '" + forumId + "';";
            resultSet = connection.createStatement().executeQuery(getForumShortName);
            while(resultSet.next()) {
                post.setForumShortName(resultSet.getString(1));
            }

            builder.setLength(0);
            builder.append("Update posts set message = \"");
            builder.append(message);
            builder.append("\", points = ");
            builder.append(post.getPoints());
            builder.append(" where id = ");
            builder.append(id);
            String setMessage = builder.toString();
            PreparedStatement preparedStatementSetMessage = connection.prepareStatement(setMessage);
            preparedStatementSetMessage.executeUpdate();

            responseBody = post.toJSONDetails();

            connection.close();
            SuccessResponse response = new SuccessResponse(0,responseBody);
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    private static final class UpdateThreadRequest{
        private String message;
        private String slug;
        private Integer threadId;

        @JsonCreator
        private UpdateThreadRequest(@JsonProperty("thread") Integer threadId,
                                    @JsonProperty("message") String message,
                                    @JsonProperty("slug") String slug) {
            this.message = message;
            this.slug = slug;
            this.threadId = threadId;
        }

        public String getMessage() {
            return message;
        }

        public String getSlug() {
            return slug;
        }

        public Integer getThreadId() {
            return threadId;
        }
    }
    private static final class ThreadStatusChangeRequest{
        private Integer threadId;

        @JsonCreator
        private ThreadStatusChangeRequest(@JsonProperty("thread") Integer threadId) {
            this.threadId = threadId;
        }

        public Integer getThreadId() {
            return threadId;
        }
    }

    private static final class UpdatePostRequest{
        private String message;
        private Integer postId;

        @JsonCreator
        private UpdatePostRequest(@JsonProperty("message") String message,
                                  @JsonProperty("post") Integer postId) {
            this.message = message;
            this.postId = postId;
        }

        public String getMessage() {
            return message;
        }

        public Integer getPostId() {
            return postId;
        }
    }

    private static final class RemoveRestorePostRequest{
        private Integer postId;

        @JsonCreator
        private RemoveRestorePostRequest(@JsonProperty("post") Integer postId) {
            this.postId = postId;
        }

        public Integer getPostId() {
            return postId;
        }
    }

    private static final class VotePostRequest{
        private Integer vote;
        private Integer postId;

        @JsonCreator
        private VotePostRequest(@JsonProperty("vote") Integer vote,
                                @JsonProperty("post") Integer postId) {
            this.vote = vote;
            this.postId = postId;
        }

        public Integer getVote() {
            return vote;
        }

        public Integer getPostId() {
            return postId;
        }
    }

    private static final class CreateUserRequest {
        private String username;
        private String about;
        private boolean isAnonymos;
        private String name;
        private String email;

        @JsonCreator
        private CreateUserRequest(@JsonProperty("username") String username,
                                  @JsonProperty("about") String about,
                                  @JsonProperty("isAnonymos") boolean isAnonymos,
                                  @JsonProperty("name") String name,
                                  @JsonProperty("email") String email) {
            this.username = username;
            this.about = about;
            this.isAnonymos = isAnonymos;
            this.name = name;
            this.email = email;
        }

        public String getUsername() {
            return username;
        }

        public String getAbout() {
            return about;
        }

        public boolean getAnonymos() {
            return isAnonymos;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }
    }

    private static final class CreateForumRequest {
        private String name;
        private String short_name;
        private String user_email;

        @JsonCreator
        private CreateForumRequest(@JsonProperty("name") String name,
                                   @JsonProperty("short_name") String short_name,
                                   @JsonProperty("user") String user_email) {
            this.name = name;
            this.short_name = short_name;
            this.user_email = user_email;
        }

        public String getName() {
            return name;
        }

        public String getShort_name() {
            return short_name;
        }

        public String getUser_email() {
            return user_email;
        }
    }

    private static final class CreateThreadRequest {
        private String date;
        private String forum_name;
        private boolean isClosed;
        private boolean isDeleted;
        private String message;
        private String slug;
        private String title;
        private String user_email;

        @JsonCreator
        private CreateThreadRequest(@JsonProperty("date") String date,
                                    @JsonProperty("forum") String forum_name,
                                    @JsonProperty("isClosed") boolean isClosed,
                                    @JsonProperty("isDeleted") boolean isDeleted,
                                    @JsonProperty("message") String message,
                                    @JsonProperty("slug") String slug,
                                    @JsonProperty("title") String title,
                                    @JsonProperty("user") String user_email) {
            this.date = date;
            this.forum_name = forum_name;
            this.isClosed = isClosed;
            this.isDeleted = isDeleted;
            this.message = message;
            this.slug = slug;
            this.title = title;
            this.user_email = user_email;
        }

        public String getDate() {
            return date;
        }

        public String getForum_name() {
            return forum_name;
        }

        public boolean isClosed() {
            return isClosed;
        }

        public boolean isDeleted() {
            return isDeleted;
        }

        public String getMessage() {
            return message;
        }

        public String getSlug() {
            return slug;
        }

        public String getTitle() {
            return title;
        }

        public String getUser_email() {
            return user_email;
        }
    }

    private static final class CreatePostRequest {
        private String date;
        private Integer threadId;
        private String message;
        private String userEmail;
        private String forumShortName; //обязательные параметры
        private Integer parentId;
        private Boolean isApproved;
        private Boolean isHoghlighted;
        private Boolean isEdited;
        private Boolean isSpam;
        private Boolean isDeleted;

        @JsonCreator
        private CreatePostRequest(@JsonProperty("date") String date,
                                  @JsonProperty("thread") Integer threadId,
                                  @JsonProperty("message") String message,
                                  @JsonProperty("user") String userEmail,
                                  @JsonProperty("forum") String forumShortName,
                                  @JsonProperty("parent") Integer parentId,
                                  @JsonProperty("isApproved") Boolean isApproved,
                                  @JsonProperty("isHighlighted") Boolean isHighlighted,
                                  @JsonProperty("isEdited") Boolean isEdited,
                                  @JsonProperty("isSpam") Boolean isSpam,
                                  @JsonProperty("isDeleted") Boolean isDeleted) {
            this.date = date;
            this.threadId = threadId;
            this.message = message;
            this.userEmail = userEmail;
            this.forumShortName = forumShortName;
            this.parentId = parentId;
            this.isApproved = isApproved;
            this.isHoghlighted = isHighlighted;
            this.isEdited = isEdited;
            this.isSpam = isSpam;
            this.isDeleted = isDeleted;
        }

        public String getDate() {
            return date;
        }

        public Integer getThreadId() {
            return threadId;
        }

        public String getMessage() {
            return message;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public String getForumShortName() {
            return forumShortName;
        }

        public Integer getParentId() {
            return parentId;
        }

        public Boolean getApproved() {
            return isApproved;
        }

        public Boolean getHoghlighted() {
            return isHoghlighted;
        }

        public Boolean getEdited() {
            return isEdited;
        }

        public Boolean getSpam() {
            return isSpam;
        }

        public Boolean getDeleted() {
            return isDeleted;
        }
    }

    private static final class SuccessResponse {
        private String body;
        private Integer code;

        private SuccessResponse(Integer code, String body) {
            this.code = code;
            this.body = body;
        }

        public String createJSONResponce(){
            String responce = "{\"code\": " + code + ", \"respose\": " + body + "}";
            return responce;
        }

    }

}
/* */