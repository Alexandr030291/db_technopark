package org.ebitbucket.model.User;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserProfile {
	private Integer id;
	private final String username;
	private final String about;
	private final String name;
	private final String email;
	private final Boolean isAnonymous;

	@JsonCreator
	public UserProfile(@JsonProperty("username") String username,
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
		} else {
			this.isAnonymous = false;
		}
	}

	public Integer getId() {
		return id;
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

	public Boolean getIsAnonymous() {
		return isAnonymous;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}