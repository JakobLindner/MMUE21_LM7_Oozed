package io.itch.jaknak72.oozed.game.resources;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;

import androidx.core.content.res.ResourcesCompat;

import java.util.HashMap;

import io.itch.jaknak72.oozed.R;

/**
 * Don't keep this on Activity change
 * Loads and stores all sprite bitmaps, gameplay music & sounds
 * @author jakob & simon
 */
public class ResourceSystem {
    private static final String TAG = "Resources";

    private Context context;
    private BitmapFactory.Options options;

    private HashMap<Integer, Bitmap> bitmaps = null;
    private static HashMap<SpriteEnum, SpriteInfo> spriteInfos = new HashMap<>();

    private HashMap<Sound,Integer> soundIds = new HashMap<>();

    private Typeface font;

    /**
     * true if resources are loaded
     */
    private boolean isLoaded = false;

    public ResourceSystem(Context context) {
        this.context = context;

        // disable scaling for crispy pixel art
        options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
    }

    /**
     * loads all resources and stores them for faster access
     */
    public void loadResources() {
        //do nothing if already loaded
        if (isLoaded)
            return;

        //load bitmaps
        bitmaps = new HashMap<>();
        bitmaps.put(R.drawable.ooze, BitmapFactory.decodeResource(context.getResources(), R.drawable.ooze, options));
        bitmaps.put(R.drawable.blocker, BitmapFactory.decodeResource(context.getResources(), R.drawable.blocker, options));
        bitmaps.put(R.drawable.flyer, BitmapFactory.decodeResource(context.getResources(), R.drawable.flyer, options));
        bitmaps.put(R.drawable.platforms, BitmapFactory.decodeResource(context.getResources(), R.drawable.platforms, options));
        bitmaps.put(R.drawable.background, BitmapFactory.decodeResource(context.getResources(), R.drawable.background, options));
        bitmaps.put(R.drawable.effects,BitmapFactory.decodeResource(context.getResources(),R.drawable.effects,options));
        bitmaps.put(R.drawable.ui, BitmapFactory.decodeResource(context.getResources(),R.drawable.ui,options));
        bitmaps.put(R.drawable.heart, BitmapFactory.decodeResource(context.getResources(),R.drawable.heart,options));
        bitmaps.put(R.drawable.jumper, BitmapFactory.decodeResource(context.getResources(),R.drawable.jumper,options));

        //load sounds
        for(Sound sound : Sound.values())
            soundIds.put(sound,SoundSystem.get().loadSound(sound.resId));

        font = ResourcesCompat.getFont(context,R.font.monogram);

        isLoaded = true;
    }

    public void releaseResources() {
        //do nothing if resources are not loaded
        if (!isLoaded)
            return;

        for (int key : bitmaps.keySet()) {
            bitmaps.get(key).recycle();
        }

        //unload sounds
        for(Integer soundId : soundIds.values())
            SoundSystem.get().unloadSound(soundId);

        isLoaded = false;
    }

    /**
     * returns the bitmap related to resource id
     * loadResources must be called before this!
     */
    public Bitmap getBitmap(int id) {
        return bitmaps.get(id);
    }

