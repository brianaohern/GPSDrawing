package edu.ics.uci.gpsdrawing;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import edu.uci.ics.ics163.gpsdrawupload.Point;
import edu.uci.ics.ics163.gpsdrawupload.StrokeManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class DrawGPS extends Activity implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

	public static StrokeManager stroke_manager;
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	public static String lastLocation = "Unavailable";
	public static int color_r, color_g, color_b = 0;
	private LocationClient mLocationClient;
	private LocationRequest loc_requester;
	public static boolean pen_status;
	public static String stroke_name;
	
	public class Manager extends AsyncTask<StrokeManager, Void, Integer> {
		
		protected void onPreExecute() {
		}
		
		protected void onPostExecute(String result) {
		}
		
		protected void onProgressUpdate(Void... values) {
		}
		
		@Override
		protected Integer doInBackground(StrokeManager... params) {
			onLocationChanged(mLocationClient.getLastLocation());
			return null;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gpsdrawing);
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		pen_status = false;
		stroke_name = String.valueOf((int)(Math.random()));
		mLocationClient = new LocationClient(this, this, this);
		loc_requester = new LocationRequest();
		loc_requester.setPriority(loc_requester.PRIORITY_HIGH_ACCURACY);
		loc_requester.setFastestInterval(1000);
		loc_requester.setInterval(3000);
		mLocationClient.connect();
		stroke_manager = new StrokeManager();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gpsdrawing, menu);
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

	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_gpsdrawing,
					container, false);
			lastLocation = "Unavailable";
			
			Button updateButton = (Button) rootView.findViewById(R.id.upload_button);
			updateButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					upload_click(v);
				}

				public void upload_click(View v) {
					stroke_manager.upload(String.valueOf(R.id.group_name), String.valueOf(R.id.drawing_id));
					Log.i("upload status", "I'm uploading like a boss");
				}
			});
			
			CheckBox redCheckbox = (CheckBox) rootView.findViewById(R.id.red_box);
			redCheckbox.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					add_red(v);
				}
			});
			
			CheckBox greenCheckbox = (CheckBox) rootView.findViewById(R.id.green_box);
			greenCheckbox.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					add_green(v);
				}
			});
			
			CheckBox blueCheckbox = (CheckBox) rootView.findViewById(R.id.blue_box);
			blueCheckbox.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					add_blue(v);
				}
			});
			
			ToggleButton change_pen_status = (ToggleButton) rootView.findViewById(R.id.pen_status);
			change_pen_status.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					change_pen_status(v);
				}
			});
			return rootView;
		}
	}
	
	public static void add_red(View v) {
		if (color_r == 0) {
			color_r = 255;
		} else {
			color_r = 0;
		}
		stroke_manager.setStrokeColor(stroke_name, color_r, color_g, color_b);
	}
	
	public static void add_green(View v) {
		if (color_g == 0) {
			color_g = 255;
		} else {
			color_g = 0;
		}
		stroke_manager.setStrokeColor(stroke_name, color_r, color_g, color_b);
	}
	
	public static void add_blue(View v) {
		if (color_b == 0) {
			color_b = 255;
		} else {
			color_b = 0;
		}
		stroke_manager.setStrokeColor(stroke_name, color_r, color_g, color_b);
	}
	
	public static void change_pen_status(View v) {
		if (pen_status == false) {
			stroke_name = String.valueOf((int)(Math.random()));
			pen_status = true;
			Log.i("pen status", "its true now");
		} else {
			pen_status = false;
		}
	}


	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (result.hasResolution()) {
			try {
				result.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
			} catch (IntentSender.SendIntentException e) {
				e.printStackTrace();
			}
		} else {
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
			if (errorDialog != null) {
				ErrorDialogFragment errorFragment = new ErrorDialogFragment();
				errorFragment.setDialog(errorDialog);
				errorFragment.show(getFragmentManager(), "Location Updates");
			}
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
		
		Location mCurrentLocation = mLocationClient.getLastLocation();
		lastLocation  = "(" + mCurrentLocation.getLatitude() + " , " + mCurrentLocation.getLongitude() + ")";
		updateUI();
	}

	public void updateUI() {
		runOnUiThread(new Runnable() {
			public void run() {
				if ((loc_requester != null) || (mLocationClient != null)) {
					TextView tv = (TextView) findViewById(R.id.location_display);
					tv.setText(lastLocation);
					TextView tv2 = (TextView) findViewById(R.id.strokes_display);
					tv2.setText(String.valueOf(stroke_manager.countStrokes()));
					TextView tv3 = (TextView) findViewById(R.id.points_display);
					tv3.setText(String.valueOf(stroke_manager.countPoints()));
				}
			}
		});
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
	
	public static class ErrorDialogFragment extends DialogFragment {
		private Dialog mDialog;
		
		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}
		
		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}
	
	public void onDestroy() {
		mLocationClient.disconnect();
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.i("location change", "Changing locale");
		Location mCurrentLocation = location;
		lastLocation  = "(" + mCurrentLocation.getLatitude() + " , " + mCurrentLocation.getLongitude() + ")";
		if (pen_status) {
			Point current = new Point(mLocationClient.getLastLocation().getTime(), mLocationClient.getLastLocation().getLatitude(), mLocationClient.getLastLocation().getLongitude());
			stroke_manager.addPoint(stroke_name, current);
		}
	}
}