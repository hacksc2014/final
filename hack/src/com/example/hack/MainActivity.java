package com.example.hack;

import java.io.File;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.ShareActionProvider;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends ActionBarActivity implements
NavigationDrawerFragment.NavigationDrawerCallbacks, SensorEventListener {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private ShareActionProvider mShareActionProvider;
	private NavigationDrawerFragment mNavigationDrawerFragment;
	private float mLastX, mLastY, mLastZ;
	private boolean mInitialized;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private final float NOISE = (float)12.0;

	private int sensor_delay = 150000;
	private ToggleButton tb;
	static public MediaPlayer mp;
	static public boolean Pressed;
	static private long lastDown;
	static private long lastDuration;
	static private long playback[];
	static private int playback_index;
	static private int final_index;
	static public int music = 0;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	//		protected void onCreate(Bundle savedInstanceState) {
	//		    super.onCreate(savedInstanceState);
	//		    setContentView(R.layout.activity_display_message);
	//	
	//		    if (savedInstanceState == null) {
	//		        getSupportFragmentManager().beginTransaction()
	//		                .add(R.id.container, new PlaceholderFragment())
	//		                .commit();
	//		    }
	//		}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));


		mTitle = getTitle();


		mInitialized = false;
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this, mAccelerometer, sensor_delay);
		mp = MediaPlayer.create(MainActivity.this, com.example.hack.R.raw.hihatcut);
		Pressed = false;
		//playback = new ArrayList<Long>();
		playback = new long[100];
		playback_index= 0;
		final_index = 0;
	}




	protected void onResume(){
		super.onResume();
		mSensorManager.registerListener(this, mAccelerometer, sensor_delay);

	}
	@Override
	protected void onPause(){
		super.onPause();
		mSensorManager.unregisterListener(this);
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy){

	}
	@Override
	public void onSensorChanged(SensorEvent event){

		ImageView iv = (ImageView)findViewById(R.id.image);
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];
		if (!mInitialized) {
			mLastX = x;
			mLastY = y;
			mLastZ = z;
			mInitialized = true;
		} else {
			float deltaX = -(mLastX - x);
			float deltaY = Math.abs(mLastY - y);
			float deltaZ = Math.abs(mLastZ - z);
			if (deltaX < NOISE) deltaX = (float)0.0;
			if (deltaY < NOISE) deltaY = (float)0.0;
			if (deltaZ < NOISE) deltaZ = (float)0.0;
			mLastX = x;
			mLastY = y;
			mLastZ = z;
			//			tvX.setText(Float.toString(deltaX));
			//			tvY.setText(Float.toString(deltaY));
			//			tvZ.setText(Float.toString(deltaZ));
			//			iv.setVisibility(View.VISIBLE);

			if (deltaX > 0f && Pressed) {
				playback[playback_index] = System.currentTimeMillis() - lastDown;
				playback_index++;
				//playback.add(System.currentTimeMillis() - lastDown);

				try {

					new PlayAsync().execute("","","");
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();

				}
			} else {
				iv.setVisibility(View.INVISIBLE);
			}
		}

	}
	public void onPrepared(MediaPlayer mp){
		Log.d("Try", "Playing");
		mp.start();
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

	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
		}
		//			getMenuInflater().inflate(R.menu.share_menu, menu);
		//			MenuItem shareItem = menu.findItem(R.id.menu_item_share);
		//			mShareActionProvider =  (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
		//			Intent shareIntent = new Intent(Intent.ACTION_SEND);
		//			shareIntent.setAction(Intent.ACTION_SEND);
		//			shareIntent.setType("text/plain");
		//			shareIntent.putExtra(Intent.EXTRA_TEXT, "Text to share");
		//			System.out.println("SI: " + shareIntent);
		//			mShareActionProvider.setShareIntent(shareIntent);
		return true;
	}

	@Override
	protected void onDestroy(){
		super.onDestroy();
		if (mp != null){
			mp.release();
			mp = null;
		}
	}
	PlayAsync newPA() {
		return new PlayAsync();
	}
	public class PlayAsync extends AsyncTask<String, String, String> {

		// Show Progress bar before downloading Music
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Shows Progress Bar Dialog and then call doInBackground method
			mp.release();
			//mp.start();
		}
		
		

		// Download Music File from Internet
		@Override
		protected String doInBackground(String... f_url) {
			mp = MediaPlayer.create(MainActivity.this,R.raw.hihatcut);
			if(music!=0){
				switch(music){
				case 1:{
					mp = MediaPlayer.create(MainActivity.this,R.raw.clave);
					break;
				}
				case 2:{
					mp = MediaPlayer.create(MainActivity.this,R.raw.hi_hat_cut);
					break;
				}
				case 3:
					mp = MediaPlayer.create(MainActivity.this,R.raw.snare_drum);
					break;
				}
			}
			mp.start();
			return null;         
		}

		// Once Music File is downloaded
		@Override
		protected void onPostExecute(String file_url) {
			// Dismiss the dialog after the Music file was downloaded

		}
	}

	@Override

	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments

		FragmentManager fragmentManager = getSupportFragmentManager();
