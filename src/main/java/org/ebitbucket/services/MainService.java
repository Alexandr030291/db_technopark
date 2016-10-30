package org.ebitbucket.services;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MainService {
    private final JdbcTemplate template;

    public MainService(JdbcTemplate template) {
        this.template = template;
    }

    public void allClear(){
        template.execute("SET FOREIGN_KEY_CHECKS = 0");
        template.execute("TRUNCATE TABLE `User`;");
        template.execute("TRUNCATE TABLE `Forum`;");
        template.execute("TRUNCATE TABLE `Thread`;");
        template.execute("TRUNCATE TABLE `Post`;");
        template.execute("TRUNCATE TABLE `Followers`;");
        template.execute("TRUNCATE TABLE `Subscription`;");
        template.execute("SET FOREIGN_KEY_CHECKS = 1");
    }
}
