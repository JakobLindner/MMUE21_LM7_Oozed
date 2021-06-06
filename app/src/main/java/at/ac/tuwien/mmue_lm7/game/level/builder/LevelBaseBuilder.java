package at.ac.tuwien.mmue_lm7.game.level.builder;

import at.ac.tuwien.mmue_lm7.game.level.Level;

public interface LevelBaseBuilder {
    PlatformBuilder platform();
    PlayerBuilder player();
    BlockerBuilder blocker();
    SpikeBuilder spikes();
    JumperBuilder jumper();
    OuterWallBuilder outerWall();
    Level build();
}
