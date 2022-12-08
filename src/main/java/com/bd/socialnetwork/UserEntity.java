package com.bd.socialnetwork;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.repository.query.Param;

@Document
public class UserEntity {
    @Id
    private long id;
    // TODO : declare this field as unique
    private String login;
    private String password;
    private String description;
    private String picture;

    public UserEntity(long id, String login, String password, String description, String picture) {
        super();
        this.id = id;
        this.login = login;
        this.description = description;
        this.password = password;
        this.picture = picture;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
