package io.itch.jaknak72.oozed.game.level;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.itch.jaknak72.oozed.game.ObjectFactories;
import io.itch.jaknak72.oozed.game.objects.GameObject;
import io.itch.jaknak72.oozed.game.objects.KillEnemiesObjective;

/**
 * Represents a level
 * A level consists of a list of tiles
 * @author simon
 */
public class Level {
    private static final String TAG = "Level";
    private static final String LEVEL_NAME_KEY = "levelName";

    private String name;
    private ArrayList<Tile> objects = new ArrayList<Tile>();

    public Level() {

    }

    public void setName(String name) {
        this.name = name;
    }

    public void addTile(Tile tile) {
        objects.add(tile);
    }

    public void build(GameObject root, Context context) {
        for(Tile tile : objects) {
            tile.build(root, context);
        }

        //add objective
        root.addChild(new KillEnemiesObjective());

        //add background
        root.addChild(ObjectFactories.makeBackground());
    }

    public void loadJSON(JSONObject json) throws JSONException {
        if(json.has(LEVEL_NAME_KEY))
            this.name = json.getString(LEVEL_NAME_KEY);

        JSONArray tiles = json.getJSONArray("tiles");
        for(int i = 0;i<tiles.length();++i) {
            JSONObject jsonTile = tiles.getJSONObject(i);
            this.objects.add(getTileFromJSON(jsonTile));
        }
    }

    public static Level fromJSON(JSONObject json) throws JSONException {
        Level level = new Level();
        level.loadJSON(json);
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
        else if(tileName.equalsIgnoreCase("jumper")) {
            tile = new Jumper();
        }
        else if(tileName.equalsIgnoreCase("copter")) {
            tile = new Copter();
        }
        else if(tileName.equalsIgnoreCase("text")) {
            tile = new Text();
        }
        else {
            //Log.e(TAG, String.format("Invalid Tile: %s",tileName));
        }

        tile.fromJSON(json);
        return tile;
    }
}
