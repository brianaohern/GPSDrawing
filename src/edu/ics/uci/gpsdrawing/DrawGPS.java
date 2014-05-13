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

public class DrawGPS extends Activity implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

//	implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LocationListener 
	public static StrokeManager stroke_manager;
	public static int color_r, color_g, color_b = 0;
	public static boolean pen_status;
	public static String stroke_name;
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gpsdrawing);
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
			pen_status = false;
			stroke_name = String.valueOf((int)(Math.random()));
			stroke_manager = new StrokeManager();
		}
		Activity parent = this.getParent();
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
		
		public static TextView location_view, stroke_view, point_view;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_gpsdrawing,
					container, false);
			
			location_view = (TextView) rootView.findViewById(R.id.location_display);
			stroke_view = (TextView) rootView.findViewById(R.id.strokes_display);
			point_view = (TextView) rootView.findViewById(R.id.points_display);
			
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
				
				public void add_red(View v) {
					if (color_r == 0) {
						color_r = 255;
					} else {
						color_r = 0;
					}
					stroke_manager.setStrokeColor(stroke_name, color_r, color_g, color_b);
				}
			});
			
			CheckBox greenCheckbox = (CheckBox) rootView.findViewById(R.id.green_box);
			greenCheckbox.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					add_green(v);
				}
				
				public void add_green(View v) {
					if (color_g == 0) {
						color_g = 255;
					} else {
						color_g = 0;
					}
					stroke_manager.setStrokeColor(stroke_name, color_r, color_g, color_b);
				}
			});
			
			CheckBox blueCheckbox = (CheckBox) rootView.findViewById(R.id.blue_box);
			blueCheckbox.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					add_blue(v);
				}
				
				public void add_blue(View v) {
					if (color_b == 0) {
						color_b = 255;
					} else {
						color_b = 0;
					}
					stroke_manager.setStrokeColor(stroke_name, color_r, color_g, color_b);
				}
			});
			
			ToggleButton change_pen_status = (ToggleButton) rootView.findViewById(R.id.pen_status);
			change_pen_status.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					change_pen_status(v);
				}
				
				public void change_pen_status(View v) {

					if (pen_status == false) {
						stroke_name = String.valueOf((int)(Math.random()));
						pen_status = true;
					} else {
						pen_status = false;
					}
				}
			});
			return rootView;
		}
	}

	public void updateUI(final Activity parent) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
			}
		});	
	}
	
	public static class ErrorDialogFragment extends DialogFragment {
		//Global field to contain the error dialog
		private Dialog mDialog;
		//default constructor.  sets the dialog field to null
		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}
		
		//set the dialog to display
		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}
		
		//return a dialog to the dialogFragment
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}
	
	public class mLocationManager extends AsyncTask<Activity, Integer, Integer> {

		@Override
		protected Integer doInBackground(Activity... params) {
			// TODO Auto-generated method stub
			return null;
		}
		
		protected void onProgressUpdate() {
			
		}
		
	}
	//@Override
	public void onConnected(Bundle connectionHint)
	{
		//Display the connection status
		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
		location mCurrentLocation = mLocationClient.getLastLocation();
		lastLocation = "("+mCurrentLocation.getLatitude()+","+mCurrentLocation.getLongitude()+")";
	}
	
	@Override
	public void onDestroy() {
		stroke_manager.upload(String.valueOf(R.id.group_name), String.valueOf(R.id.drawing_id));
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		if(result.hasResolution())
		{
			try {
				//start an activity that tries to resolve the error
				result.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
			} catch(IntentSender.SendIntentException e) {
				//log the error
				e.printStackTrace();
			}
		}
		else
		{
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog
					(result.getErrorCode(), this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
			if(errorDialog != null) {
				//create a new dialogFragment for the error dialog
				ErrorDialogFragment errorFragment = new ErrorDialogFragment();
				//set the dialog in the dialogFragment
				errorFragment.setDialog(errorDialog);
				//show the error dialog in the dialogFragment
				errorFragment.show(getFragmentManager(), "Location Updates");
			}
		}
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
	
	
}