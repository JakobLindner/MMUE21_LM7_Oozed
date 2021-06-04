package at.ac.tuwien.mmue_lm7.game.level.builder;

import at.ac.tuwien.mmue_lm7.game.level.Level;
import at.ac.tuwien.mmue_lm7.game.level.PlatformTile;
import at.ac.tuwien.mmue_lm7.game.resources.ResourceSystem;

public class PlatformBuilder extends LevelPartBuilder {
    private PlatformTile platform = new PlatformTile();

    public PlatformBuilder(Level level, LevelBuilder parent) {
        super(level, parent);
    }

    public PlatformBuilder at(int x, int y) {
        platform.x = x;
        platform.y = y;
        return this;
    }

    public PlatformBuilder sprite(ResourceSystem.SpriteEnum sprite) {
        platform.sprite = sprite;
        return this;
    }

    @Override
    protected void finish() {
        level.addTile(platform);
    }
}
