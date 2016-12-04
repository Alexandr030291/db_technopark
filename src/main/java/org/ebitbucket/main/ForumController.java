package org.ebitbucket.main;

import org.ebitbucket.lib.Functions;
import org.ebitbucket.lib.Util;
import org.ebitbucket.model.Forum.ForumDetail;
import org.ebitbucket.model.Forum.ForumRequest;
import org.ebitbucket.model.Post.PostDetails;
import org.ebitbucket.model.Tread.ThreadDetail;
import org.ebitbucket.model.User.UserDetailAll;
import org.ebitbucket.services.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
final public class ForumController extends MainController{

    public ForumController(ForumService forumService, UserService userService, ThreadService threadService, PostService postService, ControlService controlService) {
        super(forumService, userService, threadService, postService, controlService);
    }

    @RequestMapping(path = "db/api/forum/create", method = RequestMethod.POST)
    public Result forumCreate(@RequestBody ForumRequest body){
        if (    StringUtils.isEmpty(body.getEmail())||
                StringUtils.isEmpty(body.getName())||
                StringUtils.isEmpty(body.getShort_name()))
            return Result.invalidReques();
        int user_id = getUserService().getId(body.getEmail());
        int id = getForumService().getId(body.getShort_name());
        if (id != 0) {
            return Result.ok(getForumService().detail(id));
        }
        id = getForumService().create(body.getName(),body.getShort_name(),user_id);
        //body.setId(id);
        return Result.ok(body);
    }

    @RequestMapping(path = "db/api/forum/details", method = RequestMethod.GET)
    public Result forumDetails(@RequestParam(name = "forum") String short_name,
                               @RequestParam(name = "related", required = false) String[] related){
        if (StringUtils.isEmpty(short_name)) {
            return Result.invalidReques();
        }
        if (!Functions.isArrayValid(related, "user")) {
            return Result.incorrectRequest();
        }
        int id = getForumService().getId(short_name);
        if (id == 0) {
            return Result.notFound();
        }
        ForumDetail forumDetail = getForumService().detail(id);
        if (related != null && Arrays.asList(related).contains("user")) {
            forumDetail.setUserDetail(getUserService().profileAll(new Integer(String.valueOf(forumDetail.getUser()))));
        }else {
            forumDetail.setUserDetail(getUserService().getEmail(new Integer(String.valueOf(forumDetail.getUser()))));
        }

        return Result.ok(forumDetail);
    }

    @RequestMapping(path = "db/api/forum/listUsers", method = RequestMethod.GET)
    public Result listPost(   @RequestParam(name = "forum") String short_name,
                              @RequestParam(name = "limit", required = false) Integer limit,
                              @RequestParam(name = "order", required = false) String order,
                              @RequestParam(name = "since_id", required = false) Integer since) {
        if (StringUtils.isEmpty(short_name) || (limit != null && limit < 0)) {
            return Result.incorrectRequest();
        }
        int _since = (since==null)?0:since;
        String _order = (StringUtils.isEmpty(order)) ? "desc" : order;
        if (!"desc".equalsIgnoreCase(_order) && !"asc".equalsIgnoreCase(_order))
            return Result.incorrectRequest();

        Integer forum_id = getForumService().getId(short_name);
        List<UserDetailAll> listUser = getForumService().getListUser(forum_id, _since, _order, limit);
        return Result.ok(listUser);
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

        since = Util.validSince(since);
        Integer forum_id = getForumService().getId(short_name);
        List<Integer> threadListId = getForumService().getListThreadId(forum_id,since,_order,limit);
        HashMap<Integer,ThreadDetail> threadDetailsMap = getForumService().getThreadDetailList(threadListId,related);
        List<ThreadDetail> threadDetailsList = new ArrayList<>();
        for (int i=0;i<threadListId.size();i++){
            threadDetailsList.add(i,threadDetailsMap.get(threadListId.get(i)));
        }
        return Result.ok(threadDetailsList);
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

        since = Util.validSince(since);
        Integer forum_id = getForumService().getId(short_name);
        List<Integer> postListId = getForumService().getListPost(forum_id,since,_order,limit);
        List<PostDetails> postDetailsList = new ArrayList<>();
        HashMap<Integer,PostDetails> postDetailsHashMap= getPostService().listPost(postListId,related);
        for (int i =0 ; i < postListId.size();i++){
            postDetailsList.add(i,postDetailsHashMap.get(postListId.get(i)));
        }
        return Result.ok(postDetailsList);
    }

}
