package com.projectwork.sediexpress;

class Database {
    private String username;
    private String phone;
    private String _id;
    private String profile_image;
    private String role;

    private String email;
    private String body;
    private String message;


    public Database() {

    }

    public Database(String username, String phone, String _id, String role) {
        this.username = username;
        this.phone = phone;
        this._id = _id;
        this.role = role;
    }

    public Database(String profile_image) {
        this.profile_image = profile_image;
    }

    public Database(String username, String phone, String email, String body, String message) {
        this.username = username;
        this.phone = phone;
        this.email = email;
        this.body = body;
        this.message = message;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
