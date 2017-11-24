package com.example.mshd.argame;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;


//CameraView 위에 위치하며 각종 몬스터 출몰등의 게임 화면 처리
public class GameView extends View {
    Context context;
    Unit u=null;
    boolean inProcess = false;
    int ax = 10;
    int ay = 10;
    int width;
    int height;

    int interval = 10;

    MediaPlayer mediaPlayer;



    class GameViewHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (u != null) {
                if (u.getX() > width || u.getX() < 0) {
                    ax = -ax;
                    u.setVisible_time(u.getVisible_time() - 1);
                    Log.d("%%%%%%%%%%%%%%", u.getName() + "이  x축 벗어남, 재생time 1감소 현재는 = " + u.getVisible_time());
                    Log.d("%%%%%%%%%%%%%%", u.getName() + "의 현재 x 축 위치 = " + u.getX());
                }
                if (u.getY() > height || u.getY() < 0) {
                    ay = -ay;
                    u.setVisible_time(u.getVisible_time() - 1);
                    Log.d("%%%%%%%%%%%%%%", u.getName() + "이  y축 벗어남, 재생time 1감소 현재는 = " + u.getVisible_time());
                    Log.d("%%%%%%%%%%%%%%", u.getName() + "의 현재 y 축 위치 = " + u.getY());
                }
                u.setX(u.getX() + ax);
                u.setY(u.getY() + ay);
                invalidate();
                sendEmptyMessageDelayed(0, 10);
            }

        }
    }

    GameViewHandler handler = new GameViewHandler();

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight() / 2;
        Log.d("%%%%%%%%%%%", "width = " + width + " height = " + height);

        mediaPlayer = MediaPlayer.create(context, R.raw.click);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (u != null) {
            int x1 = (int) event.getX();
            int y1 = (int) event.getY();

            int a = u.getX() - x1;
            int c = u.getY() - y1;
            int b = (int) Math.sqrt(Math.pow(a, 2) + Math.pow(c, 2));
            if (b < 70) { //선택 판정
                //몬스터 수집 처리, 수집음 재생
                mediaPlayer.start();
                u.setVisible_time(0);
                u = null;
            }
        }
        return super.onTouchEvent(event);
    }

    public void showUnit(Unit u) {
        if (u != null) {
            this.u = u;
            handler.sendEmptyMessage(0);
        }else {
            u.setVisible_time(0);
        }
        Log.d("%%%%%%%%%%%%%%", "showUnit()");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setTextSize(70);
        if(u != null) {
            if (u.getVisible_time() > 0) {
                canvas.drawText(u.getName(), u.getX(), u.getY(), paint);
            } else {
                u.setX(0);
                u.setY(0);
                u = null;
                Log.d("%%%%%%%%%%%%%%", "unit_view.remove(u)");
            }
        }
//            canvas.drawText(visibleUnit.getName(), 100, 100, paint);
    }
}

//
//    ObjectAnimator oa = ObjectAnimator.ofInt(canvas, "width", (int)canvas.getWidth(), (int)canvas.getWidth()+20);
//oa.setDuration(500);
//        oa.start();
