package org.ebitbucket.main;

import org.ebitbucket.lib.Functions;
import org.ebitbucket.model.Forum.ForumDetail;
import org.ebitbucket.model.MessageUpdate;
import org.ebitbucket.model.Post.PostDetails;
import org.ebitbucket.model.Post.PostRequest;
import org.ebitbucket.model.Tread.ThreadDetail;
import org.ebitbucket.model.User.UserDetailAll;
import org.ebitbucket.model.Vote;
import org.ebitbucket.services.ForumService;
import org.ebitbucket.services.PostService;
import org.ebitbucket.services.ThreadService;
import org.ebitbucket.services.UserService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestController
public class PostController {
    private final PostService postService;
    private final ForumService forumService;
    private final UserService userService;
    private final ThreadService threadService;

    public PostController(PostService postService, ForumService forumService, UserService userService, ThreadService threadService) {
        this.postService = postService;
        this.forumService = forumService;
        this.userService = userService;
        this.threadService = threadService;
    }

    @RequestMapping(path = "db/api/post/create", method = RequestMethod.POST)
    public Result forumCreate(@RequestBody PostRequest body){
        if (    StringUtils.isEmpty(body.getDate()) ||
                StringUtils.isEmpty(body.getForum()) ||
                StringUtils.isEmpty(body.getUser()) ||
                StringUtils.isEmpty(body.getMessage()) ||
                body.getThread() == null)
            return Result.invalidReques();
        int id = postService.create(body.getUser(),
                body.getMessage(),
                body.getForum(),
                body.getThread(),
                body.getParent(),
                body.getDate(),
                body.getApproved(),
                body.getHighlighted(),
                body.getEdited(),
                body.getSpam(),
                body.getDeleted());
        body.setId(id);
        return Result.ok(body);
    }

    @RequestMapping(path = "db/api/post/details", method = RequestMethod.GET)
    public Result postDetails(@RequestParam(name = "post") Integer post,
                                 @RequestParam(name = "related", required = false) String[] related) {

        if (!Functions.isArrayValid(related, "user", "thread", "forum")||post==null) {
            return Result.incorrectRequest();
        }

        if (!Functions.correctId(post))
            return Result.notFound();

        PostDetails postDetails = getPostDetail(post,related);
        if(postDetails==null){
            return Result.notFound();
        }
        return Result.ok(postDetails);
    }

    @RequestMapping(path = "db/api/forum/listPosts", method = RequestMethod.GET)
    public Result listForumPosts(@RequestParam(name = "forum") String short_name,
                                 @RequestParam(name = "limit", required = false) Integer limit,
                                 @RequestParam(name = "order", required = false) String order,
                                 @RequestParam(name = "since", required = false) String since,
                                 @RequestParam(name = "related", required = false) String[] related) {

        if (StringUtils.isEmpty(short_name)) {
            return Result.invalidReques();
        }
        if (!Functions.isArrayValid(related, "user", "forum", "thread")) {
            return Result.incorrectRequest();
        }
        String _order = (StringUtils.isEmpty(order)) ? "desc" : order;
        if (!"desc".equalsIgnoreCase(_order) && !"asc".equalsIgnoreCase(_order))
            return Result.incorrectRequest();

        List<Integer> postListId = forumService.getListPost(short_name,since,_order,limit);
        List<PostDetails> postDetailsList= new ArrayList<>();
        for (int i =0 ; i < postListId.size();i++){
            postDetailsList.add(i,getPostDetail(postListId.get(i),related));
        }
        return Result.ok(postDetailsList);
    }

    @RequestMapping(path = "db/api/post/list", method = RequestMethod.GET)
    public Result listPosts(@RequestParam(name = "forum", required = false) String short_name,
                            @RequestParam(name = "thread", required = false) Integer thread,
                            @RequestParam(name = "limit", required = false) Integer limit,
                            @RequestParam(name = "order", required = false) String order,
                            @RequestParam(name = "since", required = false) String since) {
        since = Functions.validSince(since);
        if (StringUtils.isEmpty(short_name)==(thread==null)|| StringUtils.isEmpty(since)) {
            return Result.invalidReques();
        }
        order = (StringUtils.isEmpty(order)) ? "desc" : order;
        if (!"desc".equalsIgnoreCase(order) && !"asc".equalsIgnoreCase(order))
            return Result.incorrectRequest();

        List<Integer> postListId;
        if (!StringUtils.isEmpty(short_name)){
            postListId = forumService.getListPost(short_name,since,order,limit);
        }else {
            postListId = threadService.getListPost(thread,since,order,limit);
        }
        List<PostDetails> postDetailsList= new ArrayList<>();
        for (int i =0 ; i < postListId.size();i++){
            postDetailsList.add(i,getPostDetail(postListId.get(i),null));
        }
        return Result.ok(postDetailsList);
    }

