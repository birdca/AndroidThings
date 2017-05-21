package com.ca.threeledslighton;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {
  private static final String RED_GPIO = "BCM4";
  private static final String GRN_GPIO = "BCM17";
  private static final String YEL_GPIO = "BCM27";

  private Gpio redGpio, grnGpio, yelGpio;
  private Handler ledHandler = new Handler();
  private int TIME_DELAY = 500;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    try {
      PeripheralManagerService manager = new PeripheralManagerService();

      redGpio = manager.openGpio(RED_GPIO);
      grnGpio = manager.openGpio(GRN_GPIO);
      yelGpio = manager.openGpio(YEL_GPIO);

      redGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
      grnGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
      yelGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
    } catch (IOException e) {
      Log.w(TAG, "Unable to access GPIO", e);
    }

    ledHandler.post(ledRun);
  }

  private Runnable ledRun = new Runnable() {
    @Override
    public void run() {
      try {
        yelGpio.setValue(false);
        redGpio.setValue(true);
      } catch (IOException e) {
        Log.w(TAG, "GPIO Set Value Failed!", e);
      }

      ledHandler.postDelayed(new Runnable() {
        @Override
        public void run() {
          try {
            redGpio.setValue(false);
            grnGpio.setValue(true);
          } catch (IOException e) {
            Log.w(TAG, "GPIO Set Value Failed!", e);
          }

          ledHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
              try {
                grnGpio.setValue(false);
                yelGpio.setValue(true);
              } catch (IOException e) {
                Log.w(TAG, "GPIO Set Value Failed!", e);
              }
            }
          }, TIME_DELAY);
        }
      }, TIME_DELAY);

      ledHandler.postDelayed(this, TIME_DELAY * 3);
    }
  };

  @Override
  protected void onDestroy() {
    super.onDestroy();

    ledHandler.removeCallbacks(ledRun);

    if (redGpio != null) {
      try {
        redGpio.close();
        redGpio = null;
      } catch (IOException e) {
        Log.w(TAG, "Unable to close GPIO", e);
      }
    }

    if (grnGpio != null) {
      try {
        grnGpio.close();
        grnGpio = null;
      } catch (IOException e) {
        Log.w(TAG, "Unable to close GPIO", e);
      }
    }

    if (yelGpio != null) {
      try {
        yelGpio.close();
        yelGpio = null;
      } catch (IOException e) {
        Log.w(TAG, "Unable to close GPIO", e);
      }
    }
  }
}
