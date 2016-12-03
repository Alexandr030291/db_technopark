package org.ebitbucket.main;

import org.ebitbucket.services.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController extends MainController{
    public StatusController(ForumService forumService, UserService userService, ThreadService threadService, PostService postService, ControlService controlService) {
        super(forumService, userService, threadService, postService, controlService);
    }

    @RequestMapping(path = "db/api/clear", method = RequestMethod.POST)
    public Result clear() {
        getControlService().allClear();
        return Result.ok();
    }

    @RequestMapping(path = "db/api/status", method = RequestMethod.GET)
    public ResponseEntity status() {
        int user = getUserService().getCount();
        int thread = getThreadService().getCount();
        int forum = getForumService().getCount();
        int post = getPostService().getCount();
        return ResponseEntity.ok(Result.ok(new StatusResponse(user,thread,forum,post)));
    }
}
