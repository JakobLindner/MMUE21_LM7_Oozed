package io.itch.jaknak72.oozed.game.level.builder;

import io.itch.jaknak72.oozed.game.level.Level;
import io.itch.jaknak72.oozed.game.level.Text;

/**
 * @author simon
 */
public class TextBuilder extends LevelPartBuilder<TextBuilder>{
    private Text text = new Text();

    public TextBuilder(Level level, LevelBuilder parent) {
        super(level, parent);
    }

    public TextBuilder at(int x, int y) {
        text.x = x;
        text.y = y;
        return this;
    }

    public TextBuilder content(String text) {
        this.text.text = text;
        return this;
    }

    @Override
    public TextBuilder copy() {
        super.copy();
        this.text = new Text(this.text);
        return this;
    }

    @Override
    protected void finish() {
        level.addTile(text);
    }
}
