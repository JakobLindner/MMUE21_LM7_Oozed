package io.itch.jaknak72.oozed.game.objects;

import android.graphics.Color;
import android.graphics.Paint;

import io.itch.jaknak72.oozed.game.Game;
import io.itch.jaknak72.oozed.game.rendering.RenderSystem;

/**
 * Draws a text
 * @author simon
 */
public class Text extends GameObject {
    private static final float DEFAULT_TEXT_SIZE = 18;

    public String text;
    public Paint.Align align = Paint.Align.CENTER;
    public int color = Color.WHITE;
    private float textSize;

    public Text() {
        this("");
    }

    public Text(String text) {
        this(text,Color.WHITE,DEFAULT_TEXT_SIZE);
    }

    public Text(String text, int color, float textSize) {
        this.text = text;
        this.color = color;
        this.textSize = textSize;
    }

    @Override
    public void render(RenderSystem render) {
        render.drawText()
                .at(getGlobalPosition())
                .text(text)
                .align(align)
                .typeFace(Game.get().getResourceSystem().getFont())
                .color(color)
                .size(textSize);
    }
}