    @RequestMapping(path = "db/api/post/update", method = RequestMethod.POST)
    public Result updatePost(@RequestBody MessageUpdate body) {
        if (StringUtils.isEmpty(body.getMessage())||body.getPost()==null) {
            return Result.incorrectRequest();
        }
        if (postService.update(body.getPost(),body.getMessage())==0)
            return Result.notFound();
        PostDetails postDetails = postService.details(body.getPost());
        return Result.ok(postDetails);
    }

    @RequestMapping(path = "db/api/post/remove", method = RequestMethod.POST)
    public Result deletePost(@RequestBody Vote body) {
        if (postService.remove(body.getPost())==0)
            Result.notFound();
        return Result.ok("post: " + body.getPost());
    }

    @RequestMapping(path = "db/api/post/restore", method = RequestMethod.POST)
    public Result restorePost(@RequestBody Vote body) {
        if (postService.restore(body.getPost()) == 0)
            Result.notFound();
        return Result.ok("post: " + body.getPost());
    }

    @RequestMapping(path = "db/api/post/vote", method = RequestMethod.POST)
    public Result  ratePost(@RequestBody Vote body) {
        String field = Functions.getFieldVote(body.getVote());
        if (field == null) {
            return Result.incorrectRequest();
        }
        if (postService.vote(body.getPost(),field)==0)
            return Result.notFound();
        PostDetails postDetails = postService.details(body.getPost());
        return Result.ok(postDetails);
    }

    @RequestMapping(path = "db/api/thread/listPosts", method = RequestMethod.GET)
    public Result  listPostsInThread(@RequestParam(name = "thread") Integer thread,
                                     @RequestParam(name = "limit", required = false) Integer limit,
                                     @RequestParam(name = "sort", required = false) String sort,
                                     @RequestParam(name = "order", required = false) String order,
                                     @RequestParam(name = "since", required = false) String since) {
        if (StringUtils.isEmpty(sort)) {
            sort = "flat";
        }

        if (!Functions.correctId(thread))
            return Result.notFound();

        String _order = (StringUtils.isEmpty(order)) ? "desc" : order;
        if (    !"desc".equalsIgnoreCase(_order) &&
                !"asc".equalsIgnoreCase(_order)||
                (limit != null && limit < 0) ||
                StringUtils.isEmpty(since)){
            return Result.incorrectRequest();
        }
        List<Integer> threadListId = new ArrayList<>();
        switch (sort){
            case "flat":
                threadListId  = threadService.getListPost(thread,since,_order,limit);
                break;
            case "tree":
                threadListId  = threadService.getListPostInTree(thread,since,_order,limit);
                break;
            case "parent_tree":
                threadListId  = threadService.getListPostInParentTree(thread,since,_order,limit);
                break;
            default:
                Result.incorrectRequest();
        }
        List<PostDetails> postDetailsList = new ArrayList<>();
        Integer post;
        for (int i =0 ; i < threadListId.size();i++){
            post = threadListId.get(i);
            postDetailsList.add(i,getPostDetail(post,null));
        }
        return Result.ok(postDetailsList);
    }

    private PostDetails getPostDetail(int id, String[] related){
        PostDetails postDetails = postService.details(id);
        if (postDetails!=null){
            String forum = postDetails.getForum().toString();
            String user = postDetails.getUser().toString();
            Integer thread = new Integer (postDetails.getThread().toString());
            Integer parent = postDetails.getParent();
            String message = postDetails.getMessage();
            String date = postDetails.getDate();
            Boolean isApproved = postDetails.getIsApproved();
            Boolean isDeleted = postDetails.getIsDeleted();
            Boolean isEdited = postDetails.getIsEdited();
            Boolean isHighlighted = postDetails.getIsHighlighted();
            Boolean isSpam = postDetails.getIsSpam();

            int dislikes = postDetails.getDislikes();
            int likes = postDetails.getLikes();

            UserDetailAll userDetailAll = null;
            ForumDetail forumDetail = null;
            ThreadDetail threadDetail = null;
            int flag = 0;
            if (related != null) {
                if (Arrays.asList(related).contains("forum")) {
                    forumDetail = forumService.detail(forum);
                    flag++;
                }

                if (Arrays.asList(related).contains("user")) {
                    userDetailAll=userService.profileAll(user);
                    flag++;
                }

                if (Arrays.asList(related).contains("thread")){
                    threadDetail=(new ThreadController(threadService, forumService, userService)).getDetails(thread,null);  //threadService.detail(thread);
                    flag++;
                }

                if(flag>0) {
                    postDetails = new PostDetails(
                            id,
                            (forumDetail != null) ? forumDetail : forum,
                            (userDetailAll != null) ? userDetailAll : user,
                            (threadDetail != null) ? threadDetail : thread,
                            parent,
                            message,
                            date,
                            isApproved,
                            isDeleted,
                            isEdited,
                            isHighlighted,
                            isSpam,
                            dislikes,
                            likes
                    );
                }
            }
            postDetails.setPoints(likes-dislikes);
        }
        return postDetails;
    }
}
