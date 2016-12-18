package org.ebitbucket.services;

import org.ebitbucket.model.Forum.ForumDetail;
import org.ebitbucket.model.Tread.ThreadDetail;
import org.ebitbucket.model.User.UserDetailAll;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ForumService extends MainService{
    private final JdbcTemplate template;

    public ForumService(JdbcTemplate template) {
        super(template);
        this.template = template;
    }

    public int create(String name, String short_name, Integer user_id) {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            template.update(cnctn -> {
                        PreparedStatement ps = cnctn.prepareStatement(
                                "INSERT INTO `Forums` (`short_name`) VALUES (?)",
                                Statement.RETURN_GENERATED_KEYS);
                        ps.setString(1, short_name);
                        return ps;
                    }
                    , keyHolder);
            String sql = "INSERT INTO `ForumDetail`(`id`,`name`, `user`) VALUE(?,?,?);";
            template.update(sql, keyHolder.getKey().intValue(), name, user_id);
            getNextId("forum");
            return keyHolder.getKey().intValue();
        } catch (DuplicateKeyException dk) {
            return -1;
        }
    }

    public Integer getId(String short_name){
        try {
            return template.queryForObject("SELECT `id` FROM `Forums` WHERE `short_name` = ?;",Integer.class, short_name);
        } catch (EmptyResultDataAccessException na){
            return 0;
        }
    }

    public String getShortName(int id){
        return template.queryForObject("SELECT `short_name` FROM `Forums` WHERE `id` = ?;",String.class, id);
    }

    public ForumDetail detail(Integer id){
        String sql = "SELECT `Forums`.`id`, `Forums`.`short_name`, `ForumDetail`.`name`, `ForumDetail`.`user` " +
                     "FROM `ForumDetail` " +
                     "JOIN `Forums` ON   `ForumDetail`.`id`= `Forums`.`id`  " +
                     "AND `Forums`.`id` = ?;";
        return template.queryForObject(sql, Forum_DETAIL_ROWMAPPER ,id);
    }

    public int getCount(){
        String sql = "SELECT  `count` FROM `LastId` WHERE `table` = 'forum';";
        return template.queryForObject(sql, Integer.class);
    }

    public List<Integer> getListThreadId(int forum_id, String since, String order, Integer limit){
        if (since==null){
            String sql = "SELECT `Thread`.`id` FROM `Thread`  " +
                    "WHERE `Thread`.`forum` = ? " +
                    "ORDER BY `Thread`.`date` " + order;
            String sqlLimit=(limit!=null&&limit!=0)?" LIMIT "+limit+";":";";
            return template.queryForList(sql+sqlLimit, Integer.class, forum_id);
        }
        String sql = "SELECT `Thread`.`id` FROM `Thread`  " +
                     "WHERE `Thread`.`forum` = ? " +
                     "AND `Thread`.`date` >= ? " +
                     "ORDER BY `Thread`.`date` " + order;
        String sqlLimit=(limit!=null&&limit!=0)?" LIMIT "+limit+";":";";
        return template.queryForList(sql+sqlLimit, Integer.class, forum_id,since);
    }

    public List<Integer> getListPost(int forum_id, String since, String order, Integer limit){
        if (since==null){
            String sql = "SELECT `id` FROM `Post` " +
                    "WHERE `forum` = ? " +
                    "ORDER BY `date` " + order;
            String sqlLimit=(limit!=null&&limit>0)?" LIMIT "+limit+";":";";
            return template.queryForList(sql+sqlLimit, Integer.class, forum_id);
        }
        String sql = "SELECT `id` FROM `Post` " +
                     "WHERE `forum` = ? " +
                     "AND `date` >= ? " +
                     "ORDER BY `date` " + order;
        String sqlLimit=(limit!=null&&limit>0)?" LIMIT "+limit+";":";";
        return template.queryForList(sql+sqlLimit, Integer.class, forum_id, since);

    }

    public HashMap<Integer,ThreadDetail> getThreadDetailList(List<Integer> list, String [] related) {
        if (list.size() == 0) {
            return new HashMap<>();
        }
        HashMap<Integer, ThreadDetail> threadDetailHashMap = new HashMap<>();
        String sql = "SELECT * FROM `Thread` WHERE `id` IN ( " + list.stream().map(String::valueOf).collect(Collectors.joining(",")) + ")";
        List<ThreadDetail> threadDetailList = template.query(sql, THREAD_DETAIL_ROW_MAPPER);
        List<Integer> userIdList = new ArrayList<>();
        List<Integer> forumIdlist = new ArrayList<>();

        for (ThreadDetail aThreadDetailList : threadDetailList) {
            aThreadDetailList.setPoints(aThreadDetailList.getLikes()-aThreadDetailList.getDislikes());
            userIdList.add((Integer) aThreadDetailList.getUser());
            forumIdlist.add((Integer) aThreadDetailList.getForum());
            threadDetailHashMap.put(aThreadDetailList.getId(), aThreadDetailList);
        }

        List<Object> users;
        List<Object> forums;
        if (related != null && Arrays.asList(related).contains("user"))
            users = getUserDetails(userIdList, true);
        else
            users = getUserDetails(userIdList, false);

        if (related != null && Arrays.asList(related).contains("forum"))
            forums = getForumsDetails(forumIdlist, true);
        else
            forums = getForumsDetails(forumIdlist, false);

        ThreadDetail thread;
        for (int i = 0; i < list.size(); i++) {
            thread = threadDetailList.get(i);
            thread.setUser(users.get(i));
            thread.setForum(forums.get(i));
        }

        return threadDetailHashMap;
    }

    public List<UserDetailAll> getListUser(int forum_id, int since, String order, Integer limit){
        String sqlLimit=(limit!=null&&limit>0)?" LIMIT "+limit+"":"";
        List<Integer> user_id;
        if (since==0){
            String sql =
                    "SELECT `UsersOfForum`.`user` " +
                    "FROM `UsersOfForum` " +
                    "JOIN `UserProfile` " +
                    "ON `UsersOfForum`.`user` = `UserProfile`.`id` " +
                    "WHERE `UsersOfForum`.`forum` = ? " +
                    "ORDER BY `UserProfile`.`name` "+
                    order + sqlLimit;
            user_id =  template.queryForList(sql,Integer.class,forum_id);
        }else {
            String sql =
                    "SELECT `UsersOfForum`.`user` " +
                    "FROM `UsersOfForum` " +
                    "JOIN `UserProfile` " +
                    "ON `UsersOfForum`.`user` = `UserProfile`.`id` " +
                    "WHERE `UsersOfForum`.`forum` = ? " +
                    "AND `UsersOfForum`.`user` >= ? " +
                    "ORDER BY `UserProfile`.`name` "+
                    order + sqlLimit;
            user_id = template.queryForList(sql, Integer.class, forum_id, since);
        }
        Set<Integer> userSetId = new HashSet<>();
        for (Integer anUser_id : user_id) userSetId.add(anUser_id);
        HashMap<Integer,UserDetailAll> userDetailAllHashMap = getUserDetailAllList(userSetId);
        List<UserDetailAll> users = new ArrayList<>();
        for (Integer anUser_id : user_id) users.add(userDetailAllHashMap.get(anUser_id));
        return users;
    }
}
