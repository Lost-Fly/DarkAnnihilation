package com.warfare.darkannihilation.systemd.loading;

import static com.warfare.darkannihilation.systemd.Frontend.spriteBatch;
import static com.warfare.darkannihilation.systemd.service.Windows.SCREEN_HEIGHT;
import static com.warfare.darkannihilation.systemd.service.Windows.SCREEN_WIDTH;

import com.warfare.darkannihilation.abstraction.Scene;
import com.warfare.darkannihilation.hub.ImageHub;
import com.warfare.darkannihilation.systemd.MainGameManager;
import com.warfare.darkannihilation.utils.ClickListener;

public class Loading extends Scene {
    private final Runnable runnable;
    private float alpha = 1;
    private byte status = -1;

    public Loading(MainGameManager mainGameManager, Runnable runnable) {
        super(mainGameManager, new ClickListener(), ImageHub.loadingScreen);
        this.runnable = runnable;
    }

    @Override
    public void render() {
        screen.render();

        switch (status) {
            case -1:
                alpha -= 0.02;
                if (alpha < 0) {
                    alpha = 0;
                    status = 0;
                }
                break;
            case 0:
                if (mainGameManager.resourcesManager.update()) status = 1;
                return;
            case 1:
                alpha += 0.02f;

                if (alpha > 1) runnable.run();
        }

        spriteBatch.setColor(0, 0, 0, alpha);
        spriteBatch.draw(mainGameManager.imageHub.blackColor, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        spriteBatch.setColor(1, 1, 1, 1);
    }
}
