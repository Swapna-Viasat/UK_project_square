package construction.thesquare.employer.closejob;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import butterknife.ButterKnife;
import construction.thesquare.R;


public class CloseJobActivity extends AppCompatActivity {

    public static final String TAG = "CloseJobActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_close_job);
        ButterKnife.bind(this);
    }

}
