package com.micro.paperserve.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author 
 * @since 2023-11-06
 */
public class Papers implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "paper_id", type = IdType.AUTO)
    private Integer paperId;

    private String title;

    private String paperAbstract;

    private String pdfPath;

    private Integer authorId;

    private Integer conferenceId;

    private String status;

    public Integer getPaperId() {
        return paperId;
    }

    public void setPaperId(Integer paperId) {
        this.paperId = paperId;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getPaperAbstract() {
        return paperAbstract;
    }

    public void setPaperAbstract(String paperAbstract) {
        this.paperAbstract = paperAbstract;
    }
    public String getpDFPath() {
        return pdfPath;
    }

    public void setpDFPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }
    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }
    public Integer getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(Integer conferenceId) {
        this.conferenceId = conferenceId;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Papers{" +
            "paperId=" + paperId +
            ", title=" + title +
            ", paperAbstract=" + paperAbstract +
            ", pdfPath=" + pdfPath +
            ", authorId=" + authorId +
            ", conferenceId=" + conferenceId +
            ", status=" + status +
        "}";
    }
}
