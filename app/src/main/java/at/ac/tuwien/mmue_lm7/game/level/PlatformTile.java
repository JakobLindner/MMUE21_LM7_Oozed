package at.ac.tuwien.mmue_lm7.game.level;

import org.json.JSONException;
import org.json.JSONObject;

import at.ac.tuwien.mmue_lm7.game.ObjectFactories;
import at.ac.tuwien.mmue_lm7.game.objects.GameObject;
import at.ac.tuwien.mmue_lm7.game.resources.ResourceSystem;

public class PlatformTile extends Tile{
    public ResourceSystem.SpriteEnum sprite;

    @Override
    public void build(GameObject root) {
        root.addChild(ObjectFactories.makePlatformTile(x,y,rotation,mirrored, ResourceSystem.SpriteEnum.valueOf(tileName)));
    }

    @Override
    public void fromJSON(JSONObject json) throws JSONException {
        super.fromJSON(json);
        sprite = ResourceSystem.SpriteEnum.valueOf(tileName);
    }
}
