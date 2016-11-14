package org.ebitbucket.main;

import org.ebitbucket.lib.Functions;
import org.ebitbucket.model.Forum.ForumDetail;
import org.ebitbucket.model.MessageUpdate;
import org.ebitbucket.model.Post.PostDetails;
import org.ebitbucket.model.Post.PostRequest;
import org.ebitbucket.model.Tread.ThreadDetail;
import org.ebitbucket.model.User.UserDetailAll;
import org.ebitbucket.model.Vote;
import org.ebitbucket.services.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class PostController extends MainController{

    public PostController(ForumService forumService, UserService userService, ThreadService threadService, PostService postService, MainService mainService) {
        super(forumService, userService, threadService, postService, mainService);
    }

    @RequestMapping(path = "db/api/post/create", method = RequestMethod.POST)
    public Result forumCreate(@RequestBody PostRequest body){
        if (    StringUtils.isEmpty(body.getDate()) ||
                StringUtils.isEmpty(body.getForum()) ||
                StringUtils.isEmpty(body.getUser()) ||
                StringUtils.isEmpty(body.getMessage()) ||
                body.getThread() == null)
            return Result.invalidReques();
        int user_id = getUserService().getId(body.getUser());
        int forum_id = getForumService().getId(body.getForum());
        int id = getPostService().create(user_id,
                body.getMessage(),
                forum_id,
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

        Integer forum_id = getForumService().getId(short_name);
        List<Integer> postListId = getForumService().getListPost(forum_id,since,_order,limit);
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
            Integer forum_id =getForumService().getId(short_name);
            postListId = getForumService().getListPost(forum_id,since,order,limit);
        }else {
            postListId = getThreadService().getListPost(thread,since,order,limit);
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
        if (getPostService().update(body.getPost(),body.getMessage())==0)
            return Result.notFound();
        PostDetails postDetails = getPostService().details(body.getPost());
        return Result.ok(postDetails);
    }

    @RequestMapping(path = "db/api/post/remove", method = RequestMethod.POST)
    public Result deletePost(@RequestBody Vote body) {
        if (getPostService().remove(body.getPost())==0)
            Result.notFound();
        return Result.ok("post: " + body.getPost());
    }

    @RequestMapping(path = "db/api/post/restore", method = RequestMethod.POST)
    public Result restorePost(@RequestBody Vote body) {
        if (getPostService().restore(body.getPost()) == 0)
            Result.notFound();
        return Result.ok("post: " + body.getPost());
    }

    @RequestMapping(path = "db/api/post/vote", method = RequestMethod.POST)
    public Result  ratePost(@RequestBody Vote body) {
        String field = Functions.getFieldVote(body.getVote());
        if (field == null) {
            return Result.incorrectRequest();
        }
        if (getPostService().vote(body.getPost(),field)==0)
            return Result.notFound();
        PostDetails postDetails = getPostService().details(body.getPost());
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

        since = Functions.validSince(since);
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
                threadListId  = getThreadService().getListPost(thread,since,_order,limit);
                break;
            case "tree":
                threadListId  = getThreadService().getListPostInTree(thread,since,_order,limit);
                break;
            case "parent_tree":
                threadListId  = getThreadService().getListPostInParentTree(thread,since,_order,limit);
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


}
