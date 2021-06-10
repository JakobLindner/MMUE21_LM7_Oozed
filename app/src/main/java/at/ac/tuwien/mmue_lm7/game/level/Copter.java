package at.ac.tuwien.mmue_lm7.game.level;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import at.ac.tuwien.mmue_lm7.game.ObjectFactories;
import at.ac.tuwien.mmue_lm7.game.objects.GameObject;
import at.ac.tuwien.mmue_lm7.utils.Direction;

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
