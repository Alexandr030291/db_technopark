package org.ebitbucket.main;

import org.ebitbucket.lib.Functions;
import org.ebitbucket.model.Forum.ForumDetail;
import org.ebitbucket.model.Forum.ForumRequest;
import org.ebitbucket.model.Tread.ThreadDetail;
import org.ebitbucket.services.ForumService;
import org.ebitbucket.services.ThreadService;
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

    @RequestMapping(path = "db/api/forumService/create", method = RequestMethod.POST)
    public Result forumCreate(@RequestBody ForumRequest body){
        if (    StringUtils.isEmpty(body.getEmail())|
                StringUtils.isEmpty(body.getName())|
                StringUtils.isEmpty(body.getShort_name()))
            return Result.invalidReques();
        int id = forumService.create(body.getName(),
                body.getName(),
                body.getEmail());
        if (id <= 0) {
            return Result.ok(forumService.detail(body.getShort_name()));
        }
        body.setId(id);
        return Result.ok(body);
    }

    @RequestMapping(path = "db/api/forumService/details", method = RequestMethod.GET)
    public Result forumDetails(@RequestParam(name = "forumService") String short_name,
                                  @RequestParam(name = "related", required = false) String[] related){
        if (StringUtils.isEmpty(short_name)) {
            return Result.invalidReques();
        }
        if (Functions.isArrayValid(related, "user")) {
            return Result.incorrectRequest();
        }
        ForumDetail forumDetail = forumService.detail(short_name);
        if(forumDetail.getName()==null){
            return Result.notFound();
        }

        if (related != null && Arrays.asList(related).contains("user")) {
            forumDetail.setUserDetail(userService.profil(forumDetail.getUserDetail().toString()));
        }

        return Result.ok(forumDetail);
    }


}
