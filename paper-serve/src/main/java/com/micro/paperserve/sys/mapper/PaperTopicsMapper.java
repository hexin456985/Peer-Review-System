package com.micro.paperserve.sys.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.micro.paperserve.sys.entity.PaperTopics;


public interface PaperTopicsMapper extends BaseMapper<PaperTopics>{
    @Delete("DELETE FROM paper_topics WHERE paper_id = #{paperId}")
    int deleteByPaperId(@Param("paperId") int paperId);

    @Select("SELECT * FROM paper_topics WHERE paper_id = #{paperId}")
    List<Integer> getByPaperId(int paperId);
}
