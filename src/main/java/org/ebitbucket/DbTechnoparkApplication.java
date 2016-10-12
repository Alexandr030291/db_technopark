package org.ebitbucket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class DbTechnoparkApplication {
    @Autowired
    JdbcTemplate template;
	public static void main(String[] args) {
		SpringApplication.run(DbTechnoparkApplication.class, args);
	}
}
