package com.sam.metatrace.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sam.metatrace.Abstract.FragmentBase;
import com.sam.metatrace.R;
import com.sam.metatrace.SurfaceView.Game2.PlayGround;
import com.sam.metatrace.SurfaceView.SamEngine.Collisions.CollisionDetection;

public class Game2Fragment extends FragmentBase {

    public static Game2Fragment instance;

    private static SurfaceHolder mSurfaceHolder;
    private static PlayGround playGround;
    private static Thread surfaceViewThread;
    private static CollisionDetection collisionDetection;
    private static Thread collisionDetectionThread;

    public Game2Fragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(instance == null) instance = this;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // 启动游戏surface view
        if(playGround == null) playGround = new PlayGround(getContext());
        getActivity().setContentView(playGround);
        if(mSurfaceHolder == null) mSurfaceHolder = playGround.getSurfaceHolder();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (collisionDetectionThread == null){
            CollisionDetection.running = true;
            collisionDetection = new CollisionDetection();
            collisionDetectionThread = new Thread(collisionDetection);
            collisionDetectionThread.start();
        }

        playGround.onResume();
    }

    public static void notifySurfaceViewThreadStopped(){
        Game2Fragment.surfaceViewThread = null;
        Game2Fragment.collisionDetection = null;
        Game2Fragment.collisionDetectionThread = null;
    }



    @Override
    public void onStop() {
        super.onStop();
        CollisionDetection.running = false;
    }
}