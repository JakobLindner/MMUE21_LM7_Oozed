package io.itch.jaknak72.oozed.game.level.builder;

import org.json.JSONObject;

import io.itch.jaknak72.oozed.game.level.Level;

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
