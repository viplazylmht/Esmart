package com.viplazy.ez.esmart;

public class Question {
    private String type;
    private String id;
    private String detail;
    private String path;
    private String RA;
    private String WA1;
    private String WA2;
    private String WA3;

    public Question(String type,String id, String detail, String path, String RA, String WA1, String WA2, String WA3) {
        this.type = type;
        this.id = id;
        this.detail = detail;
        this.path = path;
        this.RA = RA;
        this.WA1 = WA1;
        this.WA2 = WA2;
        this.WA3 = WA3;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Question() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getRA() {
        return RA;
    }

    public void setRA(String RA) {
        this.RA = RA;
    }

    public String getWA1() {
        return WA1;
    }

    public void setWA1(String WA1) {
        this.WA1 = WA1;
    }

    public String getWA2() {
        return WA2;
    }

    public void setWA2(String WA2) {
        this.WA2 = WA2;
    }

    public String getWA3() {
        return WA3;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setWA3(String WA3) {
        this.WA3 = WA3;
    }
}
