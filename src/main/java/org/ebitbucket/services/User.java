package org.ebitbucket.services;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class User {
    private JdbcTemplate template;

    public User(JdbcTemplate template) {
        this.template = template;
    }

    public int create(String email,String name,String username,String about,Boolean isAnonymous){
        try {
            String sql = "INSERT INTO technopark.User(email, name, user_name, about, isAnonymous) VALUE(?,?,?,?,?)";
            template.update(sql, email, name, username, about, isAnonymous);
            sql = "SELECT id FROM technopark.User(email) VALUE (?)";
            return template.queryForObject(sql,Integer.class,email);
        }catch (DuplicateKeyException dk){
            return -1;
        }
    }

    public HashMap<String,String> profil(String email){
        HashMap<String,String> result= new HashMap<>();
        String sql = "SELECT * FROM technopark.User WHERE email = ?";
        template.queryForMap(sql,result,email);
        return result;
    }

    public List<String> following(String email){
        List<String> result = new ArrayList<>();
        String sql="SELECT email FROM technopark.User JOIN technopark.Following ON technopark.Following.parent=technopark.User.id AND technopark.Following.user = ?";
        template.queryForList(sql,result,email);
        return result;
    }

    public List<String> followers(String email){
        List<String> result = new ArrayList<>();
        String sql="SELECT email FROM technopark.User JOIN technopark.Followers ON technopark.Followers.parent=technopark.User.id AND technopark.Followers.user = ?";
        template.queryForList(sql,result,email);
        return result;
    }

    public List<Integer> subscriptions(String email){
        List<Integer> result = new ArrayList<>();
        String sql ="SELECT thread FROM technopark.Subscriptions WHERE technopark.Subscriptions.user=?";
        template.queryForList(sql,result,email);
        return result;
    }
}
