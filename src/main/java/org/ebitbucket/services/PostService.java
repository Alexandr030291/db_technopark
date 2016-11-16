package org.ebitbucket.services;

import org.ebitbucket.lib.Functions;
import org.ebitbucket.model.Post.PostDetails;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
@Transactional
public class PostService {
    private final JdbcTemplate template;

    public PostService(JdbcTemplate template) {
        this.template = template;
    }

    public int create(Integer user,
                      String message,
                      Integer forum,
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
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(con -> {
            PreparedStatement pst = con.prepareStatement(
                    "INSERT INTO `Post` (`user`, `message`, `forum`, `thread`, `parent`, " +
                    "`date`, `isApproved`, `isHighlighted`, `isEdited`, `isSpam`, `isDeleted`) VALUES " +
                    "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"
                    , Statement.RETURN_GENERATED_KEYS);
            int index = 0;
            pst.setInt(++index, user);
            pst.setString(++index, message);
            pst.setInt(++index, forum);
            pst.setInt(++index, thread);
            pst.setObject(++index, parent,JDBCType.INTEGER);
            pst.setString(++index, date);
            pst.setObject(++index, isApproved, JDBCType.BOOLEAN);
            pst.setObject(++index, isHighlighted, JDBCType.BOOLEAN);
            pst.setObject(++index, isEdited, JDBCType.BOOLEAN);
            pst.setObject(++index, isSpam, JDBCType.BOOLEAN);
            pst.setObject(++index, isDeleted, JDBCType.BOOLEAN);
            return pst;
        }, keyHolder);
        Integer root=0;
        if (parent!=null && parent>=0){
            sql = "SELECT `mpath`, `root` FROM `Post` WHERE `id` = ?;";
            SqlRowSet set = template.queryForRowSet(sql, parent);
            set.next();
            mpath = set.getString("mpath");
            root = set.getInt("root");
        }

        Integer id = keyHolder.getKey().intValue();
        if (root <= 0) {
            root = id;
        }
        sql= "UPDATE `Post` SET `root` = ?, `mpath` = ?  WHERE `id` = ?;";

        int maxCharInMpath = 4;
        int startchar = 48;
        int code = 64;
        int [] hash = new int[maxCharInMpath];
        for(int i = id, j = maxCharInMpath -1; j>=0; i/=code,j--){
            hash[j]=startchar+i%code;
        }
        for (int i = 0; i< maxCharInMpath; i++){
            mpath+=(char)hash[i];
        }
        template.update(sql,root,mpath,id);
        return id;
    }

    public PostDetails details(int id){
        String sql="SELECT * FROM `Post` WHERE `id` = ?";
        return template.queryForObject(sql,POST_DETAIL_ROW_MAPPER,id);
    }

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
        String sql = "UPDATE `Post` SET `" +vote+ "` =  `" +vote+ "` + 1 WHERE `id` = ?;";
        return template.update(sql,id);
    }

    private static final RowMapper<PostDetails> POST_DETAIL_ROW_MAPPER = (rs, rowNum) -> new PostDetails(rs.getInt("id"),
            rs.getInt("forum"),
            rs.getInt("user"),
            rs.getInt("thread"),
            (rs.getString("parent")!=null)?rs.getInt("parent"):null,
            rs.getString("message"),
            Functions.DATE_FORMAT.format(rs.getTimestamp("date")),
            rs.getBoolean("isApproved"),
            rs.getBoolean("isDeleted"),
            rs.getBoolean("isEdited"),
            rs.getBoolean("isHighlighted"),
            rs.getBoolean("isSpam"),
            rs.getInt("dislikes"),
            rs.getInt("likes"));
}