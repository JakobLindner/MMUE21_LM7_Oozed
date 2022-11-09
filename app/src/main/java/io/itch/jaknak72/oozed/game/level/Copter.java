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
public class Copter extends Tile {
    public static final String NO_HOVER_KEY = "dynamic";
    public boolean noHover = false;
    public Direction upDir = Direction.UP;

    public Copter(){}
    public Copter(Copter other) {
        super(other);
    }

    @Override
    public void build(GameObject root, Context context) {
        root.addChild(ObjectFactories.makeCopter(x,y, upDir, noHover));
    }

    @Override
    public void fromJSON(JSONObject json) throws JSONException {
        super.fromJSON(json);
        upDir = Direction.fromRotation(rotation).rotateCCW();
        if(json.has(NO_HOVER_KEY))
            noHover = json.getBoolean(NO_HOVER_KEY);
    }
}
