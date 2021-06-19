package io.itch.jaknak72.oozed.game;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

/**
 * Interface for interacting with current highscore
 * @author simon
 */
@Dao
public interface ScoreDAO {
    @Query("SELECT * FROM score WHERE id=1")
    Score getScore();
    @Query("DELETE FROM score")
    void clearScore();
    @Insert
    void insertScore(Score score);
}
