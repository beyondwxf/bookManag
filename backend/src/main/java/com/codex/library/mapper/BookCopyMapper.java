package com.codex.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codex.library.entity.BookCopy;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface BookCopyMapper extends BaseMapper<BookCopy> {

    @Update("""
            update book_copies
            set status = #{nextStatus},
                version = version + 1,
                updated_at = now()
            where id = #{id}
              and status = #{expectedStatus}
              and deleted = 0
            """)
    int updateStatusIfMatch(@Param("id") Long id,
                            @Param("expectedStatus") String expectedStatus,
                            @Param("nextStatus") String nextStatus);
}
