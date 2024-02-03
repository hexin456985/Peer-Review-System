package com.micro.conferencesserve.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author 
 * @since 2023-11-29
 */
public class pcmembers implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "pc_member_id", type = IdType.AUTO)
    private Integer pcMemberId;

    private Integer conferenceid;

    private Integer userId;

    private String invitationstatus;

    public Integer getpCMemberID() {
        return pcMemberId;
    }

    public void setpCMemberID(Integer pCMemberID) {
        this.pcMemberId = pCMemberID;
    }
    public Integer getConferenceID() {
        return conferenceid;
    }

    public void setConferenceID(Integer conferenceid) {
        this.conferenceid = conferenceid;
    }
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public String getInvitationStatus() {
        return invitationstatus;
    }

    public void setInvitationStatus(String invitationstatus) {
        this.invitationstatus = invitationstatus;
    }

    @Override
    public String toString() {
        return "PCMembers{" +
            "pCMemberID=" + pcMemberId +
            ", conferenceID=" + conferenceid +
            ", userId=" + userId +
            ", invitationstatus=" + invitationstatus +
        "}";
    }
}
