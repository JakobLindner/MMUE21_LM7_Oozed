package io.itch.jaknak72.oozed.game.objects;

import io.itch.jaknak72.oozed.game.Game;
import io.itch.jaknak72.oozed.game.TapEvent;
import io.itch.jaknak72.oozed.game.rendering.Layers;
import io.itch.jaknak72.oozed.utils.Vec2;

/**
 * Defines an area on screen, when tapped a given action is performed
 * @author simon
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
    protected Action action;

    public Button(Vec2 halfSize, Action action) {
        this.halfSize = halfSize;
        this.action = action;
    }

    @Override
    public void init() {
        super.init();

        Game.get().onTap.addListener(this,this::onTap, Layers.UI);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Game.get().onTap.removeListener(this);
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
