package org.ebitbucket.services;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
public class UserService {
	private final JdbcTemplate template;

	public UserService(JdbcTemplate template) {
		this.template = template;
	}

	public int create(String email, String name, String username, String about, Boolean isAnonymous) {
		try {
			String sql = "INSERT INTO User(email, name, user_name, about, isAnonymous) VALUE(?,?,?,?,?)";
			template.update(sql, email, name, username, about, isAnonymous);
			return template.queryForObject("SELECT id FROM User where email =?", Integer.class, email);
		} catch (DuplicateKeyException dk) {
			return -1;
		}
	}

	public HashMap<String, String> profil(String email) {
		HashMap<String, String> result = new HashMap<>();
		String sql = "SELECT * FROM User WHERE email = ?";
		template.queryForMap(sql, result, email);
		return result;
	}

	public List<String> following(String email) {
		List<String> result = new ArrayList<>();
		String sql = "SELECT email FROM User JOIN Following ON Following.parent=User.id AND Following.user = ?";
		template.queryForList(sql, result, email);
		return result;
	}

	public List<String> followers(String email) {
		List<String> result = new ArrayList<>();
		String sql = "SELECT email FROM User JOIN Followers ON Followers.parent=User.id AND Followers.user = ?";
		template.queryForList(sql, result, email);
		return result;
	}

	public List<Integer> subscriptions(String email) {
		List<Integer> result = new ArrayList<>();
		String sql = "SELECT thread FROM Subscriptions WHERE Subscriptions.user=?";
		template.queryForList(sql, result, email);
		return result;
	}
}
