package io.itch.jaknak72.oozed.game.resources;

import io.itch.jaknak72.oozed.R;
import io.itch.jaknak72.oozed.game.GameConstants;

/**
 * Information for a sprite/animation for where to find it on a spritesheet
 * @author jakob
 */
public class SpriteInfo {
    public int spriteSheetId = R.drawable.platforms;
    public int size = GameConstants.PIXELS_PER_UNIT;
    public int firstX = 0;
    public int firstY = 0;
    public int animationLength = 1;
    public int frameDuration = 30; // in frames

    public SpriteInfo() {

    }

    public SpriteInfo(int spriteSheetId, int size, int firstX, int firstY) {
        this(spriteSheetId, size, firstX, firstY, 1, 30);
    }

    public SpriteInfo(int spriteSheetId, int size, int firstX, int firstY, int animationLength, int frameDuration) {
        this.spriteSheetId = spriteSheetId;
        this.size = size;
        this.firstX = firstX;
        this.firstY = firstY;
        this.animationLength = animationLength;
        this.frameDuration = frameDuration;
    }
}
