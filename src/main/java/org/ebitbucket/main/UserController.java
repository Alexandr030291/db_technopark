package org.ebitbucket.main;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.ebitbucket.model.Post.PostDetails;
import org.ebitbucket.model.User.UserDetailAll;
import org.ebitbucket.model.User.UserProfile;
import org.ebitbucket.model.User.UserProfileUpdate;
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

    @RequestMapping(path = "db/api/user/create", method = RequestMethod.POST)
    public Result userCreate(@RequestBody UserProfile body) {
        if (StringUtils.isEmpty(body.getEmail()))
            return Result.invalidReques();
        int id =userService.create(body.getEmail(),body.getName(),body.getUsername(),body.getAbout(),body.getIsAnonymous());
        if (id == -1)
            return Result.userAlreadyExists();
        body.setId(id);
        return Result.ok(body);
    }

    @RequestMapping(path = "db/api/user/details", method = RequestMethod.GET)
    public Result userDetails(@RequestParam(name = "user") String email) {
        Integer user_id = userService.getId(email);
        if (user_id == null)
            return Result.notFound();
        return Result.ok(userService.profileAll(user_id));
    }

    @RequestMapping(path = "db/api/user/follow", method = RequestMethod.POST)
    public Result userFollow(@RequestBody FollowerRequest body) {
        int userFollower_id = userService.getId(body.getFollower());
        UserDetailAll userDetail = userService.profileAll(userFollower_id );
        if (StringUtils.isEmpty(userDetail.getEmail()))
            return Result.notFound();
        int userFollowee_id = userService.getId(body.getFollowee());
        userService.addFollowers(userFollower_id, userFollowee_id);
        userDetail.setFollowers(userDetail.getFollowers());
        return Result.ok(userDetail);
    }

    @RequestMapping(path = "db/api/user/unfollow", method = RequestMethod.POST)
    public Result userUnFollow(@RequestBody FollowerRequest body) {
        int userFollower_id = userService.getId(body.getFollower());
        UserDetailAll userDetail = userService.profileAll(userFollower_id);
        if (StringUtils.isEmpty(userDetail.getEmail()))
            return Result.notFound();
        int userFollowee_id = userService.getId(body.getFollowee());
        if(userService.delFollowers(userFollower_id, userFollowee_id)==0)
            return Result.invalidReques();
        userDetail.setFollowers(userDetail.getFollowers());
        return Result.ok(userDetail);
    }

    @RequestMapping(path = "db/api/user/updateProfile", method = RequestMethod.POST)
    public Result updateProfile(@RequestBody UserProfileUpdate body){
        if (StringUtils.isEmpty(body.getUser()))
            return Result.unkownError();
        int id = userService.getId(body.getUser());
        if (id ==0) {
            return Result.notFound();
        }
        userService.updateProfile(id,body.getName(),body.getAbout());
        return Result.ok(userService.profileAll(id));
    }

    @RequestMapping(path = "db/api/user/listFollowers", method = RequestMethod.GET)
    public Result listFollowers(
            @RequestParam("user") String user,
            @RequestParam(name = "limit", required = false) Integer limit,
            @RequestParam(name = "order", required = false) String order,
            @RequestParam(name = "since_id", required = false) Integer since_id
            ){

        String _order=(StringUtils.isEmpty(order))?"desc":order;
        if(!"desc".equalsIgnoreCase(_order)&&!"asc".equalsIgnoreCase(_order))
            return Result.incorrectRequest();
        Integer _since_id = (since_id==null)?0:since_id;
        Integer _limit = (limit == null)?0:limit;

        int id = userService.getId(user);
        List<Integer> listFollowers = userService.getListFollowers(id,_order,_since_id,_limit);
        List<UserDetailAll> userDetailList = new ArrayList<>();
        for (int i =0 ; i<listFollowers.size();i++){
            userDetailList.add(i,userService.profileAll(listFollowers.get(i)));
        }
        return Result.ok(userDetailList);
    }

    @RequestMapping(path = "db/api/user/listFollowing", method = RequestMethod.GET)
    public Result listFollowing(
            @RequestParam("user") String user,
            @RequestParam(name = "limit", required = false) Integer limit,
            @RequestParam(name = "order", required = false) String order,
            @RequestParam(name = "since_id", required = false) Integer since_id
    ){

        String _order=(StringUtils.isEmpty(order))?"desc":order;
        if(!"desc".equalsIgnoreCase(_order)&&!"asc".equalsIgnoreCase(_order))
            return Result.incorrectRequest();
        Integer _since_id = (since_id==null)?0:since_id;

        int id = userService.getId(user);
        List<Integer> listFollowing = userService.getListFollowing(id,_order,_since_id,limit);
        List<UserDetailAll> userDetailList = new ArrayList<>();
        for (int i =0 ; i<listFollowing.size();i++){
            userDetailList.add(i,userService.profileAll(listFollowing.get(i)));
        }
        return Result.ok(userDetailList);
    }

    @RequestMapping(path = "db/api/user/listPosts", method = RequestMethod.GET)
    public Result listPost(   @RequestParam(name = "user") String email,
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
            postDetailsList.add(i, postService.details(posts.get(i)));
            postDetails=postDetailsList.get(i);
            postDetails.setPoints(postDetails.getLikes()-postDetails.getDislikes());
        }
        return Result.ok(postDetailsList);
    }

    public static class FollowerRequest {
        private final String follower;
        private final String followee;

        @JsonCreator
        public FollowerRequest(@JsonProperty("follower") String follower, @JsonProperty("followee") String followee){
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
