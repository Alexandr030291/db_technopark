package org.ebitbucket.services;

import org.ebitbucket.model.User.UserDetail;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
			sql = "SELECT id FROM User where email =?";
			return template.queryForObject(sql, Integer.class, email);
		} catch (DuplicateKeyException dk) {
			return -1;
		}
	}

	public UserDetail profil(String email) {
		String sql = "SELECT * FROM User WHERE email = ?";
        return template.queryForObject(sql, UserDetail.class, email);
	}

	public List<String> following(String email) {
		List<String> result = new ArrayList<>();
		String sql = "SELECT email FROM User JOIN Followers ON Following.id=User.id AND User.email = ?";
		template.queryForList(sql, result, email);
		return result;
	}

	public List<String> followers(String email) {
		List<String> result = new ArrayList<>();
		String sql = "SELECT email FROM User JOIN Followers ON Followers.id=User.id AND Followers.followee = ?";
		template.queryForList(sql, result, email);
		return result;
	}

	public List<Integer> subscriptions(String email) {
		List<Integer> result = new ArrayList<>();
		String sql = "SELECT thread FROM Subscriptions WHERE Subscriptions.user=?";
		template.queryForList(sql, result, email);
		return result;
	}

    public void addFollowers(Integer id,String followee){
        String sql = "INSERT INTO Followers(id, followee) VALUE (?,?)";
        template.update(sql, id, followee);
    }


}
