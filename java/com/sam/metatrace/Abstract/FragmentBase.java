package com.sam.metatrace.Abstract;

import android.os.Handler;

import androidx.fragment.app.Fragment;


public abstract class FragmentBase extends Fragment {
    public static Handler mHandler;
    public static void setmHandler(Handler handler){
        mHandler = handler;
    }
}
