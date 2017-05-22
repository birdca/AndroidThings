package com.ca.sevensegments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {
  private static final String PIN_NAME[] = {"BCM19", "BCM26", "BCM4", "BCM5", "BCM17", "BCM13", "BCM6"};
  private static final boolean hex[][] = {
      {true, true, true, true, true, true, false},
      {false, true, true, false, false, false, false},
      {true, true, false, true, true, false, true},
      {true, true, true, true, false, false, true},
      {false, true, true, false, false, true, true},
      {true, false, true, true, false, true, true},
      {true, false, true, true, true, true, true},
      {true, true, true, false, false, false, false},
      {true, true, true, true, true, true, true},
      {true, true, true, true, false, true, true},
      {true, true, true, false, true, true, true},
      {false, false, true, true, true, true, true},
      {true, false, false, true, true, true, false},
      {false, true, true, true, true, false, true},
      {true, false, false, true, true, true, true},
      {true, false, false, false, true, true, true}};
  private Gpio gpios[] = new Gpio[PIN_NAME.length];
  private Handler handler = new Handler();
//  private static int n = 15;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    PeripheralManagerService manager = new PeripheralManagerService();
    for (int i = 0; i < PIN_NAME.length; ++i) {
      try {
        gpios[i] = manager.openGpio(PIN_NAME[i]);
        gpios[i].setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
        gpios[i].setActiveType(Gpio.ACTIVE_LOW);
      } catch (IOException e) {
        Log.w(TAG, "Unable to access GPIO!", e);
      }
    }

    handler.post(runNumber);
  }

  private Runnable runNumber = new Runnable() {
    int n = 15;

    @Override
    public void run() {
      for (int i = 0; i < 7; ++i) {
        try {
          gpios[i].setValue(hex[n][i]);
        } catch (IOException e) {
          Log.w(TAG, "Unable to access GPIO!", e);
        }
      }

      if (--n < 0) {
        n = 15;
      }

      handler.postDelayed(runNumber, 1000);
    }
  };

  @Override
  protected void onDestroy() {
    super.onDestroy();

    handler.removeCallbacks(runNumber);
    for (int i = 0; i < PIN_NAME.length; ++i) {
      if (gpios[i] != null) {
        try {
          gpios[i].close();
          gpios[i] = null;
        } catch (IOException e) {
          Log.w(TAG, "Unable to close GPIO", e);
        }
      }
    }
  }
}
