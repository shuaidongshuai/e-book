package com.dong.ebook.dto;

public class Menu {
    private String item;
    private String url;

    public Menu(String item, String url) {
        this.item = item;
        this.url = url;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
