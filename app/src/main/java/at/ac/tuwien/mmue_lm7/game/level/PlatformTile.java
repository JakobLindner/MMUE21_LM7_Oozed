package at.ac.tuwien.mmue_lm7.game.level;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import at.ac.tuwien.mmue_lm7.game.ObjectFactories;
import at.ac.tuwien.mmue_lm7.game.objects.GameObject;
import at.ac.tuwien.mmue_lm7.game.resources.ResourceSystem;

public class PlatformTile extends Tile{
    private static final String TAG = "PlatformTile";
    public ResourceSystem.SpriteEnum sprite;

    @Override
    public void build(GameObject root) {
        if(sprite== ResourceSystem.SpriteEnum.platformBigGears || sprite == ResourceSystem.SpriteEnum.platformBigPlate)
            root.addChild(ObjectFactories.makeBigPlatformTile(x,y,rotation,mirrored,sprite));
        else
            root.addChild(ObjectFactories.makePlatformTile(x,y,rotation,mirrored, sprite));
    }

    @Override
    public void fromJSON(JSONObject json) throws JSONException {
        super.fromJSON(json);
        try {
            sprite = ResourceSystem.SpriteEnum.valueOf(tileName);
        }
        catch(IllegalArgumentException e) {
            Log.e(TAG, String.format("Unknown platform tile name: %s",tileName));
            sprite = ResourceSystem.SpriteEnum.platformBigGears;
        }
    }
}
