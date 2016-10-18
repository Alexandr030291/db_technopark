package org.ebitbucket.main;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import org.ebitbucket.services.User;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
final public class UserController {
    private final User user;

    public UserController(User user) {
        this.user = user;

    }

    @RequestMapping(path="db/api/user/create/", method = RequestMethod.POST)
    public Result<?> userCreate(@RequestBody UserProfil body){
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
       HashMap<String,String> profil = user.profil(email);
       if (!profil.containsKey(email)) return Result.notFound();
       List <String> followers = user.followers(email);
       List <String> following = user.following(email);
       List<Integer> subscriptions = user.subscriptions(email);
       return Result.ok(new UserDetail(
               profil.get("username"),
               profil.get("about"),
               profil.get("name"),
               profil.get("email"),
               new Integer(profil.get("id")),
               Boolean.valueOf(profil.get("isAnonymous")),
               followers.toArray(new String[followers.size()]),
               following.toArray(new String[following.size()]),
               subscriptions.toArray(new Integer[subscriptions.size()])));
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

    private  static class UserProfil {
        private String username;
        private String about;
        private String name;
        private String email;
        private Integer id;
        private Boolean isAnonymous;
        @JsonCreator
        private UserProfil(@JsonProperty("username") String username,
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

    private static class UserDetail{
        private String username;
        private String about;
        private String name;
        private String email;
        private Integer id;
        private Boolean isAnonymous;
        private String[] followers;
        private String[] following;

        private UserDetail(String username, String about, String name, String email, Integer id, Boolean isAnonymous, String[] followers, String[] following, Integer[] subscriptions) {
            this.username = username;
            this.about = about;
            this.name = name;
            this.email = email;
            this.id = id;
            this.isAnonymous = isAnonymous;
            this.followers = followers;
            this.following = following;
            this.subscriptions = subscriptions;
        }

        private Integer[] subscriptions;

        public String getAbout() {
            return about;
        }

        public String getEmail() {
            return email;
        }

        public String[] getFollowers() {
            return followers;
        }

        public String[] getFollowing() {
            return following;
        }

        public Integer getId() {
            return id;
        }

        public Boolean getAnonymous() {
            return isAnonymous;
        }

        public String getName() {
            return name;
        }

        public Integer[] getSubscriptions() {
            return subscriptions;
        }

        public String getUsername() {
            return username;
        }
    }
}
