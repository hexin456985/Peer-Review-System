package com.micro.conferencesserve.sys.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("Paper_PCMember")
@AllArgsConstructor
@NoArgsConstructor
public class PaperPCMember {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer paperid;

    private Integer pcMemberId;

    private Integer state;

}
