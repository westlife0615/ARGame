package com.example.mshd.argame;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * Created by Administrator on 2017-01-13.
 */

public class LogHandler extends Handler{
    Context con ;
    public LogHandler(Context con){
        this.con = con;
    }
    public void handleMessage(Message msg) {
        if (msg.what == 0) {
            Toast.makeText(con, "몬스터출현", Toast.LENGTH_SHORT).show();
        }else if (msg.what == 1) {
            Toast.makeText(con, "유닛 중복 생성",Toast.LENGTH_SHORT).show();
        }
    }
}
