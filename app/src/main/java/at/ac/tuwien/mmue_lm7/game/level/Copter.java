package at.ac.tuwien.mmue_lm7.game.level;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import at.ac.tuwien.mmue_lm7.game.ObjectFactories;
import at.ac.tuwien.mmue_lm7.game.objects.GameObject;

public class Copter extends Tile {


    public Copter(){}
    public Copter(Copter other) {
        super(other);
    }

    @Override
    public void build(GameObject root, Context context) {
        root.addChild(ObjectFactories.makeCopter(x,y));
    }

    @Override
    public void fromJSON(JSONObject json) throws JSONException {
        super.fromJSON(json);
    }
}
