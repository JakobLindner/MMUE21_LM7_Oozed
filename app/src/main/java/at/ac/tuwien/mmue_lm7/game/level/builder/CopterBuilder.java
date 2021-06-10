package at.ac.tuwien.mmue_lm7.game.level.builder;

import at.ac.tuwien.mmue_lm7.game.level.Copter;
import at.ac.tuwien.mmue_lm7.game.level.Jumper;
import at.ac.tuwien.mmue_lm7.game.level.Level;
import at.ac.tuwien.mmue_lm7.utils.Direction;
import at.ac.tuwien.mmue_lm7.utils.Vec2;

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
