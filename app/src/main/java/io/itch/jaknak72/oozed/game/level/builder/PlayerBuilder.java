package io.itch.jaknak72.oozed.game.level.builder;

import io.itch.jaknak72.oozed.game.level.Level;
import io.itch.jaknak72.oozed.game.level.Player;
import io.itch.jaknak72.oozed.utils.Direction;

/**
 * @author simon
 */
public class PlayerBuilder extends LevelPartBuilder<PlayerBuilder>{
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
        player.upDir = upDir;
        player.runningCW = clockwise;
        return this;
    }

    @Override
    public PlayerBuilder copy() {
        super.copy();
        player = new Player(player);
        return this;
    }

    @Override
    protected void finish() {
        level.addTile(player);
    }
}
