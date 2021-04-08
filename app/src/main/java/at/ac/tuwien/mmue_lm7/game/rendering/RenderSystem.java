package at.ac.tuwien.mmue_lm7.game.rendering;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

import at.ac.tuwien.mmue_lm7.utils.ObjectPool;
import at.ac.tuwien.mmue_lm7.utils.Vec2;

/**
 * batches Draw/render commands to potentially do optimizations and ordering of objects
 */
public class RenderSystem {
    //TODO drawSprite(...)
    //TODO drawText

    private ObjectPool<DrawRect> rectCommandPool = new ObjectPool<DrawRect>(DrawRect::new);
    private ArrayList<RenderCommand> batchedCommands = new ArrayList<>(128);//TODO find good initial capacity

    public DrawRect drawRect() {
        return drawRect(Layers.DEFAULT);
    }

    public DrawRect drawRect(short layer) {
        DrawRect d = rectCommandPool.obtain();
        d.layer = layer;

        batchedCommands.add(d);
        return d;
    }

    /**
     * called once by game
     */
    public void init() {

    }

    /**
     * Renders all batched render commands
     *
     * @param canvas
     */
    public void render(Canvas canvas) {
        //sort render commands, Collections.sort is a stable sort, so the call order for same layers is preserved
        Collections.sort(batchedCommands);

        for(RenderCommand command : batchedCommands) {
            //perform and free commands again
            command.render(canvas);
            command.free();
        }

        //clear render command list
        batchedCommands.clear();
    }

    public abstract class RenderCommand implements Comparable<RenderCommand>, ObjectPool.Poolable{
        protected short layer = Layers.DEFAULT;

        @Override
        public final int compareTo(RenderCommand o) {
            return Short.compare(layer,o.layer);
        }

        /**
         * Performs this render command
         * @param canvas, !=null
         */
        public abstract void render(Canvas canvas);

        /**
         * Frees this command and makes it available again in the respective pool
         */
        public abstract void free();

        @Override
        public void reset() {
            layer = Layers.DEFAULT;
        }
    }

    /**
     * render command to draw a rectangle
     */
    public class DrawRect extends RenderCommand {
        private float left;
        private float right;
        private float top;
        private float bottom;
        private Paint paint = new Paint();

        //if both are set, then left, right, top, bottom are inferred from them
        private boolean posSet = false;
        private Vec2 pos = new Vec2(0, 0);
        private boolean sizeSet = false;
        private Vec2 halfSize = new Vec2(0, 0);

        @Override
        public void reset() {
            super.reset();
            left = right = top = bottom = 0;
            paint.reset();
            pos.zero();
            halfSize.zero();
            posSet = sizeSet = false;
        }

        public Paint getPaint() {
            return paint;
        }

        /**
         * infers edge positions if both size and position are set
         */
        private void updateBounds() {
            if (posSet && sizeSet) {
                left = pos.x - halfSize.x;
                right = pos.x + halfSize.x;
                bottom = pos.y - halfSize.y;
                top = pos.y + halfSize.y;
            }
        }

        public DrawRect at(Vec2 pos) {
            this.pos.set(pos);
            posSet = true;
            updateBounds();
            return this;
        }

        public DrawRect size(Vec2 size) {
            this.halfSize.set(size).scl(0.5f);
            sizeSet = true;
            updateBounds();
            return this;
        }

        public DrawRect left(float left) {
            this.left = left;
            return this;
        }

        public DrawRect right(float right) {
            this.right = right;
            return this;
        }

        public DrawRect bottom(float bottom) {
            this.bottom = bottom;
            return this;
        }

        public DrawRect top (float top) {
            this.top = top;
            return this;
        }

        public DrawRect edges(float left, float right, float bottom, float top) {
            this.left = left;
            this.right = right;
            this.bottom = bottom;
            this.top = top;
            return this;
        }

        public DrawRect halfSize(Vec2 halfSize) {
            this.halfSize.set(halfSize);
            sizeSet = true;
            updateBounds();
            return this;
        }

        public DrawRect style(Paint.Style style) {
            paint.setStyle(style);
            return this;
        }

        public DrawRect color(int color) {
            paint.setColor(color);
            return this;
        }

        @Override
        public void render(Canvas canvas) {
            canvas.drawRect(left, top, right, bottom, paint);
        }

        @Override
        public void free() {
            rectCommandPool.free(this);
        }
    }
}
