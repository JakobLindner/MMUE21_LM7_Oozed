package at.ac.tuwien.mmue_lm7.game.level;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import at.ac.tuwien.mmue_lm7.game.ObjectFactories;
import at.ac.tuwien.mmue_lm7.game.objects.GameObject;
import at.ac.tuwien.mmue_lm7.utils.Direction;

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
