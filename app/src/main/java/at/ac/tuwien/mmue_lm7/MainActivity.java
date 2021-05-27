package at.ac.tuwien.mmue_lm7;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import at.ac.tuwien.mmue_lm7.game.ScoreDatabase;
import at.ac.tuwien.mmue_lm7.game.resources.SoundSystem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //open database for later use
        ScoreDatabase.get(getApplicationContext());

        //initialize sound system
        SoundSystem.get(getApplicationContext());
    }

    public void onStartButtonClicked(View view) {
        //Switch to game activity
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }
}