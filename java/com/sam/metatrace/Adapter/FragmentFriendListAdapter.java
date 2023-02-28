package com.sam.metatrace.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sam.metatrace.Entity.FriendListViewItemBean;
import com.sam.metatrace.R;

import java.util.ArrayList;
import java.util.List;

public class FragmentFriendListAdapter extends BaseAdapter {

    List<FriendListViewItemBean> data = new ArrayList<>();

    LayoutInflater inflater;

    public FragmentFriendListAdapter(Context context){
        this.inflater = LayoutInflater.from(context);
    }

    public List<FriendListViewItemBean> getData(){
        return data;
    }

    public void setData(List<FriendListViewItemBean> data){
        this.data = data;
        notifyDataSetChanged();
    }

    public void addData(FriendListViewItemBean data){
        this.data.add(data);
        notifyDataSetChanged();
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
        View view = inflater.inflate(R.layout.item_friend_list_view , null);

        ImageView logo = view.findViewById(R.id.logo);
        TextView who = view.findViewById(R.id.who);

        logo.setImageResource(data.get(position).icon);
        who.setText(data.get(position).who);


        return view;
    }
}
