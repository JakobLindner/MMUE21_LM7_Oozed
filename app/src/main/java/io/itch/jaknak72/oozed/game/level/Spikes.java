package io.itch.jaknak72.oozed.game.level;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import io.itch.jaknak72.oozed.game.ObjectFactories;
import io.itch.jaknak72.oozed.game.objects.GameObject;
import io.itch.jaknak72.oozed.utils.Direction;

/**
 * @author simon
 */
public class Spikes extends Tile{
    public Direction direction = Direction.UP;

    public Spikes() {}
    public Spikes(Spikes other) {
        super(other);
        this.direction = other.direction;
    }

    @Override
    public void build(GameObject root, Context context) {
        root.addChild(ObjectFactories.makeSpikes(x,y,direction));
    }

    @Override
    public void fromJSON(JSONObject json) throws JSONException {
        super.fromJSON(json);
        direction = Direction.fromRotation(rotation).rotateCCW();
    }
}
