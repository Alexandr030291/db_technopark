package org.ebitbucket.main;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.corba.se.spi.orbutil.fsm.Guard;
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
    public Result<?> userDetails(@RequestParam("email") String email) {
        UserDetail userDetail = user.profil(email);
        if (StringUtils.isEmpty(userDetail.getEmail()))
            return Result.notFound();

        updateUserDetail(userDetail);

        return Result.ok(userDetail);
    }

    @RequestMapping(path = "db/api/user/follow", method = RequestMethod.POST)
    public Result<?> userFollow(@RequestBody FollowerRequesr body) {
        UserDetail userDetail = user.profil(body.getFollower());
        if (StringUtils.isEmpty(userDetail.getEmail()))
            return Result.notFound();

        user.addFollowers(body.getFollower(), body.getFollowee());

        updateUserDetail(userDetail);

        return Result.ok(userDetail);
    }

    @RequestMapping(path = "db/api/user/unfollow", method = RequestMethod.POST)
    public Result<?> userUnFollow(@RequestBody FollowerRequesr body) {
        UserDetail userDetail = user.profil(body.getFollower());
        if (StringUtils.isEmpty(userDetail.getEmail()))
            return Result.notFound();

        if(user.delFollowers(body.getFollower(),body.getFollowee())==0)
            return Result.invalidReques();

        updateUserDetail(userDetail);

        return Result.ok(userDetail);
    }

    @RequestMapping(path = "db/api/user/updateProfile", method = RequestMethod.POST)
    public Result<?> updateProfile(@RequestBody UserProfile body){
        UserDetail userDetail = user.profil(body.getEmail());
        if (StringUtils.isEmpty(userDetail.getEmail()))
            return Result.notFound();

        user.updateProfil(body.getEmail(),body.getName(),body.getAbout());
        userDetail.setAbout(body.getAbout());
        userDetail.setName(body.getName());

        updateUserDetail(userDetail);

        return Result.ok(userDetail);
    }

    @RequestMapping(path = "db/api/user/listFollowers", method = RequestMethod.GET)
    public Result<?> listFollowers(
            @RequestParam("email") String email,
            @RequestParam(name = "limit", required = false) Integer limit,
            @RequestParam(name = "order", required = false) String order,
            @RequestParam(name = "since_id", required = false) Integer since_id
            ){

        String _order=(StringUtils.isEmpty(order))?"desc":order;
        if(!"desc".equalsIgnoreCase(_order)&&!"asc".equalsIgnoreCase(_order))
            return Result.incorrectRequest();
        Integer _since_id = (since_id==null)?0:since_id;
        Integer _limit = (limit == null)?0:limit;

        UserDetail userDetail = user.profil(email);
        if (StringUtils.isEmpty(userDetail.getEmail())) {
            return Result.notFound();
        }
        userDetail.setFollowers(user.getListFollowers(email,_order,_since_id,_limit));
        userDetail.setSubscriptions(user.subscriptions(userDetail.getEmail()));
        userDetail.setFollowing(user.following(userDetail.getEmail()));

        return Result.ok(userDetail);
    }

    private void updateUserDetail(UserDetail userDetail){
        userDetail.setFollowers(user.followers(userDetail.getEmail()));
        userDetail.setFollowing(user.following(userDetail.getEmail()));
        userDetail.setSubscriptions(user.subscriptions(userDetail.getEmail()));
    }

    private class FollowerRequesr{
        private final String follower;
        private final String followee;

        @JsonCreator
        private FollowerRequesr(@JsonProperty("follower") String follower, @JsonProperty("followee") String followee){
            this.follower = follower;
            this.followee = followee;
        }

        public String getFollower() {
            return follower;
        }

        public String getFollowee() {
            return followee;
        }
    }
}
