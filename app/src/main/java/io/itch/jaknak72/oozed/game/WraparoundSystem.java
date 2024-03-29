package io.itch.jaknak72.oozed.game;

import java.util.ArrayList;

import io.itch.jaknak72.oozed.game.objects.GameObject;
import io.itch.jaknak72.oozed.utils.Vec2;

/**
 * Watches a certain set of entities
 * These entities are teleported to the other side of the screen when they exit the screen
 * @author simon
 */
public class WraparoundSystem {
    private final static float PADDING = 0.5f;

    private final ArrayList<GameObject> wrappables = new ArrayList<>(16);

    //bounds
    private static final Vec2 MIN = new Vec2(0 - PADDING, 0 - PADDING);
    ;
    private static final Vec2 MAX = new Vec2(GameConstants.GAME_WIDTH + PADDING, GameConstants.GAME_HEIGHT + PADDING);
    private static final Vec2 WRAP_SCREEN_SIZE = new Vec2(GameConstants.GAME_WIDTH + 2 * PADDING, GameConstants.GAME_HEIGHT + 2 * PADDING);

    /**
     * wraps given position around screen
     *
     * @param pos position to wrap
     * @return true if pos has been changed
     */
    public static boolean wrapPos(Vec2 pos) {
        boolean wrapX = pos.x < MIN.x || pos.x > MAX.x;
        boolean wrapY = pos.y < MIN.y || pos.y > MAX.y;

        if (wrapX) {
            pos.x = (pos.x + PADDING) % WRAP_SCREEN_SIZE.x;
            if(pos.x<0)
                pos.x+=WRAP_SCREEN_SIZE.x;
            pos.x-=PADDING;
        }
        if (wrapY) {
            pos.y = (pos.y + PADDING) % WRAP_SCREEN_SIZE.y;
            if(pos.y<=0)
                pos.y+=WRAP_SCREEN_SIZE.y;
            pos.y-=PADDING;
        }

        return wrapX || wrapY;
    }

    public WraparoundSystem() {
    }

    public void registerWrappable(GameObject wrappable) {
        wrappables.add(wrappable);
    }

    public void removeWrappable(GameObject wrappable) {
        wrappables.remove(wrappable);
    }

    public void update() {
        Vec2 globalPos = Game.get().tmpVec();
        Vec2 translation = Game.get().tmpVec();
        for (GameObject go : wrappables) {
            go.getGlobalPosition(globalPos);

            if(wrapPos(globalPos)) {
                //calculate the translation performed by the wrapping
                go.getGlobalPosition(translation);
                translation.sub(globalPos).inv();

                //perform wrapping
                go.setGlobalPosition(globalPos);

                go.onWrap(translation);
            }
        }
    }
}
