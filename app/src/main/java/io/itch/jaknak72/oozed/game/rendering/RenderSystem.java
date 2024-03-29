package io.itch.jaknak72.oozed.game.rendering;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.Collections;

import io.itch.jaknak72.oozed.game.Game;
import io.itch.jaknak72.oozed.game.resources.SpriteInfo;
import io.itch.jaknak72.oozed.utils.ObjectPool;
import io.itch.jaknak72.oozed.utils.Utils;
import io.itch.jaknak72.oozed.utils.Vec2;

/**
 * batches Draw/render commands to potentially do optimizations and ordering of objects
 * @author simon
 */
public class RenderSystem {

    private ObjectPool<DrawRect> rectCommandPool = new ObjectPool<DrawRect>(DrawRect::new);
    private ObjectPool<DrawSprite> spriteCommandPool = new ObjectPool<>(DrawSprite::new);
    private ObjectPool<DrawText> textCommandPool = new ObjectPool<>(DrawText::new);
    private ArrayList<RenderCommand> batchedCommands = new ArrayList<>(128);//TODO find good initial capacity

    private Paint paint = new Paint();

    public DrawRect drawRect() {
        return drawRect(Layers.DEFAULT);
    }

    public DrawRect drawRect(short layer) {
        DrawRect d = rectCommandPool.obtain();
        d.layer = layer;

        batchedCommands.add(d);
        return d;
    }

    public DrawSprite drawSprite() {
        return drawSprite(Layers.DEFAULT);
    }

    public DrawSprite drawSprite(short layer) {
        DrawSprite d = spriteCommandPool.obtain();
        d.layer = layer;

        batchedCommands.add(d);
        return d;
    }

    public DrawText drawText() {
        return drawText(Layers.DEFAULT);
    }

    public DrawText drawText(short layer) {
        DrawText d = textCommandPool.obtain();
        d.layer = layer;

        batchedCommands.add(d);
        return d;
    }

    /**
     * called once by game
     */
    public void init() {
        paint.setAntiAlias(false);
        paint.setDither(false);
        paint.setFilterBitmap(false);
    }

    /**
     * Renders all batched render commands
     *
     * @param canvas
     */
    public void render(Canvas canvas) {
        //sort render commands, Collections.sort is a stable sort, so the call order for same layers is preserved
        Collections.sort(batchedCommands);

        for (RenderCommand command : batchedCommands) {
            //perform and free commands again
            command.render(canvas);
            command.free();
        }

        //clear render command list
        batchedCommands.clear();
    }

    /**
     * @author simon
     */
    public abstract class RenderCommand implements Comparable<RenderCommand>, ObjectPool.Poolable {
        protected short layer = Layers.DEFAULT;

        @Override
        public final int compareTo(RenderCommand o) {
            return Short.compare(layer, o.layer);
        }

        /**
         * Performs this render command
         *
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
     * @author simon
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

        public DrawRect top(float top) {
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

        public DrawRect strokeWidth(float width) {
            paint.setStrokeWidth(width);
            return this;
        }

        @Override
        public void render(Canvas canvas) {
            //TODO should coordinates be rounded/truncated to int to be pixel perfect?
            canvas.drawRect(Utils.worldToScreenX(left),
                    Utils.worldToScreenY(top),
                    Utils.worldToScreenX(right),
                    Utils.worldToScreenY(bottom),
                    paint);
        }

        @Override
        public void free() {
            rectCommandPool.free(this);
        }
    }

    /**
     * render command to draw a sprite
     * @author jakob
     */
    public class DrawSprite extends RenderCommand {

        /**
         * in screen coords
         */
        private Vec2 pos = new Vec2(0, 0);
        private float rot = 0;
        private int xMir = 1;
        private int frame = 0;
        private SpriteInfo spriteInfo = new SpriteInfo();

        /**
         * used for performing the command/rendering, allocated once to save allocations
         */
        private Rect source = new Rect();
        private Rect dest = new Rect();

        @Override
        public void reset() {
            super.reset();

            pos.set(0, 0);
            rot = 0;
            xMir = 1;
            frame = 0;
            spriteInfo = new SpriteInfo();
        }

        public DrawSprite at(Vec2 pos) {
            this.pos.set(pos);
            Utils.worldToScreen(this.pos);
            return this;
        }

        public DrawSprite rotated(float rot) {
            this.rot = rot;
            return this;
        }

        public DrawSprite mirrored(boolean mir) {
            this.xMir = mir ? -1 : 1;
            return this;
        }

        public DrawSprite spriteInfo(SpriteInfo spriteInfo) {
            this.spriteInfo = spriteInfo;
            return this;
        }

        public DrawSprite frame(int frame) {
            this.frame = frame;
            return this;
        }

        @Override
        public void render(Canvas canvas) {

            Bitmap bitmap = Game.get().getResourceSystem().getBitmap(spriteInfo.spriteSheetId);

            source.set(spriteInfo.firstX + spriteInfo.size * frame,
                    spriteInfo.firstY,
                    spriteInfo.firstX + spriteInfo.size * (frame + 1),
                    spriteInfo.firstY + spriteInfo.size);

            dest.set(- spriteInfo.size / 2,
                    - spriteInfo.size / 2,
                    spriteInfo.size / 2,
                    spriteInfo.size / 2);

            canvas.save();
            canvas.scale(xMir, 1, pos.x, pos.y);
            canvas.rotate(rot, pos.x, pos.y);
            canvas.translate(pos.x, pos.y);
            canvas.drawBitmap(bitmap, source, dest, paint);
            canvas.restore();
        }

        @Override
        public void free() {
            spriteCommandPool.free(this);
        }
    }

    /**
     * Render command to draw text
     * @author simon
     */
    public class DrawText extends RenderCommand {
        private String text = "";
        private float x = 0;
        private float y = 0;
        private Paint paint = new Paint();
        private Path path;

        /**
         * @param pos !=null, is unchanged
         */
        public DrawText at(Vec2 pos) {
            this.x = pos.x;
            this.y = pos.y;
            return this;
        }

        /**
         * Copies reference of text
         *
         * @param text
         */
        public DrawText text(String text) {
            this.text = text;
            return this;
        }

        public DrawText align(Paint.Align align) {
            paint.setTextAlign(align);
            return this;
        }

        public DrawText typeFace(Typeface typeface) {
            paint.setTypeface(typeface);
            return this;
        }

        public DrawText color(int color) {
            paint.setColor(color);
            return this;
        }

        public DrawText size(float textSize) {
            paint.setTextSize(textSize);
            return this;
        }

        /**
         * When setting this, the text is drawn on given path
         * Position is then seen relative to the path position
         *
         * @param path !=null
         */
        public DrawText path(Path path) {
            this.path = path;
            return this;
        }

        @Override
        public void reset() {
            super.reset();
            text = "";
            x = y = 0;
            paint.reset();
            path = null;
        }

        @Override
        public void render(Canvas canvas) {
            //TODO should coordinates be rounded/truncated to int to be pixel perfect?
            //calculate screen coordinates
            float sx = Utils.worldToScreenX(x);
            float sy = Utils.worldToScreenY(y);
            if (path != null)
                canvas.drawTextOnPath(text, path, sx, sy, paint);
            else
                canvas.drawText(text, sx, sy, paint);
        }

        @Override
        public void free() {
            textCommandPool.free(this);
        }
    }
}
