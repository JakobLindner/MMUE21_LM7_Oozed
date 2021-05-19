package at.ac.tuwien.mmue_lm7.game.level;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import at.ac.tuwien.mmue_lm7.game.objects.GameObject;

public class Level {
    private static final String TAG = "Level";

    private String name;
    private ArrayList<Tile> objects = new ArrayList<Tile>();

    public Level() {

    }

    public void build(GameObject root) {
        for(Tile tile : objects) {
            tile.build(root);
        }
    }

    public static Level fromJSON(JSONObject json) throws JSONException {
        Level level = new Level();
        level.name = json.getString("levelName");

        JSONArray tiles = json.getJSONArray("tiles");
        for(int i = 0;i<tiles.length();++i) {
            JSONObject jsonTile = tiles.getJSONObject(i);
            level.objects.add(getTileFromJSON(jsonTile));
        }

        return level;
    }

    private static Tile getTileFromJSON(JSONObject json) throws JSONException {
        String tileName = json.getString("tileName");
        Tile tile = new InvalidTile();
        if(tileName.startsWith("platform")) {
            tile = new PlatformTile();
        }
        else if(tileName.equalsIgnoreCase("ooze")) {
            tile = new Player();
        }
        else if(tileName.equalsIgnoreCase("spikes")) {
            tile = new Spikes();
        }
        else if(tileName.equalsIgnoreCase("blocker")) {
            tile = new Blocker();
        }
        else {
            Log.e(TAG, String.format("Invalid Tile: %s",tileName));
        }

        tile.fromJSON(json);
        return tile;
    }
}
