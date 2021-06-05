package at.ac.tuwien.mmue_lm7.game.level;

import org.json.JSONException;
import org.json.JSONObject;

import at.ac.tuwien.mmue_lm7.game.ObjectFactories;
import at.ac.tuwien.mmue_lm7.game.objects.GameObject;
import at.ac.tuwien.mmue_lm7.utils.Direction;

public class Blocker extends Tile {
    public Direction direction;
    public boolean runningCW;
    public boolean dynamic = true;

    //TODO way to configure dynamic field via json

    public Blocker(){}
    public Blocker(Blocker other) {
        super(other);
        this.direction = other.direction;
        this.runningCW = other.runningCW;
        this.dynamic = other.dynamic;
    }

    @Override
    public void build(GameObject root) {
        root.addChild(ObjectFactories.makeBlocker(x, y, direction, runningCW, dynamic));
    }

    @Override
    public void fromJSON(JSONObject json) throws JSONException {
        super.fromJSON(json);

        direction = Direction.fromRotation(rotation);
        if (mirrored) {
            direction = direction.opposite();
        }
        runningCW = !mirrored;
    }
}