    /**
     * provides info to find sprites and animations on sprite sheet
     */
    public static SpriteInfo spriteInfo(SpriteEnum spriteEnum) {
        if (spriteInfos.containsKey(spriteEnum)) {
            return spriteInfos.get(spriteEnum);
        }
        SpriteInfo info = new SpriteInfo();

        switch (spriteEnum) {
            case oozeRun:
                info.spriteSheetId = R.drawable.ooze;
                info.animationLength = 2;
                info.frameDuration = 15;
                break;
            case blockerIdle:
                info.spriteSheetId = R.drawable.blocker;
                info.animationLength = 6;
                info.frameDuration = 10;
                break;
            case blockerRun:
                info.spriteSheetId = R.drawable.blocker;
                info.animationLength = 6;
                info.frameDuration = 10;
                info.firstY = 16;
                break;
            case flyerIdle:
                info.spriteSheetId = R.drawable.flyer;
                info.animationLength = 6;
                info.frameDuration = 4;
                break;
            case jumperIdle:
                info.spriteSheetId = R.drawable.jumper;
                info.animationLength = 6;
                info.frameDuration = 8;
                break;
            case jumperJump:
                info.spriteSheetId = R.drawable.jumper;
                info.animationLength = 6;
                info.frameDuration = 8;
                info.firstY = 16;
                break;
            case platformPlate:
                info.spriteSheetId = R.drawable.platforms;
                info.firstX = 112;
                break;
            case platformPipeCross:
                info.spriteSheetId = R.drawable.platforms;
                info.firstX = 48;
                info.firstY = 64;
                break;
            case platformPipeOpen:
                info.spriteSheetId = R.drawable.platforms;
                info.firstX = 48;
                info.firstY = 80;
                break;
            case platformPipeHor:
                info.spriteSheetId = R.drawable.platforms;
                info.firstX = 32;
                info.firstY = 64;
                break;
            case platformPipeVer:
                info.spriteSheetId = R.drawable.platforms;
                info.firstX = 32;
                info.firstY = 80;
                break;
            case platformPipeDR:
                info.spriteSheetId = R.drawable.platforms;
                info.firstY = 64;
                break;
            case platformPipeDL:
                info.spriteSheetId = R.drawable.platforms;
                info.firstX = 16;
                info.firstY = 64;
                break;
            case platformPipeUR:
                info.spriteSheetId = R.drawable.platforms;
                info.firstY = 80;
                break;
            case platformPipeUL:
                info.spriteSheetId = R.drawable.platforms;
                info.firstX = 16;
                info.firstY = 80;
                break;
            case platformCircuit:
                info.spriteSheetId = R.drawable.platforms;
                info.firstX = 112;
                info.firstY = 32;
                break;
            case platformMonitor:
                info.spriteSheetId = R.drawable.platforms;
                info.firstX = 112;
                info.firstY = 48;
                break;
            case platformIce:
                info.spriteSheetId = R.drawable.platforms;
                info.firstX = 112;
                info.firstY = 64;
                break;
            case platformBigGears:
                info.spriteSheetId = R.drawable.platforms;
                info.firstX = 32;
                info.size = 32;
                break;
            case platformBigPlate:
                info.spriteSheetId = R.drawable.platforms;
                info.size = 32;
                break;
            case platformBigPipes:
                info.spriteSheetId = R.drawable.platforms;
                info.firstY = 32;
                info.size = 32;
                break;
            case platformBigCross:
                info.spriteSheetId = R.drawable.platforms;
                info.firstX = 32;
                info.firstY = 32;
                info.size = 32;
                break;
            case platformHugePlate:
                info.spriteSheetId = R.drawable.platforms;
                info.firstX = 64;
                info.size = 48;
                break;
            case platformHugeSphere:
                info.spriteSheetId = R.drawable.platforms;
                info.firstX = 64;
                info.firstY = 48;
                info.size = 48;
                break;
            case background:
                info.spriteSheetId = R.drawable.background;
                info.size = 512;
                break;
            case spikes:
                info.spriteSheetId = R.drawable.platforms;
                info.firstX = 112;
                info.firstY = 16;
                break;
            case disappearEffect:
                info.spriteSheetId = R.drawable.effects;
                info.animationLength = 6;
                info.frameDuration = 5;
                break;
            case heart:
                info.spriteSheetId = R.drawable.heart;
                break;
            case muted:
                info.spriteSheetId = R.drawable.ui;
                info.firstX = 80;
                info.size = 48;
                break;
            case notMuted:
                info.spriteSheetId = R.drawable.ui;
                info.firstX = 32;
                info.size = 48;
                break;
            case pause:
                info.spriteSheetId = R.drawable.ui;
                break;
            case resume:
                info.spriteSheetId = R.drawable.ui;
                info.firstX = 48;
                info.firstY = 48;
                info.size = 48;
                break;
            case house:
                info.spriteSheetId = R.drawable.ui;
                info.firstY = 48;
                info.size = 48;
                break;
            default:
                //Log.i(TAG, String.format("spriteInfo: Sprite %s not found!",spriteEnum.toString()));
                return info;
        }
        spriteInfos.put(spriteEnum, info);
        return info;
    }

    public void playSound(Sound sound) {
        if(soundIds.containsKey(sound))
            SoundSystem.get().playSound(soundIds.get(sound));
    }

    public Typeface getFont() {
        return font;
    }

    public enum SpriteEnum {
        oozeRun,
        blockerIdle,
        blockerRun,
        flyerIdle,
        jumperIdle,
        jumperJump,
        platformPlate, // NONE = 1x1
        platformPipeCross,
        platformPipeOpen,
        platformPipeHor,
        platformPipeVer,
        platformPipeDR, // connects: D = down, U = up, R = right, L = left
        platformPipeDL,
        platformPipeUR,
        platformPipeUL,
        platformCircuit,
        platformMonitor,
        platformIce,
        platformBigPlate, // BIG = 2x2
        platformBigGears,
        platformBigPipes,
        platformBigCross,
        platformHugePlate, // HUGE = 3x3
        platformHugeSphere,
        background,
        spikes,
        disappearEffect,
        heart,
        muted,
        notMuted,
        pause,
        resume,
        house
    }

    /**
     * Gameplay sound list with resource id
     */
    public enum Sound {
        OOZE_JUMP(R.raw.ooze_jump),
        PLAYER_DEATH(R.raw.player_death),
        PLAYER_DASH(R.raw.player_dash),
        ENEMY_DEATH(R.raw.enemy_death),
        LEVEL_CLEAR(R.raw.level_clear),
        BUTTON(R.raw.button);

        private int resId;
        Sound(int resId) {
            this.resId = resId;
        }
    }
}
