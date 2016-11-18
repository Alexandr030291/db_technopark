package org.ebitbucket.services;


import org.ebitbucket.lib.Functions;
import org.ebitbucket.model.Forum.Forum;
import org.ebitbucket.model.Forum.ForumDetail;
import org.ebitbucket.model.Forum.ForumDetailKey;
import org.ebitbucket.model.ListObject;
import org.ebitbucket.model.Post.PostDetails;
import org.ebitbucket.model.Tread.ThreadDetail;
import org.ebitbucket.model.User.User;
import org.ebitbucket.model.User.UserDetailAll;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Service
class MainService {
    final private JdbcTemplate template;


    MainService(JdbcTemplate template) {
        this.template = template;
    }

    HashMap<Integer,String> getEmailList(Set<Integer> list){
        if (list.size()==0){
            return new HashMap<>();
        }
        String sql =   "SELECT DISTINCT `id`, `email` " +
                       "FROM `Users` "+
                       "WHERE `id` IN ( " +
                       list.stream().map(String::valueOf).collect(Collectors.joining(", ")) +")";
        List<User> userEmailList = template.query(sql,USER_ROW_MAPPER);
        HashMap<Integer, String> userHashMap = new HashMap<>();
        for (User anUserEmailList : userEmailList)
            userHashMap.put(anUserEmailList.getId(), anUserEmailList.getEmail());
        return userHashMap;
    }

    HashMap<Integer,UserDetailAll> getUserDetailAllList(Set<Integer> list) {
        if (list.size() == 0) {
            return new HashMap<>();
        }
        String sql = "SELECT DISTINCT `Users`.`id`, " +
                "`Users`.`email`, " +
                "`UserProfile`.`name`, " +
                "`UserProfile`.`username`, " +
                "`UserProfile`.`about`, " +
                "`UserProfile`.`isAnonymous` " +
                "FROM `Users` " +
                "JOIN `UserProfile`" +
                "ON `Users`.`id` = `UserProfile`.`id`" +
                "ON `Users`.`id` IN ( " + list.stream().map(String::valueOf).collect(Collectors.joining(", ")) + ")";
        List<UserDetailAll> users = template.query(sql, USER_DETAIL_ALL_ROW_MAPPER);
        HashMap<Integer, UserDetailAll> userDetailAllHashMap = new HashMap<>();
        for (UserDetailAll user1 : users) userDetailAllHashMap.put(user1.getId(), user1);
        sql = "SELECT `id`, `email` " +
                "FROM `Users`" +
                "JOIN `Followers`  ON `Followers`.`followee` = `Users`.`id` " +
                "AND `follower` IN ( " + list.stream().map(String::valueOf).collect(Collectors.joining(", ")) + ")";
        List<ListObject> listFollowing = template.query(sql, Following_ROWMAPPER);
        sql = "SELECT `id`, `email` " +
                "FROM `Users`" +
                "JOIN `Followers`  ON `Followers`.`follower` = `Users`.`id` " +
                "AND `followee` IN ( " + list.stream().map(String::valueOf).collect(Collectors.joining(", ")) + ")";
        List<ListObject> listFollowee = template.query(sql, Followee_ROWMAPPER);
        sql = "SELECT `user`, `thread`  " +
                "FROM `Subscriptions` " +
                "WHERE `user` IN ( " + list.stream().map(String::valueOf).collect(Collectors.joining(", ")) + ")";
        List<ListObject> listSubscriptions = template.query(sql, Subscriptions_ROWMAPPER);
        for (ListObject aListFollowee : listFollowee) {
            userDetailAllHashMap.get(aListFollowee.getId()).addFollowers(aListFollowee.getValue().toString());
        }
        for (ListObject aListFollowing : listFollowing) {
            userDetailAllHashMap.get(aListFollowing.getId()).addFollowing(aListFollowing.getValue().toString());
        }
        for (ListObject listSubscription : listSubscriptions)
            userDetailAllHashMap.get(listSubscription.getId()).addSubscriptions((Integer) listSubscription.getValue());
        return userDetailAllHashMap;
    }

