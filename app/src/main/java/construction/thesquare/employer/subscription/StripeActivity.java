
package construction.thesquare.employer.subscription;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.employer.subscription.model.CreateCardRequest;
import construction.thesquare.employer.subscription.model.CreateCardResponse;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.TextTools;
import construction.thesquare.shared.view.widget.JosefinSansEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gherg on 12/29/2016.
 */

public class StripeActivity extends AppCompatActivity {

    public static final String TAG = "StripeActivity";
    @BindView(R.id.name_in) JosefinSansEditText nameIn;
    @BindView(R.id.number_in) JosefinSansEditText numberIn;
    @BindView(R.id.month_exp) JosefinSansEditText monthIn;
    @BindView(R.id.year_exp) JosefinSansEditText yearIn;
    @BindView(R.id.cvc_in) JosefinSansEditText cvcIn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.action2)
    public void add() {
        if (validate()) {

            final ProgressDialog progressDialog = new ProgressDialog(getApplicationContext());
            progressDialog.setCancelable(false); progressDialog.show();

            Card card = new Card(numberIn.getText().toString(),
                    Integer.valueOf(monthIn.getText().toString()),
                    Integer.valueOf(yearIn.getText().toString()),
                    cvcIn.getText().toString());

            if (card.validateNumber() && card.validateNumber()) {
                try {
                    // the following key was given to me by Eduardo on
                    // 01/17/2017
                    final Stripe stripe = new Stripe("pk_test_iUGx8ZpCWm6GeSwBpfkdqjSQ");
                    stripe.createToken(card, new TokenCallback() {
                        @Override
                        public void onError(Exception error) {
                            progressDialog.dismiss();
                            CrashLogHelper.logException(error);
                        }

                        @Override
                        public void onSuccess(Token token) {
                            progressDialog.dismiss();
                            TextTools.log("Token Stripe: ", token.toString());
                            saveCard(token.getId());
                        }
                    });

                } catch (Exception e) {
                    CrashLogHelper.logException(e);
                    progressDialog.dismiss();
                }
            } else {
                progressDialog.dismiss();
            }
        }
    }

    @OnClick(R.id.close)
    public void close() {
        finish();
    }

    private boolean validate() {
        if (nameIn.getText().toString().equals("")) {
            nameIn.setError(getString(R.string.employer_payments_name_error));
            return false;
        }
        if (numberIn.getText().toString().equals("")) {
            numberIn.setError(getString(R.string.employer_payments_number_error));
            return false;
        }
        if (cvcIn.getText().toString().equals("")) {
            cvcIn.setError(getString(R.string.employer_payments_cvc_error));
            return false;
        }
        if (monthIn.getText().toString().equals("")) {
            monthIn.setError(getString(R.string.employer_payments_month_error));
            return false;
        }
        if (yearIn.getText().toString().equals("")) {
            yearIn.setError(getString(R.string.employer_payments_year_error));
            return false;
        }
        return true;
    }

    private void saveCard(String token) {
        ProgressDialog progressDialog = new ProgressDialog(getApplicationContext());
        progressDialog.setCancelable(false); progressDialog.show();
        CreateCardRequest request = new CreateCardRequest();
        request.name = nameIn.getText().toString();
        request.token = token;
        HttpRestServiceConsumer.getBaseApiClient()
                .addCard(request)
                .enqueue(new Callback<CreateCardResponse>() {
                    @Override
                    public void onResponse(Call<CreateCardResponse> call,
                                           Response<CreateCardResponse> response) {
                        if (response.code() == 201) {
                            Toast.makeText(getApplicationContext(),
                                    "Success!", Toast.LENGTH_SHORT).show();
                            // finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<CreateCardResponse> call, Throwable t) {
                        new AlertDialog.Builder(getApplicationContext())
                                .setMessage((null != t.getMessage())
                                        ? t.getMessage() : "Something went wrong.")
                                .show();
                    }
                });
    }
}