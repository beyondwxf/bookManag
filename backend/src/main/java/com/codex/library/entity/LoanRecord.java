package com.codex.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("loan_records")
public class LoanRecord {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String loanNo;
    private Long readerId;
    private Long bookCopyId;
    private Long borrowedBy;
    private Long returnedBy;
    private LocalDateTime borrowTime;
    private LocalDate dueDate;
    private LocalDateTime returnTime;
    private String status;
    @TableLogic
    private Integer deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
