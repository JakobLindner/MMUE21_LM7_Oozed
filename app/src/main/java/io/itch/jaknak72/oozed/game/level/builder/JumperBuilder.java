package io.itch.jaknak72.oozed.game.level.builder;

import io.itch.jaknak72.oozed.game.level.Jumper;
import io.itch.jaknak72.oozed.game.level.Level;
import io.itch.jaknak72.oozed.utils.Direction;

/**
 * @author simon
 */
public class JumperBuilder extends LevelPartBuilder<JumperBuilder>{
    private Jumper jumper = new Jumper();

    public JumperBuilder(Level level, LevelBuilder parent) {
        super(level, parent);
    }

    public JumperBuilder at(int x, int y) {
        jumper.x = x;
        jumper.y = y;
        return this;
    }

    /**
     * Alias for orient
     */
    public JumperBuilder upDir(Direction upDir) {
        return orient(upDir);
    }

    public JumperBuilder orient(Direction upDir) {
        jumper.upDir = upDir;
        return this;
    }

    @Override
    public JumperBuilder copy() {
        super.copy();
        jumper = new Jumper(jumper);
        return this;
    }

    @Override
    protected void finish() {
        level.addTile(jumper);
    }
}
