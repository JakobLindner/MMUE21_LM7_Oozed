package io.itch.jaknak72.oozed.game.level.builder;

import io.itch.jaknak72.oozed.game.level.Copter;
import io.itch.jaknak72.oozed.game.level.Level;
import io.itch.jaknak72.oozed.utils.Direction;

/**
 * @author simon
 */
public class CopterBuilder extends LevelPartBuilder<CopterBuilder>{
    private Copter copter = new Copter();

    public CopterBuilder(Level level, LevelBuilder parent) {
        super(level, parent);
    }

    public CopterBuilder at(int x, int y) {
        copter.x = x;
        copter.y = y;
        return this;
    }

    public CopterBuilder noHover(boolean noHover) {
        copter.noHover = noHover;
        return this;
    }

    public CopterBuilder orient(Direction upDir) {
        copter.upDir = upDir;
        return this;
    }

    @Override
    public CopterBuilder copy() {
        super.copy();
        copter = new Copter(copter);
        return this;
    }

    @Override
    protected void finish() {
        level.addTile(copter);
    }
}
