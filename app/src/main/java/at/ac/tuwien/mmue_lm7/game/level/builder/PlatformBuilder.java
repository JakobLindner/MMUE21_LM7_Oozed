package at.ac.tuwien.mmue_lm7.game.level.builder;

import android.util.Log;

import at.ac.tuwien.mmue_lm7.game.GameConstants;
import at.ac.tuwien.mmue_lm7.game.level.Level;
import at.ac.tuwien.mmue_lm7.game.level.PlatformTile;
import at.ac.tuwien.mmue_lm7.game.resources.ResourceSystem;
import at.ac.tuwien.mmue_lm7.game.resources.SpriteInfo;

public class PlatformBuilder extends LevelPartBuilder {
    private static final String TAG = "PlatformBuilder";

    int x = 0;
    int y = 0;
    int width = 1;
    int height = 1;
    String pattern = "";
    ResourceSystem.SpriteEnum sprite = ResourceSystem.SpriteEnum.platformPipeOpen;

    public PlatformBuilder(Level level, LevelBuilder parent) {
        super(level, parent);
    }

    public PlatformBuilder at(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public PlatformBuilder size(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public PlatformBuilder width(int width) {
        this.width = width;
        return this;
    }

    public PlatformBuilder height(int height) {
        this.height = height;
        return this;
    }

    public PlatformBuilder sprite(ResourceSystem.SpriteEnum sprite) {
        this.sprite = sprite;
        return this;
    }

    public PlatformBuilder pattern(String pattern) {
        this.pattern = pattern;
        return this;
    }

    @Override
    protected void finish() {
        int patternIndex = 0;
        for (int y = height - 1; y >= 0; --y) {
            for (int x = 0; x < width; ++x) {
                //get sprite
                ResourceSystem.SpriteEnum sprite = this.sprite;
                if (!pattern.isEmpty()) {
                    int i = patternIndex%pattern.length();
                    char c = pattern.charAt(i);
                    sprite = getSpriteFromChar(Character.toUpperCase(c));
                }

                if(sprite!=null) {
                    int size = ResourceSystem.spriteInfo(sprite).size/GameConstants.PIXELS_PER_UNIT;
                    PlatformTile tile = generateTile(sprite, x, y);
                    level.addTile(tile);
                    //if tile is on edge of screen, then extend it
                    if(this.x+x==0)
                        level.addTile(generateTile(sprite,x-size,y));
                    else if(this.x+x+size==GameConstants.GAME_WIDTH)
                        level.addTile(generateTile(sprite,x+size,y));
                    if(this.y+y==0)
                        level.addTile(generateTile(sprite,x,y-size));
                    else if(this.y+y+size==GameConstants.GAME_HEIGHT)
                        level.addTile(generateTile(sprite,x,y+size));
                }

                ++patternIndex;
            }
        }

    }

    private ResourceSystem.SpriteEnum getSpriteFromChar(char c) {
        switch(c) {
            case 'P':return ResourceSystem.SpriteEnum.platformPipe;
            case 'O':return ResourceSystem.SpriteEnum.platformPipeOpen;
            case 'I':return ResourceSystem.SpriteEnum.platformIce;
            case 'G':return ResourceSystem.SpriteEnum.platformBigGears;
            case 'C':return ResourceSystem.SpriteEnum.platformCircuit;
            case 'B':return ResourceSystem.SpriteEnum.platformBigPlate;
            default:return null;
        }
    }

    private PlatformTile generateTile(ResourceSystem.SpriteEnum sprite,int offsetX,int offsetY) {
        PlatformTile tile = new PlatformTile();
        tile.x = this.x+offsetX;
        tile.y = this.y+offsetY;
        tile.sprite = sprite;

        return tile;
    }
}
