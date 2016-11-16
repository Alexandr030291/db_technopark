package org.ebitbucket.model.User;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserProfileUpdate {
	private Integer id;
	private final String about;
	private final String name;
	private final String user;

	@JsonCreator
	private UserProfileUpdate(@JsonProperty("id") Integer id,
							  @JsonProperty("about") String about,
							  @JsonProperty("name") String name,
							  @JsonProperty("user") String user) {
		this.user = user;
		this.about = about;
		this.name = name;
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public String getAbout() {
		return about;
	}

	public String getName() {
		return name;
	}

	public String getUser() {
		return user;
	}
}