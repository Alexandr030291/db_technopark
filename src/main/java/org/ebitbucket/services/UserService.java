package org.ebitbucket.services;


import org.ebitbucket.model.User.UserDetailAll;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
			KeyHolder keyHolder = new GeneratedKeyHolder();
			template.update(
					new PreparedStatementCreator() {
						@Override
						public PreparedStatement createPreparedStatement(Connection cnctn) throws SQLException {
							PreparedStatement ps = cnctn.prepareStatement(
									"INSERT INTO `Users` (`email`) VALUES (?)",
									new String[] {"id"});
							ps.setString(1, email);
							return ps;
						}
					}
					, keyHolder);
			String sql = "INSERT INTO `UserProfile` (`id`,`username`, `name`, `about`, `isAnonymous`) VALUES (?, ?, ?, ?, ?);";
			template.update(sql,keyHolder.getKey().intValue(), username, name, about, isAnonymous);
			return keyHolder.getKey().intValue();
		} catch (DuplicateKeyException dk) {
			return -1;
		}
	}

	public Integer getId(String email){
		return template.queryForObject("SELECT `id` FROM `Users` WHERE `email` = ?;",Integer.class, email);
	}

	public String getEmail(int id){
		return template.queryForObject("SELECT `email` FROM `Users` WHERE `id` = ?;",String.class, id);
	}

	public UserDetailAll profileAll(Integer id) {
		String sql = "SELECT * FROM `UserProfile` " +
					 "JOIN `Users` ON `UserProfile`.`id`=`Users`.`id` " +
					 "AND `Users`.`id` = ?;";
		return template.queryForObject(sql, USER_DETAIL_ALL_ROW_MAPPER, id);
	}

	public List<String> following(Integer id) {
		String sql = "SELECT `email` FROM `Users`" +
					 "JOIN `Followers`  ON `Followers`.`followee` = `Users`.`id` " +
					 "AND `follower` = ?;";
		return template.queryForList(sql, String.class, id);
	}

	public List<String> followers(Integer id) {
		String sql = "SELECT `email` FROM `Users`" +
					 "JOIN `Followers`  ON `Followers`.`follower` = `Users`.`id` " +
					 "AND `followee` = ?;";
		return template.queryForList(sql, String.class, id);
	}

	public List<Integer> subscriptions(Integer id) {
		String sql = "SELECT `thread` FROM `Subscriptions` WHERE `Subscriptions`.`user`=?;";
		return template.queryForList(sql, Integer.class, id);
	}

	public int addFollowers(int follower, int followee) {
		try {
			String sql = "INSERT INTO `Followers`(`follower`, `followee`) VALUE (?,?);";
			template.update(sql, follower, followee);
			return 0;
		} catch (DuplicateKeyException dk) {
			return 1;
		}
	}

	public int delFollowers(int follower, int followee) {
		String sql = "DELETE FROM `Followers` WHERE `follower` = ? AND `followee` = ?;";
		return template.update(sql, follower, followee);
	}

	public int updateProfile(Integer id, String name, String about) {
		String sql = "UPDATE `UserProfile` SET `about` = ?, `name` = ? WHERE `id` = ?;";
		return template.update(sql, about, name, id);
	}

	public List<Integer> getListFollowers(Integer id, String order, Integer since_id, Integer limit) {
		String sql = "SELECT `follower` FROM `Followers` " +
					 "JOIN `UserProfile` ON `Followers`.`follower`=`UserProfile`.`id` " +
					 "AND `Followers`.`followee` = ?  " +
				 	 "AND `UserProfile`.`id` >= ? " +
				 	 "ORDER BY `UserProfile`.`name` " + order;
		String sqlLimit = (limit != null&&limit!=0) ? " LIMIT " + limit + ";" : ";";
		return template.queryForList(sql + sqlLimit, Integer.class, id, since_id);
	}

	public List<Integer> getListFollowing(Integer id, String order, Integer since_id, Integer limit) {
		String sql = "SELECT `followee` " +
					 "FROM `Followers` " +
					 "JOIN `UserProfile` ON `Followers`.`followee`=`UserProfile`.`id` " +
					 "AND `Followers`.`follower` = ?  " +
					 "AND `UserProfile`.`id` >= ? " +
					 "ORDER BY `UserProfile`.`name` " + order;
		String sqlLimit = (limit != null&&limit!=0) ? " LIMIT " + limit + ";" : ";";
		return template.queryForList(sql + sqlLimit, Integer.class, id, since_id);
	}


	public int getCount() {
		String sql = "SELECT count(*) FROM `Users`";
		return template.queryForObject(sql, Integer.class);
	}

	public List<Integer> getListPost(String email, String order, String since, Integer limit) {
		String sql = 	"SELECT `id` " +
						"FROM `Post` " +
						"WHERE `user` = ? " +
						"AND TIMESTAMPDIFF(SECOND, ?, `date`) >= 0 " +
						"ORDER BY `date` " + order;
		String sqlLimit = (limit != null&&limit!=0) ? " LIMIT " + limit + ";" : ";";
		return template.queryForList(sql + sqlLimit, Integer.class, email, since);
	}

	public List<Integer> getListThread(int id, String order, String since, Integer limit) {
		String sql = "SELECT `Thread`.`id` FROM `Thread` " +
					 "JOIN  `UserProfile` ON `Thread`.`user` = `UserProfile`.`id`" +
					 "AND `UserProfile`.`id` = ? AND TIMESTAMPDIFF(SECOND, ?, `Thread`.`date`) >= 0 " +
					 "ORDER BY `Thread`.`date` " + order;
		String sqlLimit = (limit != null&&limit!=0) ? " LIMIT " + limit + ";" : ";";
		return template.queryForList(sql + sqlLimit, Integer.class, id, since);
	}

	private final RowMapper<UserDetailAll> USER_DETAIL_ALL_ROW_MAPPER = (rs, rowNum) -> {
		int id = rs.getInt("id");
		UserDetailAll userDetailAll = new UserDetailAll(id,
				rs.getString("username"),
				rs.getString("name"),
				rs.getString("email"),
				rs.getString("about"),
				rs.getBoolean("isAnonymous"));
		userDetailAll.setFollowers(followers(id));
		userDetailAll.setFollowing(following(id));
		userDetailAll.setSubscriptions(subscriptions(id));
		return userDetailAll;
	};

}
