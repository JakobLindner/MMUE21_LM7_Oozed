package at.ac.tuwien.mmue_lm7.game;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ScoreDAO {
    @Query("SELECT * FROM score WHERE id=1")
    Score getScore();
    @Query("DELETE FROM score")
    void clearScore();
    @Insert
    void insertScore(Score score);
}
