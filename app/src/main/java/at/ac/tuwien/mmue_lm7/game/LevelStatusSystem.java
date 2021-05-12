package at.ac.tuwien.mmue_lm7.game;

/**
 * keeps track of some state of the current level, so it can be checked whether or not objectives have been fulfilled
 */
public class LevelStatusSystem {
    private int enemyCount = 0;

    public int getEnemyCount() {
        return enemyCount;
    }

    public void increaseEnemyCount() {
        ++enemyCount;
    }

    public void decreaseEnemyCount() {
        --enemyCount;
    }
}
