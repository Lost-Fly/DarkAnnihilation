package ru.warfare.darkannihilation;

import android.graphics.Bitmap;

public class BulletEnemyOrbit extends Sprite {
    private static final Vector vector = new Vector();
    private final Bitmap image;
    private float X;
    private float Y;
    private float fly;
    private double deg;

    public BulletEnemyOrbit(Object[] info) {
        super((Game) info[0], ImageHub.bulletEnemyImage.getWidth(), ImageHub.bulletEnemyImage.getHeight());

        damage = 1;
        isPassive = true;
        isBullet = true;

        X = (int) info[1];
        Y = (int) info[2];
        deg = (double) info[4];
        fly = (float) info[5];
        image = (Bitmap) info[7];

        left = (int) X;
        top = (int) Y;
        right = (int) (X + width);
        bottom = (int) (Y + height);

        vector.makeVector((int) X, (int) Y, (int) X, 0, (double) info[6]);
    }

    @Override
    public int centerX() {
        return (int) (X + halfWidth);
    }

    @Override
    public int centerY() {
        return (int) (Y + halfHeight);
    }

    @Override
    public Sprite getRect() {
        right += X - left;
        bottom += Y - top;
        left = (int) X;
        top = (int) Y;
        return this;
    }

    @Override
    public void intersection() {
        createSmallExplosion();
        Game.bullets.remove(this);
        Game.allSprites.remove(this);
    }

    @Override
    public void update() {
        double[] speeds = vector.rotateVector(deg);

        X += (speeds[0] + game.player.speedX);
        Y += (speeds[1] + game.player.speedY);

        deg += (0.035 - fly);
        fly += 0.000007;
    }

    @Override
    public void render () {
        Game.canvas.drawBitmap(ImageHub.rotateImage(image, -Vector.getAngle(X, Y,
                game.player.x, game.player.y)), X, Y, null);
    }
}