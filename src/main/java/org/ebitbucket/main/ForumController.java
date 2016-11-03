package org.ebitbucket.main;

import org.ebitbucket.lib.Functions;
import org.ebitbucket.model.Forum.ForumDetail;
import org.ebitbucket.model.Forum.ForumRequest;
import org.ebitbucket.model.User.UserDetail;
import org.ebitbucket.services.ForumService;
import org.ebitbucket.services.UserService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
final public class ForumController{
    private ForumService forumService;
    private UserService userService;

    public ForumController(ForumService forumService, UserService userService) {
        this.forumService = forumService;
        this.userService = userService;
    }

    @RequestMapping(path = "db/api/forum/create", method = RequestMethod.POST)
    public Result forumCreate(@RequestBody ForumRequest body){
        if (    StringUtils.isEmpty(body.getEmail())||
                StringUtils.isEmpty(body.getName())||
                StringUtils.isEmpty(body.getShort_name()))
            return Result.invalidReques();

        if (forumService.create(body.getName(),body.getShort_name(),body.getEmail()) == -1) {
            return Result.ok(forumService.detail(body.getShort_name()));
        }
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
        ForumDetail forumDetail = forumService.detail(short_name);
        if(forumDetail.getName()==null){
            return Result.notFound();
        }

        if (related != null && Arrays.asList(related).contains("user")) {
            forumDetail.setUserDetail(userService.profileAll(forumDetail.getUser().toString()));
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
        if (since==null)
            since=0;
        String _order = (StringUtils.isEmpty(order)) ? "desc" : order;
        if (!"desc".equalsIgnoreCase(_order) && !"asc".equalsIgnoreCase(_order))
            return Result.incorrectRequest();

        List<String> listUser = forumService.getListUser(short_name, since, _order, limit);
        List<UserDetail> userDetailsList = new ArrayList<>();
        for (int i = 0; i < listUser.size(); i++) {
            userDetailsList.add(i, userService.profile(listUser.get(i)));
        }
        return Result.ok(userDetailsList);
    }

}
