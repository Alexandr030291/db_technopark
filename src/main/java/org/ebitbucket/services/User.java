package org.ebitbucket.services;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Service
public class User {
    private JdbcTemplate template;
    private StringBuilder userCreated;

    public User(JdbcTemplate template) {
        this.template = template;
        this.userCreated.
                append("CREATE TABLE technopark.User\n").
                append("(\n").
                append("\tid INT PRIMARY KEY AUTO_INCREMENT,\n").
                append("\temail VARCHAR(50) CHARACTER SET 'utf8' NOT NULL,\n").
                append("\tname VARCHAR(50) CHARACTER SET 'utf8',\n").
                append("\tusername VARCHAR(50) CHARACTER SET 'utf8',\n").
                append("\tabout VARCHAR(50) CHARACTER SET 'utf8',\n").
                append("\tisAnonymous BOOLEAN\n").
                append(");\n").
                append("CREATE UNIQUE INDEX User_email_uindex ON technopark.User (email);");
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

    public StringBuilder getUserCreated() {
        return userCreated;
    }
}
