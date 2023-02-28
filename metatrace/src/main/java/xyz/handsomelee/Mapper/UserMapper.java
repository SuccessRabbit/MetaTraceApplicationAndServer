package xyz.handsomelee.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xyz.handsomelee.Domain.User;
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
