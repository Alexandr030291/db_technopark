package org.ebitbucket.main;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.ebitbucket.model.User.UserDetail;
import org.ebitbucket.model.User.UserProfile;
import org.ebitbucket.services.UserService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
final public class UserController {
	private final UserService user;

	public UserController(UserService user) {
		this.user = user;

	}

	@RequestMapping(path = "db/api/user/create/", method = RequestMethod.POST)
	public Result<?> userCreate(@RequestBody UserProfile body) {
		if (StringUtils.isEmpty(body.getEmail()))
			return Result.invalidReques();

		final Integer id = user.create(
            body.getEmail(),
            body.getName(),
            body.getUsername(),
            body.getAbout(),
            body.getIsAnonymous()
        );
		if (id == -1)
			return Result.userAlreadyExists();

		body.setId(id);

		return Result.ok(body);
	}

	@RequestMapping(path = "db/api/user/details/?user", method = RequestMethod.GET)
	public Result<?> userDetails(@RequestParam String email) {
		UserDetail userDetail = user.profil(email);
		if (StringUtils.isEmpty(userDetail.getEmail()))
			return Result.notFound();

		List<String> followers = user.followers(email);
		List<String> following = user.following(email);
		List<Integer> subscriptions = user.subscriptions(email);
		userDetail.setFollowers(followers.toArray(new String[followers.size()]));
		userDetail.setFollowing(following.toArray(new String[following.size()]));
		userDetail.setSubscriptions(subscriptions.toArray(new Integer[subscriptions.size()]));

		return Result.ok(userDetail);
	}

	@RequestMapping(path = "db/api/user/follow/", method = RequestMethod.POST)
	public Result<?> userFollow(@JsonProperty String follower, @JsonProperty String followee) {
		UserDetail userDetail = user.profil(follower);
		if (StringUtils.isEmpty(userDetail.getEmail()))
			return Result.notFound();

		user.addFollowers(userDetail.getId(),followee);

		List<String> followers = user.followers(follower);
		List<String> following = user.following(follower);
		List<Integer> subscriptions = user.subscriptions(follower);
		userDetail.setFollowers(followers.toArray(new String[followers.size()]));
		userDetail.setFollowing(following.toArray(new String[following.size()]));
		userDetail.setSubscriptions(subscriptions.toArray(new Integer[subscriptions.size()]));

		return Result.ok(userDetail);
	}


}
