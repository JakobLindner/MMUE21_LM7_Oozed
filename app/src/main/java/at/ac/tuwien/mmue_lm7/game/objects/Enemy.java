package at.ac.tuwien.mmue_lm7.game.objects;

import at.ac.tuwien.mmue_lm7.game.Game;
import at.ac.tuwien.mmue_lm7.game.ObjectFactories;
import at.ac.tuwien.mmue_lm7.game.resources.ResourceSystem;
import at.ac.tuwien.mmue_lm7.utils.Vec2;

public class Enemy extends GameObject{
    @Override
    public void init() {
        super.init();

        Game.get().getLevelStatusSystem().increaseEnemyCount();
    }

    @Override
    public void kill() {
        super.kill();

        Game.get().getResourceSystem().playSound(ResourceSystem.Sound.ENEMY_DEATH);

        Game.get().getLevelStatusSystem().decreaseEnemyCount();

        //create disappear effect
        Vec2 pos = getGlobalPosition();
        GameObject effect = ObjectFactories.makeKilledEffect(pos.x, pos.y);
        Game.get().getRoot().addChild(effect);
    }
}
