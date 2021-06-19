package io.itch.jaknak72.oozed.game.objects;

import io.itch.jaknak72.oozed.game.Game;

/**
 * Calls Game::clearLevel() and kills itself, when all enemies have been killed
 * @author simon
 */
public class KillEnemiesObjective extends GameObject{
    private static final int CHECK_INTERVAL = 30;

    @Override
    public void init() {
        super.init();
        registerDelayedCheck();
    }

    private void registerDelayedCheck() {
        if(!isDestroyed())
            Game.get().getTimingSystem().addDelayedAction(this::checkEnemyCount,CHECK_INTERVAL);
    }

    private void checkEnemyCount() {
        if(Game.get().getLevelStatusSystem().getEnemyCount()==0) {
            Game.get().clearLevel();
            kill();
        }
        else
            registerDelayedCheck();
    }
}
