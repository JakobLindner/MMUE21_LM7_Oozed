package io.itch.jaknak72.oozed.game.objects;

import io.itch.jaknak72.oozed.game.Game;
import io.itch.jaknak72.oozed.game.ObjectFactories;
import io.itch.jaknak72.oozed.game.resources.ResourceSystem;
import io.itch.jaknak72.oozed.utils.Vec2;

/**
 * Enemy base class that updates the enemy counter
 * Creates a disappear effect on kill
 * @author simon
 */
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
