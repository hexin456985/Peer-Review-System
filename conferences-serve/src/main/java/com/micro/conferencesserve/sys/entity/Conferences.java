package com.micro.conferencesserve.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;


/**
* <p>
* 
* </p >
*
* @author 
* @since 2023-10-31
*/
@Data
@TableName("conferences")
@AllArgsConstructor
@NoArgsConstructor
public class Conferences implements Serializable {

private static final long serialVersionUID = 1L;

@TableId(value = "conferenceId", type = IdType.AUTO)
private Integer conferenceId;

private Integer chairId;

private String shortName;

private String fullName;

private LocalDate eventDate;

private String eventLocation;

private LocalDate submissionDeadline;

private LocalDate reviewresultDate;

private String status;
}
