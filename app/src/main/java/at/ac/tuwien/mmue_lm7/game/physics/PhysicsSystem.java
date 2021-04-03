package at.ac.tuwien.mmue_lm7.game.physics;

import java.util.ArrayList;

import at.ac.tuwien.mmue_lm7.game.objects.AABB;
import at.ac.tuwien.mmue_lm7.utils.BitUtils;
import at.ac.tuwien.mmue_lm7.utils.ObjectPool;
import at.ac.tuwien.mmue_lm7.utils.Subject;
import at.ac.tuwien.mmue_lm7.utils.Vec2;

/**
 * Manages all Bounding boxes, checks collisions between them
 */
public class PhysicsSystem {

    //events
    public final Subject<Contact> onGlobalCollision = new Subject<>();

    //Possible optimization: unordered array list
    private final ArrayList<AABB>[] aabbByLayer = new ArrayList[16];//16 because collision layer is a short

    //Contact management, to reduce frequent allocations and prevent unnecessary garbage collecting
    private final ObjectPool<Contact> contactPool = new ObjectPool<>(Contact::new);
    /**
     * Contacts that are generated during collision events, these get cleared at the beginning of each physics step
     */
    private final ArrayList<Contact> usedContacts = new ArrayList<Contact>(64);//TODO find good initial capacity

    /**
     * Adds given box to the physics system
     * @param box, !=null
     */
    public void addAABB(AABB box) {
        aabbByLayer[BitUtils.getRightmostSetBit(box.getCollisionMask())].add(box);
    }

    /**
     * removes given box from the physics system
     * @param box, !=null
     */
    public void removeAABB(AABB box) {
        aabbByLayer[BitUtils.getRightmostSetBit(box.getCollisionMask())].remove(box);
    }

    /**
     * Should be called when the collision mask or layer changes
     * @param box, !=null
     */
    public void updateAABB(AABB box) {
        removeAABB(box);
        addAABB(box);
    }

    public void update() {
        //reset and clear all contact objects
        contactPool.freeAll(usedContacts);
        usedContacts.clear();

        //calculate collisions
        for(short layer = 1,li = 0; li<CollisionLayers.MAX_LAYERS;++li,layer= (short) (1<<li)) {

            ArrayList<AABB> aabbs = aabbByLayer[li];
            for(int i = 0;i<aabbs.size();++i) {
                AABB a = aabbs.get(i);

                //check which layers should collisions be tested
                for(short alayer = 1, ali = 0; ali<CollisionLayers.MAX_LAYERS;++ali, alayer=(short) (1<<ali)) {
                    if((alayer&a.getCollisionMask())==0)
                        continue;

                    //check collisions against other aabbs
                    ArrayList<AABB> others = aabbByLayer[ali];
                    for(int j = 0;j<others.size();++j) {
                        //prevent self check
                        if(layer==alayer && i==j)
                            continue;

                        AABB b = others.get(j);
                        //prevent checks that have already be done
                        if((alayer<layer || (alayer==layer && j<i)) && (b.getCollisionMask()&a.getCollisionLayer())!=0)
                            continue;

                        //test collision
                        Contact contact = testCollision(a,b);
                        if(contact!=null) {
                            //generate inverted contact
                            Contact inverted = contactPool.obtain();
                            contact.makeInvertContact(inverted);

                            //register contact objects as used
                            usedContacts.add(contact);
                            usedContacts.add(inverted);

                            //emit collision events
                            a.onCollide.notify(contact);
                            b.onCollide.notify(inverted);
                            onGlobalCollision.notify(contact);
                        }
                    }
                }
            }
        }
    }


    /**
     * Tests whether or not the two given aabbs collide with each other and generates a contact object in that case
     * inspired by:
     * https://noonat.github.io/intersect/
     * https://gamedev.docrobs.co.uk/first-steps-in-pico-8-hitting-things
     * @param a, !=null
     * @param b, !=null
     * @return null, if there is no collision, a contact object obtained from the contact pool from the view of a
     */
    private Contact testCollision(AABB a, AABB b) {
        //check if distance between midpoints is smaller than sum of half widths
        float dx = a.position.x-b.position.x;
        float px = a.getHalfSize().x+b.getHalfSize().x - Math.abs(dx);
        if(px<=0)
            return null;

        //check if vertical distance between midpoints is smaller than sum of half heights
        float dy = Math.abs(a.position.y-b.position.y);
        float py = a.getHalfSize().y+b.getHalfSize().y - Math.abs(dy);
        if(py<=0)
            return null;

        //generate contact information
        //contact is assumed to be in reset state
        Contact contact = contactPool.obtain();
        //check which axis has the least overlap
        if(px<py) {
            int sx = (int) Math.signum(dx);
            contact.overlap.x = px*sx;
            contact.normal.x = sx;
            contact.position.x = a.position.x + a.getHalfSize().x * sx;
            contact.position.y = (a.position.y+b.position.y)*0.5f;
        }
        else {
            int sy = (int) Math.signum(dy);
            contact.overlap.y = py*sy;
            contact.normal.y = sy;
            //halfway between positions is used as estimate
            contact.position.x = (a.position.x+b.position.x)*0.5f;
            contact.position.y = a.position.y + a.getHalfSize().y * sy;
        }

        return contact;
    }

    //TODO sweep move

    public static class Contact implements ObjectPool.Poolable{
        private AABB box;
        private AABB other;
        private final Vec2 position = new Vec2(0,0);
        private final Vec2 normal = new Vec2(0,0);
        private final Vec2 overlap = new Vec2(0,0);

        @Override
        public void reset() {
            box = null;
            other = null;
            position.set(0,0);
            normal.set(0,0);
            overlap.set(0,0);
        }

        /**
         * sets the fields of the given contact to reflect the same contact from the view of the other aabb
         * @param inverted, !=null
         */
        public void makeInvertContact(Contact inverted) {
            inverted.box = other;
            inverted.other = box;
            inverted.position.set(position);
            inverted.normal.set(normal).invert();
            inverted.overlap.set(overlap).invert();
        }

        public AABB getBox() {
            return box;
        }

        public AABB getOther() {
            return other;
        }

        /**
         * @return closest position of contact
         */
        public Vec2 getPosition() {
            return position;
        }

        public Vec2 getNormal() {
            return normal;
        }

        public Vec2 getOverlap() {
            return overlap;
        }

    }
}
