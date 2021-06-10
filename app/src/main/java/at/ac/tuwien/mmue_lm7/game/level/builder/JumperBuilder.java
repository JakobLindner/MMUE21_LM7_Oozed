package at.ac.tuwien.mmue_lm7.game.level.builder;

import at.ac.tuwien.mmue_lm7.game.level.Blocker;
import at.ac.tuwien.mmue_lm7.game.level.Jumper;
import at.ac.tuwien.mmue_lm7.game.level.Level;
import at.ac.tuwien.mmue_lm7.utils.Direction;

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
