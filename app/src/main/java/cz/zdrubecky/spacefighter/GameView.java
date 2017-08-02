package cz.zdrubecky.spacefighter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

import cz.zdrubecky.spacefighter.model.Boom;
import cz.zdrubecky.spacefighter.model.Enemy;
import cz.zdrubecky.spacefighter.model.Friend;
import cz.zdrubecky.spacefighter.model.Player;
import cz.zdrubecky.spacefighter.model.Star;


public class GameView extends SurfaceView implements Runnable {
    //mContext to be used in onTouchEvent to cause the activity transition from GameAvtivity to MainActivity.
    Context mContext;

    //boolean variable to track if the game is mPlaying or not
    volatile boolean mPlaying;

    //the game thread
    private Thread mGameThread = null;

    //adding the mPlayer to this class
    private Player mPlayer;

    //These objects will be used for drawing
    private Paint mPaint;
    private Canvas mCanvas;
    private SurfaceHolder mSurfaceHolder;

    //Adding mEnemy object array
    private Enemy mEnemy;

    //created a reference of the class Friend
    private Friend mFriend;

    //Adding 3 mEnemy you may increase the size
    private int mEnemyCount = 3;

    //Adding an mStars list
    private ArrayList<Star> mStars = new ArrayList<>();

    //defining a mBoom object to display blast
    private Boom mBoom;

    //a mScreenX holder
    int mScreenX;

    //to count the number of Misses
    int mCountMisses;

    //indicator that the enemy has just entered the game screen
    boolean mFlag;

    //an indicator if the game is Over
    private boolean mGameOver;

    //the mScore holder
    int mScore;

    //the high Scores Holder
    int mHighScores[] = new int[4];

    //Shared Prefernces to store the High Scores
    SharedPreferences mSharedPreferences;

    //the mediaplayer objects to configure the background music
    static MediaPlayer sGameOnsound;
    final MediaPlayer mKilledEnemysound;
    final MediaPlayer mGameOverSound;


    //Class constructor
    public GameView(Context context, int screenX, int screenY) {
        super(context);

        //initializing mContext
        this.mContext = context;

        //initializing mPlayer object
        //this time also passing screen size to mPlayer constructor
        mPlayer = new Player(context, screenX, screenY);

        //initializing drawing objects
        mSurfaceHolder = getHolder();
        mPaint = new Paint();

        //adding 100 mStars you may increase the number
        int starNums = 100;
        for (int i = 0; i < starNums; i++) {
            Star s  = new Star(screenX, screenY);
            mStars.add(s);
        }

        //single enemy initialization
        mEnemy = new Enemy(context, screenX, screenY);

        //initializing mBoom object
        mBoom = new Boom(context);

        //initializing the Friend class object
        mFriend = new Friend(context, screenX, screenY);

        this.mScreenX = screenX;

        mCountMisses = 0;

        mGameOver = false;

        //setting the mScore to 0 initially
        mScore = 0;

        mSharedPreferences = context.getSharedPreferences("SHAR_PREF_NAME",Context.MODE_PRIVATE);

        //initializing the array high scores with the previous values
        mHighScores[0] = mSharedPreferences.getInt("score1",0);
        mHighScores[1] = mSharedPreferences.getInt("score2",0);
        mHighScores[2] = mSharedPreferences.getInt("score3",0);
        mHighScores[3] = mSharedPreferences.getInt("score4",0);

        //initializing the media players for the game sounds
        sGameOnsound = MediaPlayer.create(context,R.raw.gameon);
        mKilledEnemysound = MediaPlayer.create(context,R.raw.killedenemy);
        mGameOverSound = MediaPlayer.create(context,R.raw.gameover);

        //starting the game music as the game starts
        sGameOnsound.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                //stopping the boosting when screen is released
                mPlayer.stopBoosting();
                break;
            case MotionEvent.ACTION_DOWN:
                //boosting the space jet when screen is pressed
                mPlayer.setBoosting();
                break;
        }

