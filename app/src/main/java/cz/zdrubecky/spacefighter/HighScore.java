package cz.zdrubecky.spacefighter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class HighScore extends AppCompatActivity {

    TextView mTextView, mTextView2, mTextView3, mTextView4;

    SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        //initializing the textViews
        mTextView = (TextView) findViewById(R.id.textView);
        mTextView2 = (TextView) findViewById(R.id.textView2);
        mTextView3 = (TextView) findViewById(R.id.textView3);
        mTextView4 = (TextView) findViewById(R.id.textView4);

        mSharedPreferences = getSharedPreferences("SHAR_PREF_NAME", Context.MODE_PRIVATE);

        //setting the values to the textViews
        mTextView.setText("1."+ mSharedPreferences.getInt("score1",0));
        mTextView2.setText("2."+ mSharedPreferences.getInt("score2",0));
        mTextView3.setText("3."+ mSharedPreferences.getInt("score3",0));
        mTextView4.setText("4."+ mSharedPreferences.getInt("score4",0));


    }
}
