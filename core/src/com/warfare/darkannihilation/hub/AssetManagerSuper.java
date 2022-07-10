package com.warfare.darkannihilation.hub;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.I18NBundleLoader;
import com.badlogic.gdx.assets.loaders.I18NBundleLoader.I18NBundleParameter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;
import com.warfare.darkannihilation.utils.AnimationSuper;
import com.warfare.darkannihilation.utils.Image;
import com.warfare.darkannihilation.utils.ImageAtlas;
import com.warfare.darkannihilation.utils.audio.MusicWrap;
import com.warfare.darkannihilation.utils.audio.SoundWrap;

import java.util.Locale;

public class AssetManagerSuper extends AssetManager {
    private final boolean MUTE = true;

    public final FileHandleResolver resolver = getFileHandleResolver();

    public AssetManagerSuper() {
        Texture.setAssetManager(this);
    }

    public void loadAtlas(String path) {
        load(path, TextureAtlas.class);
    }

    public void loadSounds(String... paths) {
        for (String path : paths) {
            load(path, Sound.class);
        }
    }

    public SoundWrap getSound(String path, float volume) {
        if (MUTE) volume = 0;
        return new SoundWrap(get(path, Sound.class), volume);
    }

    public void loadMusic(String path) {
        load(path, Music.class);
    }

    public MusicWrap getMusic(String path, float volume) {
        if (MUTE) volume = 0;
        return new MusicWrap(get(path, Music.class), volume);
    }

    public ImageAtlas getAtlas(String name) {
        return new ImageAtlas(get(name));
    }

    public AnimationSuper getAnimation(ImageAtlas atlas, String name, float frameDuration, float scale) {
        Array<Image> images = new Array<>(10);
        int i = 0;
        while (true) {
            Image image = atlas.getImage(name, i, scale);
            if (image == null) {
                break;
            }
            images.add(image);
            i++;
        }

        return new AnimationSuper(images.toArray(Image.class), frameDuration);
    }

    public AnimationSuper getAnimation(ImageAtlas atlas, String name, float frameDuration) {
        return getAnimation(atlas, name, frameDuration, 1);
    }

    @Override
    public synchronized void unload(String fileName) {
        if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
            Gdx.app.postRunnable(() -> super.unload(fileName));
        } else {
            super.unload(fileName);
        }
    }
}
