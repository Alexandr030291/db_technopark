package org.ebitbucket.main;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.ebitbucket.services.User;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
final public class UserController {
    private final User user;
    private StringBuilder userCreate;

    public UserController(User user) {
        this.user = user;

    }

    @RequestMapping(path="db/api/user/create/", method = RequestMethod.POST)
    public Result<?> userCreate(@RequestBody CreateRequest body){
        if (StringUtils.isEmpty(body.getEmail())) return Result.invalidReques();

        final Integer id = user.create( body.getEmail(),
                                        body.getName(),
                                        body.getUsername(),
                                        body.getAbout(),
                                        body.getAnonymous());
        if (id == -1) return Result.userAlreadyExists();

        body.setId(id);

        return Result.ok(body);
   }

   @RequestMapping(path = "db/api/user/details/?user", method = RequestMethod.GET)
   public Result<?> userDetails(@RequestParam String email){
       return Result.ok("{\n" +
               "    \"code\": 0,\n" +
               "    \"response\": {\n" +
               "        \"about\": \"hello im user1\",\n" +
               "        \"email\": \"example@mail.ru\",\n" +
               "        \"followers\": [\n" +
               "            \"example3@mail.ru\"\n" +
               "        ],\n" +
               "        \"following\": [\n" +
               "            \"example3@mail.ru\"\n" +
               "        ],\n" +
               "        \"id\": 1,\n" +
               "        \"isAnonymous\": false,\n" +
               "        \"name\": \"John\",\n" +
               "        \"subscriptions\": [\n" +
               "            4\n" +
               "        ],\n" +
               "        \"username\": \"user1\"\n" +
               "    }\n" +
               "}");
   }


   @RequestMapping(path = "db/api/user/follow/", method = RequestMethod.POST)
    public Result<?> userFollow(){

       return Result.ok("{\n" +
               "    \"code\": 0,\n" +
               "    \"response\": {\n" +
               "        \"about\": \"hello im user1\",\n" +
               "        \"email\": \"example@mail.ru\",\n" +
               "        \"followers\": [\n" +
               "            \"example3@mail.ru\"\n" +
               "        ],\n" +
               "        \"following\": [\n" +
               "            \"example3@mail.ru\"\n" +
               "        ],\n" +
               "        \"id\": 1,\n" +
               "        \"isAnonymous\": false,\n" +
               "        \"name\": \"John\",\n" +
               "        \"subscriptions\": [\n" +
               "            4\n" +
               "        ],\n" +
               "        \"username\": \"user1\"\n" +
               "    }\n" +
               "}");
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

        String getUsername() {
            return username;
        }

        String getAbout() {
            return about;
        }

        String getName() {
            return name;
        }

        String getEmail() {
            return email;
        }

        Boolean getAnonymous() {
            return isAnonymous;
        }

        Integer getId() {
            return id;
        }

        void setId(Integer id) {
            this.id = id;
        }
    }

}
