package com.micro.conferencesserve.sys.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("Topic_PCMember")
@AllArgsConstructor
@NoArgsConstructor
public class TopicPCM {
    private static final long serialVersionUID = 1L;

    @TableId(value = "topic_pcmember_id", type = IdType.AUTO)
    private Integer id;

    private Integer topicId;

    private Integer pcMemberId;

}
