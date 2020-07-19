package com.projectwork.sediexpress;

import android.content.Context;
import android.content.Intent;

class RequestModel {

    static final String AGENT = "agent";
    static final String ID = "id";
    static final String _ID = "myId";
    static final String PICKUP = "pickup";
    static final String DROP = "drop";
    static final String DISTANCE = "distance";
    static final String AMOUNT = "amount";
    static final String RECIPIENT = "recipient";
    static final String RECIPIENT_PHONE = "recipient_phone";
    static final String DESC = "desc";
    static final String DATE = "amount";
    static final String STATUS = "status";
    static final String CODE = "code";

    private String agent;
    private String id;
    private String _id;
    private String user_id;
    private String pickup;
    private String drop;
    private String distance;
    private String amount;
    private String recipient;
    private String recipient_phone;
    private String sender_phone;
    private String desc;
    private String date;
    private String status;
    private String secreteCode;
    private String locationDesc;

    public RequestModel() {
    }

    public RequestModel(String status, String agent) {
        this.id = status;
        this.agent = agent;

    }

    public RequestModel(String _id, String id, String pickup, String drop, String distance, String amount, String recipient, String recipient_phone, String desc, String date, String status, String secreteCode) {
        this._id = _id;
        this.id = id;
        this.pickup = pickup;
        this.drop = drop;
        this.distance = distance;
        this.amount = amount;
        this.recipient = recipient;
        this.recipient_phone = recipient_phone;
        this.desc = desc;
        this.date = date;
        this.status = status;
        this.secreteCode = secreteCode;

    }

    public RequestModel(String _id, String id, String pickup, String drop, String distance, String amount, String recipient, String recipient_phone, String desc, String date, String status, String sender_phone, String secreteCode) {
        this._id = _id;
        this.id = id;
        this.pickup = pickup;
        this.drop = drop;
        this.distance = distance;
        this.amount = amount;
        this.recipient = recipient;
        this.recipient_phone = recipient_phone;
        this.desc = desc;
        this.date = date;
        this.status = status;
        this.sender_phone = sender_phone;
        this.secreteCode = secreteCode;

    }

    static Intent starter(Context context, String _id, String id, String pickup, String drop, String distance, String amount,
                          String recipient, String recipient_phone, String desc, String date, String status) {
        Intent detailIntent = new Intent(context, RequestDetailsActivity.class);
        detailIntent.putExtra(_ID, _id);
        detailIntent.putExtra(ID, id);
        detailIntent.putExtra(PICKUP, pickup);
        detailIntent.putExtra(DROP, drop);
        detailIntent.putExtra(DISTANCE, distance);
        detailIntent.putExtra(AMOUNT, amount);
        detailIntent.putExtra(RECIPIENT, recipient);
        detailIntent.putExtra(RECIPIENT_PHONE, recipient_phone);
        detailIntent.putExtra(DESC, desc);
        detailIntent.putExtra(DATE, date);
        detailIntent.putExtra(STATUS, status);
        return detailIntent;
    }

    static Intent details(Context context, String _id, String id, String pickup, String drop, String distance, String amount,
                          String recipient, String recipient_phone, String desc, String date, String status, String agent) {
        Intent detailIntent = new Intent(context, DetailsActivity.class);
        detailIntent.putExtra(_ID, _id);
        detailIntent.putExtra(ID, id);
        detailIntent.putExtra(PICKUP, pickup);
        detailIntent.putExtra(DROP, drop);
        detailIntent.putExtra(DISTANCE, distance);
        detailIntent.putExtra(AMOUNT, amount);
        detailIntent.putExtra(RECIPIENT, recipient);
        detailIntent.putExtra(RECIPIENT_PHONE, recipient_phone);
        detailIntent.putExtra(DESC, desc);
        detailIntent.putExtra(DATE, date);
        detailIntent.putExtra(STATUS, status);
        detailIntent.putExtra(AGENT, agent);
        return detailIntent;
    }

    static Intent AddStatus(Context context, String _id, String id, String pickup, String drop, String distance, String amount,
                            String recipient, String recipient_phone, String desc, String date, String status, String agent) {
        Intent detailIntent = new Intent(context, AddStatus.class);
        detailIntent.putExtra(_ID, _id);
        detailIntent.putExtra(ID, id);
        detailIntent.putExtra(PICKUP, pickup);
        detailIntent.putExtra(DROP, drop);
        detailIntent.putExtra(DISTANCE, distance);
        detailIntent.putExtra(AMOUNT, amount);
        detailIntent.putExtra(RECIPIENT, recipient);
        detailIntent.putExtra(RECIPIENT_PHONE, recipient_phone);
        detailIntent.putExtra(DESC, desc);
        detailIntent.putExtra(DATE, date);
        detailIntent.putExtra(STATUS, status);
        detailIntent.putExtra(AGENT, agent);
        return detailIntent;
    }

    static Intent EndStatus(Context context, String _id, String secreteCode) {
        Intent detailIntent = new Intent(context, EndTransaction.class);
        detailIntent.putExtra(_ID, _id);
        detailIntent.putExtra(CODE, secreteCode);

        return detailIntent;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getSecreteCode() {
        return secreteCode;
    }

    public void setSecreteCode(String secreteCode) {
        this.secreteCode = secreteCode;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getLocationDesc() {
        return locationDesc;
    }

    public void setLocationDesc(String locationDesc) {
        this.locationDesc = locationDesc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPickup() {
        return pickup;
    }

    public void setPickup(String pickup) {
        this.pickup = pickup;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getDrop() {
        return drop;
    }

    public void setDrop(String drop) {
        this.drop = drop;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getRecipient_phone() {
        return recipient_phone;
    }

    public void setRecipient_phone(String recipient_phone) {
        this.recipient_phone = recipient_phone;
    }

    public String getSender_phone() {
        return sender_phone;
    }

    public void setSender_phone(String sender_phone) {
        this.sender_phone = sender_phone;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
