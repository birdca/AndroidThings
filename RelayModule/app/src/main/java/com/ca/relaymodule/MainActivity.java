package com.ca.relaymodule;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {
  private static final String RELAY_MODULE = "BCM4";
  private Handler handler = new Handler();
  private Gpio relay;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    PeripheralManagerService service = new PeripheralManagerService();
    try {
      relay = service.openGpio(RELAY_MODULE);
      relay.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
      relay.setActiveType(Gpio.ACTIVE_HIGH);

      handler.post(relayRun);
    } catch (IOException e) {
      Log.e(TAG, "Unable to access GPIO!", e);
    }
  }

  private Runnable relayRun = new Runnable() {
    @Override
    public void run() {
      try {
        relay.setValue(!relay.getValue());
        Log.i(TAG, "GPIO " + relay.getValue());
      } catch (IOException e) {
        Log.e(TAG, "Unable to set GPIO!", e);
      }

      handler.postDelayed(relayRun, 1000);
    }
  };

  @Override
  protected void onDestroy() {
    super.onDestroy();

    handler.removeCallbacks(relayRun);
    if (relay != null) {
      try {
        relay.close();
        relay = null;
      } catch (IOException e) {
        Log.w(TAG, "Unable to close GPIO", e);
      }
    }
  }
}
