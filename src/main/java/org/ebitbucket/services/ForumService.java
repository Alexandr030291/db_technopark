package org.ebitbucket.services;

import org.ebitbucket.model.Forum.ForumDetail;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional
public class ForumService {
    private final JdbcTemplate template;

    public ForumService(JdbcTemplate template) {
        this.template = template;
    }

    public int create(String name, String short_name, String email) {
        try {
            String sql = "INSERT INTO `Forum`(`name`, `short_name`,`user`) VALUE(?,?,?);";
            template.update(sql, name, short_name, email);
            return 0;
        } catch (DuplicateKeyException dk) {
            return -1;
        }
    }

    public ForumDetail detail(String short_name){
        String sql = "SELECT * FROM `Forum` WHERE `short_name` = ?;";
        return template.queryForObject(sql, Forum_DETAIL_ROWMAPPER ,short_name);
    }

    public int getCount(){
        String sql = "SELECT count(*) FROM `Forum`";
        return template.queryForObject(sql, Integer.class);
    }

    public List<Integer> getListThread(String short_name,String since, String order, Integer limit){
        String sql = "SELECT `Thread`.`id` FROM `Thread`  " +
                     "JOIN `Forum` ON `Thread`.`forum` = `Forum`.`short_name`" +
                     "AND `Forum`.`short_name` = ? AND TIMESTAMPDIFF(SECOND, ?, `Thread`.`date`) >= 0 " +
                     "ORDER BY `Thread`.`date` " + order;
        String sqlLimit=(limit!=null&&limit!=0)?" LIMIT "+limit+";":";";
        return template.queryForList(sql+sqlLimit, Integer.class, short_name,since);
    }

    public List<Integer> getListPost(String short_name, String since, String order, Integer limit){
        String sql = "SELECT `Post`.`id` FROM `Post`  " +
                     "JOIN `Forum` ON `Post`.`forum` = `Forum`.`short_name`" +
                     "AND `Forum`.`short_name` = ? AND TIMESTAMPDIFF(SECOND, ?, `Post`.`date`) >= 0 " +
                     "ORDER BY `Post`.`date` " + order;
        String sqlLimit=(limit!=null&&limit>0)?" LIMIT "+limit+";":";";
        return template.queryForList(sql+sqlLimit, Integer.class, short_name,since);

    }

    public List<String> getListUser(String short_name, Integer since, String order, Integer limit){
        String sql = "SELECT DISTINCT `UserProfile`.`id` FROM `UserProfile`" +
                     "JOIN `Post` ON `Post`.`user` = `UserProfile`.`email` " +
                     "AND `Post`.`forum` = ? " +
                     "AND `UserProfile`.`id` >= ? ";

        List<Integer> integerList=template.queryForList(sql, Integer.class, short_name,since);
        String root ="(";
        for (Integer a_root : integerList) root += a_root.toString() + ", ";
        root += "0)";
        sql =   "SELECT `email`FROM `UserProfile` "+
                "WHERE `id` IN " + root +
                "ORDER BY `name` "+ order;
        String sqlLimit=(limit!=null&&limit>0)?" LIMIT "+limit+";":";";
        return template.queryForList(sql+sqlLimit, String.class);
    }

    private final RowMapper<ForumDetail> Forum_DETAIL_ROWMAPPER = (rs, rowNum) -> new ForumDetail(rs.getInt("id"),
            rs.getString("name"),
            rs.getString("short_name"),
            rs.getString("user"));
}