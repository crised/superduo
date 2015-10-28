package it.jaschke.alexandria;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.Log;


import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


/**
 * Created by crised on 27-10-15.
 */
public class BarCodeActivity extends Activity implements ZXingScannerView.ResultHandler {

    public final String LOG_TAG = BarCodeActivity.class.getSimpleName();

    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
        mScannerView.setAutoFocus(true);
        mScannerView.setFlash(true);


    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
        // Toggle flash:
        mScannerView.setAutoFocus(true);


    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.d(LOG_TAG, rawResult.getText()); // Prints scan results
        Log.d(LOG_TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
        Intent intent = new Intent(this, MainActivity.class);
        Bundle b = new Bundle();
        b.putString(getResources().getString(R.string.scanned_esn_bundle_key), rawResult.getText()); //Your id
        intent.putExtras(b); //Put your id to your next Intent
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);

        startActivity(intent);
        finish();


    }
}
