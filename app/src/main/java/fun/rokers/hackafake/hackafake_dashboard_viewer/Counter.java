package fun.rokers.hackafake.hackafake_dashboard_viewer;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Counter implements Runnable {
    private Handler handler;
    private FakeMeter fakeMeter;
    private int pFake_count=0,pReal_count=0;
    private MediaPlayer mp;

    private static long DELAY_MILLIS=2*1000;
    private static String URL="https://api.hackafake.it/counter";

    private static String COUNTER_FAKE_FIELD = "fake";
    private static String COUNTER_REAL_FIELD = "real";


    public Counter(Context appContext) {
        this.handler=new Handler();
        mp = MediaPlayer.create(appContext,R.raw.alarm);
        fakeMeter = new FakeMeter();
        handler.post(this);
    }

    public void close() {
        handler.removeCallbacks(this);
        fakeMeter.close();
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
                e.printStackTrace();
            }
            double density;
            try {
                density = ((double)fake_count)/((double)(real_count+fake_count));
            } catch (ArithmeticException e) {
                density = 0;
            }
            fakeMeter.updateCount(density);

            //check for news
            int new_fake = fake_count-pFake_count;
            int new_real = real_count-pReal_count;
            if((new_fake > 0 || new_real > 0) && !mp.isPlaying() && pFake_count > 0) {
                Log.d("INFO", "playing sound!");
                if(new_fake > 0)
                    mp.start();
                pFake_count = fake_count;
                pReal_count = real_count;
            } else if (pFake_count <= 0) {
                pFake_count = fake_count;
                pReal_count = real_count;
            }

        }
    }
}
