package org.ebitbucket.services;


import org.ebitbucket.model.Forum.ForumDetail;
import org.ebitbucket.model.ListObject;
import org.ebitbucket.model.User.UserDetailAll;
import org.springframework.jdbc.core.RowMapper;

class MainService {

    final RowMapper<UserDetailAll> USER_DETAIL_ALL_ROW_MAPPER = (rs, rowNum) ->
            new UserDetailAll(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("about"),
                rs.getBoolean("isAnonymous"));

    final RowMapper<ForumDetail> Forum_DETAIL_ROWMAPPER = (rs, rowNum) ->
            new ForumDetail(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("short_name"),
                rs.getInt("user"));

    final RowMapper<ListObject> Following_ROWMAPPER = (rs, rowNum) ->
            new ListObject(rs.getInt("id"),rs.getString("email"));

    final RowMapper<ListObject> Followee_ROWMAPPER = (rs, rowNum) ->
            new ListObject(rs.getInt("id"),rs.getString("email"));

    final RowMapper<ListObject> Subscriptions_ROWMAPPER = (rs, rowNum) ->
            new ListObject(rs.getInt("user"),rs.getInt("thread"));

}
