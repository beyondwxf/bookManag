package com.codex.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codex.library.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserMapper extends BaseMapper<User> {

    @Select("select * from users where username = #{username} and deleted = 0 limit 1")
    User findByUsername(@Param("username") String username);
}
