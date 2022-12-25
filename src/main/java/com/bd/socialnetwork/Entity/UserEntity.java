package com.bd.socialnetwork.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
public class UserEntity {
    @Id
    private String id;
    private String login;
    private String password;
    private String description;
    private String picture;
    private List<String> friends;

    public UserEntity(String login, String password, String description, String picture) {
        super();
        this.login = login;
        this.description = description;
        this.password = password;
        this.picture = picture;
        this.friends = new ArrayList<>(); // init friends relations
    }

    public String getId() {
        return id;
    }

    public void String(String id) {
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

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }
}
