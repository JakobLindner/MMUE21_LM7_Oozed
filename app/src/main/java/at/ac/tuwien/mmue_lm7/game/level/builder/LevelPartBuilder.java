package at.ac.tuwien.mmue_lm7.game.level.builder;

import androidx.annotation.CallSuper;

import at.ac.tuwien.mmue_lm7.game.level.Level;

public abstract class LevelPartBuilder<SubBuilder extends LevelPartBuilder> implements LevelBaseBuilder {
    protected Level level;
    protected LevelBuilder parent;

    public LevelPartBuilder(Level level, LevelBuilder parent){
        this.level = level;
        this.parent = parent;
    }

    /**
     * Finishes this part
     */
    protected abstract void finish();

    @Override
    public PlatformBuilder platform() {
        finish();
        return parent.platform();
    }

    @Override
    public PlayerBuilder player() {
        finish();
        return parent.player();
    }

    @Override
    public BlockerBuilder blocker() {
        finish();
        return parent.blocker();
    }

    @Override
    public SpikeBuilder spikes() {
        finish();
        return parent.spikes();
    }

    @Override
    public OuterWallBuilder outerWall() {
        finish();
        return parent.outerWall();
    }

    @CallSuper
    public SubBuilder copy() {
        finish();
        return (SubBuilder) this;
    }

    @Override
    public Level build() {
        finish();
        return parent.build();
    }
}