//		fragmentManager
//		.beginTransaction()
//		.replace(R.id.container,
//				PlaceholderFragment.newInstance(position + 1)).commit();
		switch (position+1) {
		case 1:
			fragmentManager
			.beginTransaction()
			.replace(R.id.container,
					PlaceholderFragment.newInstance(position + 1)).commit();
			break;
		case 2:

			fragmentManager
			.beginTransaction()
			.replace(R.id.container,
					SectionOneFragment.newInstance(position + 1)).commit();

			break;
		case 3:
			fragmentManager
			.beginTransaction()
			.replace(R.id.container,
					SectionTwoFragment.newInstance(position + 1)).commit();

			break;
		}

	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = "Home";
			break;
		case 2:
			mTitle = "History";
			break;
		case 3:
			mTitle = "About and Help";
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}


	//	private void setShareIntent(Intent shareIntent) {
	//	    if (mShareActionProvider != null) {
	//	        
	//	    }
	//	}



	/**
	 * A placeholder fragment containing a simple view.
	 */



	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";
		
		ImageButton recBtn;
		ImageButton cymbalBtn;
		ImageButton drumBtn;
		ImageButton claveBtn;

		private Button playback_button;

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			final View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			setHasOptionsMenu(true);

			//				tb = (ToggleButton) findViewById(R.id.toggleButton);
			//				setContentView(R.layout.fragment_main);
			recBtn = (ImageButton) rootView.findViewById(R.id.recBtn);

			playback_button = (Button) rootView.findViewById(R.id.playbackButton);
			System.out.println(recBtn );
			System.out.println("recBTN: " + R.id.recBtn);
			System.out.println(playback_button);

			claveBtn = (ImageButton) rootView.findViewById(R.id.claveBtn);
			claveBtn.setOnClickListener(new OnClickListener(){
				public void onClick(View v){
					mp = MediaPlayer.create(getActivity(), R.raw.clave);
					music = 1;
					Log.d("MP" , "  MP set to CLAVE"+ "  " + mp);
				}
			});

			cymbalBtn = (ImageButton) rootView.findViewById(R.id.cymbalBtn);
			cymbalBtn.setOnClickListener(new OnClickListener(){
				public void onClick(View v){
					mp = MediaPlayer.create(getActivity(), R.raw.hihatcut);
					music = 2;
					Log.d("MP" , "  MP set to HIHATCUT"+ "  " + mp);
				}
			});

			drumBtn = (ImageButton) rootView.findViewById(R.id.drumBtn);
			drumBtn.setOnClickListener(new OnClickListener(){
				public void onClick(View v){
					mp = MediaPlayer.create(getActivity(), R.raw.snare_drum);
					music = 3;
					Log.d("MP" , "  MP set to DRUM"+ "  " + mp);
				}
			});



			playback_button.setOnClickListener(new OnClickListener(){
				public void onClick(View v){
					for (int i = 0; i < final_index; i++){
						if (i ==0){
							SystemClock.sleep(playback[i]);
							//							s = new PlayAsync();
//							s.execute("","","");
						}else{
							SystemClock.sleep(playback[i] - playback[i-1]);
//							s.execute("","","");
						}
					}
				}
			});
			recBtn.setOnTouchListener(new OnTouchListener(){

				@SuppressLint("NewApi")
				@Override

				public boolean onTouch(View v, MotionEvent event){
					Log.d("Debug", " ENTERED SETONTOUCHLISTNr");
					if(event.getAction() == MotionEvent.ACTION_DOWN)
					{
						Pressed = true;
						recBtn.setImageResource(R.drawable.recordbutton2);
						Toast.makeText(getActivity(), "started recording", Toast.LENGTH_SHORT).show();
						lastDown = System.currentTimeMillis();

						// do recording stuff here
					}
					else if (event.getAction() == MotionEvent.ACTION_UP)
					{
						Pressed = false;
						recBtn.setImageResource(R.drawable.recordbutton);
						lastDuration = System.currentTimeMillis() - lastDown;
						int seconds = (int) (lastDuration / 1000) % 60 ;
						int centis = ((int) (lastDuration) - seconds * 1000)/10;
						String duration = Integer.toString(seconds) + "." + Integer.toString(centis);
						TextView textView = (TextView) rootView.findViewById(R.id.durationText);
						textView.setText(duration+"s");
						//						playback_button = (Button) rootView.findViewById(R.id.playbackButton);
						Toast.makeText(getActivity(), "stopped recording, duration:"+duration+"s", Toast.LENGTH_SHORT).show();
						//Toast.makeText(MainActivity.this, playback.size(), Toast.LENGTH_SHORT).show();
						// stop recording stuff here
						for (int i = 0; i < playback_index; i++){
							System.out.println(playback[i]);
						}
						final_index = playback_index;
						playback_index = 0;
					}

					return true;
				}
			});
			return rootView;
		}





		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
	}


	public static class SectionOneFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static SectionOneFragment newInstance(int sectionNumber) {

			SectionOneFragment fragment = new SectionOneFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public SectionOneFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_1, container,
					false);
			setHasOptionsMenu(true);
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
	}

	public static class SectionTwoFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static SectionTwoFragment newInstance(int sectionNumber) {

			SectionTwoFragment fragment = new SectionTwoFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public SectionTwoFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_2, container,
					false);
			setHasOptionsMenu(true);
			//			return null;
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
	}
}
