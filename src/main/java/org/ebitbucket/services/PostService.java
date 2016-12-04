package org.ebitbucket.services;

import org.ebitbucket.model.Post.Mpath;
import org.ebitbucket.model.Post.PostDetails;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.System.out;


@Service
@Transactional
public class PostService extends MainService{
    private final JdbcTemplate template;

    public PostService(JdbcTemplate template) {
        super(template);
        this.template = template;
    }

    public PostDetails details(int id){
        String sql="SELECT `Post`.`id`, " +
                "`Post`.`forum`, " +
                "`Post`.`user`, " +
                "`Post`.`thread`, " +
                "`Post`.`parent`, " +
                "`Post`.`message`, " +
                "`Post`.`date`, " +
                "`Post`.`isApproved`, " +
                "`Post`.`isDeleted`, " +
                "`Post`.`isEdited`, " +
                "`Post`.`isHighlighted`, " +
                "`Post`.`isSpam`, " +
                "`Post`.`dislikes`, " +
                "`Post`.`likes` " +
                " FROM `Post` " +
                "WHERE `Post`.`id` = ?";
        return template.queryForObject(sql,POST_DETAIL_ROW_MAPPER,id);
    }

    public HashMap<Integer,PostDetails> listPost(List<Integer> list, String[] related){
        if (list.size()==0){
            return new HashMap<>();
        }
        String sql= "SELECT `Post`.`id`, " +
                    "`Post`.`forum`, " +
                    "`Post`.`user`, " +
                    "`Post`.`thread`, " +
                    "`Post`.`parent`, " +
                    "`Post`.`message`, " +
                    "`Post`.`date`, " +
                    "`Post`.`isApproved`, " +
                    "`Post`.`isDeleted`, " +
                    "`Post`.`isEdited`, " +
                    "`Post`.`isHighlighted`, " +
                    "`Post`.`isSpam`, " +
                    "`Post`.`dislikes`, " +
                    "`Post`.`likes` " +
                    "FROM `Post` " +
                    "WHERE `Post`.`id` IN ( " +
                   list.stream().map(String::valueOf).collect(Collectors.joining(",")) +")";
        List<PostDetails> postDetailsList = template.query(sql,POST_DETAIL_ROW_MAPPER);

        HashMap<Integer,PostDetails> postDetailsHashMap = new HashMap<>();
        List<Integer> userIdSet = new ArrayList<>();
        List<Integer> forumIdlist= new ArrayList<>();
        List<Integer> threadIdSet= new ArrayList<>();

        for (PostDetails aPostDetailsList : postDetailsList) {
            aPostDetailsList.setPoints(aPostDetailsList.getLikes()-aPostDetailsList.getDislikes());
            userIdSet.add((Integer) aPostDetailsList.getUser());
            forumIdlist.add((Integer) aPostDetailsList.getForum());
            threadIdSet.add((Integer) aPostDetailsList.getThread());
            postDetailsHashMap.put(aPostDetailsList.getId(), aPostDetailsList);
        }
        List<Object> users;
        List<Object> forums;
        List<Object> threads;
        if (related != null&&Arrays.asList(related).contains("user"))
            users = getUserDetails(userIdSet,true);
        else
           users = getUserDetails(userIdSet,false);

        if (related != null&& Arrays.asList(related).contains("forum"))
            forums=getForumsDetails(forumIdlist,true);
        else
            forums=getForumsDetails(forumIdlist,false);

        if (related != null&&Arrays.asList(related).contains("thread"))
           threads=getThreadDetails(threadIdSet,true);
        else
           threads=getThreadDetails(threadIdSet,false);
        PostDetails post;
        for (int i=0;i<postDetailsList.size();i++) {
            post = postDetailsList.get(i);
            post.setUser(users.get(i));
            post.setForum(forums.get(i));
            post.setThread(threads.get(i));
        }

        return postDetailsHashMap;
    }

