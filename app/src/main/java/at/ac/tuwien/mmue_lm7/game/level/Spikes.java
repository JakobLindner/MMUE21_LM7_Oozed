package at.ac.tuwien.mmue_lm7.game.level;

import org.json.JSONException;
import org.json.JSONObject;

import at.ac.tuwien.mmue_lm7.game.ObjectFactories;
import at.ac.tuwien.mmue_lm7.game.objects.GameObject;
import at.ac.tuwien.mmue_lm7.utils.Direction;

public class Spikes extends Tile{
    public Direction direction = Direction.UP;

    public Spikes() {}
    public Spikes(Spikes other) {
        super(other);
        this.direction = other.direction;
    }

    @Override
    public void build(GameObject root) {
        root.addChild(ObjectFactories.makeSpikes(x,y,direction));
    }

    @Override
    public void fromJSON(JSONObject json) throws JSONException {
        super.fromJSON(json);
        direction = Direction.fromRotation(rotation).rotateCCW();
    }
}
