package at.ac.tuwien.mmue_lm7.game.level;

import org.json.JSONException;
import org.json.JSONObject;

import at.ac.tuwien.mmue_lm7.game.ObjectFactories;
import at.ac.tuwien.mmue_lm7.game.objects.GameObject;
import at.ac.tuwien.mmue_lm7.utils.Direction;

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
    public void build(GameObject root) {
        root.addChild(ObjectFactories.makeOoze(x,y, upDir,runningCW));
    }

    @Override
    public void fromJSON(JSONObject json) throws JSONException {
        super.fromJSON(json);

        upDir = Direction.fromRotation(rotation).rotateCCW();
        runningCW = !mirrored;
    }
}