        //if the game's over, tapping on game Over screen sends you to MainActivity
        if(mGameOver) {
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                mContext.startActivity(new Intent(mContext,MainActivity.class));
            }
        }

        return true;
    }

    @Override
    public void run() {
        while (mPlaying) {
            //to update the frame (coordinates etc.)
            update();

            //to draw the frame
            draw();

            //to control the fps
            control();
        }
    }


    private void update() {
        //incrementing mScore as time passes
        mScore++;

        //updating mPlayer position
        mPlayer.update();

        //setting mBoom outside the screen
        mBoom.setX(-250);
        mBoom.setY(-250);

        //Updating the mStars with mPlayer speed
        for (Star s : mStars) {
            s.update(mPlayer.getSpeed());
        }

        //setting the mFlag true when the enemy just enters the screen
        if(mEnemy.getX()== mScreenX) {
            mFlag = true;
        }

        mEnemy.update(mPlayer.getSpeed());

        //if collision occurs with mPlayer
        if (Rect.intersects(mPlayer.getDetectCollision(), mEnemy.getDetectCollision())) {
            //displaying mBoom at that location
            mBoom.setX(mEnemy.getX());
            mBoom.setY(mEnemy.getY());

            //mPlaying a sound at the collision between mPlayer and the enemy
            mKilledEnemysound.start();

            mEnemy.setX(-200);
        } else if (mFlag) {
            // the condition where mPlayer misses the enemy
            //if the enemy has just entered
            //if mPlayer's x coordinate is more than the mEnemy's x coordinate.i.e. enemy has just passed across the mPlayer
            if(mPlayer.getDetectCollision().exactCenterX() >= mEnemy.getDetectCollision().exactCenterX()) {
                //increment mCountMisses
                mCountMisses++;

                //setting the mFlag false so that the else part is executed only when new enemy enters the screen
                mFlag = false;

                //if no of Misses is equal to 3, then game is over.
                if(mCountMisses ==3) {
                    endGame();
                }
            }
        }

        //updating the mFriend ships coordinates
        mFriend.update(mPlayer.getSpeed());
        //checking for a collision between mPlayer and a mFriend
        if(Rect.intersects(mPlayer.getDetectCollision(), mFriend.getDetectCollision())) {

            //displaying the mBoom at the collision
            mBoom.setX(mFriend.getX());
            mBoom.setY(mFriend.getY());

            endGame();
        }
    }

    private void draw() {
        //checking if surface is valid
        if (mSurfaceHolder.getSurface().isValid()) {
            //locking the mCanvas
            mCanvas = mSurfaceHolder.lockCanvas();

            //drawing a background color for mCanvas
            mCanvas.drawColor(Color.BLACK);

            //setting the mPaint color to white to draw the mStars
            mPaint.setColor(Color.WHITE);

            //drawing all mStars
            for (Star s : mStars) {
                mPaint.setStrokeWidth(s.getStarWidth());
                mCanvas.drawPoint(s.getX(), s.getY(), mPaint);
            }

            //drawing the mScore on the game screen
            mPaint.setTextSize(30);
            mCanvas.drawText("Score:"+ mScore,100,50, mPaint);

            //Drawing the mPlayer
            mCanvas.drawBitmap(
                    mPlayer.getBitmap(),
                    mPlayer.getX(),
                    mPlayer.getY(),
                    mPaint);

            //drawing the mEnemy
            mCanvas.drawBitmap(
                    mEnemy.getBitmap(),
                    mEnemy.getX(),
                    mEnemy.getY(),
                    mPaint
            );

            //drawing mBoom image
            mCanvas.drawBitmap(
                    mBoom.getBitmap(),
                    mBoom.getX(),
                    mBoom.getY(),
                    mPaint
            );

            //drawing friends image
            mCanvas.drawBitmap(
                    mFriend.getBitmap(),
                    mFriend.getX(),
                    mFriend.getY(),
                    mPaint
            );

            //draw game Over when the game is over
            if(mGameOver) {
                mPaint.setTextSize(150);
                mPaint.setTextAlign(Paint.Align.CENTER);

                int yPos=(int) ((mCanvas.getHeight() / 2) - ((mPaint.descent() + mPaint.ascent()) / 2));
                mCanvas.drawText("Game Over", mCanvas.getWidth()/2,yPos, mPaint);
            }

            //Unlocking the mCanvas
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    private void control() {
        try {
            // simulate the 60 fps using a sleep
            mGameThread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        //when the game is paused
        //setting the variable to false
        mPlaying = false;
        try {
            //stopping the thread - wait for it to die
            mGameThread.join();
        } catch (InterruptedException e) {
        }
    }

    public void resume() {
        //when the game is resumed
        //starting the thread again
        mPlaying = true;
        mGameThread = new Thread(this);
        mGameThread.start();
    }

    private void endGame() {
        //setting mPlaying false to stop the game.
        mPlaying = false;
        mGameOver = true;

        //stopping the gameon music
        sGameOnsound.stop();
        //play the game over sound
        mGameOverSound.start();

        //Assigning the scores to the highscore integer array
        for(int i = 0; i < 4; i++) {
            if(mHighScores[i]< mScore) {
                final int finalI = i;
                mHighScores[i] = mScore;
                break;
            }
        }

        //storing the scores through shared Preferences
        SharedPreferences.Editor e = mSharedPreferences.edit();
        for(int i = 0; i < 4; i++) {
            int j = i+1;
            e.putInt("mScore"+j, mHighScores[i]);
        }
        e.apply();
    }

    //stop the music on exit
    public static void stopMusic() {
        sGameOnsound.stop();
    }
}
