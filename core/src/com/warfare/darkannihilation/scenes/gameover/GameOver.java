package com.warfare.darkannihilation.scenes.gameover;

import static com.badlogic.gdx.math.MathUtils.random;
import static com.badlogic.gdx.math.MathUtils.randomBoolean;
import static com.warfare.darkannihilation.constants.Names.HUGE_EXPLOSION;
import static com.warfare.darkannihilation.constants.Names.MEDIUM_EXPLOSION_TRIPLE;
import static com.warfare.darkannihilation.constants.Names.SMALL_EXPLOSION_DEFAULT;
import static com.warfare.darkannihilation.hub.Resources.getPools;
import static com.warfare.darkannihilation.systemd.service.Windows.SCREEN_HEIGHT;
import static com.warfare.darkannihilation.systemd.service.Windows.SCREEN_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.warfare.darkannihilation.Explosion;
import com.warfare.darkannihilation.abstraction.Scene;
import com.warfare.darkannihilation.abstraction.sprite.Opponent;
import com.warfare.darkannihilation.bullet.BaseBullet;
import com.warfare.darkannihilation.pools.ExplosionPool;
import com.warfare.darkannihilation.systemd.MainGameManager;
import com.warfare.darkannihilation.utils.GameTask;

import java.util.Iterator;

public class GameOver extends Scene {
    private final GameTask gameTask1 = new GameTask(this::boomInRandomArea, 50);
    private final GameTask gameTask2 = new GameTask(this::boomInRandomArea, 100);
    private final GameTask gameTask3 = new GameTask(this::killSprites, 150);

    private final ExplosionPool explosionPool = getPools().explosionPool;

    private final Array<Opponent> empire;
    private final Array<BaseBullet> bulletsEnemy;
    private final Array<BaseBullet> bullets;
    private final Array<Explosion> explosions;

    public GameOver(MainGameManager mainGameManager, Array<Opponent> empire, Array<BaseBullet> bullets,
                    Array<BaseBullet> bulletsEnemy, Array<Explosion> explosions) {
        super(mainGameManager, new GameOverClickListener(mainGameManager));

        this.empire = empire;
        this.bullets = bullets;
        this.bulletsEnemy = bulletsEnemy;
        this.explosions = explosions;
    }

    @Override
    public void resume() {
        super.resume();
        gameTask1.start();
        gameTask2.start();
        gameTask3.start();
    }

    @Override
    public void pause() {
        super.pause();
        gameTask1.stop();
        gameTask2.stop();
        gameTask3.stop();
    }

    @Override
    public void update() {
        for (Opponent opponent : empire) {
            if (opponent.visible) {
                opponent.update();
            }
        }

        for (BaseBullet bullet : bulletsEnemy) {
            bullet.update();
        }

        for (BaseBullet bullet : bullets) {
            bullet.update();
        }

        for (Iterator<Explosion> iterator = explosions.iterator(); iterator.hasNext(); ) {
            Explosion explosion = iterator.next();
            if (!explosion.visible) {
                iterator.remove();
                explosionPool.free(explosion);
            } else {
                explosion.update();
                explosion.updateInThread();
            }
        }
    }

    private void obtainRandomExplosion(float x, float y) {
        explosionPool.obtain(x, y, (byte) -random(-MEDIUM_EXPLOSION_TRIPLE, -SMALL_EXPLOSION_DEFAULT));
    }

    private void boomInRandomArea() {
        for (int i = 0; i < 4; i++) {
            if (randomBoolean()) {
                obtainRandomExplosion(random(SCREEN_WIDTH), random(SCREEN_HEIGHT));
            }
        }

        if (randomBoolean(0.02857f)) {
            explosionPool.obtain(random(SCREEN_WIDTH), random(SCREEN_HEIGHT), HUGE_EXPLOSION);
        }
    }

    private void killSprites() {
        boomInRandomArea();

        killInArray(empire);
        killInArray(bullets);
        killInArray(bulletsEnemy);
    }

    private void killInArray(Array<?> sprites) {
        if (sprites.isEmpty()) {
            boomInRandomArea();
            return;
        }

        Gdx.app.postRunnable(() -> {
            Opponent opponent = (Opponent) sprites.pop();
            if (opponent.visible) opponent.kill();
        });
    }

    @Override
    public void render() {
    }

    @Override
    public void dispose() {
        super.dispose();
        gameTask1.shutdown();
        gameTask2.shutdown();
        gameTask3.shutdown();
    }
}
