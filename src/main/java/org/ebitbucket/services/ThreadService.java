package org.ebitbucket.services;

import org.ebitbucket.model.Tread.ThreadDetail;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@Transactional
public class ThreadService extends MainService{
    private final JdbcTemplate template;

    public ThreadService(JdbcTemplate template) {
        super(template);
        this.template = template;

    }

    public boolean subscribe(Integer thread, int user){
        try {
            String sql="INSERT INTO `Subscriptions` (`user`, `thread`) VALUES (?, ?);";
            template.update(sql,user,thread);
            return true;
        }catch (DataIntegrityViolationException dive){
            return false;
        }
    }

    public boolean unsubscribe(Integer thread, int user){
        try {
            String sql="DELETE FROM `Subscriptions` WHERE `thread`= ? AND `user` = ?;";
            template.update(sql,thread,user);
            return true;
        }catch (DataIntegrityViolationException dive){
            return false;
        }
    }

    public ThreadDetail detail(Integer id) {
        try {
            String sql = "SELECT * FROM `Thread` WHERE `Thread`.`id` = ?;";
            return template.queryForObject(sql, THREAD_DETAIL_ROW_MAPPER, id);
        }catch (EmptyResultDataAccessException na){
            return null;
        }
    }

    public int getCountPost(Integer id){
        String sql = "SELECT `posts` FROM `Thread` WHERE `id` = ?;";
        return template.queryForObject(sql, Integer.class, id);
    }

    public int getCount(){
        String sql = "SELECT  `count` FROM `LastId` WHERE `table` = 'thread';";
        return template.queryForObject(sql, Integer.class);
    }

    public List<Integer> getListPost(Integer id, String since, String order, Integer limit){
        if (since==null){
            String sql ="SELECT `Post`.`id` FROM `Post`  " +
                    "WHERE `Post`.`thread` = ? " +
                    "ORDER BY `Post`.`date` " + order;
            String sqlLimit=(limit!=null&&limit>0)?" LIMIT "+limit+";":";";
            return template.queryForList(sql+sqlLimit, Integer.class, id);
        }
        String sql ="SELECT `Post`.`id` FROM `Post`  " +
                    "WHERE `Post`.`thread` = ? " +
                    "AND `Post`.`date` >= ? "+
                    "ORDER BY `Post`.`date` " + order;
        String sqlLimit=(limit!=null&&limit>0)?" LIMIT "+limit+";":";";
        return template.queryForList(sql+sqlLimit, Integer.class, id, since);

    }

    public List<Integer> getListPostInTree(Integer id, String since,String order, Integer limit){
        if (since==null){
            String sql ="SELECT `Post`.`id` FROM `Post`  " +
                    "WHERE `Post`.`thread` = ? " +
                    "ORDER BY `Post`.`root` " + order + ", `Post`.`mpath` ASC";
            String sqlLimit=(limit!=null&&limit>0)?" LIMIT "+limit+";":";";
            return template.queryForList(sql+sqlLimit, Integer.class, id);
        }
        String sql ="SELECT `Post`.`id` FROM `Post`  " +
                    "WHERE `Post`.`thread` = ? " +
                    "AND `Post`.`date` >= ? " +
                    "ORDER BY `Post`.`root` " + order + ", `Post`.`mpath` ASC";
        String sqlLimit=(limit!=null&&limit>0)?" LIMIT "+limit+";":";";
        return template.queryForList(sql+sqlLimit, Integer.class, id, since);
    }

    public List<Integer> getListPostInParentTree(Integer thread, String since,String order, Integer limit){
        if (since==null){
            String LIMIT = (limit!=null&&limit!=0)?" LIMIT "+limit+" ":" ";
            String sql =   "SELECT `Post`.`id` FROM " +
                    "(" +
                    "SELECT DISTINCT `Post`.`root` " +
                    "FROM `Post` " +
                    "WHERE `thread` = ? " +
                    "ORDER BY `Post`.`root` "+order + LIMIT +
                    ") p2 "+
                    "JOIN `Post`" +
                    "ON  `Post`.`root` = p2 .`root` " +
                    "ORDER BY `Post`.`root` "+order +", `mpath` ASC;";
            return template.queryForList(sql, Integer.class, thread);
        }

        String LIMIT = (limit!=null&&limit!=0)?" LIMIT "+limit+" ":" ";
        String sql =   "SELECT `Post`.`id` FROM " +
                "(" +
                    "SELECT DISTINCT `Post`.`root` " +
                    "FROM `Post` " +
                    "WHERE `thread` = ? " +
                    "AND `date` >= ? " +
                    "ORDER BY `Post`.`root` "+order + LIMIT +
                ") p2 "+
                "JOIN `Post`" +
                "ON  `Post`.`root` = p2 .`root` " +
                "AND `Post`.`date` >= ? " +
                "ORDER BY `Post`.`root` "+order +", `mpath` ASC;";
        return template.queryForList(sql, Integer.class, thread,since,since);
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
        String sql ="UPDATE `Thread` SET `isDeleted` = TRUE, `posts` = 0 WHERE `id` = ?;";
        if (template.update(sql,id)==0)
            return 0;
        sql = "UPDATE `Post` SET `isDeleted` = TRUE WHERE `thread` = ?;";
        template.update(sql,id);
        return 1;
    }

    public int restore(int id){
        String sql ="UPDATE `Post` SET `isDeleted` = FALSE WHERE `thread` = ?;";
        int posts = template.update(sql,id);
        if (posts==0)
            return 0;
        sql = "UPDATE `Thread` SET `isDeleted` = FALSE, `posts` = ? WHERE `id` = ?;";
        return template.update(sql,posts,id);
    }

    public int update(int id, String message,String slug){
        String sql = "UPDATE `Thread` SET `message` = ?, `slug` = ? WHERE `id` = ?;";
        return template.update(sql,message,slug,id);
    }

    public int createNotAutoId(
                      int forum,
                      int user,
                      String title,
                      String message,
                      String slug,
                      String date,
                      Boolean isClosed,
                      Boolean isDeleted) {
        String sql;
        int id = getNextId("thread");
        sql = "INSERT INTO `Thread` (`id`,`forum`, `user`, `title`, `message`, `slug`, `date`, `isClosed`, `isDeleted`) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
        template.update(sql,id,forum,user,title,message,slug,date,isClosed,isDeleted);
        return id;
    }

}
