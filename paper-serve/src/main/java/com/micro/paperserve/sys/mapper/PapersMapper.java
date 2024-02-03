package com.micro.paperserve.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.micro.paperserve.sys.entity.Author;
import com.micro.paperserve.sys.entity.Papers;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 
 * @since 2023-11-06
 */
public interface PapersMapper extends BaseMapper<Papers> {
    public List<Papers> getPapersListByPaperId(@Param("paperId") Integer paperId);

    @Select("SELECT * FROM Papers WHERE paper_id = #{paperId}")
    Papers getPaperWithId(int paperId);

    @Select("SELECT a.* FROM Authors a JOIN PaperAuthors pa ON a.authorId = pa.authorId WHERE pa.paperId = #{paperId}")
    List<Author> selectAuthorsForPaper(int paperId);
}
