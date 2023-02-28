package com.sam.metatrace.Fragments;

import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.sam.metatrace.Abstract.FragmentBase;
import com.sam.metatrace.Adapter.FragmentChatAdapter;
import com.sam.metatrace.Entity.ChatBubbleItemBean;
import com.sam.metatrace.Entity.ChatListViewItemBean;
import com.sam.metatrace.Enums.AndroidMessageEnum;
import com.sam.metatrace.MainPage;
import com.sam.metatrace.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FragmentChat extends FragmentBase implements AdapterView.OnItemClickListener {
    private View root;

    private static FragmentChatAdapter fragmentChatAdapter;
    private static Map<String, List<Object>> finalChatLastMsg = new HashMap<>();
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd日\na hh:mm");
    private static List<ChatListViewItemBean> data  = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return root  = inflater.inflate(R.layout.fragment_chat, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(fragmentChatAdapter == null){
            fragmentChatAdapter = new FragmentChatAdapter(root.getContext());
        }

        ListView lv = root.findViewById(R.id.lv_chat);

        lv.setAdapter(fragmentChatAdapter);

        // 设置list view 短按的点击事件
        lv.setOnItemClickListener(this);
        // 设置listview长按菜单
        lv.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("操作");
                menu.add(0, 0, 0, "删除聊天记录");

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume() {
        super.onResume();
        updateChatListViewItemByFinalChatLastMsg(null);
    }


    /**
     * 通过最终消息快照集合更新对应的视图
     *
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void updateChatListViewItemByFinalChatLastMsg(String whoShowMeSignUsername){
        /*
         * 获取数据库中的消息快照，每次返回到FragmentChat消息列表的时候会调用，包括首次进入app时进入到消息列表时也会调用
         * getChatUsernamesAndLastMsg方法返回一个集合，键是对方的用户名，如果是本人发给对方的消息则保存的键是I+对方的用户名
         * 值是一个列表 第一个元素是string类型的消息内容，第二个元素是该消息的存入数据库中的时间 long型。
         */
        Map<String, List<Object>> chatUsernamesAndLastMsg = MainPage.sqLiteHelper.getChatUsernamesAndLastMsg();
        // 处理消息快照，只保存最新的消息，即判断对方发给本人的和本人发给对方的哪个更新，选择最新的进行展示
        finalChatLastMsg.clear();
        Set<String> keySet = chatUsernamesAndLastMsg.keySet();
        Set<String> userNames = new HashSet<>();
        for (String s : keySet) {
            if (s.charAt(0) == 'I'){
                userNames.add(s.substring(1));
            }else userNames.add(s);
        }

        for (String userName : userNames) {
            String lastUsername = chatUsernamesAndLastMsg.containsKey(userName)?
                    chatUsernamesAndLastMsg.containsKey("I"+userName)?
                            (long)chatUsernamesAndLastMsg.get(userName).get(1) < (long)chatUsernamesAndLastMsg.get("I"+userName).get(1)?
                                    "I"+userName
                                    : userName
                            : userName
                    : "I"+userName;
            finalChatLastMsg.put(lastUsername, chatUsernamesAndLastMsg.get(lastUsername));
        }

        // 将最终消息快照交由线程进行更新显示
        Set<String> _userNames = finalChatLastMsg.keySet();

outer:  for (String userName : _userNames) {
            String showUserName;
            if(userName.charAt(0) == 'I' && userName.length()>1 && userName.charAt(1) != 'I'){
                showUserName = userName.substring(1);
            }else showUserName = userName;
            for (ChatListViewItemBean viewItemBean : data) {
                if(viewItemBean.who.equals(showUserName)){
                    viewItemBean.timeInMilli = finalChatLastMsg.containsKey(showUserName)? (long)finalChatLastMsg.get(showUserName).get(1):
                            (long)finalChatLastMsg.get("I"+showUserName).get(1);
                    viewItemBean.msg = finalChatLastMsg.containsKey(showUserName)? (String)finalChatLastMsg.get(showUserName).get(0): (String)finalChatLastMsg.get("I"+showUserName).get(0);
                    viewItemBean.time = simpleDateFormat.format(finalChatLastMsg.containsKey(showUserName)? (long)finalChatLastMsg.get(showUserName).get(1):
                            (long)finalChatLastMsg.get("I"+showUserName).get(1));
                    continue outer;
                }
            }
            ChatListViewItemBean c = new ChatListViewItemBean();
            c.icon = R.drawable.ic_baseline_chat_24;
            c.msg = finalChatLastMsg.containsKey(showUserName)? (String)finalChatLastMsg.get(showUserName).get(0): (String)finalChatLastMsg.get("I"+showUserName).get(0);
            c.who = showUserName;
            c.time = simpleDateFormat.format(finalChatLastMsg.containsKey(showUserName)? (long)finalChatLastMsg.get(showUserName).get(1):
                    (long)finalChatLastMsg.get("I"+showUserName).get(1));
            c.timeInMilli = finalChatLastMsg.containsKey(showUserName)? (long)finalChatLastMsg.get(showUserName).get(1):
                    (long)finalChatLastMsg.get("I"+showUserName).get(1);
            data.add(c);
        }
        Collections.sort(data, (o1, o2)-> -(int) (o1.timeInMilli - o2.timeInMilli));
        if(whoShowMeSignUsername != null){
            for (ChatListViewItemBean viewItemBean : data) {
                if(viewItemBean.who.equals(whoShowMeSignUsername))
                    viewItemBean.showNewMsgSign = true;
            }
        }
        if(data != null && fragmentChatAdapter!=null) {
            fragmentChatAdapter.setData(data);
            fragmentChatAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置listview的长按菜单触发操作
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int id = (int)info.id;
        if(item.getGroupId() != 0) return super.onContextItemSelected(item);
        switch (item.getItemId()) {
            case 0:
                // 如果是删除聊天记录
                MainPage.sqLiteHelper.deleteAllByUser(fragmentChatAdapter.getData().get(id).who);
                fragmentChatAdapter.getData().remove(id);
                fragmentChatAdapter.notifyDataSetChanged();
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return super.onContextItemSelected(item);
    }

    public static void setChatItemSignStatus(String userName, boolean show){
        for (ChatListViewItemBean itemBean : data) {
            if(itemBean.who.equals(userName))
                itemBean.showNewMsgSign = show;
        }
        fragmentChatAdapter.setData(data);
        fragmentChatAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 通知mainPage跳转到聊天fragment
        Message message = new Message();
        message.what = AndroidMessageEnum.GO_TO_ChatRoom_FRAGMENT.getType();
        message.obj = fragmentChatAdapter.getData().get(position).who;
        mHandler.sendMessage(message);

        fragmentChatAdapter.getData().get(position).showNewMsgSign = false;

        ChatWithOneFriendFragment.intentUsername = (String)message.obj;
        // Toast.makeText(root.getContext(), fragmentChatAdapter.getData().get(position).who, Toast.LENGTH_SHORT).show();
    }

    /**
     * 通过对方昵称设置右上角小红点状态
     * @param who 对方昵称
     * @param flag 小红点状态
     */
    public static void setOneChatItemShowMeSign(String who, boolean flag){
        int position = -1;
        for (ChatListViewItemBean datum : fragmentChatAdapter.getData()) {
            if (datum.who.equals(who)) position = fragmentChatAdapter.getData().indexOf(datum);
        }
        fragmentChatAdapter.getData().get(position).showNewMsgSign = flag;
    }

}
