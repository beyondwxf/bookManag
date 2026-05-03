package com.codex.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("book_copies")
public class BookCopy {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long bookId;
    private String barcode;
    private String locationCode;
    private String status;
    private Integer version;
    @TableLogic
    private Integer deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
