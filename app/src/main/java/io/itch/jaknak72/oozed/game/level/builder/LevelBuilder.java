package io.itch.jaknak72.oozed.game.level.builder;

import org.json.JSONException;
import org.json.JSONObject;

import io.itch.jaknak72.oozed.game.level.Level;

/**
 * @author simon
 */
public class LevelBuilder implements LevelBaseBuilder{
    public static final String TAG = "LevelBuilder";
    private Level level = new Level();

    public LevelBuilder(String name) {
        level.setName(name);
    }

    /**
     * after a call of build(), the builder should not be used anymore
     * @return the configured level
     */
    public Level build() {
        return level;
    }

    public PlatformBuilder platform() {
        return new PlatformBuilder(level,this);
    }

    public PlayerBuilder player() {
        return new PlayerBuilder(level,this);
    }

    @Override
    public BlockerBuilder blocker() {
        return new BlockerBuilder(level,this);
    }

    @Override
    public SpikeBuilder spikes() {
        return new SpikeBuilder(level, this);
    }

    @Override
    public OuterWallBuilder outerWall() {
        return new OuterWallBuilder(level,this);
    }

    @Override
    public JumperBuilder jumper() {
        return new JumperBuilder(level,this);
    }

    @Override
    public CopterBuilder copter() {
        return new CopterBuilder(level, this);
    }

    @Override
    public LevelBaseBuilder json(JSONObject json) {
        try {
            level.loadJSON(json);
        } catch (JSONException e) {
            //Log.w(TAG,"Could not load from json", e);
        }
        return this;
    }

    @Override
    public TextBuilder text(String text) {
        return new TextBuilder(level,this).content(text);
    }
}
