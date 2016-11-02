package org.ebitbucket.services;

import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager;
import org.ebitbucket.lib.Functions;
import org.ebitbucket.model.Tread.ThreadDetail;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ThreadService {
    private final JdbcTemplate template;

    public ThreadService(JdbcTemplate template) {
        this.template = template;

    }

    public int create(String forum,
                      String user,
                      String title,
                      String message,
                      String slug,
                      String date,
                      Boolean isClosed,
                      Boolean isDeleted) {
        String sql = "INSERT INTO `Thread` (`forum`, `user`, `title`, `message`, `slug`, `date`, `isClosed`, `isDeleted`) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
        template.update(sql, forum, user, title, message,  slug, date, isClosed, isDeleted);
        sql = "SELECT LAST_INSERT_ID();";
        return template.queryForObject(sql, Integer.class);
    }

    public boolean subscribe(Integer thread, String user){
        try {
            String sql="INSERT INTO `Subscriptions` (`user`, `thread`) VALUES (?, ?);";
            template.update(sql,thread,user);
            return true;
        }catch (DataIntegrityViolationException dive){
            return false;
        }
    }

    public boolean unsubscribe(Integer thread, String user){
        try {
            String sql="DELETE FROM `Subscriptions` WHERE `thread`= ? AND `user` = ?;";
            template.update(sql,thread,user);
            return true;
        }catch (DataIntegrityViolationException dive){
            return false;
        }
    }

    public ThreadDetail detail(Integer id) {
        String sql = "SELECT * FROM `Thread` WHERE `Thread`.`id` = ?;";
        return template.queryForObject(sql, THREAD_DETAIL_ROW_MAPPER, id);
    }

    public int getCountPost(Integer id){
        String sql = "SELECT count(*) FROM `Post` WHERE `thread` = ? AND `isDeleted` = FALSE;";
        return template.queryForObject(sql, Integer.class, id);
    }

    public int getCount(){
        String sql = "SELECT count(*) FROM `Thread` WHERE `isDeleted` = FALSE;";
        return template.queryForObject(sql, Integer.class);
    }

    public List<Integer> getListPost(Integer id, String since, String order, Integer limit){
        String sql ="SELECT `Post`.`id` FROM `Post`  " +
                    "JOIN `Thread` ON `Post`.`thread` = `Thread`.`id`" +
                    "AND `Thread`.`id` = ? AND TIMESTAMPDIFF(SECOND, ?, `Post`.`date`) >= 0 " +
                    "ORDER BY `Post`.`date` " + order;
        String sqlLimit=(limit!=null)?" LIMIT "+limit+";":";";
        return template.queryForList(sql+sqlLimit, Integer.class, id,since);

    }

    public List<Integer> getListPostInTree(Integer id, String since,String order, Integer limit){
        String sql ="SELECT `Post`.`id` FROM `Post`  " +
                    "JOIN `Thread` ON `Post`.`thread` = `Thread`.`id`" +
                    "AND `Thread`.`id` = ? AND TIMESTAMPDIFF(SECOND, ?, `Post`.`date`) >= 0 " +
                    "ORDER BY `Post`.`mpath` " + order;
        String sqlLimit=(limit!=null)?" LIMIT "+limit+";":";";
        return template.queryForList(sql+sqlLimit, Integer.class, id,since);
    }

    public List<Integer> getListPostInParentTree(Integer id, String since,String order, Integer limit){
        String LIMIT = (limit!=null)?" LIMIT "+limit+"":"";
        String sql ="SELECT `Post`.`id` FROM `Post` JOIN " +
                    "(SELECT `Post`.`id` FROM `Post` " +
                    "JOIN `Thread` ON `Post`.`thread` = `Thread`.`id` " +
                    "AND `Post`.`parent` = NULL " +
                    "AND `Thread`.`id` = ? " +
                    "AND TIMESTAMPDIFF(SECOND, ?, `Post`.`date`) >= 0 " +
                    "ORDER BY `Post`.`parent` "+order + LIMIT+ ")"+
                    "AS Selection ON `Post`.`id` = Selection.`id` OR `Post`.`parent` = Selection.`id`"+
                    "ORDER BY `Post`.`mpath` " + order + ";";
        return template.queryForList(sql, Integer.class, id,since);
    }

    public int vote(int id, String vote){
        String sql = "UPDATE `Thread` SET ? = ? + 1 WHERE `id` = ?;";
        return template.update(sql,vote,vote,id);
    }

    public int close(int id){
        String sql = "UPDATE `Thread` SET `isClosed` = TRUE WHERE `id` = ?;";
        return template.update(sql,id);
    }

    public int open(int id){
        String sql = "UPDATE `Thread` SET `isClosed` = FALSE WHERE `id` = ?;";
        return template.update(sql,id);
    }

    public int remove(int id){
        String sql ="UPDATE `Thread` SET `isDeleted` = TRUE WHERE `id` = ?;";
        if (template.update(sql,id)==0)
            return 0;
        sql = "UPDATE `Post` SET `isDeleted` = TRUE WHERE `thread` = ?;";
        template.update(sql,id);
        return 1;
    }

    public int restore(int id){
        String sql ="UPDATE `Thread` SET `isDeleted` = FALSE WHERE `id` = ?;";
        if (template.update(sql,id)==0)
            return 0;
        sql = "UPDATE `Post` SET `isDeleted` = FALSE WHERE `thread` = ?;";
        template.update(sql,id);
        return 1;
    }

    public int update(int id, String message,String slug){
        String sql = "UPDATE `Thread` SET `message` = ?, `slug` = ? WHERE `id` = ?;";
        return template.update(sql,message,slug,id);
    }

    private static final RowMapper<ThreadDetail> THREAD_DETAIL_ROW_MAPPER = (rs, rowNum) -> new ThreadDetail(rs.getInt("id"),
            rs.getString("forum"),
            rs.getString("user"),
            rs.getString("title"),
            rs.getString("message"),
            rs.getString("slug"),
            Functions.DATE_FORMAT.format(rs.getTimestamp("date")),
            rs.getBoolean("isClosed"),
            rs.getBoolean("isDeleted"),
            rs.getInt("likes"),
            rs.getInt("dislikes"));
}
