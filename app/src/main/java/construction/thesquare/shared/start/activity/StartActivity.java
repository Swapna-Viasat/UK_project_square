package construction.thesquare.shared.start.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.employer.StartEmployerActivity;
import construction.thesquare.shared.veriphone.VerifyPhoneActivity;
import construction.thesquare.shared.utils.Constants;
import construction.thesquare.worker.signup.StartWorkerActivity;

/**
 * Refactored by Evgheni Gherghelejiu
 * on 01/06/2016
 */
public class StartActivity extends Activity {

    public static final String TAG = "StartActivity";
    private final int REQUEST_PERMISSIONS_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS,
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, REQUEST_PERMISSIONS_CODE);
        }

    }

    @OnClick(R.id.register_employer)
    public void startEmployerFlow(View view) {
        startActivity(new Intent(this, StartEmployerActivity.class));
    }

    @OnClick(R.id.register_worker)
    public void startWorkerFlow(View view) {
        startActivity(new Intent(this, StartWorkerActivity.class));
    }

    @OnClick(R.id.login)
    public void alreadyHaveAccount(View view) {
        Intent intent = new Intent(StartActivity.this, VerifyPhoneActivity.class);
        intent.putExtra(Constants.KEY_VERIFY_PHONE, Constants.KEY_VERIFY_PHONE_LOGIN);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}