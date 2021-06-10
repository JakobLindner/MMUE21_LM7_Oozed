package at.ac.tuwien.mmue_lm7.game.level.builder;

import org.json.JSONObject;

import at.ac.tuwien.mmue_lm7.game.level.Level;

/**
 * @author simon
 */
public interface LevelBaseBuilder {
    PlatformBuilder platform();
    PlayerBuilder player();
    BlockerBuilder blocker();
    SpikeBuilder spikes();
    JumperBuilder jumper();
    CopterBuilder copter();
    OuterWallBuilder outerWall();
    LevelBaseBuilder json(JSONObject json);
    TextBuilder text(String text);
    Level build();
}
