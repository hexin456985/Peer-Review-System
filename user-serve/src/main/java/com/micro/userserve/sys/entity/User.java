package com.micro.userserve.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author 作者
 * @since 2023-09-20
 */
@TableName("user")
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "UID", type = IdType.AUTO)
    private Integer uid;

    private String name;

    private String gender;

    private Integer age;

    private String password;

    private String email;

    private String institution;

    private String region;

    @TableField(exist = false)
    private List<Integer> roleUidList;
}
