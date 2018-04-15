package fun.rokers.hackafake.hackafake_dashboard_viewer;


import android.os.Handler;
import android.util.Log;

import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.Pwm;

import java.io.IOException;

public class FakeMeter {

    private FakeMeter_handler fakeMeter_handler;

    public FakeMeter()  {
        fakeMeter_handler = new RPI3_handler();
    }

    public void updateCount(double density) {
        fakeMeter_handler.displayDensiy(density);
    }

    public void close() {
        fakeMeter_handler.close();
    }

    private abstract class FakeMeter_handler {
        public abstract void displayDensiy(double density);
        public abstract void close();
    }

    private class RPI3_handler extends FakeMeter_handler {
        private Pwm mPwm;
        private final String PWM="PWM1";
        private Handler h;
        private Runnable runnable;
        private final long DELAY_MILLIS = 1*1000;

        public RPI3_handler() {
            try {
                mPwm=PeripheralManager.getInstance().openPwm(PWM);
                mPwm.setEnabled(false);
            } catch (IOException e) {
                Log.d("INFO","IOException turning on pwm");
            }
            h = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        mPwm.setEnabled(false);
                    } catch (IOException e) {
                        Log.d("ERROR","IOException turning off pwm");
                    } catch (NullPointerException e) {

                    }
                }
            };
        }


        @Override
        public void displayDensiy(double density) {
            try {
                mPwm.setPwmDutyCycle(density);
                mPwm.setEnabled(true);
            } catch (IOException e) {

            } catch (NullPointerException e) {

            }
            h.postDelayed(runnable, DELAY_MILLIS);

            return;
        }

        @Override
        public void close() {
            h.removeCallbacks(runnable);
            try {
                mPwm.close();
            } catch (IOException e) {
                Log.d("ERROR","IOException closing pwm control");
            } catch (NullPointerException e) {

            }
            return;
        }
    }
}
