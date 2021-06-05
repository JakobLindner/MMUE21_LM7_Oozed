package at.ac.tuwien.mmue_lm7.game.level;

import androidx.annotation.CallSuper;

import org.json.JSONException;
import org.json.JSONObject;

import at.ac.tuwien.mmue_lm7.game.objects.GameObject;

public abstract class Tile {
    public String tileName;
    public int x;
    public int y;
    public int rotation;
    public boolean mirrored;

    public abstract void build(GameObject root);

    public Tile(){}
    public Tile(Tile tile) {
        this.tileName = tile.tileName;
        this.x = tile.x;
        this.y = tile.y;
        this.rotation = tile.rotation;
        this.mirrored = tile.mirrored;
    }

    @CallSuper
    public void fromJSON(JSONObject json) throws JSONException {
        tileName = json.getString("tileName");
        x = json.getInt("x");
        y = json.getInt("y");
        rotation = json.getInt("rotation");
        mirrored = json.getBoolean("mirrored");
    }
}
