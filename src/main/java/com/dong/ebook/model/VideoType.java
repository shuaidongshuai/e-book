package com.dong.ebook.model;

public class VideoType {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column video_type.id
     *
     * @mbg.generated
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column video_type.class_name
     *
     * @mbg.generated
     */
    private String className;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column video_type.id
     *
     * @return the value of video_type.id
     *
     * @mbg.generated
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column video_type.id
     *
     * @param id the value for video_type.id
     *
     * @mbg.generated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column video_type.class_name
     *
     * @return the value of video_type.class_name
     *
     * @mbg.generated
     */
    public String getClassName() {
        return className;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column video_type.class_name
     *
     * @param className the value for video_type.class_name
     *
     * @mbg.generated
     */
    public void setClassName(String className) {
        this.className = className == null ? null : className.trim();
    }
}