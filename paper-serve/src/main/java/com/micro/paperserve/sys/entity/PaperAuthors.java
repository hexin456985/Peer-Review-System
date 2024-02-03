package com.micro.paperserve.sys.entity;

import java.io.Serializable;

/**
 * Represents a relationship between a paper and an author.
 */
public class PaperAuthors implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer paperAuthorId; // Primary key, if you have one
    private Integer paperId;       // Foreign key referencing 'Papers'
    private Integer authorId;      // Foreign key referencing 'Authors'

    // Constructors, getters, setters, and toString methods go here

    public Integer getPaperAuthorId() {
        return paperAuthorId;
    }

    public void setPaperAuthorId(Integer paperAuthorId) {
        this.paperAuthorId = paperAuthorId;
    }

    public Integer getPaperId() {
        return paperId;
    }

    public void setPaperId(Integer paperId) {
        this.paperId = paperId;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    @Override
    public String toString() {
        return "PaperAuthors{" +
            "paperAuthorId=" + paperAuthorId +
            ", paperId=" + paperId +
            ", authorId=" + authorId +
            "}";
    }
}
