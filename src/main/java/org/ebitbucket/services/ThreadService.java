package org.ebitbucket.services;

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
        sql = "SELECT `id` FROM `Post` WHERE `id` = last_insert_id();";
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

    public ThreadDetail detail(Integer id){
        String sql="SELECT * FROM `Thread` WHERE `id` = ?;";
       return template.queryForObject(sql,THREAD_DETAIL_ROW_MAPPER,id);
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
        String sql ="SELECT `id` FROM `Post`  " +
                    "JOIN `Thread` ON `Post`.`thread` = `Thread`.`id`" +
                    "AND `Thread`.`id` = ? AND `Thread`.TIMESTAMPDIFF(SECOND, ?, `Post`.`date`) >= 0 " +
                    "ORDER BY `Post`.`date` ? ";
        List<Integer> result = new ArrayList<>();
        if(limit>0){
            template.queryForList(sql+"LIMIT ?;",result,id,since,order,limit);
        }else {
            template.queryForList(sql+";", result, id,since,order);
        }
        return result;
    }

    public List<Integer> getListPostInTree(Integer id, String since,String order, Integer limit){
        String sql ="SELECT `id` FROM `Post`  " +
                    "JOIN `Thread` ON `Post`.`thread` = `Thread`.`id`" +
                    "AND `Thread`.`id` = ? AND TIMESTAMPDIFF(SECOND, ?, `Post`.`date`) >= 0 " +
                    "ORDER BY `Post`.`mpath` ? ";
        List<Integer> result = new ArrayList<>();
        if(limit>0){
            template.queryForList(sql+"LIMIT ?;",result,id,since,order,limit);
        }else {
            template.queryForList(sql+";", result, id,since,order);
        }
        return result;
    }

    public List<Integer> getListPostInParentTree(Integer id, String since,String order, Integer limit){
        String LIMIT = (limit>0)?"LIMIT ?":"";
        String sql ="SELECT `id` FROM `Post` WHEN `parent` IN " +
                    "(SELECT `id` FROM `Post` " +
                    "JOIN `Thread` ON `Post`.`thread` = `Thread`.`id` AND `Post`.`parent` = NULL " +
                    "AND `Thread`.`id` = ? " +
                    "ORDER BY `Post`.`parent` ?" +LIMIT+ ")"+
                    "AND TIMESTAMPDIFF(SECOND, ?, `Post`.`date`) >= 0 " +
                    "ORDER BY `Post`.`mpath` ? ;";
        List<Integer> result = new ArrayList<>();
        if(limit>0){
            template.queryForList(sql,result,id,order,limit,since,order);
        }else {
            template.queryForList(sql, result, id,order,since,order);
        }
        return result;
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

    private static final RowMapper<ThreadDetail> THREAD_DETAIL_ROW_MAPPER = new RowMapper<ThreadDetail>() {

        @Override
        public ThreadDetail mapRow(ResultSet rs, int rowNum) throws SQLException {

            return new ThreadDetail(rs.getInt("id"),
                    rs.getString("forum"),
                    rs.getString("user"),
                    rs.getString("title"),
                    rs.getString("message"),
                    rs.getString("slug"),
                    rs.getString("date"),
                    rs.getBoolean("isClosed"),
                    rs.getBoolean("isDeleted"),
                    rs.getInt("likes"),
                    rs.getInt("dislikes"));
        }
    };
}
