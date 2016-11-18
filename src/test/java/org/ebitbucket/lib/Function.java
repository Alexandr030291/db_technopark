package org.ebitbucket.lib;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.ebitbucket.model.Forum.ForumRequest;
import org.ebitbucket.model.Post.PostRequest;
import org.ebitbucket.model.Tread.ThreadRequest;
import org.ebitbucket.model.User.UserProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.ebitbucket.lib.Functions.DATE_FORMAT;


public class Function {
    private final static Random random = new Random();

    public List<UserProfile> generedUserList(int number){
        List<UserProfile> userProfileArrayList = new ArrayList<>();
        for (int i = 0; i<number; i++){
            userProfileArrayList.add(i,
                    new UserProfile(
                            "User name №" + i,
                            "About user " + i,
                            "Nick " + i,
                            "user"+i+"@mail.ru",
                            false
                    )
            );
            userProfileArrayList.get(i).setId(i);
        }
        return userProfileArrayList;
    }

    public List<ForumRequest> generedForumList(int number, List<UserProfile> userProfileList){
        List<ForumRequest> forumRequestList =new ArrayList<>();
        for(int i=0, id = 0;i<number;i++){
            id = (random.nextInt(userProfileList.size()));
            forumRequestList.add(i, new ForumRequest(
                    "Forum #" + i,
                    "f#" + i,
                    userProfileList.get(id).getEmail()
                    )

            );
        }
        return forumRequestList;
    }

    public List<ThreadRequest> generedThreadList(int number, List<UserProfile> userProfileList, List<ForumRequest> forumRequestList){
        List<ThreadRequest> threadRequests =new ArrayList<>();
        for(int i=0, userid = 0,forumid =0;i<number;i++){
            userid = (random.nextInt(userProfileList.size()));
            forumid = (random.nextInt(forumRequestList.size()));
            threadRequests.add(i, new ThreadRequest(
                    i ,
                    forumRequestList.get(forumid).getShort_name(),
                    userProfileList.get(userid).getEmail(),
                    "title" + i,
                    "message" + i,
                    "slug" + i ,
                    DATE_FORMAT.format(random.nextInt()),
                    false,
                    false
                    )

            );
        }
        return threadRequests;
    }

    public List<PostRequest> generedPostList(int number,
                                             List<UserProfile> userProfileList,
                                             List<ForumRequest> forumRequestList,
                                             List<ThreadRequest> threadRequestList){
        List<PostRequest> postRequests =new ArrayList<>();
        for(int i = 0, userid, forumid, threadid; i<number; i++){
            userid = (random.nextInt(userProfileList.size()));
            forumid = (random.nextInt(forumRequestList.size()));
            threadid = (random.nextInt(threadRequestList.size()));
            Integer parent =(random.nextInt(postRequests.size()));
            if (parent==0) parent = null;
            postRequests.add(i, new PostRequest(
                            i,
                            DATE_FORMAT.format(random.nextInt()),
                            forumRequestList.get(forumid).getShort_name(),
                            false,
                            false,
                            false,
                            false,
                            false,
                            "message" + i,
                            parent,
                            threadid,
                    userProfileList.get(userid).getEmail()
                    )

            );
        }
        return postRequests;
    }

}
