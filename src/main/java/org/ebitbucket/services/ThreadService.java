package org.ebitbucket.services;

import org.ebitbucket.lib.Functions;
import org.ebitbucket.model.Tread.ThreadDetail;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@Transactional
public class ThreadService {
    private final JdbcTemplate template;

    public ThreadService(JdbcTemplate template) {
        this.template = template;

    }

    public int create(int forum,
                      int user,
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
            template.update(sql,user,thread);
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
        String sqlLimit=(limit!=null&&limit>0)?" LIMIT "+limit+";":";";
        return template.queryForList(sql+sqlLimit, Integer.class, id,since);

    }

    public List<Integer> getListPostInTree(Integer id, String since,String order, Integer limit){
        String sql ="SELECT `Post`.`id` FROM `Post`  " +
                    "JOIN `Thread` ON `Post`.`thread` = `Thread`.`id`" +
                    "AND `Thread`.`id` = ? AND TIMESTAMPDIFF(SECOND, ?, `Post`.`date`) >= 0 " +
                    "ORDER BY `Post`.`root` " + order + ", `Post`.`mpath` ASC";
        String sqlLimit=(limit!=null&&limit>0)?" LIMIT "+limit+";":";";
        return template.queryForList(sql+sqlLimit, Integer.class, id,since);
    }

    public List<Integer> getListPostInParentTree(Integer thread, String since,String order, Integer limit){
        String LIMIT = (limit!=null&&limit!=0)?" LIMIT "+limit+";":";";
        String sql ="SELECT  DISTINCT `Post`.`root` FROM `Post` " +
                    "JOIN `Thread` ON `Post`.`thread` = `Thread`.`id` " +
                    "AND TIMESTAMPDIFF(SECOND, ?, `Post`.`date`) >= 0 "+
                    "AND `Thread`.`id` = ? "  +
                    "ORDER BY `Post`.`root` "+order + LIMIT;
        List<Integer> integerList=template.queryForList(sql,Integer.class, since,thread);
        String root ="(";
        for (Integer a_root : integerList) root += a_root.toString() + ", ";
        root += "0)";
        sql =   "SELECT `id` FROM `Post` "+
                "WHERE `root` IN " + root+
                "AND TIMESTAMPDIFF(SECOND, ?, `date`) >= 0 "+
                "ORDER BY `root` " + order + ", `mpath` ASC;";
        return template.queryForList(sql, Integer.class, since);
    }

    public int vote(int id, String vote){
        String sql = "UPDATE `Thread` SET `" +vote+ "` =  `" +vote+ "` + 1 WHERE `id` = ?;";
        return template.update(sql,id);
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
