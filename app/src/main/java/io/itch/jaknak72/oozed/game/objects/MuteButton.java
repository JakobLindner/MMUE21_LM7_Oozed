package io.itch.jaknak72.oozed.game.objects;

import io.itch.jaknak72.oozed.game.Game;
import io.itch.jaknak72.oozed.game.resources.ResourceSystem;
import io.itch.jaknak72.oozed.game.resources.SoundSystem;
import io.itch.jaknak72.oozed.game.resources.SpriteInfo;
import io.itch.jaknak72.oozed.utils.Vec2;

/**
 * Button that mutes/unmutes music
 * sprite is changed depending on current mute state
 * @author simon
 */
public class MuteButton extends Button {

    private Sprite sprite;
    private SpriteInfo mutedSprite;
    private SpriteInfo notMutedSprite;

    public MuteButton(Vec2 halfSize, Sprite sprite, SpriteInfo mutedSprite, SpriteInfo notMutedSprite) {
        super(halfSize, null);

        this.sprite = sprite;
        this.mutedSprite = mutedSprite;
        this.notMutedSprite = notMutedSprite;
    }

    @Override
    public void init() {
        super.action = this::press;
        super.init();

        updateSprite();
    }

    private void press(Button button) {
        Game.get().getResourceSystem().playSound(ResourceSystem.Sound.BUTTON);
        SoundSystem.get().toggleMuted();

        updateSprite();
    }

    private void updateSprite() {
        if(SoundSystem.get().isMuted())
            sprite.setSpriteInfo(mutedSprite);
        else
            sprite.setSpriteInfo(notMutedSprite);
    }
}
