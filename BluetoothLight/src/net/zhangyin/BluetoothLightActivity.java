package net.zhangyin;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class BluetoothLightActivity extends Activity {
    /** Called when the activity is first created. */
	
	private final String ARDUINO_BLUETOOTH_MAC="00:19:5D:EE:05:D0";
	BluetoothAdapter localAdapter = null;
	BluetoothSocket lightSocket;
	OutputStream btout;
	boolean lightStatus=false;
	ImageButton lightButton;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        lightButton = (ImageButton) findViewById(R.id.imageButton1);
        lightButton.setOnClickListener(mOnClickListener);
        
        enableBT();

	    BluetoothDevice myLightDevice = localAdapter.getRemoteDevice(ARDUINO_BLUETOOTH_MAC);
	    		    
	    try {
	    	lightSocket = myLightDevice.createRfcommSocketToServiceRecord(UUID
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

}