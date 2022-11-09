package io.itch.jaknak72.oozed.game.level;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import io.itch.jaknak72.oozed.game.GameConstants;
import io.itch.jaknak72.oozed.game.ObjectFactories;
import io.itch.jaknak72.oozed.game.objects.GameObject;
import io.itch.jaknak72.oozed.game.resources.ResourceSystem;
import io.itch.jaknak72.oozed.game.resources.SpriteInfo;

/**
 * @author simon
 */
public class PlatformTile extends Tile{
    private static final String TAG = "PlatformTile";
    public ResourceSystem.SpriteEnum sprite = ResourceSystem.SpriteEnum.platformPipeOpen;

    @Override
    public void build(GameObject root, Context context) {
        SpriteInfo info = ResourceSystem.spriteInfo(sprite);
        int platformSize = info.size/ GameConstants.PIXELS_PER_UNIT;
        root.addChild(ObjectFactories.makePlatformTile(x,y,platformSize,platformSize,rotation,mirrored,sprite));
    }

    @Override
    public void fromJSON(JSONObject json) throws JSONException {
        super.fromJSON(json);
        try {
            sprite = ResourceSystem.SpriteEnum.valueOf(tileName);
        }
        catch(IllegalArgumentException e) {
            //Log.e(TAG, String.format("Unknown platform tile name: %s",tileName));
            sprite = ResourceSystem.SpriteEnum.platformBigGears;
        }
    }
}
