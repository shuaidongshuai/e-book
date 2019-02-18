package com.dong.ebook.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Menu {
    private String item;
    private String url;

    public Menu(String item, String url) {
        this.item = item;
        this.url = url;
    }
}
