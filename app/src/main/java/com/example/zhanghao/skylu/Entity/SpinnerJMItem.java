package com.example.zhanghao.skylu.Entity;

public class SpinnerJMItem {
    private String name;
    private String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public SpinnerJMItem(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.id;
    }
}
