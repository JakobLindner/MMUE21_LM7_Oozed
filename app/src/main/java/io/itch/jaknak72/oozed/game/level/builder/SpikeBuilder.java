package io.itch.jaknak72.oozed.game.level.builder;

import io.itch.jaknak72.oozed.game.level.Level;
import io.itch.jaknak72.oozed.game.level.Spikes;
import io.itch.jaknak72.oozed.utils.Direction;

/**
 * @author simon
 */
public class SpikeBuilder extends LevelPartBuilder<SpikeBuilder>{
    private Spikes spikes = new Spikes();

    public SpikeBuilder(Level level, LevelBuilder parent) {
        super(level, parent);
    }

    public SpikeBuilder x(int x) {
        spikes.x = x;
        return this;
    }

    public SpikeBuilder y(int y) {
        spikes.y = y;
        return this;
    }

    public SpikeBuilder at(int x, int y) {
        spikes.x = x;
        spikes.y = y;
        return this;
    }

    public SpikeBuilder dir(Direction dir) {
        spikes.direction = dir;
        return this;
    }

    @Override
    public SpikeBuilder copy() {
        super.copy();
        spikes = new Spikes(spikes);
        return this;
    }

    @Override
    protected void finish() {
        level.addTile(spikes);
    }
}
