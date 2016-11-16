package org.ebitbucket.services;

import org.ebitbucket.main.Result;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Service
@Transactional
public class ControlService {
    private final JdbcTemplate template;

    public ControlService(JdbcTemplate template) {
        this.template = template;
    }

    public void allClear(){
        template.execute("SET FOREIGN_KEY_CHECKS = 0");
        template.execute("TRUNCATE TABLE `Users`;");
        template.execute("TRUNCATE TABLE `Forums`;");
        template.execute("TRUNCATE TABLE `UserProfile`;");
        template.execute("TRUNCATE TABLE `ForumDetail`;");
        template.execute("TRUNCATE TABLE `Thread`;");
        template.execute("TRUNCATE TABLE `Post`;");
        template.execute("TRUNCATE TABLE `Followers`;");
        template.execute("TRUNCATE TABLE `Subscriptions`;");
        template.execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity handleDataAccessException() {
        return ResponseEntity.ok(Result.unkownError());
    }
}
