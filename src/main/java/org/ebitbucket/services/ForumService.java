package org.ebitbucket.services;

import org.ebitbucket.model.Forum.ForumDetail;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ForumService {
    private final JdbcTemplate template;

    public ForumService(JdbcTemplate template) {
        this.template = template;
    }

    public int create(String name, String short_name, String email){
        try {
            String sql = "INSERT INTO `Forum`(`name`, `short_name`,`user`) VALUE(?,?,?);";
            template.update(sql,name, short_name, email);
            return 0 ;
        }catch (DuplicateKeyException dk) {
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
        String sql ="SELECT `id` FROM `Thread`  " +
                "JOIN `Forum` ON `Thread`.`forum` = `Forum`.`short_name`" +
                "AND `Forum`.`short_name` = ? AND `Thread`.TIMESTAMPDIFF(SECOND, ?, `date`) >= 0 " +
                "ORDER BY `date` ? ";
        List<Integer> result = new ArrayList<>();
        if(limit>0){
            template.queryForList(sql+"LIMIT ?;",result,short_name,since,order,limit);
        }else {
            template.queryForList(sql+";", result, short_name,since,order);
        }
        return result;
    }

    public List<Integer> getListPost(String short_name,String since, String order, Integer limit){
        String sql ="SELECT `id` FROM `Post`  " +
                "JOIN `Forum` ON `Post`.`forum` = `Forum`.`short_name`" +
                "AND `Forum`.`short_name` = ? AND `Thread`.TIMESTAMPDIFF(SECOND, ?, `date`) >= 0 " +
                "ORDER BY `date` ? ";
        List<Integer> result = new ArrayList<>();
        if(limit>0){
            template.queryForList(sql+"LIMIT ?;",result,short_name,since,order,limit);
        }else {
            template.queryForList(sql+";", result, short_name,since,order);
        }
        return result;
    }

    private static final RowMapper<ForumDetail> Forum_DETAIL_ROWMAPPER = new RowMapper<ForumDetail>() {

        @Override
        public ForumDetail mapRow(ResultSet rs, int rowNum) throws SQLException {

            return new ForumDetail(rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("short_name"),
                    rs.getString("user"));
        }
    };
}
