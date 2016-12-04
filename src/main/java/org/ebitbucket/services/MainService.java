package org.ebitbucket.services;


import org.ebitbucket.lib.Util;
import org.ebitbucket.lib.Functions;
import org.ebitbucket.model.FollowerTable;
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

import java.util.*;
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

    public HashMap<Integer,UserDetailAll> getUserDetailAllList(Set<Integer> list) {
        if (list.size() == 0) {
            return new HashMap<>();
        }
        String userListIN="("+ list.stream().map(String::valueOf).collect(Collectors.joining(", ")) + ")";
        String sql = "SELECT `Users`.`id`, " +
                "`Users`.`email`, " +
                "`UserProfile`.`name`, " +
                "`UserProfile`.`username`, " +
                "`UserProfile`.`about`, " +
                "`UserProfile`.`isAnonymous` " +
                "FROM `Users` " +
                "JOIN `UserProfile`" +
                "ON `Users`.`id` = `UserProfile`.`id`" +
                "WHERE `Users`.`id` IN "+userListIN;
        List<UserDetailAll> users = template.query(sql, USER_DETAIL_ALL_ROW_MAPPER);
        users = setFollowee(users,userListIN);
        HashMap<Integer, UserDetailAll> userDetailAllHashMap = new HashMap<>();
        for (UserDetailAll user1 : users) userDetailAllHashMap.put(user1.getId(), user1);
        sql = "SELECT `user`, `thread`  " +
                "FROM `Subscriptions` " +
                "WHERE `user` IN "+userListIN;
        List<ListObject> listSubscriptions = template.query(sql, Subscriptions_ROWMAPPER);
        for (ListObject listSubscription : listSubscriptions)
            userDetailAllHashMap.get(listSubscription.getId()).addSubscriptions((Integer) listSubscription.getValue());
        return userDetailAllHashMap;
    }

    public int getNextId(String tableName){
        template.update("UPDATE `LastId` SET `count` = `count` + 1 WHERE `table` = ?",tableName);
        String sql = "SELECT `count` FROM `LastId` WHERE `table` = ?";
        return template.queryForObject(sql,Integer.class,tableName);
    }

    public List<UserDetailAll> setFollowee(List<UserDetailAll> userList,String userListIN){
        String sql = "SELECT  `UFwers`.`id` as `frID`, " +
                "`UFwers`.`email` as `frEmail`, " +
                "`UFwee`.`id` as feId, " +
                "`UFwee`.`email` as feEmail " +
                "FROM `Followers` " +
                "JOIN `Users` AS `UFwers` ON `UFwers`.`id` = `Followers`.`followee` " +
                "JOIN `Users` AS `UFwee` ON `UFwee`.`id` = `Followers`.`follower` " +
                "WHERE `follower` IN "+userListIN + " " +
                "OR `followee` IN " +userListIN;
        List<FollowerTable> listFollowing = template.query(sql, folloverTableRowMapper);
        HashMap<Integer,String> followingMap = new HashMap<>();
        HashMap<Integer,String> followerMap = new HashMap<>();
        for (FollowerTable aListFollowing : listFollowing) {
            followerMap.put(aListFollowing.getFolloweeId(), aListFollowing.getFollowerEmail());
            followingMap.put(aListFollowing.getFollowerId(), aListFollowing.getFolloweeEmail());
        }
        for (UserDetailAll anUserList : userList) {
            anUserList.addFollowing(followingMap.get(anUserList.getId()));
            anUserList.addFollowers(followerMap.get(anUserList.getId()));
        }
        return userList;
    }


    HashMap<Integer,String> getForumShortNameList(Set<Integer> list){
        if (list.size()==0){
            return new HashMap<>();
        }
        String sql =   "SELECT `id`, `short_name` " +
                "FROM `Forums` "+
                "WHERE `id` IN ( " +
                list.stream().map(String::valueOf).collect(Collectors.joining(", ")) +")";
        List<Forum> forumEmailList = template.query(sql,forumRowMapper);
        HashMap<Integer, String> forumHashMap = new HashMap<>();
        for (Forum aForumEmailList : forumEmailList)
            forumHashMap.put(aForumEmailList.getId(), aForumEmailList.getShort_name());
        return forumHashMap;
    }

    public void createUserForumKey(int user, int forum) {
        template.update("INSERT IGNORE `UsersOfForum`(`user`,`forum`) VALUE (?,?);", user, forum);
    }

    HashMap<Integer,ForumDetail> getForumDetailsList(Set<Integer> list){
        if (list.size()==0){
            return new HashMap<>();
        }
        String sql =   "SELECT `ForumDetail`.`id`, " +
                "`ForumDetail`.`name`, " +
                "`Forums`.`short_name`, " +
                "`Users`.`email` " +
                "FROM `ForumDetail` " +
                "JOIN `Forums` " +
                "ON `ForumDetail`.`id` = `Forums`.`id` "+
                "JOIN `Users` " +
                "ON  `ForumDetail`.`user` = `Users`.`id` " +
                "WHERE `Forums`.`id` IN ( "
                + list.stream().map(String::valueOf).collect(Collectors.joining(",")) +")";
        List<ForumDetailKey> forumList = template.query(sql,Forum_DETAIL_Email_ROWMAPPER);
        HashMap<Integer, ForumDetail> forumHashMap = new HashMap<>();
        for (ForumDetailKey aForumEmailList : forumList) {
            int id = aForumEmailList.getId();
            forumHashMap.put(id, new ForumDetail(id,aForumEmailList.getName(),aForumEmailList.getShort_name(),aForumEmailList.getUser()));
        }
        return forumHashMap;
    }

    public HashMap<Integer,ThreadDetail> getThreadDetailMap(Set<Integer> list){
        if (list.size()==0){
            return new HashMap<>();
        }
        String sql =   "SELECT `Thread`.`id`, " +
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
                "WHERE `Thread`.`id` IN ("
                + list.stream().map(String::valueOf).collect(Collectors.joining(",")) +")";
        List<ThreadDetail> threadList = template.query(sql,THREAD_DETAIL_SHORT_ROW_MAPPER);
        HashMap<Integer, ThreadDetail> threadHashMap = new HashMap<>();
        for (ThreadDetail aThreadList : threadList) {
            aThreadList.setPoints(aThreadList.getLikes()-aThreadList.getDislikes());
            threadHashMap.put(aThreadList.getId(), aThreadList);
        }
        return threadHashMap;
    }

    List<Object> getUserDetails(List<Integer> list, Boolean detail){
        List<Object> userDetailAllList=new ArrayList<>();
        Set<Integer> userIdSet = new HashSet<>();
        for (Integer aList : list)
            userIdSet.add(aList);

        if (detail){
            HashMap<Integer,UserDetailAll> userHashMap = getUserDetailAllList(userIdSet);
            for (int i =0; i<list.size();i++)
                userDetailAllList.add(i,userHashMap.get(list.get(i)));
        }else{
            HashMap<Integer,String> userHashMap = getEmailList(userIdSet);
            for (int i =0; i<list.size();i++)
                userDetailAllList.add(i,userHashMap.get(list.get(i)));
        }
        return userDetailAllList;
    }

    List<Object> getForumsDetails(List<Integer> list,Boolean detail){
        List<Object> arrayList=new ArrayList<>();
        Set<Integer> forumIdSet = new HashSet<>();
        for (Integer aList : list)
            forumIdSet.add(aList);

        if (detail){
            HashMap<Integer,ForumDetail> userHashMap = getForumDetailsList(forumIdSet);
            for (int i =0; i<list.size();i++)
                arrayList.add(i,userHashMap.get(list.get(i)));
        }else{
            HashMap<Integer,String> userHashMap = getForumShortNameList(forumIdSet);
            for (int i =0; i<list.size();i++)
                arrayList.add(i,userHashMap.get(list.get(i)));
        }
        return arrayList;
    }

    List<Object> getThreadDetails(List<Integer> list,Boolean detail){

        List<Object> arrayList=new ArrayList<>();
        if(!detail) {
            for (int i=0;i<list.size();i++)
                arrayList.add(i,list.get(i));
            return arrayList;
        }
        Set<Integer> threadIdSet = new HashSet<>();
        for (Integer aList : list)
            threadIdSet.add(aList);
        HashMap<Integer,ThreadDetail> userHashMap = getThreadDetailMap(threadIdSet);
        for (int i =0; i<list.size();i++)
            arrayList.add(i,userHashMap.get(list.get(i)));
        return arrayList;
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

    final RowMapper<FollowerTable> folloverTableRowMapper = (rs, rowNum) ->
            new FollowerTable(rs.getInt("frId"),rs.getString("frEmail"),rs.getInt("feId"),rs.getString("feEmail"));


    final RowMapper<ListObject> Subscriptions_ROWMAPPER = (rs, rowNum) ->
            new ListObject(rs.getInt("user"),rs.getInt("thread"));

    final RowMapper<ThreadDetail> THREAD_DETAIL_ROW_MAPPER = (rs, rowNum) -> new ThreadDetail(rs.getInt("id"),
            rs.getInt("forum"),
            rs.getInt("user"),
            rs.getString("title"),
            rs.getString("message"),
            rs.getString("slug"),
            Util.dateToStr(rs.getTimestamp("date").toLocalDateTime()),
            rs.getBoolean("isClosed"),
            rs.getBoolean("isDeleted"),
            rs.getInt("likes"),
            rs.getInt("dislikes"),
            rs.getInt("posts"));

    private final RowMapper<ThreadDetail> THREAD_DETAIL_SHORT_ROW_MAPPER = (rs, rowNum) -> new ThreadDetail(rs.getInt("id"),
            rs.getString("short_name"),
            rs.getString("email"),
            rs.getString("title"),
            rs.getString("message"),
            rs.getString("slug"),
            Util.dateToStr(rs.getTimestamp("date").toLocalDateTime()),
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
                Util.dateToStr(rs.getTimestamp("date").toLocalDateTime()),
                rs.getBoolean("isApproved"),
                rs.getBoolean("isDeleted"),
                rs.getBoolean("isEdited"),
                rs.getBoolean("isHighlighted"),
                rs.getBoolean("isSpam"),
                rs.getInt("dislikes"),
                rs.getInt("likes"));

}
