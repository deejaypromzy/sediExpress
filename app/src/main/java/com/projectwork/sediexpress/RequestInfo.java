package com.projectwork.sediexpress;

class RequestInfo {


    private String agent_id;
    private String date;
    private String description;
    private String destination;
    private String status;
    private String _id;

    public RequestInfo() {
    }

    public RequestInfo(String agent_id, String date, String description, String destination, String status, String _id) {
        this.agent_id = agent_id;
        this.date = date;
        this.description = description;
        this.destination = destination;
        this.status = status;
        this._id = _id;
    }

    public String getAgent_id() {
        return agent_id;
    }

    public void setAgent_id(String agent_id) {
        this.agent_id = agent_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
