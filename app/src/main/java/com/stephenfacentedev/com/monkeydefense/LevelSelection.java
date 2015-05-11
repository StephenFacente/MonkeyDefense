package com.stephenfacentedev.com.monkeydefense;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class LevelSelection extends ActionBarActivity
{
    private ListView scoreListView ;

    @Override
    public void onPause()
    {
        super.onPause();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_selection);

        try {
            // Enable Local Datastore.
            Parse.enableLocalDatastore(getApplicationContext());

            Parse.initialize(this, "82ru1UWv5AC3Jd90Im7KvADb0FxY7V8WxhfrGVtx", "6ZCfdM08OHhh5C0ezfzuow6LfnGl0ppnZUdqpjmb");
            ParseAnalytics.trackAppOpenedInBackground(getIntent());
        }
        catch (Exception e)
        {
            Log.v("Error", e.getStackTrace().toString());
        }

        scoreListView = (ListView)findViewById(R.id.topPlayers);


        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Highscores");
        query.setLimit(10);
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
                    ProgressBar spin = (ProgressBar)findViewById(R.id.spinner);
                    spin.setVisibility(View.GONE);

                } else {
                    // handle Parse Exception here
                }
            }
        });

        Button b = (Button)findViewById(R.id.playButton);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/font.otf");
        b.setText("PLAY");
        b.setTypeface(font);
        applyFonts(findViewById(R.id.highscores), font);
        applyFonts(findViewById(R.id.topPlayers), font);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_level_selection, menu);
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
            Intent intent = new Intent(this, HighScores.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void start(View view)
    {
        Intent intent = new Intent(this, LevelPlay.class);
        startActivity(intent);
        finish();
    }
}
