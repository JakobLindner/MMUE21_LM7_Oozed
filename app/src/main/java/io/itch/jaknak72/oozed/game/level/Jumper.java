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
public class Jumper extends Tile {
    public Direction upDir;


    public Jumper(){}
    public Jumper(Jumper other) {
        super(other);
        this.upDir = other.upDir;
    }

    @Override
    public void build(GameObject root, Context context) {
        root.addChild(ObjectFactories.makeJumper(x, y, upDir));
    }

    @Override
    public void fromJSON(JSONObject json) throws JSONException {
        super.fromJSON(json);

        upDir = Direction.fromRotation(rotation).rotateCCW();
    }
}
