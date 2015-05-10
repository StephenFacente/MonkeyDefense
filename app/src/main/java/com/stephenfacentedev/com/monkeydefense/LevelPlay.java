package com.stephenfacentedev.com.monkeydefense;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LevelPlay extends Activity {

    FrameLayout gameScreen;
    LinearLayout bananaHolder;
    TextView count;
    int[] availableBananas = new int[] {1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
    int[] bananaIds = new int[10];
    int[] monkeyCenterPoint = new int[2];
    float[] lastTouchPoint;
    float splatRadius = 20;
    int level =1;
    int score=0;
    int secondsLeft;
    boolean _alreadyEnded=false;
    boolean _canThrow=true;
    Vibrator vibe;

    Random randomNumberGenerator = new Random();

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);   //new
        getActionBar().hide();                                   //new
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_level_play);
        ParseObject gameScore = new ParseObject("GamesPlayed");
        gameScore.saveInBackground();

        vibe = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/font.otf");

        TextView v = (TextView)findViewById(R.id.levelView);
        v.setText("Wave: " + level);
        v.setTypeface(font);

        TextView scoreView = (TextView)findViewById(R.id.scoreView);
        scoreView.setText("Score: ");
        scoreView.setTypeface(font);

        TextView score = (TextView)findViewById(R.id.score);
        score.setText("0");
        score.setTypeface(font);

        TextView countdown = (TextView)findViewById(R.id.countDownText);
        countdown.setText("5");
        countdown.setTypeface(font);


        monkeyCenterPoint = getCenter(findViewById(R.id.monkey));

        gameScreen = (FrameLayout)findViewById(R.id.gameScreen);
        bananaHolder = (LinearLayout)findViewById(R.id.bananaHolder);

        bananaIds[0] = findViewById(R.id.banana1).getId();
        bananaIds[1] = findViewById(R.id.banana2).getId();
        bananaIds[2] = findViewById(R.id.banana3).getId();
        bananaIds[3] = findViewById(R.id.banana4).getId();
        bananaIds[4] = findViewById(R.id.banana5).getId();
        bananaIds[5] = findViewById(R.id.banana6).getId();
        bananaIds[6] = findViewById(R.id.banana7).getId();
        bananaIds[7] = findViewById(R.id.banana8).getId();
        bananaIds[8] = findViewById(R.id.banana9).getId();
        bananaIds[9] = findViewById(R.id.banana10).getId();



        secondsLeft = 0;
        count = (TextView) findViewById(R.id.countDownText);

        new CountDownTimer(3000, 100) {
            public void onTick(long ms) {
                if (Math.round((float)ms / 1000.0f) != secondsLeft)
                {
                    secondsLeft = Math.round((float)ms / 1000.0f);
                    count.setText(secondsLeft+" ");
                }
            }

            public void onFinish() {
                gameScreen.removeView(count);

                addHandler();

                addBats();
            }
        }.start();
    }

    private void addHandler()
    {
        gameScreen.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if(!_canThrow) return true;

                    _canThrow = false;

                    Handler handler = new Handler();

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            _canThrow=true;
                        }
                    }, 250);

                    final float[] touchPoint = new float[]{event.getX(), event.getY()};
                    lastTouchPoint = touchPoint;
                    //animate to that point

                    //gets centerpoint of monkey
                    ImageView monkey = (ImageView)findViewById(R.id.monkey);



                    ImageView banana = getAvailableBanana();



                    if(banana != null)
                    {
                        final ImageView secondBanana = new ImageView(getApplicationContext());
                        secondBanana.setImageResource(R.drawable.banana_bright);

                        monkeyCenterPoint = getCenter(findViewById(R.id.monkey));

                        /*
                        //make banana half size
                        banana.getLayoutParams().width = banana.getWidth()/2;
                        banana.getLayoutParams().height = banana.getHeight()/2;
                        */
                        //move banana to gamescreen
                        banana.setVisibility(View.INVISIBLE);
                        //bananaHolder.removeView(banana);
                        gameScreen.addView(secondBanana);

                        secondBanana.getLayoutParams().width = gameScreen.getHeight()/12;
                        secondBanana.getLayoutParams().height = gameScreen.getHeight()/12;

                        //create animation from points
                        TranslateAnimation throwBanana = new TranslateAnimation(Animation.ABSOLUTE, (float) monkeyCenterPoint[0], Animation.ABSOLUTE, (float) touchPoint[0] - (secondBanana.getLayoutParams().width / 2), Animation.ABSOLUTE, (float) monkeyCenterPoint[1], Animation.ABSOLUTE, (float) touchPoint[1] - (secondBanana.getLayoutParams().height/2));
                        throwBanana.setDuration(650);
                        throwBanana.setFillAfter(true);

                        //create rotate animation
                        RotateAnimation spinBanana = new RotateAnimation(0, 360, secondBanana.getLayoutParams().width/2, secondBanana.getLayoutParams().height/2);
                        spinBanana.setDuration(500);
                        spinBanana.setFillAfter(true);


                        AnimationSet bothAnimations = new AnimationSet(false);//false mean dont share interpolators
                        bothAnimations.setFillAfter(true);
                        bothAnimations.addAnimation(spinBanana);
                        bothAnimations.addAnimation(throwBanana);
                        bothAnimations.setInterpolator(new LinearInterpolator());

                        bothAnimations.setAnimationListener(new Animation.AnimationListener(){
                            @Override
                            public void onAnimationStart(Animation arg0) {
                            }
                            @Override
                            public void onAnimationRepeat(Animation arg0) {
                            }
                            @Override
                            public void onAnimationEnd(Animation arg0) {
                                gameScreen.removeView(secondBanana);
                                createSplat(touchPoint);
                            }
                        });


                        secondBanana.startAnimation(bothAnimations);

                        for(int i=0; i<availableBananas.length; i++)
                        {
                            if(availableBananas[i]==1) return true;
                        }

                        Toast.makeText(getApplicationContext(), "No More Bananas", Toast.LENGTH_SHORT).show();
                    }

                }
                return true; // indicate event was handled
            }
        });
    }

    private void addBats()
    {
        Handler addBatToScreen = new Handler();

        for(int i=0; i<level; i++)
        {
            final int finalI = i;
            addBatToScreen.postDelayed(new Runnable() {

                @Override
                public void run() {
                    final ImageView bat = new ImageView(getApplicationContext());
                    bat.setImageResource(R.drawable.bats04right);
                    gameScreen.addView(bat);
                    bat.getLayoutParams().width = gameScreen.getHeight()/8;
                    bat.getLayoutParams().height = gameScreen.getHeight()/8;
                    bat.setTranslationY((float)randomNumberGenerator.nextInt(gameScreen.getHeight()/4));
                    bat.setTag("bat");

                    int repeatValue = randomNumberGenerator.nextInt(5)+5;

                    addAnimator(bat, repeatValue);

                }
            }, 100 * i);
        }
    }

    private void addAnimator(final View view, int repeat)
    {
        final ObjectAnimator objectAnimator= ObjectAnimator.ofFloat(view, "translationX", -view.getLayoutParams().width, gameScreen.getWidth());
        objectAnimator.setDuration(randomNumberGenerator.nextInt(500) + 2500);
        objectAnimator.setRepeatMode(2);
        objectAnimator.setRepeatCount(repeat);
        if(repeat != ValueAnimator.INFINITE) {
            objectAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if(view.getParent()!= null) swoopForBanana(view);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    //change Y Randomly
                    //view.setTranslationY(randomNumberGenerator.nextInt(gameScreen.getHeight() / 3));


                    //change interpolater randomly
                    switch (randomNumberGenerator.nextInt(3)) {
                        case 0:
                            objectAnimator.setInterpolator(new AccelerateInterpolator());
                            break;
                        case 1:
                            objectAnimator.setInterpolator(new DecelerateInterpolator());
                            break;
                        case 2:
                            objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                            break;
                        case 3:
                            objectAnimator.setInterpolator(new LinearInterpolator());
                            break;
                    }

                }
            });
        }
        objectAnimator.start();
    }

    private void swoopForBanana(View view)
    {
        final ImageView bat = (ImageView)view;

        final ImageView availableBanana = getAvailableBanana();
        if(availableBanana==null)
        {
            return;
        }
        final int[] availableBananaPosition = getAvailableBananaPosition(availableBanana);


        final AnimatorSet goToBanana = new AnimatorSet();
        goToBanana.setDuration(randomNumberGenerator.nextInt(500)+1000);
        goToBanana.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(bat.getParent()!= null) grabBanana(bat, availableBanana, availableBananaPosition, (int) goToBanana.getDuration()*3, bat.getTranslationX(), bat.getTranslationY());
                //MOVE BANANA WITH BAT HERE
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        ObjectAnimator goToBananaX= ObjectAnimator.ofFloat(view, "translationX", bat.getTranslationX(), (float)availableBananaPosition[0]);
        //goToBananaX.setDuration(goToBanana.getDuration());

        ObjectAnimator goToBananaY= ObjectAnimator.ofFloat(view, "translationY", bat.getTranslationY(), (float)availableBananaPosition[1]);
        //goToBananaY.setDuration(goToBanana.getDuration());

        List<Animator> animations = new ArrayList<Animator>();
        animations.add(goToBananaX);
        animations.add(goToBananaY);

        goToBanana.playTogether(animations);
        goToBanana.start();

    }

    private void grabBanana(final View bat, final View banana, int[] bananaPosition, int duration, float batX, float batY)
    {
        final ImageView secondBanana = new ImageView(getApplicationContext());
        secondBanana.setImageResource(R.drawable.banana_bright);
        //move banana to gamescreen
        banana.setVisibility(View.INVISIBLE);
        //bananaHolder.removeView(banana);
        gameScreen.addView(secondBanana);

        secondBanana.getLayoutParams().width = gameScreen.getHeight()/8;
        secondBanana.getLayoutParams().height = gameScreen.getHeight()/8;

        secondBanana.setTag("banana");

        int randomToXValue = randomNumberGenerator.nextInt(gameScreen.getWidth());

        final AnimatorSet moveBatOffScreen = new AnimatorSet();
        moveBatOffScreen.setDuration(duration);
        moveBatOffScreen.setInterpolator(new AccelerateInterpolator());
        moveBatOffScreen.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(bat.getParent()!= null) {
                    gameScreen.removeView(bat);
                    gameScreen.removeView(secondBanana);
                    checkForPlayerLose();
                    checkForLevelCompletion();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        ObjectAnimator offScreenX= ObjectAnimator.ofFloat(bat, "translationX", bat.getTranslationX(), (float)randomToXValue);
        ObjectAnimator offScreenY= ObjectAnimator.ofFloat(bat, "translationY", bat.getTranslationY(), (float)-bat.getLayoutParams().height);

        List<Animator> animations = new ArrayList<Animator>();
        animations.add(offScreenX);
        animations.add(offScreenY);
        moveBatOffScreen.playTogether(animations);
        moveBatOffScreen.start();

        final AnimatorSet moveBananaWithBat = new AnimatorSet();
        moveBananaWithBat.setDuration(duration);
        moveBananaWithBat.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator bananaOffScreenX= ObjectAnimator.ofFloat(secondBanana, "translationX", bat.getTranslationX(), (float)randomToXValue);
        ObjectAnimator bananaOffScreenY= ObjectAnimator.ofFloat(secondBanana, "translationY", bat.getTranslationY(), (float)-bat.getLayoutParams().height);

        List<Animator> bananaAnimations = new ArrayList<Animator>();
        bananaAnimations.add(bananaOffScreenX);
        bananaAnimations.add(bananaOffScreenY);
        moveBananaWithBat.playTogether(bananaAnimations);
        moveBananaWithBat.start();
    }

    public int[] getAvailableBananaPosition(View view)
    {
        if(view==null) return null;

        int[] returnValue = new int[2];
        view.getLocationOnScreen(returnValue);
        return returnValue;
    }

    private void createSplat(float[] touchPoint)
    {
        splatRadius = gameScreen.getHeight()/20;
        checkForBats(touchPoint);

        final ImageView splatView = new ImageView(getApplicationContext());
        splatView.setImageResource(R.drawable.splat);

        gameScreen.addView(splatView);

        splatView.getLayoutParams().width = (int)splatRadius*2;
        splatView.getLayoutParams().height = (int)splatRadius*2;

        splatView.setTranslationX(touchPoint[0]-splatRadius);
        splatView.setTranslationY(touchPoint[1]-splatRadius);

        ScaleAnimation growSplat = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.ABSOLUTE, touchPoint[0], ScaleAnimation.ABSOLUTE, touchPoint[1]);
        growSplat.setDuration(50);
        growSplat.setFillAfter(true);

        growSplat.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation arg0) {
            }
            @Override
            public void onAnimationRepeat(Animation arg0) {
            }
            @Override
            public void onAnimationEnd(Animation arg0) {
                fadeSplatAndRemove(splatView);
            }
        });

        splatView.startAnimation(growSplat);

    }

    private boolean checkForBanana(int[] batCenter)
    {
        try {
            int viewsInGameScreen = gameScreen.getChildCount();
            for (int i = 0; i < viewsInGameScreen; i++) {
                if (gameScreen.getChildAt(i) instanceof ImageView) {
                    //if the image is not the monkey check for hit
                    //if(!((ImageView) gameScreen.getChildAt(i)).getDrawable())
                    //{
                    final ImageView image = (ImageView) gameScreen.getChildAt(i);

                    int[] imageCenter = getCenter(image);

                    double distance = getDistance(imageCenter, batCenter);

                    if ((int) distance < 25 && image.getTag() != null && image.getTag().toString().equals("banana")) {
                        addBananaToHolder();
                        gameScreen.removeView(image);
                        return true;
                    }
                }
            }
            return false;
        }
        catch(Exception e)
        {
            return false;
        }
    }

    private void bananaFall(final View image)
    {
        final AnimatorSet moveBananaToGround = new AnimatorSet();
        moveBananaToGround.setDuration(500);
        moveBananaToGround.setInterpolator(new AccelerateInterpolator());
        moveBananaToGround.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                gameScreen.removeView(image);
                addBananaToHolder();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        ObjectAnimator bananaX= ObjectAnimator.ofFloat(image, "translationX", image.getTranslationX(), (float)image.getTranslationX());
        ObjectAnimator bananaY= ObjectAnimator.ofFloat(image, "translationY", image.getTranslationY(), (float)gameScreen.getHeight()-(float)image.getLayoutParams().height);

        List<Animator> bananaAnimations = new ArrayList<Animator>();
        bananaAnimations.add(bananaX);
        bananaAnimations.add(bananaY);
        moveBananaToGround.playTogether(bananaAnimations);

        moveBananaToGround.start();
    }

    private void checkForBats(float[] touchpoint)
    {
        Log.v("Info", "Checking for Bats");
        try {
            int viewsInGameScreen = gameScreen.getChildCount();
            for (int i = 0; i < viewsInGameScreen; i++) {
                if (gameScreen.getChildAt(i) instanceof ImageView) {

                    //if the image is not the monkey check for hit
                    //if(!((ImageView) gameScreen.getChildAt(i)).getDrawable())
                    //{
                    if ((ImageView)gameScreen.getChildAt(i)==null)break;
                    ImageView image = (ImageView) gameScreen.getChildAt(i);

                    int[] imageCenter = getCenter(image);

                    double distance = getDistance(imageCenter, touchpoint);

                    if ((int) distance < (int) ((image.getLayoutParams().width / 2) + splatRadius) && image.getTag() != null && image.getTag().toString().equals("bat")) {
                        Log.v("Info", "Found an imageview close");
                        if (!checkForBanana(imageCenter)) {
                            for (int j = 0; j < availableBananas.length; j++) {
                                if (availableBananas[j] == 0) {
                                    if (level >= (randomNumberGenerator.nextInt(level) * 2))
                                        randomlyDropBanana();
                                    break;
                                }
                            }
                        }

                        Log.v("Info", "Removing Bat");

                        score += 100;
                        TextView myScore = (TextView) findViewById(R.id.score);
                        myScore.setText(score + "");
                        gameScreen.removeView(image);
                        checkForBats(touchpoint);
                        return;
                    }
                }
            }
        }
        catch (Exception e)
        {
            return;
        }



        checkForPlayerLose();
        checkForLevelCompletion();
    }

    private void randomlyDropBanana()
    {
        /*
        ImageView banana = new ImageView(this);
        banana.setImageResource(R.drawable.banana_bright);
        gameScreen.addView(banana);
        banana.setTranslationX(randomNumberGenerator.nextInt(gameScreen.getWidth()));
        banana.getLayoutParams().width = gameScreen.getHeight()/8;
        banana.getLayoutParams().height = gameScreen.getHeight()/8;
        banana.setTranslationY(-banana.getLayoutParams().height);

        bananaFall(banana);
        */
        addBananaToHolder();
        vibe.vibrate(100);
        Toast.makeText(getApplicationContext(), "Extra Banana Added", Toast.LENGTH_SHORT).show();
    }

    private void checkForPlayerLose()
    {
        for(int i=0; i<availableBananas.length; i++)
        {
            if(availableBananas[i]==1) return;
        }

        int viewsInGameScreen = gameScreen.getChildCount();
        for (int i=0; i<viewsInGameScreen; i++)
        {
            if(gameScreen.getChildAt(i) instanceof ImageView)
            {
                //if the image is not the monkey check for hit
                //if(!((ImageView) gameScreen.getChildAt(i)).getDrawable())
                //{
                ImageView image = (ImageView)gameScreen.getChildAt(i);
                if(image.getTag()!=null&&image.getTag().toString().equals("banana")) return;
            }
        }

        for(int i=0; i<availableBananas.length; i++)
        {
            if(availableBananas[i]==1) return;
        }

        Handler handler =  new Handler();
        handler.postDelayed(end, 2000);
    }



    public Runnable end = new Runnable() {

        public void run() {
            endGame();
        }
    };

    private void checkForLevelCompletion()
    {
        int viewsInGameScreen = gameScreen.getChildCount();

        int batsLeft = 0;
        for (int i=0; i<viewsInGameScreen; i++)
        {
            if(gameScreen.getChildAt(i) instanceof ImageView && gameScreen.getChildAt(i).getTag()!=null&&gameScreen.getChildAt(i).getTag().toString().equals("bat"))
            {
                batsLeft++;
            }
        }

        if(batsLeft==0)
        {
            incrementLevelAndPlay();
        }
    }

    private void incrementLevelAndPlay()
    {
        level++;
        TextView v = (TextView)findViewById(R.id.levelView);
        v.setText("Wave: "+level);
        addBats();
    }

    private void endGame()
    {
        if(!_alreadyEnded)
        {
            _alreadyEnded=true;

            Toast.makeText(getApplicationContext(), "Well Done!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, HighScore.class);
            intent.putExtra("score", score + "");
            startActivity(intent);
            finish();
        }
    }

    private double getDistance(int[] image, float[] touch)
    {
        float changeX = (float)Math.abs(image[0]-touch[0]);
        float changeY = (float)Math.abs(image[1]-touch[1]);
        double toSqareRoot = (Math.pow(changeX, 2))+(Math.pow(changeY, 2));
        double distance = Math.sqrt(toSqareRoot);
        return distance;
    }

    private double getDistance(int[] image, int[] image2)
    {
        float changeX = (float)Math.abs(image[0]-image2[0]);
        float changeY = (float)Math.abs(image[1]-image2[1]);
        double toSqareRoot = (Math.pow(changeX, 2))+(Math.pow(changeY, 2));
        double distance = Math.sqrt(toSqareRoot);
        return distance;
    }

    private void fadeSplatAndRemove(final View view)
    {
        AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setDuration(1000);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                gameScreen.removeView(view);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        view.startAnimation(fadeOut);
    }

    private int[] getCenter(View view)
    {
        int[] viewXY = new int[2];

        view.getLocationOnScreen(viewXY);
        int viewWidth = view.getLayoutParams().width;
        int viewHeight = view.getLayoutParams().height;
        viewXY[0] = viewXY[0]+(viewWidth/2);
        viewXY[1] = viewXY[1]+(viewHeight/2);

        return viewXY;
    }



    private ImageView getAvailableBanana()
    {
        ImageView returnBanana;
        int myBanana = -1;

        for(int i =0; i<availableBananas.length; i++)
        {
            int check = availableBananas[i];
            if(check==1)
            {
                myBanana = i;
                break;
            }
        }

        if(myBanana>=0)
        {

            returnBanana = (ImageView)findViewById(bananaIds[myBanana]);

            availableBananas[myBanana]=0;

        }
        else
        {
            returnBanana = null;
        }


        return returnBanana;
    }

    private void addBananaToHolder()
    {
        for(int i =0; i<availableBananas.length; i++)
        {
            int check = availableBananas[i];
            if(check==0)
            {
                availableBananas[i]=1;
                ImageView showBanana = (ImageView)findViewById(bananaIds[i]);
                showBanana.setVisibility(View.VISIBLE);
                return;
            }
        }
    }
}
