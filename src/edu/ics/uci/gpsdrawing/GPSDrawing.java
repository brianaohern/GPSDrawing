package edu.ics.uci.gpsdrawing;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

import android.R;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class GPSDrawing extends Activity implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

	public static StrokeManager stroke_manager;
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	public LocationClient mLocationClient = new LocationClient(this, this, this);
	public String lastLocation = "Unavailable";
	public int stroke_color = Color.rgb(255, 255, 255);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gpsdrawing);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}

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
			return rootView;
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
				TextView tv = (TextView) findViewById(R.id.location_display);
				tv.setText(lastLocation);
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
}
