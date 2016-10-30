package org.ebitbucket.User;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.ebitbucket.main.Result;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
@Transactional
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testCreate() throws Exception {
        String user = "{\"username\": \"user1\", \"about\": \"hello im user1\","
                + " \"isAnonymous\": false,"
                + " \"name\": \"John\", \"email\": \"example@mail.ru\"}";

        mockMvc.perform(post("/db/api/user/create/")
                .content(user)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("code").value(Result.OK))
                .andExpect(jsonPath("response.username").value("user1"))
                .andExpect(jsonPath("response.about").value("hello im user1"))
                .andExpect(jsonPath("response.isAnonymous").value(false))
                .andExpect(jsonPath("response.email").value("example@mail.ru"))
                .andExpect(jsonPath("response.id").isNumber())
                .andExpect(jsonPath("response.name").value("John"));
    }

}
