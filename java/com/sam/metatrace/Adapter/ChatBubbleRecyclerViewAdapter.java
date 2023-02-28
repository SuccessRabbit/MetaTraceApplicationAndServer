package com.sam.metatrace.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.InflateException;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sam.metatrace.Entity.ChatBubbleItemBean;
import com.sam.metatrace.Fragments.ChatWithOneFriendFragment;
import com.sam.metatrace.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class ChatBubbleRecyclerViewAdapter extends RecyclerView.Adapter implements RecyclerView.OnItemTouchListener {
    // 定义常量标识不同类型的条目
    public static final int ME = 0;
    public static final int NOT_ME = 1;
    public static final int HINT_MSG = 2;
    public static final int IMG_ME = 3;
    public static final int IMG_NOTME = 4;

    private final List<ChatBubbleItemBean> mdatas;
    private Activity activity;
    private Context context;

    public ChatBubbleRecyclerViewAdapter(List<ChatBubbleItemBean> chatBubbleItemBeans, Activity activity, Context context) {
        this.mdatas = chatBubbleItemBeans;
        this.activity = activity;
        this.context = context;
    }



    // 获取条目类型
    @Override
    public int getItemViewType(int position) {
        ChatBubbleItemBean chatBubbleItemBean = mdatas.get(position);
        if(chatBubbleItemBean.type == ME){
            return ME;
        }else if(chatBubbleItemBean.type == HINT_MSG){
            return HINT_MSG;
        }else if(chatBubbleItemBean.type == NOT_ME){
            return NOT_ME;
        }else if(chatBubbleItemBean.type == IMG_ME){
            return IMG_ME;
        }else if(chatBubbleItemBean.type == IMG_NOTME){
            return IMG_NOTME;
        }
        else return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //传进去的view是条目的界面
        View view;
        if(viewType == ME){
            view = View.inflate(parent.getContext(), R.layout.item_list_me_view, null);

            return new MeHolder(view);
        }else if(viewType == HINT_MSG){
            view = View.inflate(parent.getContext(), R.layout.item_hint_msg_view, null);
            return new HintHolder(view);
        }
        else if(viewType == NOT_ME){
            view = View.inflate(parent.getContext(), R.layout.item_list_not_me_view, null);
            return new NotMeHolder(view);
        } else if(viewType == IMG_ME){
            view = View.inflate(parent.getContext(), R.layout.item_img_me_view, null);
            return new ImgMeHolder(view);
        }else if(viewType == IMG_NOTME){
            view = View.inflate(parent.getContext(), R.layout.item_img_not_me_view, null);
            return new ImgNotMeHolder(view);
        }
        else return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatBubbleItemBean chatBubbleItemBean = mdatas.get(position);
        if(chatBubbleItemBean.type == ME){
            MeHolder meHolder = (MeHolder)holder;
            meHolder.setData(chatBubbleItemBean);
        }else if(chatBubbleItemBean.type == HINT_MSG){
            HintHolder hintHolder = (HintHolder) holder;
            hintHolder.setData(chatBubbleItemBean);
        }else if(chatBubbleItemBean.type == NOT_ME){
            NotMeHolder notMeHolder = (NotMeHolder) holder;
            notMeHolder.setData(chatBubbleItemBean);
        }else if(chatBubbleItemBean.type == IMG_ME){
            ImgMeHolder imgMeHolder = (ImgMeHolder) holder;
            imgMeHolder.setData(chatBubbleItemBean);
        }else if(chatBubbleItemBean.type == IMG_NOTME){
            ImgNotMeHolder imgNotMeHolder = (ImgNotMeHolder) holder;
            imgNotMeHolder.setData(chatBubbleItemBean);
        }
        holder.itemView.setOnClickListener(v->{
            ChatWithOneFriendFragment.hidePopup();
        });
    }


    /**
     * 返回条目个数
     * @return
     */
    @Override
    public int getItemCount() {
        if (mdatas != null) {
            return mdatas.size();
        }
        return 0;
    }


    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    private class NotMeHolder extends RecyclerView.ViewHolder{
        private ImageView mIcon;
        private TextView mMsg;

        public NotMeHolder(@NonNull View itemView) {
            super(itemView);
            // 找到条目的控件
            mIcon = itemView.findViewById(R.id.list_view_icon);
            mMsg = itemView.findViewById(R.id.list_view_msg);
        }

        public void setData(ChatBubbleItemBean chatBubbleItemBean) {
            // 设置数据
            mIcon.setImageResource(chatBubbleItemBean.icon);
            mMsg.setText(chatBubbleItemBean.msg);
        }
    }

    private class MeHolder extends RecyclerView.ViewHolder{
        private ImageView mIcon;
        private TextView mMsg;

        public MeHolder(@NonNull View itemView) {
            super(itemView);
            // 找到条目的控件
            mIcon = itemView.findViewById(R.id.list_view_icon);
            mMsg = itemView.findViewById(R.id.list_view_msg);
        }

        public void setData(ChatBubbleItemBean chatBubbleItemBean) {
            // 设置数据
            mIcon.setImageResource(chatBubbleItemBean.icon);
            mMsg.setText(chatBubbleItemBean.msg);
        }
    }

    private class HintHolder extends RecyclerView.ViewHolder{

        private TextView mMsg;
        public HintHolder(@NonNull View itemView) {
            super(itemView);
            // 找到条目的控件

            mMsg = itemView.findViewById(R.id.list_view_msg);
        }

        public void setData(ChatBubbleItemBean chatBubbleItemBean) {
            // 设置数据
            mMsg.setText(chatBubbleItemBean.msg);
        }
    }

    private class ImgMeHolder extends RecyclerView.ViewHolder{

        private ImageView imgView;
        public ImgMeHolder(@NonNull View itemView) {
            super(itemView);
            // 找到条目的控件

            imgView = itemView.findViewById(R.id.list_view_img_me);

        }

        public void setData(ChatBubbleItemBean chatBubbleItemBean) {
            // 设置数据
            imgView.setImageBitmap(getLoacalBitmap(chatBubbleItemBean.msg));
            imgView.setOnClickListener(v->{
                bigImageLoader(getLoacalBitmap(chatBubbleItemBean.msg));
            });
        }
    }

    private class ImgNotMeHolder extends RecyclerView.ViewHolder{

        private ImageView imgView;
        public ImgNotMeHolder(@NonNull View itemView) {
            super(itemView);
            // 找到条目的控件

            imgView = itemView.findViewById(R.id.list_view_img_not_me);
        }

        public void setData(ChatBubbleItemBean chatBubbleItemBean) {
            // 设置数据
            imgView.setImageBitmap(getLoacalBitmap(chatBubbleItemBean.msg));
            imgView.setOnClickListener(v->{
                bigImageLoader(getLoacalBitmap(chatBubbleItemBean.msg));
            });
        }
    }

    /**
     * 加载本地图片
     * http://bbs.3gstdy.com
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    //方法里直接实例化一个imageView不用xml文件，传入bitmap设置图片 点击放大图片
    private void bigImageLoader(Bitmap bitmap){
        final Dialog dialog = new Dialog(activity);
        ImageView image = new ImageView(context);
        image.setImageBitmap(bitmap);
        dialog.setContentView(image);
        //将dialog周围的白块设置为透明
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //显示
        dialog.show();
        //点击图片取消
        image.setOnClickListener(v -> dialog.cancel());
    }

}
