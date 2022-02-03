package com.warfare.darkannihilation.systemd.gameover;

import static com.warfare.darkannihilation.systemd.service.Windows.HALF_SCREEN_HEIGHT;
import static com.warfare.darkannihilation.systemd.service.Windows.HALF_SCREEN_WIDTH;
import static com.warfare.darkannihilation.systemd.service.Windows.SCREEN_HEIGHT;
import static com.warfare.darkannihilation.systemd.service.Windows.SCREEN_WIDTH;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.warfare.darkannihilation.abstraction.sprite.BaseSprite;
import com.warfare.darkannihilation.hub.FontHub;
import com.warfare.darkannihilation.utils.FontWrap;

public class GameOverScreen extends BaseSprite {
    private final FontWrap fontFinger;
    private final float textFingerX, textFingerY;
    private static final String TEXT_FINGER = "Tap four or more fingers to restart";

    private final FontWrap fontDead;
    private static final String YOU_DEAD = "You Dead";
    private final float textDeadX, textDeadY;

    public GameOverScreen(TextureAtlas.AtlasRegion texture, FontHub fontHub) {
        super(texture, SCREEN_WIDTH, SCREEN_HEIGHT);

        fontDead = FontWrap.scaledFontWrap(fontHub.fiendish, HALF_SCREEN_WIDTH + 150, YOU_DEAD);
        textDeadX = HALF_SCREEN_WIDTH - fontDead.getTextWidth(YOU_DEAD) / 2f;
        textDeadY = HALF_SCREEN_HEIGHT + fontDead.getTextHeight(YOU_DEAD) / 1.4f;

        fontFinger = FontWrap.scaledFontWrap(fontHub.canisMinor, HALF_SCREEN_WIDTH - 100, TEXT_FINGER);
        textFingerX = HALF_SCREEN_WIDTH - fontFinger.getTextWidth(TEXT_FINGER) / 2f;
        textFingerY = HALF_SCREEN_HEIGHT / 4f + fontFinger.getTextHeight(TEXT_FINGER) / 2f;
    }

    @Override
    public void update() {

    }

    @Override
    public void render() {
        super.render();

        fontDead.draw(textDeadX, textDeadY, YOU_DEAD);
        fontFinger.draw(textFingerX, textFingerY, TEXT_FINGER);
    }
}
