package at.ac.tuwien.mmue_lm7.utils;

import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

/**
 * Manages an ordered list of listeners that can be notified of events
 * Order is descending by priority
 * no guarantee is made for the order with listeners of the same priority
 * The values in the layers class can be used for priority values
 * since lambdas are not compareable and no equals can be applied a key has to be used when adding a listener in order to remov it again
 * @author simon
 * @param <Event> defines the type of events
 */
public class Subject<Event> {
    private class PrioritizedListener<Event> implements ObjectPool.Poolable,Comparable<PrioritizedListener<Event>>{
        Object key = null;
        Listener<Event> listener = null;
        int priority = 0;

        @Override
        public void reset() {
            key = null;
            listener = null;
            priority = 0;
        }

        @Override
        public int compareTo(PrioritizedListener<Event> o) {
            return Integer.compare(priority,o.priority);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PrioritizedListener<?> that = (PrioritizedListener<?>) o;
            return Objects.equals(listener, that.listener);
        }

        @Override
        public int hashCode() {
            return Objects.hash(listener, priority);
        }
    }


    private LinkedList<PrioritizedListener<Event>> observers = new LinkedList<>();
    private ObjectPool<PrioritizedListener<Event>> listenerPool = new ObjectPool<>(PrioritizedListener<Event>::new);

    /**
     * Notifies all listeners of given event
     * @param event, !=null
     */
    public void notify(Event event) {
        for(PrioritizedListener<Event> l : observers) {
            if(l.listener.handle(event))
                return;
        }
    }

    /**
     * Adds given listener to the end of the listener list
     * @param listener, !=null
     */
    public void addListener(Object key, Listener<Event> listener) {
        int lastPriority = observers.isEmpty()?0:observers.getLast().priority;

        PrioritizedListener<Event> pl = listenerPool.obtain();
        pl.key = key;
        pl.listener = listener;
        pl.priority = lastPriority;

        observers.addLast(pl);
    }

    /**
     * Adds given listener with given priority to the listener list
     * @param listener
     * @param priority
     */
    public void addListener(Object key, Listener<Event> listener, int priority) {
        //find position to add listener
        int i = 0;
        for(PrioritizedListener<Event> pl : observers) {
            if(pl.priority<priority)
                break;
        }

        //create listener object
        PrioritizedListener<Event> pl = listenerPool.obtain();
        pl.key = key;
        pl.listener = listener;
        pl.priority = priority;

        observers.add(i,pl);
    }

    /**
     * Removes all listeners with given key from the list of listeners
     * @param key
     */
    public void removeListener(Object key) {
        for(Iterator<PrioritizedListener<Event>> it = observers.iterator();it.hasNext();) {
            PrioritizedListener<Event> pl = it.next();
            if(pl.key.equals(key))
                it.remove();
        }
    }
}
