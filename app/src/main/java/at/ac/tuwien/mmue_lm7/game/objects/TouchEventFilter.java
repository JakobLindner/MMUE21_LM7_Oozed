package at.ac.tuwien.mmue_lm7.game.objects;

import at.ac.tuwien.mmue_lm7.game.Game;
import at.ac.tuwien.mmue_lm7.game.SwipeEvent;
import at.ac.tuwien.mmue_lm7.game.TapEvent;

/**
 * As long as this game object exists, touch events are filtered.
 * For priority of the listeners, the configured layer is used
 * Used for pause screen
 */
public class TouchEventFilter extends GameObject {
    @Override
    public void init() {
        super.init();

        Game.get().onTap.addListener(this,this::onTap,layer);
        Game.get().onSwipe.addListener(this,this::onSwipe,layer);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Game.get().onTap.removeListener(this);
        Game.get().onSwipe.removeListener(this);
    }

    private boolean onTap(TapEvent event) {
        return true;
    }

    private boolean onSwipe(SwipeEvent event) {
        return true;
    }
}
