package at.ac.tuwien.mmue_lm7.game.level;

import org.json.JSONException;
import org.json.JSONObject;

import at.ac.tuwien.mmue_lm7.game.ObjectFactories;
import at.ac.tuwien.mmue_lm7.game.objects.GameObject;
import at.ac.tuwien.mmue_lm7.utils.Direction;

public class Text extends Tile{
    public String text;

    public Text() {}
    public Text(Text other) {
        super(other);
        this.text = other.text;
    }

    @Override
    public void build(GameObject root) {
        root.addChild(ObjectFactories.makeText(x,y,text));
    }

    @Override
    public void fromJSON(JSONObject json) throws JSONException {
        super.fromJSON(json);
        this.text = json.getString("text");
    }
}
