package cz.zdrubecky.spacefighter;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton mButtonPlay;
    private ImageButton mButtonScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setting the orientation to landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Init buttons
        mButtonPlay = (ImageButton) findViewById(R.id.buttonPlay);
        mButtonScore = (ImageButton) findViewById(R.id.buttonScore);

        // Handle the click events by itself
        mButtonPlay.setOnClickListener(this);
        mButtonScore.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // Start the appropriate activity
        if (v == mButtonPlay) {
            startActivity(new Intent(MainActivity.this, GameActivity.class));
        } else if (v == mButtonScore) {
            startActivity(new Intent(MainActivity.this, HighScore.class));
        }
    }
}
