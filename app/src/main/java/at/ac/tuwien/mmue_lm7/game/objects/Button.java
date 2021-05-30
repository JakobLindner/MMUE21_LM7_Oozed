package at.ac.tuwien.mmue_lm7.game.objects;

import at.ac.tuwien.mmue_lm7.game.Game;
import at.ac.tuwien.mmue_lm7.game.TapEvent;
import at.ac.tuwien.mmue_lm7.utils.Vec2;

/**
 * Defines an area on screen, when tapped a given action is performed
 */
public class Button extends GameObject {
    @FunctionalInterface
    public interface Action {
        void perform(Button button);
    }

    private Vec2 halfSize;
    /**
     * Action performed on press
     */
    private Action action;

    public Button(Vec2 halfSize, Action action) {
        this.halfSize = halfSize;
        this.action = action;
    }

    @Override
    public void init() {
        super.init();

        Game.get().onTap.addListener(this::onTap);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Game.get().onTap.addListener(this::onTap);
    }

    private boolean onTap(TapEvent event) {
        Vec2 topLeft = getGlobalPosition().sub(halfSize);
        boolean outside = event.getPosition().x<topLeft.x ||
                event.getPosition().x>topLeft.x+2*halfSize.x ||
                event.getPosition().y<topLeft.y ||
                event.getPosition().y>topLeft.y+2*halfSize.y;

        if(!outside) {
            action.perform(this);
            return true;
        }

        return false;
    }
}
