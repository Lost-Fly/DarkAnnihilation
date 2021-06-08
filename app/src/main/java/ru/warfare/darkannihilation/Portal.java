package ru.warfare.darkannihilation;

public class Portal extends Sprite {
    private int frame = 0;
    private float frameTime = 100;
    private long lastFrame;
    public boolean touch = false;

    public Portal(Game game) {
        super(game, ImageHub.portalImages[0].getWidth(), ImageHub.portalImages[0].getHeight());

        x = randInt(0, Game.screenWidth);
        y = 100;
        isPassive = true;

        recreateRect(x + 15, y + 15, right() - 15, bottom() - 15);

        lastFrame = System.currentTimeMillis();

        AudioHub.portalSound.seekTo(0);
        AudioHub.portalSound.start();
    }

    public void kill() {
        ImageHub.deletePortalImages();
        game.portal = null;
    }

    @Override
    public Sprite getRect() {
        return goTO(x + 15, y + 15);
    }

    @Override
    public void intersectionPlayer() {
        if (Game.level == 2) {
            ImageHub.loadWinImages(game.context);
            game.generateWin();
            kill();
        } else {
            AudioHub.timeMachineFirstSnd.start();
            AudioHub.timeMachineNoneSnd.start();

            touch = true;
            game.player.god = true;
            frameTime = 0;
            game.player.lock = true;
            ImageHub.loadSecondLevelImages(game.context);
        }
    }

    @Override
    public void update() {
        long now = System.currentTimeMillis();
        if (now - lastFrame > frameTime) {
            lastFrame = now;
            if (frame < ImageHub.portalImages.length - 1) {
                frame += 1;
            } else {
                frame = 0;
            }
            if (!AudioHub.portalSound.isPlaying()) {
                if (!touch) {
                    if (Game.gameStatus != 7) {
                        Game.gameStatus = 0;
                        game.portal = null;
                        AudioHub.resumeBackgroundMusic();
                    }
                }
            }
        }
        if (!AudioHub.timeMachineNoneSnd.isPlaying() & touch) {
            AudioHub.timeMachineSecondSnd.start();
            Game.level++;
            game.loadingScreen.newJob("newGame");
            kill();
        }
    }

    @Override
    public void render() {
        Game.canvas.drawBitmap(ImageHub.portalImages[frame], x, y, null);
    }
}
