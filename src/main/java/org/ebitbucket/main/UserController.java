package org.ebitbucket.main;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.ebitbucket.model.Post.PostDetails;
import org.ebitbucket.model.User.UserDetail;
import org.ebitbucket.model.User.UserProfile;
import org.ebitbucket.services.PostService;
import org.ebitbucket.services.UserService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
final public class UserController {
    private final UserService userService;
    private final PostService postService;

    public UserController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @RequestMapping(path = "db/api/user/create/", method = RequestMethod.POST)
    public Result userCreate(@RequestBody UserProfile body) {
        if (StringUtils.isEmpty(body.getEmail()))
            return Result.invalidReques();

        final Integer id = userService.create(
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
    public Result userDetails(@RequestParam("email") String email) {
        UserDetail userDetail = userService.profil(email);
        if (StringUtils.isEmpty(userDetail.getEmail()))
            return Result.notFound();

        return Result.ok(userDetail);
    }

    @RequestMapping(path = "db/api/user/follow/", method = RequestMethod.POST)
    public Result userFollow(@RequestBody FollowerRequesr body) {
        UserDetail userDetail = userService.profil(body.getFollower());
        if (StringUtils.isEmpty(userDetail.getEmail()))
            return Result.notFound();

        userService.addFollowers(body.getFollower(), body.getFollowee());
        userDetail.setFollowers(userDetail.getFollowers());
        return Result.ok(userDetail);
    }

    @RequestMapping(path = "db/api/user/unfollow", method = RequestMethod.POST)
    public Result userUnFollow(@RequestBody FollowerRequesr body) {
        UserDetail userDetail = userService.profil(body.getFollower());
        if (StringUtils.isEmpty(userDetail.getEmail()))
            return Result.notFound();

        if(userService.delFollowers(body.getFollower(),body.getFollowee())==0)
            return Result.invalidReques();
        userDetail.setFollowers(userDetail.getFollowers());
        return Result.ok(userDetail);
    }

    @RequestMapping(path = "db/api/user/updateProfile", method = RequestMethod.POST)
    public Result updateProfile(@RequestBody UserProfile body){
        UserDetail userDetail = userService.profil(body.getEmail());
        if (StringUtils.isEmpty(userDetail.getEmail()))
            return Result.notFound();

        userService.updateProfil(body.getEmail(),body.getName(),body.getAbout());
        userDetail.setAbout(body.getAbout());
        userDetail.setName(body.getName());

        return Result.ok(userDetail);
    }

    @RequestMapping(path = "db/api/user/listFollowers", method = RequestMethod.GET)
    public Result listFollowers(
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

        UserDetail userDetail = userService.profil(email);
        if (StringUtils.isEmpty(userDetail.getEmail())) {
            return Result.notFound();
        }
        userDetail.setFollowers(userService.getListFollowers(email,_order,_since_id,_limit));
        userDetail.setSubscriptions(userService.subscriptions(userDetail.getEmail()));
        userDetail.setFollowing(userService.following(userDetail.getEmail()));

        return Result.ok(userDetail);
    }

    @RequestMapping(path = "db/api/user/listFollowing", method = RequestMethod.GET)
    public Result listFollowing(
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

        UserDetail userDetail = userService.profil(email);
        if (StringUtils.isEmpty(userDetail.getEmail())) {
            return Result.notFound();
        }
        userDetail.setFollowing(userService.getListFollowing(email,_order,_since_id,_limit));
        userDetail.setSubscriptions(userService.subscriptions(userDetail.getEmail()));
        userDetail.setFollowers(userService.followers(userDetail.getEmail()));

        return Result.ok(userDetail);
    }

    @RequestMapping(path = "db/api/forum/listUsers", method = RequestMethod.GET)
    public Result listPost(@RequestParam(name = "user") String email,
                              @RequestParam(name = "limit", required = false) Integer limit,
                              @RequestParam(name = "order", required = false) String order,
                              @RequestParam(name = "since", required = false) String since) {
        if (StringUtils.isEmpty(email) || (limit != null && limit < 0)||StringUtils.isEmpty(since)) {
            return Result.incorrectRequest();
        }
        String _order=(StringUtils.isEmpty(order))?"desc":order;
        if(!"desc".equalsIgnoreCase(_order)&&!"asc".equalsIgnoreCase(_order))
            return Result.incorrectRequest();

        List<Integer> posts = userService.getListPost(email,_order,since,limit);
        List<PostDetails> postDetailsList = new ArrayList<>();
        PostDetails postDetails;
        for (int i =0 ; i < posts.size();i++){
            postDetailsList.add(i, postService.details(i));
            postDetails=postDetailsList.get(i);
            postDetails.setPoints(postDetails.getLikes()-postDetails.getDislikes());
        }
        return Result.ok(postDetailsList);
    }

    public static class FollowerRequesr{
        private final String follower;
        private final String followee;

        @JsonCreator
        public FollowerRequesr(@JsonProperty("follower") String follower, @JsonProperty("followee") String followee){
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
