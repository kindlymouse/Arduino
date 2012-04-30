package name.zhangyin.bluetoothlight;

/**
 * 基于Arduino的Android手机控制的电灯
 * Android端程序
 * 
 * kindlymouse@gmail.com
 */
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.content.SharedPreferences;

public class Bluetooth_LightActivity extends Activity {
    /** Called when the activity is first created. */
	
	private static final String PREF_BLUETOOTH_DEVICE_MAC = "BLUETOOTH_DEVICE_MAC";
	BluetoothAdapter localAdapter = null;
	BluetoothSocket lightSocket;
	OutputStream btout;
	boolean lightStatus=false;
	ImageButton lightButton;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TextView author = (TextView) findViewById(R.id.textView1);
        author.setText("@再续朝圣路");
        lightButton = (ImageButton) findViewById(R.id.imageButton1);
        lightButton.setOnClickListener(mOnClickListener);
        enableBT();

        SharedPreferences settingActivity= getPreferences(MODE_PRIVATE);
        String device_mac = settingActivity.getString(PREF_BLUETOOTH_DEVICE_MAC, null);
        if(device_mac == null){
        	Intent serverIntent = null;
            serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, 1);        	
        }else{
        	BluetoothDevice myLightDevice = localAdapter.getRemoteDevice(device_mac);
        	connectDevice(myLightDevice);
        }
    }
    
    public void enableBT(){
        localAdapter=BluetoothAdapter.getDefaultAdapter();
        //If Bluetooth not enable then do it

        if(!localAdapter.isEnabled()){
            localAdapter.enable();
            while(!(localAdapter.isEnabled())){

            }
        }
    }

	@Override
	protected void onDestroy() {

		try {
			btout.close();
			lightSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}    
    
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			try {
				if(lightStatus){				
					btout.write("0".getBytes());
					btout.flush();
					lightButton.setImageResource(R.drawable.light_close);
					lightStatus = false;
				}else{
					btout.write("1".getBytes());
					btout.flush();
					lightButton.setImageResource(R.drawable.light_open);
					lightStatus = true;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};
	
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case 1:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
            	connectDevice(getBluetoothDevice(data, true));
            }
            break;
        }
    }	
    
    private BluetoothDevice getBluetoothDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        
        SharedPreferences settingActivity= getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = settingActivity.edit();
        prefEditor.putString(PREF_BLUETOOTH_DEVICE_MAC, address);
        prefEditor.commit();
        // Get the BLuetoothDevice object
        return localAdapter.getRemoteDevice(address);
    }    
    
    private void connectDevice(BluetoothDevice device){
    	
	    try {
	    	lightSocket = device.createRfcommSocketToServiceRecord(UUID
	                .fromString("00001101-0000-1000-8000-00805F9B34FB"));
	    			    	
	    	lightSocket.connect();
	    	
		    if(lightSocket!=null){
		        try {
		            btout=lightSocket.getOutputStream();
		            //btout.write("MobilePhone Connected!".getBytes());
		            //btout.flush();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		    }else{
		        //Error
		    }				
			
		}catch(IllegalArgumentException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}    
	}

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent serverIntent = null;
        switch (item.getItemId()) {
        case R.id.secure_connect_scan:
            // Launch the DeviceListActivity to see devices and do scan
            serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, 1);
            return true;
        }
        return false;
    }    
}