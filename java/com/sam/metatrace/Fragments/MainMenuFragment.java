package com.sam.metatrace.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sam.metatrace.Abstract.FragmentBase;
import com.sam.metatrace.Adapter.SectionPagerAdapter;
import com.sam.metatrace.R;

import java.util.ArrayList;
import java.util.List;


public class MainMenuFragment extends FragmentBase implements ViewPager.OnPageChangeListener, BottomNavigationView.OnNavigationItemSelectedListener{


    private ViewPager viewPager;
    private BottomNavigationView bottomNavigationView;
    private List<Fragment> fragments;
    private  View root = null;

    private static FragmentBase fragmentChat = new FragmentChat();
    private static FragmentBase fragmentFriend = new FragmentFriend();
    private static FragmentBase fragmentPlay = new FragmentPlay();



    public MainMenuFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        root = inflater.inflate(R.layout.fragment_main_menu, container, false);

        // 获取组件
        viewPager = root.findViewById(R.id.view_pager);
        bottomNavigationView = root.findViewById(R.id.bottom_nav_view);
        // 初始化组件
        initBottomNavView();
        initViewPager();


        // Inflate the layout for this fragment
        return root;
    }

    public void initBottomNavView(){
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    public void initViewPager(){
        viewPager.setOffscreenPageLimit(3);
        fragments = new ArrayList<>();

        fragments.add(fragmentChat);
        fragments.add(fragmentFriend);
        fragments.add(fragmentPlay);
        viewPager.setAdapter(new SectionPagerAdapter(getChildFragmentManager(), fragments));

        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(0);
    }




    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        viewPager.setCurrentItem(position);
        switch (position){
            case 0:
                bottomNavigationView.setSelectedItemId(R.id.bottom_chat);
                break;
            case 1:
                bottomNavigationView.setSelectedItemId(R.id.bottom_friend);
                break;
            case 2:
                bottomNavigationView.setSelectedItemId(R.id.bottom_play);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.bottom_chat:
                viewPager.setCurrentItem(0);
                return true;
            case R.id.bottom_friend:
                viewPager.setCurrentItem(1);
                return true;
            case R.id.bottom_play:
                viewPager.setCurrentItem(2);
                return true;
        }
        return false;
    }
}