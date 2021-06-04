package at.ac.tuwien.mmue_lm7.game.level.builder;

import at.ac.tuwien.mmue_lm7.game.level.Level;
import at.ac.tuwien.mmue_lm7.game.level.Player;
import at.ac.tuwien.mmue_lm7.utils.Direction;

public class PlayerBuilder extends LevelPartBuilder{
    private Player player = new Player();

    public PlayerBuilder(Level level, LevelBuilder parent) {
        super(level, parent);
    }

    public PlayerBuilder at(int x, int y) {
        player.x = x;
        player.y = y;
        return this;
    }

    public PlayerBuilder orient(Direction upDir, boolean clockwise) {
        player.direction = upDir.rotateCW();
        player.runningCW = clockwise;
        return this;
    }

    @Override
    protected void finish() {
        level.addTile(player);
    }
}
