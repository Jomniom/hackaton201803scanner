package com.superaligator.hackathonscanner;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/****
 * Docelowo przed wydarzeniem skaner powinien pobierać pełną bazę uczestników lokalnie
 */
public class MainActivity extends AppCompatActivity implements DialogInterface.OnCancelListener {
    private ProgressDialog progressDialog;
    IntentIntegrator integrator;
    TextView info;
    Call<CheckResponse> eventCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        info = (TextView) findViewById(R.id.textView2);
        ((Button) findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan();
            }
        });

        integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);

        integrator.setPrompt("Skanuj zaproszenie");
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
    }

    private void scan() {
        info.setText("");
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                //Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                info.setText("nie znaleziono kodu");
            } else {
                //Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                info.setText("kod: " + result.getContents());
                checkCode();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void checkCode() {
        if (eventCheck != null) {
            eventCheck.cancel();
            eventCheck = null;
        }
        showLoading();
        eventCheck = Comunicator.getInstance().getApiService().check();
        eventCheck.enqueue(new Callback<CheckResponse>() {
            @Override
            public void onResponse(Call<CheckResponse> call, Response<CheckResponse> response) {
                MainActivity.this.hideLoading();
                if (response.isSuccessful() == false) {
                    info.setText("Błąd");
                    return;
                }

                String msg;
                if (response.body().access == -2) {
                    msg = "Uczestnik odrzucony";
                } else if (response.body().access == -1) {
                    msg = "Nie znaleziono uczestnika";
                } else if (response.body().access == 1) {
                    msg = "Ok. " + response.body().name;
                } else if (response.body().access == 2) {
                    msg = "Już ozdnaczony. " + response.body().name;
                } else {
                    msg = "Nie przewidziana odpowiedź";
                }
                info.setText(msg);
            }

            @Override
            public void onFailure(Call<CheckResponse> call, Throwable t) {
                MainActivity.this.hideLoading();
                if (call.isCanceled()) {
                    Log.e("x", "request was cancelled");
                    info.setText("Anulowano");
                } else {
                    Log.w("x", "błąd rejestracji w wydarzeniu");
                    info.setText("Błąd połączenia");
                    t.printStackTrace();
                }
            }
        });
    }

    public void showLoading() {
        if (progressDialog == null)
            progressDialog = MyProgressDialog.show(this);
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(this);
        progressDialog.show();
    }

    public void hideLoading() {
        if (progressDialog == null)
            return;
        progressDialog.dismiss();
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (eventCheck != null) {
            eventCheck.cancel();
            eventCheck = null;
        }
    }
}
