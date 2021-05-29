package at.ac.tuwien.mmue_lm7.game.objects;

import at.ac.tuwien.mmue_lm7.game.Game;
import at.ac.tuwien.mmue_lm7.game.resources.ResourceSystem;

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
    }
}
