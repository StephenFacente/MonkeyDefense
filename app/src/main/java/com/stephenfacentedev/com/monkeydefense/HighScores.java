package com.stephenfacentedev.com.monkeydefense;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class HighScores extends ActionBarActivity {
    private AdapterView.AdapterContextMenuInfo info;
    private ListView scoreListView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);

        scoreListView = (ListView)findViewById(R.id.theScores);
        Button playAgain = (Button)findViewById(R.id.playAgain);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/font.otf");
        playAgain.setText("Play Again");
        playAgain.setTypeface(font);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Highscores");
        query.addDescendingOrder("score");
        query.addAscendingOrder("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    ArrayList<lineItem> rows = new ArrayList<lineItem>();
                    int counter=1;
                    for(ParseObject object : objects)
                    {
                        String uname = object.get("username")+"";
                        int score = (int) object.get("score");

                        lineItem li = new lineItem();
                        li.setScore(score);
                        li.setUsername(uname);
                        li.setIndex(counter);

                        rows.add(li);

                        counter++;
                    }

                    scoreListView.setAdapter( new CustomAdapter(rows, getApplicationContext()) );

                    ProgressBar spin = (ProgressBar)findViewById(R.id.spinner2);
                    spin.setVisibility(View.GONE);


                } else {
                    // handle Parse Exception here
                }
            }
        });
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_high_scores, menu);
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

    public void backToSelection(View view)
    {
        Intent intent = new Intent(this, LevelPlay.class);
        startActivity(intent);
        finish();
    }
}
