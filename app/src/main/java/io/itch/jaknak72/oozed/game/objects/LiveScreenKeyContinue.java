package io.itch.jaknak72.oozed.game.objects;

import android.view.KeyEvent;

import io.itch.jaknak72.oozed.game.Game;

/**
 * on live screen any key can be pressed to resume game
 * @author simon
 */
public class LiveScreenKeyContinue extends GameObject{
    @Override
    public void init() {
        super.init();
        Game.get().onKeyUp.addListener(this, this::onKeyUp);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Game.get().onKeyUp.removeListener(this);
    }

    private boolean onKeyUp(KeyEvent event) {
        Game.get().resumeGame();
        return true; //catch event
    }
}
