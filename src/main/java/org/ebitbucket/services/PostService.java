package org.ebitbucket.services;

import org.ebitbucket.lib.Functions;
import org.ebitbucket.model.Post.PostDetails;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;

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
            mpath = template.queryForObject(sql, String.class, parent);
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
        return template.queryForObject(sql,POST_DETAIL_ROW_MAPPER,id);
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

    private static final RowMapper<PostDetails> POST_DETAIL_ROW_MAPPER = new RowMapper<PostDetails>() {

        @Override
        public PostDetails mapRow(ResultSet rs, int rowNum) throws SQLException {

            return new PostDetails(rs.getInt("id"),
                    rs.getString("forum"),
                    rs.getString("user"),
                    rs.getString("thread"),
                    rs.getInt("parent"),
                    rs.getString("message"),
                    Functions.DATE_FORMAT.format(rs.getTimestamp("date")),
                    rs.getBoolean("isApproved"),
                    rs.getBoolean("isDeleted"),
                    rs.getBoolean("isEdited"),
                    rs.getBoolean("isHighlighted"),
                    rs.getBoolean("isSpam"),
                    rs.getInt("likes"),
                    rs.getInt("dislikes"));
        }
    };
}