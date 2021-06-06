package at.ac.tuwien.mmue_lm7;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.IOException;

import at.ac.tuwien.mmue_lm7.game.LevelLoader;
import at.ac.tuwien.mmue_lm7.game.ScoreDatabase;
import at.ac.tuwien.mmue_lm7.game.resources.SoundSystem;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //open database for later use
        ScoreDatabase.get(getApplicationContext());

        //initialize sound system
        SoundSystem.load(getApplicationContext());

        //init level list
        LinearLayout list = findViewById(R.id.level_list);
        if(list!=null) {
            String[] levels = getLevelNames();
            int i = 0;
            for(String level : levels) {
                int dotIndex = level.lastIndexOf('.');
                String noEnding = dotIndex==-1?level:level.substring(0,dotIndex);

                Button button = new Button(this);
                button.setText(noEnding);
                button.setId(i++);
                button.setOnClickListener(v -> {
                    startGame(noEnding);
                });

                list.addView(button);
            }
        }
    }

    public void onStartButtonClicked(View view) {
        startGame("1");
    }

    private void startGame(String startLevel) {
        //Switch to game activity
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(GameActivity.START_LEVEL_KEY,startLevel);
        startActivity(intent);
    }

    /**
     * @return array of all levels (files + coded)
     */
    private String[] getLevelNames() {
        //load level file names
        String[] levelFiles = new String[0];
        try {
            levelFiles = getAssets().list("levels");
        } catch(IOException e) {
            Log.e(TAG, "Exception listing level files", e);
        }

        //combine file list and coded level list
        String[] combined = new String[levelFiles.length+LevelLoader.levelsByName.size()];
        int i = 0;
        for(int filesI = 0;filesI<levelFiles.length;++i,++filesI)
            combined[i] = levelFiles[filesI];
        for(String level : LevelLoader.levelsByName.keySet()) {
            combined[i] = level;
            ++i;
        }

        return combined;
    }
}