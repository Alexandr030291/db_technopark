package org.ebitbucket.services;

import org.ebitbucket.model.Forum.Forum;
import org.ebitbucket.model.Forum.ForumDetail;
import org.ebitbucket.model.Post.PostDetails;
import org.ebitbucket.model.Tread.ThreadDetail;
import org.ebitbucket.model.User.UserDetailAll;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional
public class PostService extends MainService{
    private final JdbcTemplate template;

    public PostService(JdbcTemplate template) {
        super(template);
        this.template = template;
    }

    public int create(Integer user,
                      String message,
                      Integer forum,
                      Integer thread,
                      Integer parent,
                      String date,
                      Boolean isApproved,
                      Boolean isHighlighted,
                      Boolean isEdited,
                      Boolean isSpam,
                      Boolean isDeleted) {
        String sql;
        String mpath ="";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(con -> {
            PreparedStatement pst = con.prepareStatement(
                    "INSERT INTO `Post` (`user`, `message`, `forum`, `thread`, `parent`, " +
                    "`date`, `isApproved`, `isHighlighted`, `isEdited`, `isSpam`, `isDeleted`) VALUES " +
                    "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"
                    , Statement.RETURN_GENERATED_KEYS);
            int index = 0;
            pst.setInt(++index, user);
            pst.setString(++index, message);
            pst.setInt(++index, forum);
            pst.setInt(++index, thread);
            pst.setObject(++index, parent,JDBCType.INTEGER);
            pst.setString(++index, date);
            pst.setObject(++index, isApproved, JDBCType.BOOLEAN);
            pst.setObject(++index, isHighlighted, JDBCType.BOOLEAN);
            pst.setObject(++index, isEdited, JDBCType.BOOLEAN);
            pst.setObject(++index, isSpam, JDBCType.BOOLEAN);
            pst.setObject(++index, isDeleted, JDBCType.BOOLEAN);
            return pst;
        }, keyHolder);
        Integer root=0;
        if (parent!=null && parent>=0){
            sql = "SELECT `mpath`, `root` FROM `Post` WHERE `id` = ?;";
            SqlRowSet set = template.queryForRowSet(sql, parent);
            set.next();
            mpath = set.getString("mpath");
            root = set.getInt("root");
        }

        Integer id = keyHolder.getKey().intValue();
        if (root <= 0) {
            root = id;
        }
        sql= "UPDATE `Post` SET `root` = ?, `mpath` = ?  WHERE `id` = ?;";

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
        template.update(sql,root,mpath,id);
        template.update("UPDATE `Thread` SET `posts` = `posts` + 1 WHERE `id` = ?",thread);
        return id;
    }

    public PostDetails details(int id){
        String sql="SELECT * FROM `Post` WHERE `id` = ?";
        return template.queryForObject(sql,POST_DETAIL_ROW_MAPPER,id);
    }

    public HashMap<Integer,PostDetails> listPost(List<Integer> list, String[] related){
        if (list.size()==0){
            return new HashMap<>();
        }
        String sql="SELECT * FROM `Post` WHERE `id` IN ( " + list.stream().map(String::valueOf).collect(Collectors.joining(",")) +")";
        List<PostDetails> postDetailsList = template.query(sql,POST_DETAIL_ROW_MAPPER);
        for (PostDetails aPostDetailsList : postDetailsList) {
            aPostDetailsList.setPoints(aPostDetailsList.getLikes()-aPostDetailsList.getDislikes());
        }

        HashMap<Integer,PostDetails> postDetailsHashMap = new HashMap<>();
        Set<Integer> userIdSet = new HashSet<>();
        Set<Integer> forumIdSet= new HashSet<>();
        Set<Integer> threadIdSet= new HashSet<>();

        for (PostDetails aPostDetailsList : postDetailsList) {
            userIdSet.add((Integer) aPostDetailsList.getUser());
            forumIdSet.add((Integer) aPostDetailsList.getForum());
            threadIdSet.add((Integer) aPostDetailsList.getThread());
            postDetailsHashMap.put(aPostDetailsList.getId(), aPostDetailsList);
        }

        int objectId;
        if (related != null&&Arrays.asList(related).contains("user")){
            HashMap<Integer,UserDetailAll> userHashMap = getUserDetailAllList(userIdSet);
            for (PostDetails aPostDetailsList : postDetailsList) {
                objectId = (Integer) aPostDetailsList.getUser();
                aPostDetailsList.setUser(userHashMap.get(objectId));
            }
        }else{
            HashMap<Integer,String> userHashMap = getEmailList(userIdSet);
            for (PostDetails aPostDetailsList : postDetailsList) {
                objectId = (Integer) aPostDetailsList.getUser();
                aPostDetailsList.setUser(userHashMap.get(objectId));
            }
        }

        if (related != null&& Arrays.asList(related).contains("forum")) {
            HashMap<Integer, ForumDetail> forumHashMap = getForumDetailsList(forumIdSet);
            for (PostDetails aPostDetailsList : postDetailsList) {
                objectId = (Integer) aPostDetailsList.getForum();
                aPostDetailsList.setForum(forumHashMap.get(objectId));
            }
        }else{
            HashMap<Integer, String> forumHashMap = getForumShortNameList(forumIdSet);
            for (PostDetails aPostDetailsList : postDetailsList) {
                objectId = (Integer) aPostDetailsList.getForum();
                aPostDetailsList.setForum(forumHashMap.get(objectId));
            }
        }

        if (related != null&&Arrays.asList(related).contains("thread")){
            HashMap<Integer, ThreadDetail> threadHashMap = getThreadDetail(threadIdSet);
            for (PostDetails aPostDetailsList : postDetailsList) {
                objectId = (Integer) aPostDetailsList.getThread();
                aPostDetailsList.setThread(threadHashMap.get(objectId));
            }
        }

        return postDetailsHashMap;
    }

    public int getCount(){
        String sql = "SELECT count(*) FROM `Post` WHERE `isDeleted` = FALSE;";
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
}