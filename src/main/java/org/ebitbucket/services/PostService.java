package org.ebitbucket.services;

import org.ebitbucket.model.Post.PostDetails;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PostService {
    final int max = 4;
    private final JdbcTemplate template;

    public PostService(JdbcTemplate template) {
        this.template = template;
    }

    public int create(String user,
                      String message,
                      String forum,
                      Integer thread,
                      Integer parent,
                      String date,
                      Boolean isApproved,
                      Boolean isHighlighted,
                      Boolean isEdited,
                      Boolean isSpam,
                      Boolean isDeleted) {
        String sql;
        String mpath ="";

        if (parent!=null && parent>=0){
            sql = "SELECT `mpath` FROM `Post` WHERE `id` = ?;";
            mpath = template.queryForObject(sql, String.class,parent);
            int pow=max;
            for(int i= parent;i>0;i/=10){
                pow--;
            }
            for (;pow>0;pow--){
                mpath+='0';
            }
            mpath+= String.valueOf(parent);
        }
        sql = "INSERT INTO `Post` (`user`, `message`, `forum`, `thread`, `parent`, " +
                "`date`, `isApproved`, `isHighlighted`, `isEdited`, `isSpam`, `isDeleted`,`mpath`) VALUES " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?);";
        template.update(sql, user, message, forum, thread, parent, date, isApproved, isHighlighted, isEdited, isSpam, isDeleted,mpath);
        sql = "SELECT `id` FROM `Post` WHERE id = last_insert_id();";
        return template.queryForObject(sql, Integer.class);
    }

    public PostDetails details(int id){
        String sql="SELECT * FROM `Post` WHERE `id` = ?";
        return template.queryForObject(sql,PostDetails.class,id);
    };

    public int getCount(){
        String sql = "SELECT count(*) FROM `Post` WHERE `isDeleted` = FALSE;";
        return template.queryForObject(sql, Integer.class);
    }

    public int update(int id, String message){
        String sql = "UPDATE `Post` SET `message` = ? WHERE `id` = ?;";
        return template.update(sql,message,id);
    }

    public int remove(int id){
        String sql = "UPDATE `Post` SET `isDeleted` = TRUE WHERE `id` = ?;";
        return template.update(sql,id);
    }

    public int restore(int id){
        String sql = "UPDATE `Post` SET `isDeleted` = FALSE WHERE `id` = ?;";
        return template.update(sql,id);
    }

    public int vote(int id, String vote){
        String sql = "UPDATE `Post` SET ? = ? + 1 FALSE WHERE `id` = ?;";
        return template.update(sql,vote,vote,id);
    }


}
