package xyz.handsomelee;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.handsomelee.Domain.ChatMessage;
import xyz.handsomelee.Domain.Friends;
import xyz.handsomelee.Domain.User;
import xyz.handsomelee.Mapper.ChatMessageMapper;
import xyz.handsomelee.Mapper.FriendsMapper;
import xyz.handsomelee.Mapper.UserMapper;

import java.util.List;

@SpringBootTest
class MetatraceApplicationTests {
//    @Autowired
//    private UserMapper userMapper;
//    @Autowired
//    private ChatMessageMapper chatMessageMapper;
//    @Autowired
//    private FriendsMapper friendsMapper;
//
//    @Test
//    void contextLoads() {
//        List<User> users = userMapper.selectList(null);
//        System.out.println(users);
//    }
//
//    @Test
//    void testChatMessageMapper(){
//        List<ChatMessage> chats = chatMessageMapper.selectList(null);
//        System.out.println(chats);
//    }
//
//    @Test
//    void testFriendsInterface(){
//        QueryWrapper<Friends> queryWrapper = new QueryWrapper<>();
//
//
//        queryWrapper.eq("is_accepted", true)
//                .and(wrapper ->{
//                    wrapper.eq("sender_id", "admin")
//                            .or()
//                            .eq("receiver_id", "admin");
//                });
//
//        List<Friends> friends = friendsMapper.selectList(queryWrapper);
//        for (Friends friend : friends) {
//            System.out.println(friend);
//        }
//    }



}
