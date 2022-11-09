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
public class Player extends Tile {
    public Direction upDir;
    public boolean runningCW;

    public Player(){}
    public Player(Player other) {
        super(other);
        this.upDir = other.upDir;
        this.runningCW = other.runningCW;
    }

    @Override
    public void build(GameObject root, Context context) {
        root.addChild(ObjectFactories.makeOoze(x,y, upDir,runningCW));
    }

    @Override
    public void fromJSON(JSONObject json) throws JSONException {
        super.fromJSON(json);

        upDir = Direction.fromRotation(rotation).rotateCCW();
        runningCW = !mirrored;
    }
}
