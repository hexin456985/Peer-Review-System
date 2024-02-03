package com.micro.conferencesserve.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author x
 * @since 2023-10-28
 */
public class Invitation implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "invitationId", type = IdType.AUTO)
    private Integer invitationId;

    @TableField(value = "conferenceId")
    private Integer conferenceId;

    @TableField(value = "userId")
    private Integer userId;

    @TableField(value = "status")
    private Integer status;

    public Integer getInvitationId() {
        return invitationId;
    }

    public void setInvitationId(Integer invitationId) {
        this.invitationId = invitationId;
    }
    public Integer getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(Integer conferenceId) {
        this.conferenceId = conferenceId;
    }
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Invitation{" +
                "invitationId=" + invitationId +
                ", conferenceId=" + conferenceId +
                ", userId=" + userId +
                ", status=" + status +
                "}";
    }
}