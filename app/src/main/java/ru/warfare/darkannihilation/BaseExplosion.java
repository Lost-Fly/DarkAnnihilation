package ru.warfare.darkannihilation;

import android.graphics.Bitmap;

public class BaseExplosion extends Sprite {
    public Bitmap[] img;
    public int frame = 0;

    public BaseExplosion(int w, int h) {
        super(w, h);
        isPassive = true;
        isBullet = true;
    }

    public void start(int X, int Y) {
        x = X - halfWidth;
        y = Y - halfHeight;
        lock = false;
    }

    public void stop() {
        lock = true;
        frame = 0;
    }
}
