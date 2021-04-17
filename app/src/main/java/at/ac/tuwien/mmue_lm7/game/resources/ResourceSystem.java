package at.ac.tuwien.mmue_lm7.game.resources;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.HashMap;

import at.ac.tuwien.mmue_lm7.R;

/**
 * Don't keep this on Activity change
 */
public class ResourceSystem {

    private Context context;
    private BitmapFactory.Options options;

    private HashMap<Integer, Bitmap> bitmaps = new HashMap<>();
    private HashMap<SpriteEnum, SpriteInfo> spriteInfos = new HashMap<>();

    public ResourceSystem (Context context) {
        this.context = context;

        // disable scaling for crispy pixel art
        options = new BitmapFactory.Options();
        options.inScaled = false;
    }

    public Bitmap getBitmap(int id) {
        if (bitmaps.containsKey(id)) {
            return bitmaps.get(id);
        }
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), id, options);
        bitmaps.put(id, bitmap);
        return bitmap;
    }

    public SpriteInfo spriteInfo(SpriteEnum spriteEnum) {
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
            case tileCenter:
                info.spriteSheetId = R.drawable.tile;
                break;
            default:
                return info;
        }
        spriteInfos.put(spriteEnum, info);
        return info;
    }

    public enum SpriteEnum {
        oozeRun,
        tileCenter,
    }
}
