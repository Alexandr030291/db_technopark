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

    public void userClear(){
        template.execute("TRUNCATE TABLE `User`;");
    }

    public void forumClear() {
        template.execute("TRUNCATE TABLE `Forum`;");
    }

    public void treadClear() {
        template.execute("TRUNCATE TABLE `Thread`;");
    }

    public void postClear() {
        template.execute("TRUNCATE TABLE `Post`;");
    }

    public void followersClear() {
        template.execute("TRUNCATE TABLE `Followers`;");
    }

    public void subscriptionClear() {
        template.execute("TRUNCATE TABLE `Subscription`;");
    }

    public void allClear(){
        postClear();
        followersClear();
        subscriptionClear();
        postClear();
        treadClear();
        forumClear();
        userClear();
    }
}
