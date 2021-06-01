package at.ac.tuwien.mmue_lm7.game.objects;

import at.ac.tuwien.mmue_lm7.game.resources.SoundSystem;
import at.ac.tuwien.mmue_lm7.game.resources.SpriteInfo;
import at.ac.tuwien.mmue_lm7.utils.Vec2;

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
