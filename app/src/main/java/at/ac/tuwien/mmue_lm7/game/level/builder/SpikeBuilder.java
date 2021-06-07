package at.ac.tuwien.mmue_lm7.game.level.builder;

import at.ac.tuwien.mmue_lm7.game.level.Level;
import at.ac.tuwien.mmue_lm7.game.level.Spikes;
import at.ac.tuwien.mmue_lm7.utils.Direction;

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
