package com.codex.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("reader_profiles")
public class ReaderProfile {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String readerNo;
    private String name;
    private String phone;
    private String email;
    private String idCard;
    private String status;
    @TableLogic
    private Integer deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
