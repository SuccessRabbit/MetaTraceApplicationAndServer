package com.sam.metatrace.Adapter;

import android.content.Context;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sam.metatrace.Entity.ChatListViewItemBean;
import com.sam.metatrace.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FragmentChatAdapter extends BaseAdapter {

    List<ChatListViewItemBean> data = new ArrayList<>();
    LayoutInflater inflater;

    public List<ChatListViewItemBean> getData() {
        return data;
    }

    public void setData(List<ChatListViewItemBean> data) {
        this.data = data;
    }

    public FragmentChatAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }



    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_chat_view , null);

        ImageView logo = view.findViewById(R.id.logo);
        TextView who = view.findViewById(R.id.who);
        TextView time = view.findViewById(R.id.msg_time);
        TextView msg = view.findViewById(R.id.msg);
        ImageView new_massage_sign = view.findViewById(R.id.new_msg_sign);

        logo.setImageResource(data.get(position).icon);
        who.setText(data.get(position).who);
        time.setText(data.get(position).time);
        msg.setText(data.get(position).msg);
        new_massage_sign.setVisibility(data.get(position).showNewMsgSign? View.VISIBLE: View.INVISIBLE);

        return view;
    }
}
