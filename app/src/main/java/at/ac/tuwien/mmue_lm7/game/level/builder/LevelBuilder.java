package at.ac.tuwien.mmue_lm7.game.level.builder;

import at.ac.tuwien.mmue_lm7.game.level.Level;

public class LevelBuilder implements LevelBaseBuilder{
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
}
