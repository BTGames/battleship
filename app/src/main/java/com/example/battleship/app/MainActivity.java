package com.example.battleship.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

	public int turn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
	    final int PLAYING = 100;
	    final int PLAYER_WON = 101;
	    final int PLAYER_LOST = 102;
	    final int PLAYER = 998;
	    final int AI = 999;
	    final Player player = new Player();
	    final AI ai = new AI(player);
	    final TextView primary = (TextView) findViewById(R.id.primary);
	    final TextView tracking = (TextView) findViewById(R.id.tracking);
	    final TextView aifeedback = (TextView) findViewById(R.id.aifeedback);
	    Button btn = (Button) findViewById(R.id.play);
	    final EditText xCoord = (EditText) findViewById(R.id.xcoord);
	    final EditText yCoord = (EditText) findViewById(R.id.ycoord);

//	    GridView gridview = (GridView) findViewById(R.id.gridview);
//	    gridview.setAdapter(new ImageAdapter(this));
//
//	    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//		    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//			    Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();
//		    }
//	    });

	    int status;
	    turn = PLAYER;
	    btn.setText("PLAY");

//	    do {
		    player.show(primary, tracking);
			if (turn == PLAYER) {
				btn.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						// Do something in response to button click
						int x, y;
						int[] res;
						x = Integer.parseInt(xCoord.getText().toString());
						y = Integer.parseInt(yCoord.getText().toString());
						res = ai.hit(x, y);
						player.setTracking(x, y, res[0], res[1]);
						player.show(primary, tracking);
						turn = AI;
						if (turn == AI) {
							ai.hunt(player,aifeedback);
							player.show(primary, tracking);
							turn = PLAYER;
						}
						if(ai.isLoser()) {
							aifeedback.setText("Player won!");
						} else if(ai.isVictorious()) {
							aifeedback.setText("AI won!");
						}
					}
				});
			}

//	    } while (status != PLAYING);

	     /*
         Game status printout.
		 */

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
