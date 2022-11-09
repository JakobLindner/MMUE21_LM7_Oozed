package io.itch.jaknak72.oozed.game.level.builder;

import io.itch.jaknak72.oozed.game.level.Blocker;
import io.itch.jaknak72.oozed.game.level.Level;
import io.itch.jaknak72.oozed.utils.Direction;

public class BlockerBuilder extends LevelPartBuilder<BlockerBuilder>{
    private Blocker blocker = new Blocker();

    public BlockerBuilder(Level level, LevelBuilder parent) {
        super(level, parent);
    }

    public BlockerBuilder at(int x, int y) {
        blocker.x = x;
        blocker.y = y;
        return this;
    }

    public BlockerBuilder orient(Direction upDir, boolean clockwise) {
        blocker.upDir = upDir;
        blocker.runningCW = clockwise;
        return this;
    }

    public BlockerBuilder dynamic(boolean dynamic) {
        blocker.dynamic = dynamic;
        return this;
    }

    @Override
    public BlockerBuilder copy() {
        super.copy();
        blocker = new Blocker(blocker);
        return this;
    }

    @Override
    protected void finish() {
        level.addTile(blocker);
    }
}
