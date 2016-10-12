package org.ebitbucket.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @RequestMapping(path="api/user/create/", method = RequestMethod.POST)
    public ResponseEntity user(@RequestBody RegistRequest body){

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("`{\"code\": ${code}, \"response\": error message}");
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
