package at.ac.tuwien.mmue_lm7.game;

import android.util.Log;

import java.util.Iterator;
import java.util.LinkedList;

import at.ac.tuwien.mmue_lm7.Constants;
import at.ac.tuwien.mmue_lm7.utils.ObjectPool;

/**
 * With the timing system you can register functions, which are called after a given delay
 * The timing system is updated before the game scene, the timer is reduced by 1 every update
 * If the timer of a registered function reaches 0, it is executed
 * @author simon
 */
public class TimingSystem {
    private static final String TAG = "TimingSystem";

    public static class TimedAction implements ObjectPool.Poolable {

        private int remainingFrames = 0;
        private Action action = null;

        private TimedAction() {

        }

        @Override
        public void reset() {
            remainingFrames = 0;
            action = null;
        }
    }

    @FunctionalInterface
    public interface Action {
        void perform();
    }

    private final ObjectPool<TimedAction> freeActions = new ObjectPool<>(TimedAction::new,16);
    private final LinkedList<TimedAction> waitingActions = new LinkedList<TimedAction>();
    /**
     * new timed actions are first added to this collection, so timed actions can be added inside a timed action
     */
    private final LinkedList<TimedAction> newActions = new LinkedList<>();

    /**
     * if true all actions are cleared on next update
     */
    private boolean clearActions = false;

    public TimingSystem() {

    }

    public void update() {
        if(clearActions) {
            freeActions.freeAll(waitingActions);
            waitingActions.clear();
            clearActions = false;
        }

        //add new actions
        waitingActions.addAll(newActions);
        newActions.clear();

        //update timers and execute actions
        for(TimedAction action : waitingActions) {
            --action.remainingFrames;
            if(action.remainingFrames <= 0) {
                action.action.perform();
            }
        }

        //remove every Action from linked list, which has been executed
        //Sadly we cannot use LinkedList::removeIf with API 19
        for(Iterator<TimedAction> it = waitingActions.iterator(); it.hasNext();) {
            TimedAction action = it.next();
            if(action.remainingFrames<=0) {
                it.remove();
                freeActions.free(action);
            }
        }
    }

    /**
     *
     * @param action !=null
     * @param frameDelay frames that have to pass until the action is executed
     */
    public void addDelayedAction(Action action,int frameDelay) {
        TimedAction timedAction = freeActions.obtain();
        timedAction.action = action;
        timedAction.remainingFrames = frameDelay;

        newActions.add(timedAction);
    }

    /**
     *
     * @param action !=null
     * @param secondsDelay delay in seconds until action is executed
     */
    public void addDelayedActionSeconds(Action action, float secondsDelay) {
        addDelayedAction(action, (int)(secondsDelay/ Constants.FIXED_DELTA)+1);
    }

    /**
     * Removes all queued up actions
     */
    public void clearActions() {
        clearActions = true;
    }
}
