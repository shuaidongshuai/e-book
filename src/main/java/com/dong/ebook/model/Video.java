package com.dong.ebook.model;

import java.util.Date;

public class Video {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column video.id
     *
     * @mbg.generated
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column video.file_url
     *
     * @mbg.generated
     */
    private String fileUrl;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column video.cover_url
     *
     * @mbg.generated
     */
    private String coverUrl;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column video.title
     *
     * @mbg.generated
     */
    private String title;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column video.video_type_id
     *
     * @mbg.generated
     */
    private Long videoTypeId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column video.modify_user_id
     *
     * @mbg.generated
     */
    private Long modifyUserId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column video.create_time
     *
     * @mbg.generated
     */
    private Date createTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column video.modify_time
     *
     * @mbg.generated
     */
    private Date modifyTime;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column video.id
     *
     * @return the value of video.id
     *
     * @mbg.generated
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column video.id
     *
     * @param id the value for video.id
     *
     * @mbg.generated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column video.file_url
     *
     * @return the value of video.file_url
     *
     * @mbg.generated
     */
    public String getFileUrl() {
        return fileUrl;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column video.file_url
     *
     * @param fileUrl the value for video.file_url
     *
     * @mbg.generated
     */
    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl == null ? null : fileUrl.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column video.cover_url
     *
     * @return the value of video.cover_url
     *
     * @mbg.generated
     */
    public String getCoverUrl() {
        return coverUrl;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column video.cover_url
     *
     * @param coverUrl the value for video.cover_url
     *
     * @mbg.generated
     */
    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl == null ? null : coverUrl.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column video.title
     *
     * @return the value of video.title
     *
     * @mbg.generated
     */
    public String getTitle() {
        return title;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column video.title
     *
     * @param title the value for video.title
     *
     * @mbg.generated
     */
    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column video.video_type_id
     *
     * @return the value of video.video_type_id
     *
     * @mbg.generated
     */
    public Long getVideoTypeId() {
        return videoTypeId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column video.video_type_id
     *
     * @param videoTypeId the value for video.video_type_id
     *
     * @mbg.generated
     */
    public void setVideoTypeId(Long videoTypeId) {
        this.videoTypeId = videoTypeId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column video.modify_user_id
     *
     * @return the value of video.modify_user_id
     *
     * @mbg.generated
     */
    public Long getModifyUserId() {
        return modifyUserId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column video.modify_user_id
     *
     * @param modifyUserId the value for video.modify_user_id
     *
     * @mbg.generated
     */
    public void setModifyUserId(Long modifyUserId) {
        this.modifyUserId = modifyUserId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column video.create_time
     *
     * @return the value of video.create_time
     *
     * @mbg.generated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column video.create_time
     *
     * @param createTime the value for video.create_time
     *
     * @mbg.generated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column video.modify_time
     *
     * @return the value of video.modify_time
     *
     * @mbg.generated
     */
    public Date getModifyTime() {
        return modifyTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column video.modify_time
     *
     * @param modifyTime the value for video.modify_time
     *
     * @mbg.generated
     */
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
}