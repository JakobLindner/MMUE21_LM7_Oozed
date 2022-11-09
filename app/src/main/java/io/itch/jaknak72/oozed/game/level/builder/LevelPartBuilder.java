package io.itch.jaknak72.oozed.game.level.builder;

import androidx.annotation.CallSuper;

import org.json.JSONObject;

import io.itch.jaknak72.oozed.game.level.Level;

/**
 * Base class for specific parts of a level
 * @author simon
 */
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

    @Override
    public JumperBuilder jumper() {
        finish();
        return parent.jumper();
    }

    @Override
    public CopterBuilder copter() {
        finish();
        return parent.copter();
    }

    @CallSuper
    public SubBuilder copy() {
        finish();
        return (SubBuilder) this;
    }

    @Override
    public LevelBaseBuilder json(JSONObject json) {
        finish();
        return parent.json(json);
    }

    @Override
    public TextBuilder text(String text) {
        finish();
        return parent.text(text);
    }

    @Override
    public Level build() {
        finish();
        return parent.build();
    }
}
