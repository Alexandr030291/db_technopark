package org.ebitbucket.main;

import org.ebitbucket.lib.Functions;
import org.ebitbucket.lib.Util;
import org.ebitbucket.model.MessageUpdate;
import org.ebitbucket.model.Post.PostDetails;
import org.ebitbucket.model.Subscription;
import org.ebitbucket.model.Tread.ThreadDetail;
import org.ebitbucket.model.Tread.ThreadRequest;
import org.ebitbucket.model.Vote;
import org.ebitbucket.services.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class ThreadController extends MainController{
    public ThreadController(ForumService forumService, UserService userService, ThreadService threadService, PostService postService, ControlService controlService) {
        super(forumService, userService, threadService, postService, controlService);
    }


    @RequestMapping(path = "db/api/thread/create", method = RequestMethod.POST)
    public Result threadCreate(@RequestBody ThreadRequest body){
        if (    StringUtils.isEmpty(body.getForum()) ||
                StringUtils.isEmpty(body.getUser()) ||
                StringUtils.isEmpty(body.getTitle()) ||
                StringUtils.isEmpty(body.getMessage()) ||
                StringUtils.isEmpty(body.getSlug()) ||
                StringUtils.isEmpty(body.getDate()) ||
                body.getClosed() == null)
            return Result.invalidReques();
        int forum_id = getForumService().getId(body.getForum());
        int user_id = getUserService().getId(body.getUser());
        if (forum_id==0 || user_id == 0)
            return Result.invalidReques();
        int id = //getThreadService().getNextId("thread");
        getThreadService().createNotAutoId(
               // id,
                forum_id,
                user_id,
                body.getTitle(),
                body.getMessage(),
                body.getSlug(),
                body.getDate(),
                body.getClosed(),
                body.getDeleted());
        body.setId(id);
        return Result.ok(body);
    }

    @RequestMapping(path = "db/api/thread/subscribe", method = RequestMethod.POST)
    public Result subscribe(@RequestBody Subscription body) {
        if (    body.getThread() == null ||
                StringUtils.isEmpty(body.getUser()))
            return Result.invalidReques();
        Integer user_id = getUserService().getId(body.getUser());
        if(!getThreadService().subscribe(body.getThread(),user_id)){
            return Result.incorrectRequest();
        }
        return Result.ok(body);
    }

    @RequestMapping(path = "db/api/thread/unsubscribe", method = RequestMethod.POST)
    public Result unsubscribe(@RequestBody Subscription body) {
        if (    body.getThread() == null ||
                StringUtils.isEmpty(body.getUser()))
            return Result.invalidReques();
        Integer user_id = getUserService().getId(body.getUser());
        if(!getThreadService().unsubscribe(body.getThread(),user_id)){
            return Result.incorrectRequest();
        }
        return Result.ok(body);
    }

    @RequestMapping(path = "db/api/thread/details", method = RequestMethod.GET)
    public Result threadDetails(@RequestParam(name = "thread") Integer thread,
                                @RequestParam(name = "related", required = false) String[] related) {
        if (!Functions.correctId(thread))
            return Result.notFound();


        if (!Functions.isArrayValid(related, "user","forum")) {
            return Result.incorrectRequest();
        }
        ThreadDetail threadDetail = getThreadDetails(thread,related);
        if (threadDetail == null)
            return Result.notFound();

        return Result.ok(threadDetail);
    }

    @RequestMapping(path = "db/api/thread/list", method = RequestMethod.GET)
    public Result listThreads(@RequestParam(name = "forum", required = false) String short_name,
                              @RequestParam(name = "user", required = false) String email,
                              @RequestParam(name = "limit", required = false) Integer limit,
                              @RequestParam(name = "order", required = false) String order,
                              @RequestParam(name = "since", required = false) String since) {
        if (StringUtils.isEmpty(short_name)==StringUtils.isEmpty(email)|| (limit != null && limit < 0) || StringUtils.isEmpty(since)) {
            return Result.invalidReques();
        }

        String _order = (StringUtils.isEmpty(order)) ? "desc" : order;
        if (!"desc".equalsIgnoreCase(_order) && !"asc".equalsIgnoreCase(_order))
            return Result.incorrectRequest();

        List<Integer> threadListId;
        if (!StringUtils.isEmpty(short_name)){
            Integer forum_id = getForumService().getId(short_name);
            threadListId = getForumService().getListThreadId(forum_id,since,_order,limit);
        }else {
            int user_id = getUserService().getId(email);
            threadListId = getUserService().getListThread(user_id,_order,since,limit);
        }
        HashMap<Integer,ThreadDetail> threadDetailHashMap =getForumService().getThreadDetailList(threadListId,null);
        List<ThreadDetail> threadDetailsLists = new ArrayList<>();

        for (int i =0 ; i < threadListId.size();i++){
            threadDetailsLists.add(i, threadDetailHashMap.get(threadListId.get(i)));
        }
        return Result.ok(threadDetailsLists);
    }

    @RequestMapping(path = "db/api/thread/vote", method = RequestMethod.POST)
    public Result  ratePost(@RequestBody Vote body) {
        String field = Functions.getFieldVote(body.getVote());
        if (field == null) {
            return Result.incorrectRequest();
        }
        if (getThreadService().vote(body.getThread(),field)==0)
            return Result.notFound();
            ThreadDetail threadDetails = getThreadService().detail(body.getThread());
        return Result.ok(threadDetails);
    }


    @RequestMapping(path = "db/api/thread/close", method = RequestMethod.POST)
    public Result  closeThread(@RequestBody Vote body) {
        if (getThreadService().close(body.getThread())==0)
            return Result.notFound();
        return Result.ok("thread: " + body.getThread());
    }

    @RequestMapping(path = "db/api/thread/open", method = RequestMethod.POST)
    public Result  openThread(@RequestBody Vote body) {
        if (getThreadService().open(body.getThread())==0)
            return Result.notFound();
        return Result.ok("thread: " + body.getThread());
    }

    @RequestMapping(path = "db/api/thread/restore", method = RequestMethod.POST)
    public Result  restoreThread(@RequestBody Vote body) {
        if (getThreadService().restore(body.getThread())==0)
            return Result.notFound();
        return Result.ok("thread: " + body.getThread());
    }

    @RequestMapping(path = "db/api/thread/remove", method = RequestMethod.POST)
    public Result  removeThread(@RequestBody Vote body) {
        if (getThreadService().remove(body.getThread())==0)
            return Result.notFound();
        return Result.ok("thread: " + body.getThread());
    }

    @RequestMapping(path = "db/api/thread/update", method = RequestMethod.POST)
    public Result updatePost(@RequestBody MessageUpdate body) {
        if (StringUtils.isEmpty(body.getMessage()) ||StringUtils.isEmpty(body.getSlug())|| body.getThread() == null) {
            return Result.incorrectRequest();
        }
        if (getThreadService().update(body.getThread(), body.getMessage(),body.getSlug())==0)
            return Result.notFound();
        ThreadDetail threadDetail = getThreadService().detail(body.getThread());
        return Result.ok(threadDetail);
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

        since = Util.validSince(since);
        String _order = (StringUtils.isEmpty(order)) ? "desc" : order;
        if (    !"desc".equalsIgnoreCase(_order) &&
                !"asc".equalsIgnoreCase(_order)||
                (limit != null && limit < 0)){
            return Result.incorrectRequest();
        }
        List<Integer> postListId = new ArrayList<>();
        switch (sort){
            case "flat":
                postListId  = getThreadService().getListPost(thread,since,_order,limit);
                break;
            case "tree":
                postListId  = getThreadService().getListPostInTree(thread,since,_order,limit);
                break;
            case "parent_tree":
                postListId  = getThreadService().getListPostInParentTree(thread,since,_order,limit);
                break;
            default:
                Result.incorrectRequest();
        }
        List<PostDetails> postDetailsList = new ArrayList<>();
        HashMap<Integer,PostDetails> postDetailsHashMap= getPostService().listPost(postListId,null);
        for (int i =0 ; i < postListId.size();i++){
            postDetailsList.add(i,postDetailsHashMap.get(postListId.get(i)));
        }
        return Result.ok(postDetailsList);
    }

}
