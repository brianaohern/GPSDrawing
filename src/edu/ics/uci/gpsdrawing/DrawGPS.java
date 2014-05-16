package edu.ics.uci.gpsdrawing;
//i started adding things based on this tutorial
// http://www.vogella.com/tutorials/AndroidLocationAPI/article.html

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

//import edu.uci.ics.ics163.gpsdrawupload.Point;
import edu.uci.ics.ics163.gpsdrawupload.StrokeManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.PendingIntent;
//import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
//import android.location.Criteria; //added this
import android.location.Location;
//import android.location.LocationListener; //thought i needed this, it's up there though
//import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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

public class DrawGPS extends FragmentActivity implements GooglePlayServicesClient.ConnectionCallbacks, 
GooglePlayServicesClient.OnConnectionFailedListener, 
LocationListener, android.location.LocationListener {

//	implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LocationListener 
	public static StrokeManager stroke_manager;
	public static int color_r, color_g, color_b = 0;
	public static boolean pen_status;
	public static String stroke_name;
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	public static boolean mLocationClientConnected = false;
	public String lastLocation;
	public static LocationClient mLocationClient;
	public static LocationRequest mLocationRequest;
	public static Activity parent;
	//new variables
	private static TextView latitudeField;
	private static TextView longitudeField;
	//private LocationManager locationManager;
	//private String provider;
	public static TextView stroke_view, point_view;
	//more variables
	//private static Location mCurrentLocation = mLocationClient.getLastLocation();
	private static final int MILLISECONDS_PER_SECOND = 1000;
	public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
	private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
	private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
	private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
	//private boolean mUpdatesRequested;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gpsdrawing);
		mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

		latitudeField = (TextView) this.findViewById(R.id.lat_display);
		longitudeField = (TextView) this.findViewById(R.id.lng_display);
		stroke_view = (TextView) this.findViewById(R.id.strokes_display);
		point_view = (TextView) this.findViewById(R.id.points_display);
		
		//locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		//Criteria criteria = new Criteria();
		//provider = locationManager.getBestProvider(criteria, false);
		//Location location = locationManager.getLastKnownLocation(provider);
		mLocationClient = new LocationClient(this, this, this);
		//mUpdatesRequested = false;
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
			pen_status = false;
			stroke_name = String.valueOf((int)(Math.random()));
			stroke_manager = new StrokeManager();
			//mLocationClient = new LocationClient(this, this, this);
			//mLocationRequest = LocationRequest.create();
			//mLocationRequest.setFastestInterval(2000);
			//mLocationRequest.setInterval(5000);
			//mLocationRequest.setPriority(102);
			mLocationClient.connect();
			
		}
		parent = this.getParent();
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
						Log.i("pen status", "Pen down");
						if ((mLocationClient != null) && (mLocationClientConnected) && (mLocationRequest != null)) {
							PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent("android.intent.action.LOCALE_CHANGED"), PendingIntent.FLAG_UPDATE_CURRENT);
							mLocationClient.requestLocationUpdates(mLocationRequest, pendingIntent);
							Log.i("requesting", "updating");
						}
					} else {
						pen_status = false;
					}
				}
			});
			return rootView;
		}
	}

	public void updateUI(final Location location) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Log.i("UI", "in update");
				servicesConnected();
				if((latitudeField != null) && (longitudeField != null) && (lastLocation != null)) {
					latitudeField.setText(String.valueOf(location.getLatitude()));
					longitudeField.setText(String.valueOf(location.getLongitude()));
					//PlaceholderFragment.location_view.setText(lastLocation);
					Log.i("UI", "updated UI");
				}
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
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//Decide what to do based on the original request code
		 switch (requestCode) {
         
         case CONNECTION_FAILURE_RESOLUTION_REQUEST :
         /*
          * If the result code is Activity.RESULT_OK, try
          * to connect again
          */
             switch (resultCode) {
                 case Activity.RESULT_OK :
                 /*
                  * Try the request again
                  */
                 
                 break;
             }
         }
		 
	}
	 private boolean servicesConnected() {
	        // Check that Google Play services is available
	        int resultCode =
	                GooglePlayServicesUtil.
	                        isGooglePlayServicesAvailable(this);
	        // If Google Play services is available
	        if (ConnectionResult.SUCCESS == resultCode) {
	            // In debug mode, log the status
	            Log.d("Location Updates",
	                    "Google Play services is available.");
	            // Continue
	            return true;
	        // Google Play services was not available for some reason
	        } else {
	            // Get the error code
	            int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	            
	            if (errorCode != ConnectionResult.SUCCESS) {
	            	GooglePlayServicesUtil.getErrorDialog(errorCode, this, 0).show();
	            }
	            // Get the error dialog from Google Play services
	            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
	                    errorCode,
	                    this,
	                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

	            // If Google Play services can provide an error dialog
	            if (errorDialog != null) {
	                // Create a new DialogFragment for the error dialog
	                ErrorDialogFragment errorFragment =
	                        new ErrorDialogFragment();
	                // Set the dialog in the DialogFragment
	                errorFragment.setDialog(errorDialog);
	                // Show the error dialog in the DialogFragment
	                //errorFragment.show(getSupportFragmentManager(), "Location Updates");
	            }
	            return false;
	        }
	    }
	
	
	public class mLocationManager extends AsyncTask<Activity, Integer, Integer> {
		
		@Override
		protected Integer doInBackground(Activity... params) {
			return null;
		}
	}
	@Override
	public void onConnected(Bundle connectionHint)
	{
		//Display the connection status
		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
		mLocationClientConnected = true;
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		stroke_manager.upload(String.valueOf(R.id.group_name), String.valueOf(R.id.drawing_id));
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {

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
		Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onLocationChanged(Location location)
	{
		//report to the UI that the location was updated
		String msg = "Updated Location: " + 
				Double.toString(location.getLatitude()) + "," + 
				Double.toString(location.getLongitude());
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	
	
	/*
	@Override
	public void onLocationChanged(Location location) {
		double lat = (double) (location.getLatitude());
		double lng = (double) (location.getLongitude());
		if (latitudeField != null && longitudeField != null)
		{
			String display = "(" + lat + "," + lng + ")";
			this.lastLocation = display;
			updateUI(location);
			Log.i("location", "in on location changed");
			Point current = new Point(location.getTime(), lat, lng);
			stroke_manager.addPoint(stroke_name, current);
			Log.i("adding", "points added");
		}
		else
		{
			Log.i("debug", "its null");
			latitudeField = (TextView) this.findViewById(R.id.lat_display);
			longitudeField = (TextView) this.findViewById(R.id.lng_display);
		}
		*/		
		//String display = "("+location.getLatitude()+","+location.getLongitude()+")";

	@Override
    protected void onStart() 
    {
        super.onStart();
        // Connect the client.
        mLocationClient.connect();
    }
    @Override
	protected void onStop()
	{
		//Disconnecting the client invalidates it
		if (mLocationClient.isConnected())
		{
			mLocationClient.removeLocationUpdates(this);
		}
		mLocationClient.disconnect();
		super.onStop();
	}
	@Override
	protected void onResume()
	{
	//	if (mPrefs.contains("KEY_UPDATES_ON")) {
    //        mUpdatesRequested =
    //                mPrefs.getBoolean("KEY_UPDATES_ON", false);

        // Otherwise, turn off location updates
    //    } else {
    //        mEditor.putBoolean("KEY_UPDATES_ON", false);
    //        mEditor.commit();
    //    }
		super.onResume();
		//locationManager.requestLocationUpdates(provider, 400, 1, this);
	}
	@Override
	protected void onPause()
	{
		super.onPause();
		//locationManager.removeUpdates(this);
	}
	//@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
	}
	//@Override
	public void onProviderEnabled(String provider)
	{
		Toast.makeText(this, "Enabled new provider" + provider, Toast.LENGTH_SHORT).show();
	}
	//@Override
	public void onProviderDisabled(String provider)
	{
		Toast.makeText(this, "Disabled provider" + provider, Toast.LENGTH_SHORT).show();

	}
}