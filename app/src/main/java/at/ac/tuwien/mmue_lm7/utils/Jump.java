package at.ac.tuwien.mmue_lm7.utils;

/**
 * can be used to perform a jump
 */
public class Jump {
    /**
     * parabola used for describing the jump
     */
    private Parabola parabola = new Parabola();
    /**
     * Current position on the parabola
     */
    private float parPos;
    /**
     * starting position of jump
     */
    private Vec2 startPos = new Vec2();

    /**
     * current position of object considering startPos, parPos and mirroring
     */
    private Vec2 position = new Vec2();

    /**
     * swaps x and y when true
     */
    private boolean mirrored = false;

    /**
     * can be set to -1 to mirror along y axis
     */
    private int negatedX = 1;
    /**
     * can be set to -1 to mirror along x axis
     */
    private int negatedY = 1;

    public Parabola getParabola() {
        return parabola;
    }

    public float getParPos() {
        return parPos;
    }

    public void setParPos(float parPos) {
        this.parPos = parPos;
        recalculatePosition();
    }

    /**
     * advances the jump by given step, may be negative
     */
    public void advance(float step) {
        parPos += step;
        recalculatePosition();
    }

    public boolean isMirrored() {
        return mirrored;
    }

    public boolean isXNegated() {
        return negatedX<0;
    }

    public boolean isYNegated() {
        return negatedY<0;
    }

    /**
     * Reconfigures the jump parabola to meet given criteria
     * @param distance, >0
     * @param height, >0
     */
    public void setJump(float distance, float height){
        parabola.c = 0;
        parabola.b = 4*height/distance;
        parabola.a = -parabola.b/distance;

        recalculatePosition();
    }

    public void setJumpByGravityAndHeight(float gravity, float jumpHeight) {
        parabola.c = 0;
        parabola.b = (float) Math.sqrt(gravity*jumpHeight/2.0f);
        parabola.a = gravity*0.5f;

        recalculatePosition();
    }

    /**
     * Sets position, negation and mirroring based on given parameters
     * @param dir
     * @param up
     * @param startPos is unchanged
     * @param parPos how far jump has progressed
     */
    public void setPositioningAndMirroring(Direction dir, Direction up, Vec2 startPos, float parPos) {
        this.startPos.set(startPos);
        this.parPos = parPos;

        negatedX = Utils.signum(dir.dir.x+dir.dir.y);
        negatedY = Utils.signum(up.dir.x+up.dir.y);
        mirrored = dir.dir.isCCW(up.dir);

        recalculatePosition();
    }

    /**
     * sets negation and mirroring based on looking direction and up vector
     * @param dir !=null
     * @param up !=null
     */
    public void setMirroring(Direction dir, Direction up) {
        setPositioningAndMirroring(dir,up,startPos,parPos);
    }

    /**
     * @return copy of the current position
     */
    public Vec2 getPosition() {
        return position.copy();
    }

    /**
     * sets position of given vector to current position
     * @param vec !=null
     */
    public void getPosition(Vec2 vec) {
        vec.set(position);
    }

    public float getGravity() {
        return parabola.a*2;
    }

    private void recalculatePosition() {
        //calculate parabola pos
        float x = parPos*negatedX;
        float y = parabola.y(parPos)*negatedY;

        //set vector and mirror if desired
        position.set(x,y);
        if(mirrored)
            position.swap();

        //add starting position to obtain world pos
        position.add(startPos);
    }
}
