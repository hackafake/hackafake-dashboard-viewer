package fun.rokers.hackafake.hackafake_dashboard_viewer;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Counter implements Runnable {
    private Handler handler;
    private int pFake_count=0;
    private boolean blinking=false;
    private Gpio mGpio = null;
    private Handler h;

    private static String GPIO = "BCM26";
    private static long DELAY_MILLIS=2*1000;
    private static long LED_DELAY_MILLIS=2*1000;
    //TODO: set right url address
    private static String URL="https://api.hackafake.it/counter";

    private static String COUNTER_FAKE_FIELD = "fake";
    private static String COUNTER_REAL_FIELD = "real";


    public Counter(Context appContext) {
        this.handler=new Handler();
        PeripheralManager peripheralManager = PeripheralManager.getInstance();
        try {
            mGpio = peripheralManager.openGpio(GPIO);
            mGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
         } catch (IOException e) {
            Log.d("ERROR", e.getStackTrace().toString());
        }

        h = new Handler();
        handler.post(this);
    }

    public void close() {
        handler.removeCallbacks(this);
    }

    @Override
    public void run() {
        new HttpGetter_counter().execute(URL);
        handler.postDelayed(this,DELAY_MILLIS);
    }

    private class HttpGetter_counter extends HttpGetter {

        @Override
        protected void onPostExecute(String s) {
            int fake_count=0,real_count=0;
            try {
                JSONObject jsonObject = new JSONObject(s);
                fake_count=jsonObject.getInt(COUNTER_FAKE_FIELD);
                real_count=jsonObject.getInt(COUNTER_REAL_FIELD);
            } catch (NullPointerException e) {
                Log.d("ERROR","NullPointerException");
            } catch (JSONException e) {
                Log.d("ERROR", e.getStackTrace().toString());
            }
            //play sound if new fake news
            if(fake_count-pFake_count > 0 && !blinking && pFake_count > 0) {
                Log.d("INFO","Blinking");
                try {
                    mGpio.setValue(true);
                    blinking=true;
                } catch (IOException e) {
                    Log.d("ERROR", e.getStackTrace().toString());
                }
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("INFO","Stop blinking");
                        try{
                            mGpio.setValue(false);
                            blinking=false;
                        } catch (IOException e) {
                            Log.d("ERROR", e.getStackTrace().toString());
                        }
                    }
                },LED_DELAY_MILLIS);
                pFake_count = fake_count;
            } else if (pFake_count <= 0)
                pFake_count = fake_count;
        }
    }
}
