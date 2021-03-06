package org.ebitbucket.main;

import org.ebitbucket.model.Forum.ForumDetail;
import org.ebitbucket.model.Post.PostDetails;
import org.ebitbucket.model.Tread.ThreadDetail;
import org.ebitbucket.services.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@RestController
public class MainController {
    private ForumService forumService;
    private UserService userService;
    private ThreadService threadService;
    private PostService postService;
    private ControlService controlService;

    public MainController(ForumService forumService, UserService userService, ThreadService threadService, PostService postService, ControlService controlService) {
        this.forumService = forumService;
        this.userService = userService;
        this.threadService = threadService;
        this.postService = postService;
        this.controlService = controlService;
    }

    public static final class StatusResponse {
        private int user;
        private int thread;
        private int forum;
        private int post;

        protected StatusResponse(int user, int thread, int forum, int post) {
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

    public ControlService getControlService() {
        return controlService;
    }

    public ThreadDetail getThreadDetails(Integer id, String[] related){
        ThreadDetail threadDetail = getThreadService().detail(id);
        if (threadDetail != null) {
            Integer forum = (Integer)threadDetail.getForum();
            Integer user = (Integer)threadDetail.getUser();
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

            if (related != null&&Arrays.asList(related).contains("user")) {
                userDetail = getUserService().profileAll(user);
            }else {
                userDetail = user_email;
            }

            if (related != null&& Arrays.asList(related).contains("forum")) {
                ForumDetail obj = getForumService().detail(forum);
                user_email = getUserService().getEmail((Integer) obj.getUser());
                obj.setUserDetail(user_email);
                forumDetail = obj;
            }else{
                forumDetail = short_name;
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
                    dislikes,
                    0
            );
            threadDetail.setPoints(threadDetail.getLikes() - threadDetail.getDislikes());
            threadDetail.setPosts(getThreadService().getCountPost(id));
        }
        return threadDetail;
    }

    public PostDetails getPostDetail(int id, String[] related){
        PostDetails postDetails = postService.details(id);
        if (postDetails!=null){
            Integer forum = (Integer)postDetails.getForum();
            Integer user = (Integer)postDetails.getUser();
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

            ThreadDetail threadDetail = null;
            String user_email = getUserService().getEmail(user);
            String short_name = getForumService().getShortName(forum);
            Object forumDetail = null;
            Object userDetail = null;

            if (related != null&&Arrays.asList(related).contains("user")) {
                userDetail = getUserService().profileAll(user);
            }else {
                userDetail = user_email;
            }

            if (related != null&& Arrays.asList(related).contains("forum")) {
                ForumDetail obj = getForumService().detail(forum);
                user_email = getUserService().getEmail((Integer) obj.getUser());
                obj.setUserDetail(user_email);
                forumDetail = obj;
            }else{
                forumDetail = short_name;
            }

            if (related != null&&Arrays.asList(related).contains("thread")){
                threadDetail=getThreadDetails(thread,null);  //threadService.detail(thread);
            }

            postDetails = new PostDetails(
                    id,
                    forumDetail,
                    userDetail,
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
            postDetails.setPoints(likes-dislikes);
        }
        return postDetails;
    }

    //сделан на случай если нужно будет делить на несколько транзакций из-за большого числа
    public HashMap<Integer,PostDetails> getListPostDetail(List<Integer> list, String [] related){
        return postService.listPost(list,related);
    }
}
