package at.ac.tuwien.mmue_lm7.utils;

import java.util.LinkedList;

/**
 * @author simon
 * Manages an ordered list of listeners that can be notified of events
 * @param <Event> defines the type of events
 */
public class Subject<Event> {
    private LinkedList<Listener<Event>> observers = new LinkedList<>();

    /**
     * Notifies all listeners of given event
     * @param event, !=null
     */
    public void notify(Event event) {
        for(Listener<Event> l : observers) {
            if(l.handle(event))
                return;
        }
    }

    /**
     * Adds given listener to the end of the listener list
     * @param listener, !=null
     */
    public void addListener(Listener<Event> listener) {
        observers.add(listener);
    }

    /**
     * Removes given listener from the list of listeners
     * @param listener
     */
    public void removeListener(Listener<Event> listener) {
        observers.remove(listener);
    }
}
