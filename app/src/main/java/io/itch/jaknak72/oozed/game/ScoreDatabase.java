package io.itch.jaknak72.oozed.game;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Database class for scores, Accessible via singleton pattern
 * @author simon
 */
@Database(entities={Score.class}, version = 1)
public abstract class ScoreDatabase extends RoomDatabase {
    public abstract ScoreDAO scoreDAO();

    private static ScoreDatabase INSTANCE;
    public static ScoreDatabase get(Context context) {
        if(INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                    context.getApplicationContext(),
                    ScoreDatabase.class,
                    "score_database"
            ).build();
        }
        return INSTANCE;
    }
}