    public int getCount(){
        String sql = "SELECT  `count` FROM `LastId` WHERE `table` = 'post';";
        return template.queryForObject(sql, Integer.class);
    }

    public int update(int id, String message){
        String sql = "UPDATE `Post` SET `message` = ? WHERE `id` = ?;";
        return template.update(sql,message,id);
    }

    public int remove(int id){
        int thread = template.queryForObject("SELECT `thread` FROM `Post` WHERE `id` = ?",Integer.class,id);
        template.update("UPDATE `Thread` SET `posts` = `posts` - 1 WHERE `id` = ?",thread);
        String sql = "UPDATE `Post` SET `isDeleted` = TRUE WHERE `id` = ?;";
        return template.update(sql,id);
    }

    public int restore(int id){
        int thread = template.queryForObject("SELECT `thread` FROM `Post` WHERE `id` = ?",Integer.class,id);
        template.update("UPDATE `Thread` SET `posts` = `posts` + 1 WHERE `id` = ?",thread);
        String sql = "UPDATE `Post` SET `isDeleted` = FALSE WHERE `id` = ?;";
        return template.update(sql,id);
    }

    public int vote(int id, String vote){
        String sql = "UPDATE `Post` SET `" +vote+ "` =  `" +vote+ "` + 1 WHERE `id` = ?;";
        return template.update(sql,id);
    }

    public int createNotAutoId(
                      Integer user,
                      Integer forum,
                      String message,
                      Integer thread,
                      Integer parent,
                      String date,
                      Boolean isApproved,
                      Boolean isHighlighted,
                      Boolean isEdited,
                      Boolean isSpam,
                      Boolean isDeleted) {
        int id = getNextId("post");
        String sql;
        String mpath ="";
        Integer root;
        boolean flagError = false;
        if (parent!=null && parent>=0){
            sql = "SELECT `mpath`, `root` FROM `Post` WHERE `id` = ?;";
            List<Mpath> set = template.query(sql,POST_MPATH_ROW_MAPPER,parent);
            if (set.size()>0){
                mpath = set.get(0).getPath();
                root = set.get(0).getRoot();
            }else{
                mpath = intToCode(parent);
                root = parent;
                flagError =true;
            }
        }else{
            root = id;
        }
        mpath+= intToCode(id);
        sql =  "INSERT INTO `Post` (`id`,`user`, `forum`,`message`, `thread`, `parent`, " +
                "`date`, `isApproved`, `isHighlighted`, `isEdited`, `isSpam`, `isDeleted`, `mpath`, `root`) VALUES " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?);";

        template.update(sql,id, user,forum, message, thread, parent, date, isApproved, isHighlighted, isEdited, isSpam, isDeleted, mpath, root);
        if (flagError){
            out.print("from post = "+id+" not found parent = " + parent+"\n");
        }
        try {
            template.update("UPDATE `Thread` SET `posts` = `posts` + 1 WHERE `id` = ?;", thread);
        }catch (DeadlockLoserDataAccessException dl){
            out.print("from post = "+id+" error. Thread.posts++ for id =" +thread+"\n");
            template.update("UPDATE `Thread` SET `posts` = `posts` + 1 WHERE `id` = ?;", thread);
        }
        return id;
    }


    private String intToCode(Integer id){
        String mpath ="";
        int maxCharInMpath = 4;
        int startchar = 48;
        int code = 64;
        int [] hash = new int[maxCharInMpath];
        for(int i = id, j = maxCharInMpath -1; j>=0; i/=code,j--){
            hash[j]=startchar+i%code;
        }
        for (int i = 0; i< maxCharInMpath; i++){
            mpath+=(char)hash[i];
        }
        return mpath;
    }

    private final RowMapper<Mpath> POST_MPATH_ROW_MAPPER = (rs, rowNum) ->
            new Mpath(rs.getString("mpath"),rs.getInt("root"));
}