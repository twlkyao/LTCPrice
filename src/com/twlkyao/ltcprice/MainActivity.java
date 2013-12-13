package com.twlkyao.ltcprice;
/**
 * @author Shiyao Qi
 * @email qishiyao2008@126.com
 * @function Get the trade feed of LTC from OKCoin.
 */
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private TextView trade_price; // The trade price.
	private TextView buy_price; // The buy one price.
	private TextView sell_price; // The sell one price.
	private TextView high_price; // The highest price.
	private TextView low_price; // The lowest price.
	private TextView volume; // The trade volume.
	
	private String str_trade_price; // The trade price string.
	private String str_buy_price; // The buy one price string.
	private String str_sell_price; // The sell one price string.
	private String str_high_price; // The highest price string.
	private String str_low_price; // The lowest price string.
	private String str_volume; // The trade volume string.
	
	private int interval = 5; // The default interval is 5 second.
	// The server's url.
	private String urlString = "https://www.okcoin.com/api/ticker.do?symbol=ltc_cny";

	// The UI handler.
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what) { // Operation is succeeded.
			case 1:
				trade_price.setText(str_trade_price);
		    	buy_price.setText(str_buy_price);
		    	sell_price.setText(str_sell_price);
		    	high_price.setText(str_high_price);
		    	low_price.setText(str_low_price);
		    	volume.setText(str_volume);
				break;
			case 0: // Operation is failed.
				Toast.makeText(getApplicationContext(),
						R.string.fail, Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        startUpdate(urlString);
    }

    /**
     * Find the views by id.
     */
    public void findViews() {
    	trade_price = (TextView) this.findViewById(R.id.trade_price);
    	buy_price = (TextView) this.findViewById(R.id.buy_price);
    	sell_price = (TextView) this.findViewById(R.id.sell_price);
    	high_price = (TextView) this.findViewById(R.id.highest_price);
    	low_price = (TextView) this.findViewById(R.id.lowest_price);
    	volume = (TextView) this.findViewById(R.id.volume);
    }
    
    /**
     * Start to update the feed according to the response from the url.
     * @param url The server's url.
     */
    public void startUpdate(String url) {
    	
    	Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true) {
					Message msg = Message.obtain();
					
					try {
						URL url = new URL(urlString); // Create an new URL instance.
						HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // Get a connection according to the url.
						conn.setRequestMethod("GET"); // Set the request method GET.
						conn.setConnectTimeout(5 * 1000); // Set the connect timeout as 5 seconds.
						InputStream is = conn.getInputStream(); // Get the response.
						
						InputStreamReader isr = new InputStreamReader(is, "utf-8");  
						BufferedReader br = new BufferedReader(isr, 1024);  
						String result = br.readLine();
						Log.d("Response", result);
					
						JSONObject jsonObject = new JSONObject(result).getJSONObject("ticker");
						
						str_high_price = jsonObject.getString("high");
						str_buy_price = jsonObject.getString("buy");
						str_sell_price = jsonObject.getString("sell");
						str_trade_price = jsonObject.getString("last");
						str_low_price = jsonObject.getString("low");
						str_volume = jsonObject.getString("vol");
						
						Thread.sleep(interval * 1000);
						
						msg.what = 1;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						msg.what = 0;
					} 
					handler.sendMessage(msg);
				}
			}
		});
    	thread.start();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
    	menu.add(0, 0, 0, getString(R.string.action_settings));
    	menu.add(1, 1, 1, getString(R.string.about));
    	menu.add(2, 2, 2, getString(R.string.exit_settings));
        return super.onCreateOptionsMenu(menu);
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		super.onOptionsItemSelected(item);
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		switch(item.getItemId()) {
		case 0:
			/**
			 * The setting here is not work
			 */
			final EditText editText = new EditText(getApplicationContext());
			editText.setText(interval + "");
			builder.setTitle(R.string.action_settings)
			.setIcon(android.R.drawable.ic_dialog_info)
			.setMessage(getString(R.string.update_interval))
			.setView(editText)
			.setPositiveButton(getString(R.string.submit), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					interval = Integer.parseInt(editText.getText().toString());
				}
			});
			//Create the AlertDialog  
			AlertDialog alertDialog = builder.create();
			//Show the AlertDialog 
			alertDialog.show();
			break;
		case 1:
			/**
			 * The setting here is not work
			 */
			
			builder.setTitle(R.string.about)
			.setIcon(android.R.drawable.ic_dialog_info)
			.setMessage(getString(R.string.about_info))
			.setPositiveButton(getString(R.string.submit), null);
			//Create the AlertDialog  
			AlertDialog alertDialog1 = builder.create();
			//Show the AlertDialog 
			alertDialog1.show();
			break;
		case 2:
			finish();
			break;
		}
		return true;
	}

	
}