    HashMap<Integer,String> getForumShortNameList(Set<Integer> list){
        if (list.size()==0){
            return new HashMap<>();
        }
        String sql =   "SELECT DISTINCT `id`, `short_name` " +
                "FROM `Forums` "+
                "WHERE `id` IN ( " +
                list.stream().map(String::valueOf).collect(Collectors.joining(", ")) +")";
        List<Forum> forumEmailList = template.query(sql,forumRowMapper);
        HashMap<Integer, String> forumHashMap = new HashMap<>();
        for (Forum aForumEmailList : forumEmailList)
            forumHashMap.put(aForumEmailList.getId(), aForumEmailList.getShort_name());
        return forumHashMap;
    }

    HashMap<Integer,ForumDetail> getForumDetailsList(Set<Integer> list){
        if (list.size()==0){
            return new HashMap<>();
        }
        String sql =   "SELECT DISTINCT `ForumDetail`.`id`, " +
                "`ForumDetail`.`name`, " +
                "`Forums`.`short_name`, " +
                "`Users`.`email` " +
                "FROM `ForumDetail` " +
                "JOIN `Forums` " +
                "ON `ForumDetail`.`id` = `Forums`.`id` "+
                "JOIN `Users` " +
                "ON  `ForumDetail`.`user` = `Users`.`id` " +
                "AND `Forums`.`id` IN ( "
                + list.stream().map(String::valueOf).collect(Collectors.joining(",")) +")";
        List<ForumDetailKey> forumList = template.query(sql,Forum_DETAIL_Email_ROWMAPPER);
        HashMap<Integer, ForumDetail> forumHashMap = new HashMap<>();
        for (ForumDetailKey aForumEmailList : forumList) {
            int id = aForumEmailList.getId();
            forumHashMap.put(id, new ForumDetail(id,aForumEmailList.getName(),aForumEmailList.getShort_name(),aForumEmailList.getUser()));
        }
        return forumHashMap;
    }

    HashMap<Integer,ThreadDetail> getThreadDetail(Set<Integer> list){
        if (list.size()==0){
            return new HashMap<>();
        }
        String sql =   "SELECT DISTINCT `Thread`.`id`, " +
                "`Forums`.`short_name`, " +
                "`Users`.`email`, " +
                "`Thread`.`title`," +
                "`Thread`.`message`, " +
                "`Thread`.`slug`, " +
                "`Thread`.`date`, " +
                "`Thread`.`isClosed`, " +
                "`Thread`.`isDeleted`, " +
                "`Thread`.`likes`, " +
                "`Thread`.`dislikes`, " +
                "`Thread`.`posts` " +
                "FROM `Thread` " +
                "JOIN `Forums` " +
                "ON `Forums`.`id` = `Thread`.`forum` " +
                "JOIN `Users` " +
                "ON `Users`.`id` = `Thread`.`user` "+
                "AND `Thread`.`id` IN ("
                + list.stream().map(String::valueOf).collect(Collectors.joining(",")) +")";
        List<ThreadDetail> threadList = template.query(sql,THREAD_DETAIL_SHORT_ROW_MAPPER);
        HashMap<Integer, ThreadDetail> threadHashMap = new HashMap<>();
        for (ThreadDetail aThreadList : threadList) {
            aThreadList.setPoints(aThreadList.getLikes()-aThreadList.getDislikes());
            threadHashMap.put(aThreadList.getId(), aThreadList);
        }
        return threadHashMap;
    }

    final RowMapper<UserDetailAll> USER_DETAIL_ALL_ROW_MAPPER = (rs, rowNum) ->
            new UserDetailAll(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("about"),
                rs.getBoolean("isAnonymous"));

