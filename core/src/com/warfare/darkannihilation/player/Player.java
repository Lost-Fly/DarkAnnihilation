package com.warfare.darkannihilation.player;

import static com.badlogic.gdx.math.MathUtils.roundPositive;
import static com.warfare.darkannihilation.constants.Constants.MILLENNIUM_FALCON_HEALTH;
import static com.warfare.darkannihilation.constants.Constants.MILLENNIUM_FALCON_SHOOT_TIME;
import static com.warfare.darkannihilation.constants.Names.FULL_HEART;
import static com.warfare.darkannihilation.constants.Names.HALF_HEART;
import static com.warfare.darkannihilation.constants.Names.NULL_HEART;
import static com.warfare.darkannihilation.systemd.service.Watch.time;
import static com.warfare.darkannihilation.systemd.service.Windows.HALF_SCREEN_HEIGHT;
import static com.warfare.darkannihilation.systemd.service.Windows.HALF_SCREEN_WIDTH;
import static com.warfare.darkannihilation.systemd.service.Windows.SCREEN_HEIGHT;

import com.badlogic.gdx.utils.Array;
import com.warfare.darkannihilation.Explosion;
import com.warfare.darkannihilation.abstraction.sprite.movement.MovementSprite;
import com.warfare.darkannihilation.bullet.Bullet;
import com.warfare.darkannihilation.hub.ImageHub;
import com.warfare.darkannihilation.utils.PoolWrap;
import com.warfare.darkannihilation.utils.audio.SoundWrap;

public class Player extends MovementSprite {
    private final Array<Heart> hearts = new Array<>(true, 20, Heart.class);
    private final SoundWrap sound;
    private final PoolWrap<Bullet> bulletPool;
    private final PoolWrap<Heart> armorPool;

    private float lastShot;
    private float endX, endY;
    private int heartX = 25;
    private int heartY = SCREEN_HEIGHT - 90;
    private final int mxHealthMinus5;

    public Player(ImageHub imageHub, SoundWrap sound, PoolWrap<Bullet> bulletPool, PoolWrap<Explosion> explosionPool) {
        super(explosionPool, imageHub.millenniumFalcon, MILLENNIUM_FALCON_HEALTH, 10000, 0);
        this.bulletPool = bulletPool;
        this.sound = sound;
        armorPool = new PoolWrap<Heart>(hearts) {
            @Override
            protected ArmorHeart newObject() {
                return new ArmorHeart(imageHub);
            }
        };

        mxHealthMinus5 = maxHealth - 5;
        health = maxHealth;

        int len = maxHealth / 10;
        for (int i = 0; i < len; i++) {
            hearts.add(new Heart(imageHub, heartX, heartY));

            heartX += 90;
            checkLevel(true);
        }

        shrinkBorders(25, 30, 25, 25);

        reset();
    }

    private void changeHearts() {
        final int len = health / 10;
        int bar;

        for (bar = 0; bar < len; bar++) hearts.get(bar).setType(FULL_HEART);

        if (health % 10 == 5) hearts.get(bar++).setType(HALF_HEART);

        while (bar < hearts.size) {
            Heart heart = hearts.get(bar);
            heart.setType(NULL_HEART);

            if (!heart.visible) {
                hearts.removeIndex(bar);
                armorPool.free(heart);

                heartX -= 90;
                checkLevel(false);
            } else bar++;
        }
    }

    public void heal(int value) {
        int len = roundPositive(value / 10f);

        if (value % 10 == 5 && health % 10 == 5) len--;

        addArmorHeart(len);
        health += value;
        changeHearts();
    }

    private void addArmorHeart(int times) {
        int hlth = health;

        for (int i = 1; i <= times; i++) {
            hlth += i * 10;

            if (hlth >= mxHealthMinus5) {
                ((ArmorHeart) armorPool.obtain()).start(heartX, heartY);
                heartX += 90;

                checkLevel(true);
            }
        }
    }

    private void checkLevel(boolean up) {
        if (up) {
            if (heartX > 385) {
                heartX = 25;
                heartY -= 70;
            }
        } else {
            if (heartX < 25) {
                heartX = 385;
                heartY += 70;
            }
        }
    }

    public void shoot() {
        if (time - lastShot >= MILLENNIUM_FALCON_SHOOT_TIME) {
            lastShot = time;

            float X = centerX();
            float Y = top() + 5;
            bulletPool.obtain().start(X - 7, Y);
            bulletPool.obtain().start(X, Y);
            bulletPool.obtain().start(X + 7, Y);

            sound.play();
        }
    }

    @Override
    public void reset() {
        x = HALF_SCREEN_WIDTH;
        y = HALF_SCREEN_HEIGHT;

        endX = (x -= halfWidth);
        endY = (y -= halfHeight);
    }

    public void setCoordinates(float X, float Y) {
        endX = X - halfWidth;
        endY = Y - halfHeight;
    }

    @Override
    public void update() {
        x += speedX;
        y += speedY;

        speedX = (endX - x) / 3f;
        speedY = (endY - y) / 3f;
    }

    @Override
    public boolean damage(int dmg) {
        health -= dmg;
        changeHearts();

        if (isDead() && visible) kill();
        return false;
    }

    @Override
    public void kill() {
        visible = false;
        explodeHuge();
    }

    public boolean isDead() {
        return health <= 0;
    }

    public void renderHearts() {
        for (Heart heart : hearts) heart.render();
    }

    @Override
    public void render() {
        if (visible) super.render();
    }
}
