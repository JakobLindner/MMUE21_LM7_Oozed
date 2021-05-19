package at.ac.tuwien.mmue_lm7.game.level;

import org.json.JSONException;
import org.json.JSONObject;

import at.ac.tuwien.mmue_lm7.game.ObjectFactories;
import at.ac.tuwien.mmue_lm7.game.objects.GameObject;
import at.ac.tuwien.mmue_lm7.utils.Direction;

public class Blocker extends Tile{
    private Direction direction;
    private boolean runningCW;



    @Override
    public void build(GameObject root) {
        root.addChild(ObjectFactories.makeBlocker(x,y,direction,runningCW));
    }

    @Override
    public void fromJSON(JSONObject json) throws JSONException {
        super.fromJSON(json);

        direction = Direction.fromRotation(rotation);
        runningCW = mirrored;
    }
}
