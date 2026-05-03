package com.codex.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codex.library.entity.ReaderProfile;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ReaderProfileMapper extends BaseMapper<ReaderProfile> {

    @Select("select * from reader_profiles where user_id = #{userId} and deleted = 0 limit 1")
    ReaderProfile findByUserId(@Param("userId") Long userId);
}
