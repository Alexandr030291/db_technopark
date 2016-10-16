package org.ebitbucket.main;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.ebitbucket.services.User;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;


@RestController
public class UserController {
    private final User user;

    public UserController(User user) {
        this.user = user;
    }

    @RequestMapping(path="api/user/create/", method = RequestMethod.POST)
    public Result<String> userCreate(@RequestBody RegistRequest body){
        final String username = body.getUsername();
        final String about = body.getAbout();
        final String name = body.getName();
        final String email= body.getEmail();
        final Boolean isAnonymous = body.getAnonymous();

        if (StringUtils.isEmpty(username)) return Result.invalidReques();
        if (StringUtils.isEmpty(name)) return Result.invalidReques();
        if (StringUtils.isEmpty(email)) return Result.invalidReques();

        user.create(email, name, username, about, isAnonymous);
        return Result.ok("");
   }

    @ExceptionHandler(DuplicateKeyException.class)
    public Result<String> userAlreadyExists(DuplicateKeyException ex) {
        return Result.userAlreadyExists();
    }

    private  static final class RegistRequest {
        private Boolean isAnonymous;
        private String username;
        private String about;
        private String name;
        private String email;

        @JsonCreator
        private RegistRequest(@JsonProperty("username") String username,
                              @JsonProperty("about") String about,
                              @JsonProperty("name") String name,
                              @JsonProperty("email") String email,
                              @JsonProperty("isAnonymous") Boolean isAnonymous) {
            this.username = username;
            this.about = about;
            this.name = name;
            this.email = email;

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
    }
}
