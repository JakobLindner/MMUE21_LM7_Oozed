package at.ac.tuwien.mmue_lm7.game.level.builder;

import at.ac.tuwien.mmue_lm7.game.GameConstants;
import at.ac.tuwien.mmue_lm7.game.level.Level;
import at.ac.tuwien.mmue_lm7.game.level.PlatformTile;
import at.ac.tuwien.mmue_lm7.game.level.Spikes;
import at.ac.tuwien.mmue_lm7.game.level.Tile;
import at.ac.tuwien.mmue_lm7.game.resources.ResourceSystem;
import at.ac.tuwien.mmue_lm7.utils.Direction;

public class PlatformBuilder extends LevelPartBuilder<PlatformBuilder> {
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

    public PlatformBuilder x(int x) {
        this.x = x;
        return this;
    }

    public PlatformBuilder y(int y) {
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
        this.width = this.height = ResourceSystem.spriteInfo(sprite).size/GameConstants.PIXELS_PER_UNIT;
        return this;
    }

    public PlatformBuilder pattern(String pattern) {
        this.pattern = pattern;
        return this;
    }

    @Override
    protected void finish() {
        int patternIndex = 0;
        for (int offsetY = height - 1; offsetY >= 0; --offsetY) {
            for (int offsetX = 0; offsetX < width; ++offsetX) {
                //get sprite
                ResourceSystem.SpriteEnum sprite = this.sprite;
                char c='O';
                if (!pattern.isEmpty()) {
                    int i = patternIndex%pattern.length();
                    c = pattern.charAt(i);
                    sprite = getSpriteFromChar(c);
                }

                if(sprite!=null) {
                    if(sprite == ResourceSystem.SpriteEnum.spikes) {
                        Spikes spikes = new Spikes();
                        spikes.x = this.x+offsetX;
                        spikes.y = this.y+offsetY;
                        if(c=='<')
                            spikes.direction = Direction.LEFT;
                        else if(c=='>')
                            spikes.direction = Direction.RIGHT;
                        else if(c=='^')
                            spikes.direction = Direction.UP;
                        else if(c=='v')
                            spikes.direction = Direction.DOWN;
                        level.addTile(spikes);
                    }
                    else {
                        int size = ResourceSystem.spriteInfo(sprite).size / GameConstants.PIXELS_PER_UNIT;
                        //check if tile does not extend platform size
                        if (offsetX + size <= width && offsetY - (size - 1) >= 0) {
                            int patternOffset = 1 - size;
                            Tile tile = generateTile(sprite, offsetX, offsetY + patternOffset);
                            level.addTile(tile);
                            //if tile is on edge of screen, then extend it
                            if (this.x + offsetX == 0)
                                level.addTile(generateTile(sprite, offsetX - size, offsetY + patternOffset));
                            else if (this.x + offsetX + size == GameConstants.GAME_WIDTH)
                                level.addTile(generateTile(sprite, offsetX + size, offsetY + patternOffset));
                            if (this.y + offsetY + patternOffset == 0)
                                level.addTile(generateTile(sprite, offsetX, offsetY - size + patternOffset));
                            else if (this.y + offsetY + 1 == GameConstants.GAME_HEIGHT)
                                level.addTile(generateTile(sprite, offsetX, offsetY + size + patternOffset));
                        }
                    }
                }

                ++patternIndex;
            }
        }

    }

    public static ResourceSystem.SpriteEnum getSpriteFromChar(char c) {
        switch(c) {
            case '+':return ResourceSystem.SpriteEnum.platformPipeCross;
            case 'O':return ResourceSystem.SpriteEnum.platformPipeOpen;
            case 'I':return ResourceSystem.SpriteEnum.platformIce;
            case 'G':return ResourceSystem.SpriteEnum.platformBigGears;
            case 'C':return ResourceSystem.SpriteEnum.platformCircuit;
            case 'P':return ResourceSystem.SpriteEnum.platformBigPlate;
            case 'p':return ResourceSystem.SpriteEnum.platformPlate;
            case '^':
            case 'v':
            case '<':
            case '>':
                return ResourceSystem.SpriteEnum.spikes;
            default:return null;
        }
    }

    private Tile generateTile(ResourceSystem.SpriteEnum sprite,int offsetX,int offsetY) {
        PlatformTile tile = new PlatformTile();
        tile.x = this.x+offsetX;
        tile.y = this.y+offsetY;
        tile.sprite = sprite;

        return tile;
    }
}
