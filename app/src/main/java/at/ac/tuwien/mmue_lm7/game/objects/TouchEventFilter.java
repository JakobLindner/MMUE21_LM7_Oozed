package at.ac.tuwien.mmue_lm7.game.objects;

import android.view.KeyEvent;

import at.ac.tuwien.mmue_lm7.game.Game;
import at.ac.tuwien.mmue_lm7.game.SwipeEvent;
import at.ac.tuwien.mmue_lm7.game.TapEvent;

/**
 * As long as this game object exists, touch and key events are filtered.
 * For priority of the listeners, the configured layer is used
 * Used for pause screen
 * @author simon
 */
public class TouchEventFilter extends GameObject {
    @Override
    public void init() {
        super.init();

        Game.get().onTap.addListener(this,this::onTap,layer);
        Game.get().onSwipe.addListener(this,this::onSwipe,layer);
        Game.get().onKeyUp.addListener(this,this::onKey,layer);
        Game.get().onKeyDown.addListener(this,this::onKey,layer);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Game.get().onTap.removeListener(this);
        Game.get().onSwipe.removeListener(this);
        Game.get().onKeyUp.removeListener(this);
        Game.get().onKeyDown.removeListener(this);
    }

    private boolean onTap(TapEvent event) {
        return true;
    }

    private boolean onSwipe(SwipeEvent event) {
        return true;
    }

    private boolean onKey(KeyEvent event) {
        return true;
    }
}
