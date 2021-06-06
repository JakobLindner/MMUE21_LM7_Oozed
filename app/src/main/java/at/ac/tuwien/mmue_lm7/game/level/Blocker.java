package at.ac.tuwien.mmue_lm7.game.level;

import org.json.JSONException;
import org.json.JSONObject;

import at.ac.tuwien.mmue_lm7.game.ObjectFactories;
import at.ac.tuwien.mmue_lm7.game.objects.GameObject;
import at.ac.tuwien.mmue_lm7.utils.Direction;

public class Blocker extends Tile {
    public Direction upDir;
    public boolean runningCW;
    public boolean dynamic = true;

    //TODO way to configure dynamic field via json

    public Blocker(){}
    public Blocker(Blocker other) {
        super(other);
        this.upDir = other.upDir;
        this.runningCW = other.runningCW;
        this.dynamic = other.dynamic;
    }

    @Override
    public void build(GameObject root) {
        root.addChild(ObjectFactories.makeBlocker(x, y, upDir, runningCW, dynamic));
    }

    @Override
    public void fromJSON(JSONObject json) throws JSONException {
        super.fromJSON(json);

        upDir = Direction.fromRotation(rotation).rotateCCW();
        runningCW = !mirrored;
    }
}
