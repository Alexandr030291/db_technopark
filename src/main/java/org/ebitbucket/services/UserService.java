package org.ebitbucket.services;

import org.ebitbucket.model.User.UserDetail;
import org.ebitbucket.model.User.UserDetailAll;
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
public class UserService {
	private final JdbcTemplate template;

	public UserService(JdbcTemplate template) {
		this.template = template;
	}

	public int create(String email, String name, String username, String about, Boolean isAnonymous) {
		try {
			String sql = "INSERT INTO `UserProfile` (`username`, `email`, `name`, `about`, `isAnonymous`) VALUES (?, ?, ?, ?, ?);";
			template.update(sql, username, email, name, about, isAnonymous);
			return 0;
		} catch (DuplicateKeyException dk) {
			return -1;
		}
	}

	public UserDetail profile(String email) {
		String sql = "SELECT * FROM `UserProfile` WHERE `email` = ?;";
		return template.queryForObject(sql, USER_DETAIL_ROWMAPPER, email);
	}

	public UserDetailAll profileAll(String email) {
		String sql = "SELECT * FROM `UserProfile` WHERE `email` = ?;";
		return template.queryForObject(sql, USER_DETAIL_ALL_ROW_MAPPER, email);
	}

	public List<String> following(String email) {
		String sql = "SELECT `followee` FROM `Followers` WHERE `follower` = ?;";
		return template.queryForList(sql, String.class, email);
	}

	public List<String> followers(String email) {
		String sql = "SELECT `follower` FROM `Followers` WHERE `followee` = ?;";
		return template.queryForList(sql, String.class, email);
	}

	public List<Integer> subscriptions(String email) {
		String sql = "SELECT `thread` FROM `Subscriptions` WHERE `Subscriptions`.`user`=?;";
		return template.queryForList(sql, Integer.class, email);
	}

	public int addFollowers(String follower, String followee) {
		try {
			String sql = "INSERT INTO `Followers`(`follower`, `followee`) VALUE (?,?);";
			template.update(sql, follower, followee);
			return 0;
		} catch (DuplicateKeyException dk) {
			return 1;
		}
	}

	public int delFollowers(String follower, String followee) {
		String sql = "DELETE FROM `Followers` WHERE `follower` = ? AND `followee` = ?;";
		return template.update(sql, follower, followee);
	}

	public int updateProfile(String email, String name, String about) {
		String sql = "UPDATE `UserProfile` SET `name` = ?, `about` = ? WHERE `email` = ?;";
		return template.update(sql, name, about, email);
	}

	public List<String> getListFollowers(String email, String order, Integer since_id, Integer limit) {
		String sql = "SELECT `follower` " +
				"FROM `Followers` " +
				"JOIN `User` ON `Followers`.`follower`=`User`.`email` " +
				"AND `Followers`.`followee` = ?  " +
				"AND `User`.`id` = ? " +
				"ORDER BY `USER`.`name` " + order;
		String sqlLimit = (limit != null) ? " LIMIT " + limit + ";" : ";";
		return template.queryForList(sql + sqlLimit, String.class, email, since_id);
	}

	public List<String> getListFollowing(String email, String order, Integer since_id, Integer limit) {
		String sql = "SELECT `followee` " +
				"FROM `Followers` " +
				"JOIN `User` ON `Followers`.`follower`=`User`.`email` " +
				"AND `Followers`.`follower` = ?  " +
				"AND `User`.`id` = ? " +
				"ORDER BY `USER`.`name` " + order;
		String sqlLimit = (limit != null) ? " LIMIT " + limit + ";" : ";";
		return template.queryForList(sql + sqlLimit, String.class, email, since_id);
	}


	public int getCount() {
		String sql = "SELECT count(*) FROM `User`";
		return template.queryForObject(sql, Integer.class);
	}

	public List<Integer> getListPost(String email, String order, String since, Integer limit) {
		String sql = "SELECT `id` " +
				"FROM `Post` " +
				"WHERE `user` = ? AND TIMESTAMPDIFF(SECOND, ?, `date`) >= 0 " +
				"ORDER BY `date` ?" + order;
		String sqlLimit = (limit != null) ? " LIMIT " + limit + ";" : ";";
		return template.queryForList(sql + sqlLimit, Integer.class, email, since);
	}

	public List<Integer> getListThread(String email, String order, String since, Integer limit) {
		String sql = "SELECT `Thread`.`id` FROM `Thread` " +
				"JOIN  `UserProfile` ON `Thread`.`user` = `UserProfile`.`email`" +
				"AND `UserProfile`.`email` = ? AND TIMESTAMPDIFF(SECOND, ?, `Thread`.`date`) >= 0 " +
				"ORDER BY `Thread`.`date` " + order;
		String sqlLimit = (limit != null) ? " LIMIT " + limit + ";" : ";";
		return template.queryForList(sql + sqlLimit, Integer.class, email, since);
	}

	private static final RowMapper<UserDetail> USER_DETAIL_ROWMAPPER = (rs, rowNum) -> new UserDetail(rs.getInt("id"),
			rs.getString("username"),
			rs.getString("name"),
			rs.getString("email"),
			rs.getString("about"),
			rs.getBoolean("isAnonymous"));
/*
Integer id, String username, String name, String email,  String about, Boolean isAnonymous
 */
	private final RowMapper<UserDetailAll> USER_DETAIL_ALL_ROW_MAPPER = (rs, rowNum) -> {

		String email = rs.getString("email");
		UserDetailAll userDetailAll = new UserDetailAll(rs.getInt("id"),
				rs.getString("username"),
				rs.getString("name"),
				rs.getString("email"),
				rs.getString("about"),
				rs.getBoolean("isAnonymous"));
		userDetailAll.setFollowers(followers(email));
		userDetailAll.setFollowing(following(email));
		userDetailAll.setSubscriptions(subscriptions(email));
		return userDetailAll;
	};

}
