package com.stephenfacentedev.com.monkeydefense;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.parse.ParseObject;



public class HighScore extends ActionBarActivity {
    int score;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        Intent intent = getIntent();
        String intentInfo = intent.getExtras().getString("score");
        score = Integer.parseInt(intentInfo);
        TextView scoreView = (TextView)findViewById(R.id.scoreView);
        scoreView.setText(score + " ");

        final Button submitButton = (Button)findViewById(R.id.submit);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/font.otf");
        submitButton.setText("Submit High Score");
        submitButton.setTypeface(font);

        final EditText username = (EditText)findViewById(R.id.scoreName);
        username.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (username.getText().toString().equals("")) {
                    submitButton.setEnabled(false);
                } else {
                    submitButton.setEnabled(true);
                }

                //check for bad words
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
        applyFonts(findViewById(R.id.scoreView), font);
        applyFonts(findViewById(R.id.textView2), font);

    }

    public static void applyFonts(final View v, Typeface fontToSet)
    {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    applyFonts(child, fontToSet);
                }
            } else if (v instanceof TextView) {
                ((TextView)v).setTypeface(fontToSet);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // ignore
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_high_score, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void submitScore(View view)
    {
        EditText getUsername = (EditText)findViewById(R.id.scoreName);

        String username = getUsername.getText()+"";

        ParseObject highScoreSubmission = new ParseObject("Highscores");
        highScoreSubmission.put("username", username);
        highScoreSubmission.put("score", score);
        highScoreSubmission.saveInBackground();


        goToHighScores();
    }

    private void goToHighScores()
    {
        Intent intent = new Intent(this, HighScores.class);
        startActivity(intent);
        finish();
    }
}
