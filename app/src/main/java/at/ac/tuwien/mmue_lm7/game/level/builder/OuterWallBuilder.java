package at.ac.tuwien.mmue_lm7.game.level.builder;

import java.util.Arrays;

import at.ac.tuwien.mmue_lm7.game.GameConstants;
import at.ac.tuwien.mmue_lm7.game.level.Level;

public class OuterWallBuilder extends LevelPartBuilder {
    private String topPattern = "+";
    private String bottomPattern = "+";
    private String leftPattern = "+";
    private String rightPattern = "+";
    private int thickness = 1;

    private int[] horHoles = new int[0];
    private int[] verHoles = new int[0];

    public OuterWallBuilder(Level level, LevelBuilder parent) {
        super(level, parent);
    }

    public OuterWallBuilder topPattern(String pattern) {
        this.topPattern = pattern;
        return this;
    }

    public OuterWallBuilder bottomPattern(String pattern) {
        this.bottomPattern = pattern;
        return this;
    }

    public OuterWallBuilder leftPattern(String pattern) {
        this.leftPattern = pattern;
        return this;
    }

    public OuterWallBuilder rightPattern(String pattern) {
        this.rightPattern = pattern;
        return this;
    }

    public OuterWallBuilder horizontalPattern(String pattern) {
        this.topPattern = this.bottomPattern = pattern;
        return this;
    }

    public OuterWallBuilder verticalPattern(String pattern) {
        this.leftPattern = this.rightPattern = pattern;
        return this;
    }

    /**
     * @param thickness >=1
     */
    public OuterWallBuilder thickness(int thickness) {
        this.thickness = thickness;
        return this;
    }

    /**
     * creates holes on top and bottom side
     *
     * @param holes x coordinates
     */
    public OuterWallBuilder holeX(int... holes) {
        this.horHoles = holes;
        return this;
    }

    /**
     * creates holes on left and right side
     *
     * @param holes y coordinates
     */
    public OuterWallBuilder holeY(int... holes) {
        this.verHoles = holes;
        return this;
    }

    @Override
    protected void finish() {
        //sort holes
        Arrays.sort(horHoles);
        Arrays.sort(verHoles);

        PlatformBuilder pb = new PlatformBuilder(level, parent);

        //create horizontal
        for (int x = 0, holeI = 0; holeI <= horHoles.length; ++holeI) {
            int width = getXHole(holeI) - x;
            if(width<0)
                continue;//holes in corner
            else if(width==0) {
                //hole at current position, just increment coord
                ++x;
                continue;
            }
            //create bottom
            pb.at(x, 0)
                    .height(thickness)
                    .width(width)
                    .pattern(topPattern)
                    .finish();

            //create top
            pb.at(x,(int)GameConstants.GAME_HEIGHT-thickness)
                    .pattern(bottomPattern)
                    .finish();

            x+=width+1;
        }

        //create vertical
        for (int y = thickness, holeI = 0; holeI <= verHoles.length; ++holeI) {
            int height = getYHole(holeI) - y;
            if(height<0)
                continue;//holes in corner
            else if(height==0) {
                //hole at current position, just increment coord
                ++y;
                continue;
            }
            //create left
            pb.at(0, y)
                    .width(thickness)
                    .height(height)
                    .pattern(leftPattern)
                    .finish();

            //create right
            pb.at((int)GameConstants.GAME_WIDTH-thickness,y)
                    .pattern(rightPattern)
                    .finish();

            y+=height+1;
        }

    }

    private int getXHole(int holeIndex) {
        if (holeIndex >= horHoles.length)
            return (int) GameConstants.GAME_WIDTH;
        else
            return horHoles[holeIndex];
    }

    private int getYHole(int holeIndex) {
        if (holeIndex >= verHoles.length)
            return (int) GameConstants.GAME_HEIGHT-thickness;
        else
            return verHoles[holeIndex];
    }
}
