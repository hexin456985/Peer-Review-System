package com.micro.paperserve.sys.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.micro.paperserve.sys.entity.Author;

public interface AuthorMapper extends BaseMapper<Author>{
    public List<Author> getAuthorListByAuthorId(@Param("authorId")Integer authorId);

    @Insert("INSERT INTO Author (author_name, author_institution, author_region, author_email) VALUES (#{authorName}, #{authorInstitution}, #{authorRegion}, #{authorEmail})")
    @Options(useGeneratedKeys = true, keyProperty = "authorId")
    void insertAuthor(Author author);

    @Select("SELECT a.* FROM Author a INNER JOIN paper_authors ap ON a.author_id =ap.author_id INNER JOIN papers p ON ap.paper_id = p.paper_id WHERE p.paper_id = #{paperId}") 
    public List<Author> getAuthorsByPaperId(Integer Id);

    
}
