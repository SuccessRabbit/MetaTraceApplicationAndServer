package xyz.handsomelee.Controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.netty.handler.codec.http.multipart.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.handsomelee.Domain.ChatMessage;
import xyz.handsomelee.Domain.Friends;
import xyz.handsomelee.Domain.User;
import xyz.handsomelee.Mapper.ChatMessageMapper;
import xyz.handsomelee.Mapper.FriendsMapper;
import xyz.handsomelee.Mapper.UserMapper;
import xyz.handsomelee.Protocal.MessageContent;
import xyz.handsomelee.Utils.JsonUtils;

import java.io.File;
import java.sql.Wrapper;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${my.requestMapping}")
public class UserController {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private FriendsMapper friendsMapper;
    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @GetMapping("/register")
    public String register(String username, String password){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("nickname", username);
        User user = userMapper.selectOne(wrapper);
        if(user != null){
            // 如果已经有这个用户了
            return "用户已存在，请直接尝试登录";
        }else{
            // 如果没有这个用户，则允许注册
            User newUser = new User();
            newUser.setLevel(1);
            newUser.setMoney(0);
            newUser.setNickname(username);
            newUser.setPassword(password);
            userMapper.insert(newUser);
            return "注册成功";
        }
    }

    @GetMapping("/login")
    public String getByUserName(String username, String password){
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("nickname", username);
        User user = userMapper.selectOne(wrapper);
        if(user == null) return "此用户名不存在";
        if(user.getPassword().equals(password)){
            return "欢迎";
        }
        return "密码错误";
    }

    @GetMapping("/friends")
    public String getFriendsList(String username){
        QueryWrapper<Friends> queryWrapper = new QueryWrapper<>();


//        queryWrapper.eq("is_accepted", true)
//                .and(wrapper ->{
//                    wrapper.eq("sender_id", username)
//                            .or()
//                            .eq("receiver_id", username);
//                });
        queryWrapper.eq("sender_id", username)
                            .or()
                            .eq("receiver_id", username);

        List<Friends> friends = friendsMapper.selectList(queryWrapper);
        return JsonUtils.objectToJson(friends);

    }

    @GetMapping("/messages")
    public List<ChatMessage> getUnsignedMessages(String receiverUserName){
        QueryWrapper<ChatMessage> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("sign_flag", false)
                .and(wrapper ->{
                    wrapper.eq("receive_user_id", receiverUserName);
                });

        return chatMessageMapper.selectList(queryWrapper);
    }

    @GetMapping("/acceptOrDenyFriend")
    public String acceptOrDenyFriend(String action, String myUsername, String hisUsername){
        QueryWrapper<Friends> friendQueryWrapper = new QueryWrapper<>();
        friendQueryWrapper.and(wrapper->{
            wrapper.eq("sender_id", myUsername)
                    .eq("receiver_id", hisUsername);
        }).or(wrapper->{
            wrapper.eq("sender_id", hisUsername)
                    .eq("receiver_id", myUsername);
        });
        Friends one = friendsMapper.selectOne(friendQueryWrapper);
        if(action.equals("accept")){
            // 如果是同意添加好友
            one.setIsAccepted(true);
            friendsMapper.updateById(one);
            return "add ok";
        }else{
            // 如果是拒绝添加好友
            friendsMapper.delete(friendQueryWrapper);
            return "delete ok";
        }
    }

    @GetMapping("/addFriend")
    public String tryAddFriend(String myUsername, String hisUsername){
        if(myUsername.equals(hisUsername)) return "不能添加自己为好友";
        QueryWrapper<Friends> friendQueryWrapper = new QueryWrapper<>();
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();

        // 首先判定数据库中是否有这个用户
        userQueryWrapper.eq("nickname", hisUsername);
        User user = userMapper.selectOne(userQueryWrapper);
        if(user == null) return "该用户不存在";
        else{
            // 该用户存在 判断是否已经是好友
            friendQueryWrapper.and(wrapper->{
                wrapper.eq("sender_id", myUsername)
                        .eq("receiver_id", hisUsername);
            }).or(wrapper->{
                wrapper.eq("sender_id", hisUsername)
                        .eq("receiver_id", myUsername);
            });
            Friends one = friendsMapper.selectOne(friendQueryWrapper);
            if(one == null){
                // 如果数据库中没有这条添加好友的记录
                Friends friends = new Friends();
                friends.setSenderId(myUsername);
                friends.setReceiverId(hisUsername);
                friends.setIsAccepted(false);
                friendsMapper.insert(friends);
                return "已经向对方发送好友请求";
            }else{
                // 数据库中有这条记录 需要判断是否已经是好友了
                if(one.isAccepted) return "你们已经是好友了";
                else {
                    // 如果发起者是本人 则告知已经发送好友请求，等待对方回应
                    if(one.getSenderId().equals(myUsername)) return "已经发送过好友请求，等待对方回应";
                    // 如果发起者是对方，则直接添加好友
                    else{
                        one.isAccepted = true;
                        friendsMapper.updateById(one);
                        return "成功添加对方为好友";
                    }
                }
            }
        }
    }


}
