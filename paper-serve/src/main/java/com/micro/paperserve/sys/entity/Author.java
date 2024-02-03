package com.micro.paperserve.sys.entity;

public class Author {
    private Integer authorId; // 对应 author_id
    private String authorName; // 对应 author_name
    private String authorInstitution; // 对应 author_institution
    private String authorRegion; //对应 author_region
    private String authorEmail; // 对应 author_email

    // 构造函数
    public Author() {}

    // getter 和 setter 方法
    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorInstitution() {
        return authorInstitution;
    }

    public void setAuthorInstitution(String authorInstitution) {
        this.authorInstitution = authorInstitution;
    }

    public String getAuthorRegion(){
        return this.authorRegion;
    }

    public void setAuthorRegion(String authorRegion) {
        this.authorRegion = authorRegion;
    }
    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    // 重写toString方法，便于打印和调试
    @Override
    public String toString() {
        return "Author{" +
               "authorId=" + authorId +
               ", authorName='" + authorName + '\'' +
               ", authorInstitution='" + authorInstitution + '\'' +
               ", authorEmail='" + authorEmail + '\'' +
               '}';
    }
}

