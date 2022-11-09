package io.itch.jaknak72.oozed.game;

/**
 * keeps track of some state of the current level, so it can be checked whether or not objectives have been fulfilled
 * @author simon
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
