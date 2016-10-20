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
			String sql = "INSERT INTO `User`(`email`, `name`, `user_name`, `about`, `isAnonymous`) VALUE(?,?,?,?,?);";
			template.update(sql, email, name, username, about, isAnonymous);
			sql = "SELECT `id` FROM `User` WHERE `email` =?;";
			return template.queryForObject(sql, Integer.class, email);
		} catch (DuplicateKeyException dk) {
			return -1;
		}
	}

	public UserDetail profil(String email) {
		String sql = "SELECT * FROM `User` WHERE `email` = ?;";
        return template.queryForObject(sql, UserDetail.class, email);
	}

	public List<String> following(String email) {
		List<String> result = new ArrayList<>();
		String sql = "SELECT `followee` FROM `Followers` WHERE `follower` = ?;";
		template.queryForList(sql, result, email);
		return result;
	}

	public List<String> followers(String email) {
		List<String> result = new ArrayList<>();
		String sql = "SELECT `follower` FROM `Followers` WHERE `followee` = ?;";
		template.queryForList(sql, result, email);
		return result;
	}

	public List<Integer> subscriptions(String email) {
		List<Integer> result = new ArrayList<>();
		String sql = "SELECT `thread` FROM `Subscriptions` WHERE `Subscriptions`.`user`=?;";
		template.queryForList(sql, result, email);
		return result;
	}

    public int addFollowers(String follower,String followee){
        try {
            String sql = "INSERT INTO `Followers`(`follower`, `followee`) VALUE (?,?);";
            template.update(sql, follower, followee);
            return 0;
        }catch (DuplicateKeyException dk){
            return 1;
        }
    }

    public int delFollowers(String follower, String followee){
        String sql = "DELETE FROM `Followers` WHERE `follower` = ? AND `followee` = ?;";
        return template.update(sql,follower,followee);
    }

    public void updateProfil(String email, String name, String about){
        String sql =  "UPDATE `User` SET `name` = ?, `about` = ? WHERE `email` = ?;";
        template.update(sql,name,about,email);
    }

    public List<String> getListFollowers(String email, String order, Integer since_id, Integer limit){
        List<String> result = new ArrayList<>();
        String sql =    "SELECT `follower` " +
                        "FROM `Followers` " +
                        "JOIN `User` ON `Followers`.`follower`=`User`.`email` " +
                                    "AND `Followers`.`followee` = ?  " +
                                    "AND `User`.`id` = ? " +
                        "ORDER BY `USER`.`name` ?";
        if(limit>0){
            template.queryForList(sql+"LIMIT ?;",result,email,since_id,order,limit);
        }else {
            template.queryForList(sql+";", result, email,since_id,order);
        }
        return result;
    }
}
