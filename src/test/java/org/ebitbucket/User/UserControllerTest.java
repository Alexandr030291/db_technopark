package org.ebitbucket.User;
/*
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
import org.springframework.test.web.servlet.ResultActions;
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
		createUser("user1", "example1@mail.ru");
	}

	@Test
	public void testFollow() throws Exception {
		createUser("user1", "example1@mail.ru");
		createUser("user2", "example2@mail.ru");

		ResultActions followResult = mockMvc.perform(post("/db/api/user/follow/")
				.content("{\"follower\": \"example1@mail.ru\", \"followee\": \"example2@mail.ru\"}")
				.contentType(MediaType.APPLICATION_JSON));

		testUser(followResult, "user1", "example1@mail.ru");

	}

	@Test
	public void testCreate1000() throws Exception {
		for (int i = 0; i < 1000; i++) {
			createUser("user" + i, "example" + i + "@mail.ru");
		}
	}

	private void createUser(String username, String email) throws Exception {
		String user = "{\"username\": \"" + username + "\", \"about\": \"hello im user1\","
				+ " \"isAnonymous\": false,"
				+ " \"name\": \"John\", \"email\": \"" + email + "\"}";

		ResultActions createuserResult = mockMvc.perform(post("/db/api/user/create/")
				.content(user)
				.contentType(MediaType.APPLICATION_JSON));
		testUser(createuserResult, username, email);
	}

	private static void testUser(ResultActions createuserResult, String username, String email) throws Exception {
		createuserResult.andExpect(jsonPath("code").value(Result.OK))
				.andExpect(jsonPath("response.username").value(username))
				.andExpect(jsonPath("response.about").value("hello im user1"))
				.andExpect(jsonPath("response.isAnonymous").value(false))
				.andExpect(jsonPath("response.email").value(email))
				.andExpect(jsonPath("response.id").isNumber())
				.andExpect(jsonPath("response.name").value("John"));
	}

}
*/