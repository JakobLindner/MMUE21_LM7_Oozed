package at.ac.tuwien.mmue_lm7.game;

/**
 * keeps track of some state of the current level, so it can be checked whether or not objectives have been fulfilled
 */
public class LevelStatusSystem {
    public static class LevelStatus {
        private int enemyCount = 0;
    }

    private LevelStatus levelStatus = new LevelStatus();

    public void clearLevelStatus() {
        levelStatus = new LevelStatus();
    }

    public int getEnemyCount() {
        return levelStatus.enemyCount;
    }

    public void increaseEnemyCount() {
        ++levelStatus.enemyCount;
    }

    public void decreaseEnemyCount() {
        --levelStatus.enemyCount;
    }
}