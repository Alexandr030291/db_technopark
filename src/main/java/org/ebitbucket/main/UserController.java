package org.ebitbucket.main;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.ebitbucket.services.User;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import static org.ebitbucket.main.Result.invalidReques;


@RestController
public class UserController {
    private final User user;

    public UserController(User user) {
        this.user = user;
    }

    @RequestMapping(path="api/user/create/", method = RequestMethod.POST)
    public Object userCreate(@RequestBody CreateRequest body){
        final String username = body.getUsername();
        final String about = body.getAbout();
        final String name = body.getName();
        final String email= body.getEmail();
        final Boolean isAnonymous = body.getAnonymous();

        if (StringUtils.isEmpty(email)) return Result.invalidReques();

        final Integer id = user.create(email, name, username, about, isAnonymous);
        if (id == -1) return Result.userAlreadyExists();

        body.setId(id);

        return Result.ok(body);
   }

    private  static final class CreateRequest {
        private Boolean isAnonymous;
        private String username;
        private String about;
        private String name;
        private String email;
        private Integer id;

        @JsonCreator
        private CreateRequest(@JsonProperty("username") String username,
                              @JsonProperty("about") String about,
                              @JsonProperty("name") String name,
                              @JsonProperty("email") String email,
                              @JsonProperty("isAnonymous") Boolean isAnonymous) {
            this.username = username;
            this.about = about;
            this.name = name;
            this.email = email;
            this.id = -1;

            if (isAnonymous != null) {
                this.isAnonymous = isAnonymous;
            }else{
                this.isAnonymous = false;
            }
        }

        public String getUsername() {
            return username;
        }

        public String getAbout() {
            return about;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public Boolean getAnonymous() {
            return isAnonymous;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }

}
