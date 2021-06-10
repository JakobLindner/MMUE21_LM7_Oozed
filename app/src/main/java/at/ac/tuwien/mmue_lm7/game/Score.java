package at.ac.tuwien.mmue_lm7.game;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity class for score entries in room db
 * @author simon
 */
@Entity(tableName = "score")
public class Score {

    /**
     * There is only one score so primary key can be constant
     */
    @PrimaryKey
    public int id = 1;
    /**
     * Level reached
     */
    public int score;
    /**
     * time in frames
     */
    public int time;
    /**
     * true if all levels have been completed
     */
    public boolean gameCompleted;

    public Score(int score, int time, boolean gameCompleted) {
        this.score = score;
        this.time = time;
        this.gameCompleted = gameCompleted;
    }

    public int getId(){
        return id;
    }


    public int getScore() {
        return score;
    }

    public int getTime() {
        return time;
    }

    public boolean gameCompleted() {
        return gameCompleted;
    }
}