    final RowMapper<User> USER_ROW_MAPPER= (rs, rowNum) ->
            new User(rs.getInt("id"), rs.getString("email"));

    final RowMapper<ForumDetail> Forum_DETAIL_ROWMAPPER = (rs, rowNum) ->
            new ForumDetail(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("short_name"),
                rs.getInt("user"));

    final RowMapper<ForumDetailKey> Forum_DETAIL_Email_ROWMAPPER = (rs, rowNum) ->
            new ForumDetailKey(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("short_name"),
                    rs.getString("email"));

    final RowMapper<Forum> forumRowMapper = (rs, rowNum) ->
            new Forum(rs.getInt("id"),rs.getString("short_name"));

    final RowMapper<ListObject> Following_ROWMAPPER = (rs, rowNum) ->
            new ListObject(rs.getInt("id"),rs.getString("email"));

    final RowMapper<ListObject> Followee_ROWMAPPER = (rs, rowNum) ->
            new ListObject(rs.getInt("id"),rs.getString("email"));

    final RowMapper<ListObject> Subscriptions_ROWMAPPER = (rs, rowNum) ->
            new ListObject(rs.getInt("user"),rs.getInt("thread"));

    final RowMapper<ThreadDetail> THREAD_DETAIL_ROW_MAPPER = (rs, rowNum) -> new ThreadDetail(rs.getInt("id"),
            rs.getInt("forum"),
            rs.getInt("user"),
            rs.getString("title"),
            rs.getString("message"),
            rs.getString("slug"),
            Functions.DATE_FORMAT.format(rs.getTimestamp("date")),
            rs.getBoolean("isClosed"),
            rs.getBoolean("isDeleted"),
            rs.getInt("likes"),
            rs.getInt("dislikes"),
            rs.getInt("posts"));

    final RowMapper<ThreadDetail> THREAD_DETAIL_SHORT_ROW_MAPPER = (rs, rowNum) -> new ThreadDetail(rs.getInt("id"),
            rs.getString("short_name"),
            rs.getString("email"),
            rs.getString("title"),
            rs.getString("message"),
            rs.getString("slug"),
            Functions.DATE_FORMAT.format(rs.getTimestamp("date")),
            rs.getBoolean("isClosed"),
            rs.getBoolean("isDeleted"),
            rs.getInt("likes"),
            rs.getInt("dislikes"),
            rs.getInt("posts"));

    final RowMapper<PostDetails> POST_DETAIL_ROW_MAPPER = (rs, rowNum) ->
            new PostDetails(
                rs.getInt("id"),
                rs.getInt("forum"),
                rs.getInt("user"),
                rs.getInt("thread"),
                (rs.getString("parent")!=null)?rs.getInt("parent"):null,
                rs.getString("message"),
                Functions.DATE_FORMAT.format(rs.getTimestamp("date")),
                rs.getBoolean("isApproved"),
                rs.getBoolean("isDeleted"),
                rs.getBoolean("isEdited"),
                rs.getBoolean("isHighlighted"),
                rs.getBoolean("isSpam"),
                rs.getInt("dislikes"),
                rs.getInt("likes"));

    final RowMapper<PostDetails> POST_DETAIL_SHORT_ROW_MAPPER = (rs, rowNum) ->
            new PostDetails(
                    rs.getInt("id"),
                    rs.getString("short_name"),
                    rs.getString("email"),
                    rs.getInt("thread"),
                    (rs.getString("parent")!=null)?rs.getInt("parent"):null,
                    rs.getString("message"),
                    Functions.DATE_FORMAT.format(rs.getTimestamp("date")),
                    rs.getBoolean("isApproved"),
                    rs.getBoolean("isDeleted"),
                    rs.getBoolean("isEdited"),
                    rs.getBoolean("isHighlighted"),
                    rs.getBoolean("isSpam"),
                    rs.getInt("dislikes"),
                    rs.getInt("likes"));

}
