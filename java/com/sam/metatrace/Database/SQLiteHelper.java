package com.sam.metatrace.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.sam.metatrace.MainPage;
import com.sam.metatrace.Protocal.ChatMsg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String ID = "id";
    public static final String TABLE_NAME = "chat_message";
    public static final String SENDER_ID = "send_user_id";
    public static final String MSG = "msg";
    public static final String CREATE_TIME = "create_time";

    public SQLiteHelper(@Nullable Context context, int version) {
        //             数据库名字  null  1
        super(context, "metatrace.db", null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 只会在没有数据库的时候调用一次
        String sql = "CREATE TABLE " + TABLE_NAME + " (" + ID + " varchar(40) primary key , " + SENDER_ID + " varchar(10), " + MSG + " varchar(1024),"
        + CREATE_TIME + " datetime)";
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 只在version版本号更新的时候运行
        if (oldVersion < newVersion){
            String sql = "CREATE TABLE " + TABLE_NAME + " (" + ID + " varchar(40) primary key , " + SENDER_ID + " varchar(10), " + MSG + " varchar(1024),"
                    + CREATE_TIME + " datetime)";
            db.execSQL(sql);
        }
    }

    public int deleteAll(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        int delete = sqLiteDatabase.delete(TABLE_NAME, "1=1", null);
        sqLiteDatabase.close();
        // 如果返回值是0，则没有删除
        return delete;
    }

    public int addOne(String id, String send_user_id, String msg, Long create_time){

        ContentValues cv = new ContentValues();
        cv.put(ID, id);
        cv.put(SENDER_ID, send_user_id);
        cv.put(MSG, msg);
        cv.put(CREATE_TIME, create_time);

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        long insert = sqLiteDatabase.insert(TABLE_NAME, SENDER_ID, cv);
        // 如果是-1则添加失败
        sqLiteDatabase.close();

        return (int)insert;
    }

    public int deleteAllByUser(String send_user_id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        int delete = sqLiteDatabase.delete(TABLE_NAME, SENDER_ID + "=? or " + SENDER_ID +"=?", new String[]{send_user_id, "I"+send_user_id});
        sqLiteDatabase.close();
        // 如果返回值是0，则没有删除
        return delete;
    }

    public int updateOne(String send_user_id, String msg, Long create_time){
        ContentValues cv = new ContentValues();
        cv.put(SENDER_ID, send_user_id);
        cv.put(MSG, msg);
        cv.put(CREATE_TIME, create_time);

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        long update = sqLiteDatabase.update(TABLE_NAME, cv, SENDER_ID + "=?", new String[]{send_user_id});
        // 如果是0则没有更新
        sqLiteDatabase.close();

        return (int)update;
    }

    /**
     * 将远程http图像下载消息转换为本地图片链接
     * @param originalMsg 原始下载链接
     * @param newMsg 新的本地图片链接
     */
    public void updateImageDatabaseMessage(String originalMsg, String newMsg){
        ContentValues cv = new ContentValues();
        cv.put(MSG, newMsg);
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        long update = sqLiteDatabase.update(TABLE_NAME, cv, MSG+"=?", new String[]{originalMsg});
        sqLiteDatabase.close();
    }

    public List<String> getAll(){
        String sql = "select * from " + TABLE_NAME;
        List<String> result = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);

        int idIdx = cursor.getColumnIndex(ID);
        int nameIdx = cursor.getColumnIndex(SENDER_ID);
        int descIdx = cursor.getColumnIndex(MSG);
        int time = cursor.getColumnIndex(CREATE_TIME);

        while(cursor.moveToNext()){
            String s = cursor.getString(idIdx) + " " + cursor.getString(nameIdx) + " "
                    + cursor.getString(descIdx) + ' ' + cursor.getString(time);
            result.add(s);
        }
        sqLiteDatabase.close();

        return result;
    }

    /**
     * 获取全部发生了聊天的用户名和最后一条消息
     * @return 集合，键：对方的用户名【如果是本人向对方发送的消息则用户名前有I字符】, 值：列表【消息内容String， 消息时间long】
     */
    public Map<String, List<Object>> getChatUsernamesAndLastMsg(){
        Map<String, List<Object>> result = new HashMap<>();

        String sql = "select " + SENDER_ID +", " + MSG + ", max("+ CREATE_TIME +") as create_time" + " from " + TABLE_NAME + " group by " + SENDER_ID;

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        int sendUserIdx = cursor.getColumnIndex(SENDER_ID);
        int msgIdx = cursor.getColumnIndex(MSG);
        int timeIdx = cursor.getColumnIndex(CREATE_TIME);
        while (cursor.moveToNext()){
            String sendUserId = cursor.getString(sendUserIdx);
            String msg = cursor.getString(msgIdx);
            long createTime = cursor.getLong(timeIdx);
            List<Object> t = new ArrayList<>();
            t.add(msg);
            t.add(createTime);
            result.put(sendUserId, t);
        }

        return result;
    }

    public List<ChatMsg> getAllBySenderName(String send_user_id){
        String sql = "select * from " + TABLE_NAME + " where " + SENDER_ID + "=? or " + SENDER_ID + " =?";

        List<ChatMsg> result = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(sql, new String[]{send_user_id, "I"+send_user_id});

        int idIdx = cursor.getColumnIndex(ID);
        int nameIdx = cursor.getColumnIndex(SENDER_ID);
        int descIdx = cursor.getColumnIndex(MSG);
        int time = cursor.getColumnIndex(CREATE_TIME);

        while(cursor.moveToNext()){
            ChatMsg chatMsg = new ChatMsg(
                    cursor.getString(nameIdx),
                    MainPage.username,
                    cursor.getString(descIdx),
                    cursor.getString(time)
            );

            result.add(chatMsg);
        }
        sqLiteDatabase.close();

        return result;
    }
}
