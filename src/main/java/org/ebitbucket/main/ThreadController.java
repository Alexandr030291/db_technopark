package org.ebitbucket.main;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.ebitbucket.lib.Functions;
import org.ebitbucket.model.MessageUpdate;
import org.ebitbucket.model.Tread.ThreadDetail;
import org.ebitbucket.model.Tread.ThreadRequest;
import org.ebitbucket.model.Vote;
import org.ebitbucket.services.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ThreadController extends MainController{
    public ThreadController(ForumService forumService, UserService userService, ThreadService threadService, PostService postService, MainService mainService) {
        super(forumService, userService, threadService, postService, mainService);
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
        Integer forum_id = getForumService().getId(body.getForum());
        Integer user_id = getUserService().getId(body.getUser());
        if (forum_id==null || user_id == null)
            return Result.invalidReques();
        int id = getThreadService().create(
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

    @RequestMapping(path = "db/api/forum/listThreads", method = RequestMethod.GET)
    public Result listForumThreads(@RequestParam(name = "forum") String short_name,
                                   @RequestParam(name = "limit", required = false) Integer limit,
                                   @RequestParam(name = "order", required = false) String order,
                                   @RequestParam(name = "since", required = false) String since,
                                   @RequestParam(name = "related", required = false) String[] related) {

        if (StringUtils.isEmpty(short_name)) {
            return Result.invalidReques();
        }
        if (!Functions.isArrayValid(related, "user", "forum")) {
            return Result.incorrectRequest();
        }
        String _order = (StringUtils.isEmpty(order)) ? "desc" : order;
        if (!"desc".equalsIgnoreCase(_order) && !"asc".equalsIgnoreCase(_order))
            return Result.incorrectRequest();

        Integer forum_id = getForumService().getId(short_name);
        List<Integer> threadListId = getForumService().getListThread(forum_id,since,_order,limit);
        List<ThreadDetail> threadDetailsList = new ArrayList<>();
        for (int i =0 ; i < threadListId.size();i++){
            threadDetailsList.add(i, getThreadDetails(threadListId.get(i),related));
        }
        return Result.ok(threadDetailsList);
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
            threadListId = getForumService().getListThread(forum_id,since,_order,limit);
        }else {
            int user_id = getUserService().getId(email);
            threadListId = getUserService().getListThread(user_id,_order,since,limit);
        }
        List<ThreadDetail> threadDetailsLists = new ArrayList<>();
        for (int i =0 ; i < threadListId.size();i++){
            threadDetailsLists.add(i, getThreadDetails(threadListId.get(i),null));
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

    private static class Subscription{
        private final Integer thread;
        private final String user;

        @JsonCreator
        public Subscription(@JsonProperty("thread") Integer thread,
                            @JsonProperty("user") String user) {
            this.thread = thread;
            this.user = user;
        }

        public Integer getThread() {
            return thread;
        }

        public String getUser() {
            return user;
        }
    }
}
