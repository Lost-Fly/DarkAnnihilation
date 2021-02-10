package ru.startandroid.surfacedemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

public class Game extends SurfaceView implements Runnable, SurfaceHolder.Callback {

    SurfaceHolder holder;
    Thread thread;
    Canvas canvas;
    SpriteGroup spriteGroup = new SpriteGroup(canvas);

    public int screenWidth;
    public int screenHeight;
    private Paint textPaint = new Paint();
    private volatile boolean playing = false;
    private int fps;
    private static final int MILLIS_IN_SECOND = 1000000000;
    private long timeFrame;
    public Player player;
    public final Vader[] vaders = new Vader[12];
    public Screen screen;
    private final int vaderNumbers = vaders.length;

    public Game(Context context, AttributeSet attrs) {
        super(context, attrs);

        holder = getHolder();
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(40);
        screen = new Screen(context);
        SpriteGroup.append(screen);
        player = new Player(context);
        SpriteGroup.append(player);
        for (int i = 0; i < vaderNumbers; i++) {
            vaders[i] = new Vader(context);
            SpriteGroup.append(vaders[i]);
        }
    }

    private void draw() {
        if (holder.getSurface().isValid()) {
            canvas = holder.lockCanvas();
            timeFrame = System.nanoTime();
            screen.update(canvas);

            screen.x -= player.speedX / 3;

            for (int i = 0; i < vaderNumbers; i++) {
                player.bullet[i].update(canvas, player);
                player.bullet[i].x -= player.speedX / 3;
                vaders[i].check_intersection(player.x, player.y, player.width, player.height);
                for (int j = 0; j < vaderNumbers; j++) {
                    vaders[i].check_intersection(player.bullet[j].x, player.bullet[j].y, player.bullet[j].width, player.bullet[j].height);
                }
                vaders[i].x -= player.speedX / 3;
                vaders[i].update(canvas);
            }
            player.update(canvas);

//            SpriteGroup.update();

            fps = (int) (MILLIS_IN_SECOND / (System.nanoTime() - timeFrame));
            canvas.drawText("FPS: " + fps, 50, 50, textPaint);

            timeFrame = System.nanoTime();
            holder.unlockCanvasAndPost(canvas);
        }
    }

    public void pause() {
        playing = false;
        try {
            thread.join();
        } catch(Exception e) {
            Log.e("Error: ", "thread join");
        }
    }

    public void resume() {
        playing = true;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        player.start();
        while(playing) {
            draw();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        player.endX = event.getX() - player.width / 2;
        player.endY = event.getY()  - player.height / 2;

        return true;
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        resume();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        pause();
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        pause();
    }

    public void setScreenSizes(int width, int height) {
        screenWidth = width;
        screenHeight = height;
        player.setCoords(width, height);
        for (int i = 0; i < vaders.length; i++) {
            vaders[i].setCoords(width, height);
            player.bullet[i].setCoords(width, height);
        }
        screen.setCoords(width, height);
    }
}
