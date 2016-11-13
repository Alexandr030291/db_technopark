package org.ebitbucket.main;

import org.ebitbucket.model.Tread.ThreadDetail;
import org.ebitbucket.services.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

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
    public Result clear() {
        mainService.allClear();
        return Result.ok();
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

    public ForumService getForumService() {
        return forumService;
    }

    public UserService getUserService() {
        return userService;
    }

    public ThreadService getThreadService() {
        return threadService;
    }

    public PostService getPostService() {
        return postService;
    }

    public MainService getMainService() {
        return mainService;
    }

    public ThreadDetail getThreadDetails(Integer id, String[] related){
        ThreadDetail threadDetail = getThreadService().detail(id);
        if (threadDetail != null) {
            Integer forum = (int)threadDetail.getForum();
            Integer user = (int)threadDetail.getUser();
            String title = threadDetail.getTitle();
            String message = threadDetail.getMessage();
            String slug = threadDetail.getSlug();
            String date = threadDetail.getDate();
            Boolean isClosed = threadDetail.getIsClosed();
            Boolean isDeleted = threadDetail.getIsDeleted();
            Integer likes = threadDetail.getLikes();
            Integer dislikes = threadDetail.getDislikes();
            String user_email = getUserService().getEmail(user);
            String short_name = getForumService().getShortName(forum);
            Object forumDetail = null;
            Object userDetail = null;

            if (related != null&& Arrays.asList(related).contains("forum")) {
                forumDetail = getForumService().detail(forum);
            }else{
                forumDetail=short_name;
            }

            if (related != null&Arrays.asList(related).contains("user")) {
                userDetail = getUserService().profileAll(user);
            }else {
                userDetail = user_email;
            }
            threadDetail = new ThreadDetail(
                    id,
                    forumDetail,
                    userDetail,
                    title,
                    message,
                    slug,
                    date,
                    isClosed,
                    isDeleted,
                    likes,
                    dislikes
            );
            threadDetail.setPoints(threadDetail.getLikes() - threadDetail.getDislikes());
            threadDetail.setPosts(getThreadService().getCountPost(id));
        }
        return threadDetail;
    }
}
