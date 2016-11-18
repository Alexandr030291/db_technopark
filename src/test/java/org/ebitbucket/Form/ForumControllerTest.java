package org.ebitbucket.Form;

import org.ebitbucket.main.Result;
import org.ebitbucket.model.Forum.ForumRequest;
import org.ebitbucket.model.Post.PostRequest;
import org.ebitbucket.model.Tread.ThreadRequest;
import org.ebitbucket.model.User.UserProfile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.ebitbucket.lib.Function;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc()
@Transactional
public class ForumControllerTest {
    final private Function function= new Function();

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void addforum() throws Exception {
       /* mockMvc.perform(post("db/api/forum/create"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(Result.OK))
                .andExpect(jsonPath("response").value("OK"));
*/
    }

    @Test
    public void getUserList() throws Exception{
        List<UserProfile> userProfileList = function.generedUserList(5);
        List<ForumRequest> forumRequestList =function.generedForumList(5,userProfileList);
        List<ThreadRequest> threadRequestList = function.generedThreadList(5,userProfileList,forumRequestList);
        List<PostRequest> postRequestList = function.generedPostList(5,userProfileList,forumRequestList,threadRequestList);
    }

}
