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
public class Blocker extends Tile {
    public static final String DYNAMIC_KEY = "dynamic";

    public Direction upDir;
    public boolean runningCW;
    public boolean dynamic = true;

    public Blocker(){}
    public Blocker(Blocker other) {
        super(other);
        this.upDir = other.upDir;
        this.runningCW = other.runningCW;
        this.dynamic = other.dynamic;
    }

    @Override
    public void build(GameObject root, Context context) {
        root.addChild(ObjectFactories.makeBlocker(x, y, upDir, runningCW, dynamic));
    }

    @Override
    public void fromJSON(JSONObject json) throws JSONException {
        super.fromJSON(json);

        if(json.has(DYNAMIC_KEY))
            dynamic = json.getBoolean(DYNAMIC_KEY);

        upDir = Direction.fromRotation(rotation).rotateCCW();
        runningCW = !mirrored;
    }
}
