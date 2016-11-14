package org.ebitbucket.services;

import org.ebitbucket.model.Forum.ForumDetail;
import org.springframework.beans.NullValueInNestedPathException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
@Service
@Transactional
public class ForumService {
    private final JdbcTemplate template;

    public ForumService(JdbcTemplate template) {
        this.template = template;
    }

    public int create(String name, String short_name, Integer user_id) {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            template.update(
                    new PreparedStatementCreator() {
                        @Override
                        public PreparedStatement createPreparedStatement(Connection cnctn) throws SQLException {
                            PreparedStatement ps = cnctn.prepareStatement(
                                    "INSERT INTO `Forums` (`short_name`) VALUES (?)",
                                    new String[] {"id"});
                            ps.setString(1, short_name);
                            return ps;
                        }
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

    public List<Integer> getListUser(int forum_id, Integer since, String order, Integer limit){
        String sql = "SELECT DISTINCT `UserProfile`.`id` FROM `UserProfile`" +
                     "JOIN `Post` ON `Post`.`user` = `UserProfile`.`id` " +
                     "AND `Post`.`forum` = ? " +
                     "AND `UserProfile`.`id` >= ? ";

        List<Integer> integerList=template.queryForList(sql, Integer.class, forum_id,since);
        String root ="(";
        for (Integer a_root : integerList) root += a_root.toString() + ", ";
        root += "0)";
        sql =   "SELECT `id`FROM `UserProfile` "+
                "WHERE `id` IN " + root +
                "ORDER BY `name` "+ order;
        String sqlLimit=(limit!=null&&limit>0)?" LIMIT "+limit+";":";";
        return template.queryForList(sql+sqlLimit, Integer.class);
    }

    private final RowMapper<ForumDetail> Forum_DETAIL_ROWMAPPER = (rs, rowNum) -> new ForumDetail(rs.getInt("id"),
            rs.getString("name"),
            rs.getString("short_name"),
            rs.getString("user"));
}