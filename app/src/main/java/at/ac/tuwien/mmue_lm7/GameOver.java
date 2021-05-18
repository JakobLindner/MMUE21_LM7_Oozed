package at.ac.tuwien.mmue_lm7;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class GameOver extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        //set highscore text
        TextView highscore = findViewById(R.id.highscore);
        highscore.setText(getResources().getString(R.string.highscore,123));//TODO replace 123 with real highscore
    }
}