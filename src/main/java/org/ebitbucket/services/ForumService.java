package org.ebitbucket.services;

import org.ebitbucket.model.Forum.ForumDetail;
import org.ebitbucket.model.ListObject;
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
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
public class ForumService extends MainService{
    private final JdbcTemplate template;

    public ForumService(JdbcTemplate template) {
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
            return template.queryForObject("SELECT LAST_INSERT_ID()",Integer.class);
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
        String sql = "SELECT * FROM `ForumDetail` " +
                     "JOIN `Forums` ON   `ForumDetail`.`id`= `Forums`.`id`  " +
                     "AND `Forums`.`id` = ?;";
        return template.queryForObject(sql, Forum_DETAIL_ROWMAPPER ,id);
    }

    public int getCount(){
        String sql = "SELECT count(*) FROM `Forums`";
        return template.queryForObject(sql, Integer.class);
    }

    public List<Integer> getListThread(int forum_id,String since, String order, Integer limit){
        String sql = "SELECT `Thread`.`id` FROM `Thread`  " +
                     "JOIN `ForumDetail` ON `Thread`.`forum` = `ForumDetail`.`id`" +
                     "AND `ForumDetail`.`id` = ? AND TIMESTAMPDIFF(SECOND, ?, `Thread`.`date`) >= 0 " +
                     "ORDER BY `Thread`.`date` " + order;
        String sqlLimit=(limit!=null&&limit!=0)?" LIMIT "+limit+";":";";
        return template.queryForList(sql+sqlLimit, Integer.class, forum_id,since);
    }

    public List<Integer> getListPost(int forum_id, String since, String order, Integer limit){
        String sql = "SELECT `Post`.`id` FROM `Post`  " +
                     "JOIN `ForumDetail` ON `Post`.`forum` = `ForumDetail`.`id`" +
                     "AND `ForumDetail`.`id` = ? AND TIMESTAMPDIFF(SECOND, ?, `Post`.`date`) >= 0 " +
                     "ORDER BY `Post`.`date` " + order;
        String sqlLimit=(limit!=null&&limit>0)?" LIMIT "+limit+";":";";
        return template.queryForList(sql+sqlLimit, Integer.class, forum_id,since);

    }

    public List<UserDetailAll> getListUser(int forum_id, int since, String order, Integer limit){
        String sql =    "SELECT DISTINCT `Users`.`id`, " +
                        "`Users`.`email`, " +
                        "`UserProfile`.`name`, " +
                        "`UserProfile`.`username`, " +
                        "`UserProfile`.`about`, " +
                        "`UserProfile`.`isAnonymous` " +
                        "FROM `Users` "+
                        "JOIN `UserProfile`"+
                        "ON `Users`.`id` = `UserProfile`.`id`"+
                        "JOIN `Post` " +
                        "ON `Post`.`user` = `Users`.`id` " +
                        "AND `forum` = ? " +
                        "AND `Users`.`id` >= ? " +
                        "ORDER BY `name` "+ order;
        String sqlLimit=(limit!=null&&limit>0)?" LIMIT "+limit+"":"";
        List<UserDetailAll> users = template.query(sql+sqlLimit, USER_DETAIL_ALL_ROW_MAPPER,forum_id,since);
        if (users.size()>0) {
            HashMap<Integer,UserDetailAll> userList = new HashMap<>();
            for (UserDetailAll user1 : users) userList.put(user1.getId(), user1);
            String userListId = "(";
            for (UserDetailAll user : users) {
                userListId += user.getId().toString() + ", ";
            }
            userListId += "0)";
            sql = "SELECT `id`, `email` " +
                  "FROM `Users`" +
                  "JOIN `Followers`  ON `Followers`.`followee` = `Users`.`id` " +
                  "AND `follower` IN " + userListId;
            List<ListObject> listFollowing = template.query(sql,Following_ROWMAPPER);
            sql = "SELECT `id`, `email` " +
                  "FROM `Users`" +
                  "JOIN `Followers`  ON `Followers`.`follower` = `Users`.`id` " +
                  "AND `followee` IN " + userListId;
            List<ListObject> listFollowee =template.query(sql, Followee_ROWMAPPER);
            sql = "SELECT `user`, `thread`  " +
                  "FROM `Subscriptions` " +
                  "WHERE `user` IN " + userListId;
            List<ListObject> listSubscriptions = template.query(sql, Subscriptions_ROWMAPPER);
            for (ListObject aListFollowee : listFollowee) {
                userList.get(aListFollowee.getId()).addFollowers(aListFollowee.getValue().toString());
            }
            for (ListObject aListFollowing : listFollowing) {
                userList.get(aListFollowing.getId()).addFollowing(aListFollowing.getValue().toString());
            }
            for (ListObject listSubscription : listSubscriptions)
                userList.get(listSubscription.getId()).addSubscriptions((Integer)listSubscription.getValue());
        }
        return users;
    }
}
