package com.dong.ebook.model;

public class Concern {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column concern.id
     *
     * @mbg.generated
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column concern.from_user_id
     *
     * @mbg.generated
     */
    private Long fromUserId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column concern.to_user_id
     *
     * @mbg.generated
     */
    private Long toUserId;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column concern.id
     *
     * @return the value of concern.id
     *
     * @mbg.generated
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column concern.id
     *
     * @param id the value for concern.id
     *
     * @mbg.generated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column concern.from_user_id
     *
     * @return the value of concern.from_user_id
     *
     * @mbg.generated
     */
    public Long getFromUserId() {
        return fromUserId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column concern.from_user_id
     *
     * @param fromUserId the value for concern.from_user_id
     *
     * @mbg.generated
     */
    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column concern.to_user_id
     *
     * @return the value of concern.to_user_id
     *
     * @mbg.generated
     */
    public Long getToUserId() {
        return toUserId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column concern.to_user_id
     *
     * @param toUserId the value for concern.to_user_id
     *
     * @mbg.generated
     */
    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }
}