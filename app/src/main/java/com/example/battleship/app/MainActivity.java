package com.example.battleship.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
	    final int PLAYING = 100;
	    final int PLAYER_WON = 101;
	    final int PLAYER_LOST = 102;
	    AI ai = new AI();
	    Player player = new Player();
	    TextView primary = (TextView) findViewById(R.id.primary);
	    TextView tracking = (TextView) findViewById(R.id.tracking);
	    int status;

//	    GridView gridview = (GridView) findViewById(R.id.gridview);
//	    gridview.setAdapter(new ImageAdapter(this));
//
//	    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//		    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//			    Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();
//		    }
//	    });

	    player.show(primary, tracking);

//	    do {
//		    player.show(primary, tracking);
//
//		    /*
//		    Game status check.
//		     */
//			if(player.isVictorious()) {
//				status = PLAYER_WON;
//			} else if(ai.isVictorious()) {
//				status = PLAYER_LOST;
//			} else {
//				status = PLAYING;
//			}
//	    } while (status != PLAYING);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
