package com.dong.ebook.dto;

public class ResponseSaveBookDto extends ResponseCommonDto {
    private Long bookId;

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
}
