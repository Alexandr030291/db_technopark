package org.ebitbucket.services;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Service
public class User {
    JdbcTemplate template;

    public User(JdbcTemplate template) {
        this.template = template;
    }

    public int create(String email,String name,String username,String about,Boolean isAnonymous){
        try {
            String sql = "INSERT INTO User(email, name, user_name, about, isAnonymous) VALUE(?,?,?,?,?)";
            template.update(sql, email, name, username, about, isAnonymous);
            sql = "SELECT id FROM User WHERE email = ?";
            return template.queryForObject(sql,Integer.class,email);
        }catch (DuplicateKeyException dk){
            return -1;
        }
    }

}
