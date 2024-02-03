package com.micro.paperserve.sys.mapper;


import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.micro.paperserve.sys.entity.PaperAuthors;

public interface PaperAuthorsMapper extends BaseMapper<PaperAuthors> {
    @Delete("DELETE FROM paper_authors WHERE paper_id = #{paperId}")
    int deleteByPaperId(@Param("paperId") int paperId);
    
}
