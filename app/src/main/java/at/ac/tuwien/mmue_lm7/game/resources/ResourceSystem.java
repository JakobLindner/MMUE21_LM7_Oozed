package at.ac.tuwien.mmue_lm7.game.resources;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.util.HashMap;
import java.util.MissingResourceException;

import at.ac.tuwien.mmue_lm7.R;

/**
 * @author jakob
 * Don't keep this on Activity change
 * Loads and stores all sprite bitmaps
 */
public class ResourceSystem {

    private Context context;
    private BitmapFactory.Options options;

    private HashMap<Integer, Bitmap> bitmaps = null;
    private static HashMap<SpriteEnum, SpriteInfo> spriteInfos = new HashMap<>();

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
        
        bitmaps = new HashMap<>();
        bitmaps.put(R.drawable.ooze, BitmapFactory.decodeResource(context.getResources(), R.drawable.ooze, options));
        bitmaps.put(R.drawable.blocker, BitmapFactory.decodeResource(context.getResources(), R.drawable.blocker, options));
        bitmaps.put(R.drawable.platforms, BitmapFactory.decodeResource(context.getResources(), R.drawable.platforms, options));
        bitmaps.put(R.drawable.background, BitmapFactory.decodeResource(context.getResources(), R.drawable.background, options));

        isLoaded = true;
    }

    public void releaseResources() {
        //do nothing if resources are not loaded
        if (!isLoaded)
            return;

        for (int key : bitmaps.keySet()) {
            bitmaps.get(key).recycle();
        }

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
                info.animationLength = 4;
                info.frameDuration = 20;
                break;
            case platformPipe:
                info.spriteSheetId = R.drawable.platforms;
                break;
            case platformPipeOpen:
                info.spriteSheetId = R.drawable.platforms;
                info.firstX = 16;
                break;
            case platformCircuit:
                info.spriteSheetId = R.drawable.platforms;
                info.firstX = 32;
                break;
            case platformIce:
                info.spriteSheetId = R.drawable.platforms;
                info.firstX = 48;
                break;
            case bigPlatformGears:
                info.spriteSheetId = R.drawable.platforms;
                info.firstY = 16;
                info.size = 32;
                break;
            case background:
                info.spriteSheetId = R.drawable.background;
                info.size = 512;
                break;
            default:
                Log.i("Resources", "spriteInfo: Sprite not found!");
                return info;
        }
        spriteInfos.put(spriteEnum, info);
        return info;
    }

    public enum SpriteEnum {
        oozeRun,
        blockerIdle,
        platformPipe,
        platformPipeOpen,
        platformCircuit,
        platformIce,
        bigPlatformGears,
        background,
    }
}
