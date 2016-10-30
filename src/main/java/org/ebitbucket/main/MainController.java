package org.ebitbucket.main;

import org.ebitbucket.services.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    private ForumService forumService;
    private UserService userService;
    private ThreadService threadService;
    private PostService postService;
    private MainService mainService;

    public MainController(ForumService forumService, UserService userService, ThreadService threadService, PostService postService, MainService mainService) {
        this.forumService = forumService;
        this.userService = userService;
        this.threadService = threadService;
        this.postService = postService;
        this.mainService = mainService;
    }

    @RequestMapping(path = "db/api/clear", method = RequestMethod.POST)
    public ResponseEntity clear() {
        mainService.allClear();
        return ResponseEntity.ok().body(Result.ok());
    }

    @RequestMapping(path = "db/api/status", method = RequestMethod.GET)
    public ResponseEntity status() {
        int user = userService.getCount();
        int thread = threadService.getCount();
        int forum = forumService.getCount();
        int post = postService.getCount();
        return ResponseEntity.ok(Result.ok(new StatusResponse(user,thread,forum,post)));
    }

    private static final class StatusResponse {
        private int user;
        private int thread;
        private int forum;
        private int post;

        private StatusResponse(int user, int thread, int forum, int post) {
            this.user = user;
            this.thread = thread;
            this.forum = forum;
            this.post = post;
        }

        public int getUser() {
            return user;
        }

        public int getThread() {
            return thread;
        }

        public int getForum() {
            return forum;
        }

        public int getPost() {
            return post;
        }
    }
}
