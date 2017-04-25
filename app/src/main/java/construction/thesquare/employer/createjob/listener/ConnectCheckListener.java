package construction.thesquare.employer.createjob.listener;

import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import construction.thesquare.shared.utils.TextTools;

/**
 * Created by gherg on 4/4/17.
 */

public class ConnectCheckListener
        implements CompoundButton.OnCheckedChangeListener {

    public static final String TAG = "connectListener";

    private CheckBox connect;
    private CheckBox book;
    private CardView connectContact;
    private CardView bookContact;
    private CardView connectDeadline;
    private IsConnectInterface isConnectInterface;

    public interface IsConnectInterface {
        void onConnectSelected(boolean yes);
    }

    public ConnectCheckListener(IsConnectInterface listener,
                                CheckBox checkBox1,
                                CheckBox checkBox2,
                                CardView cardView1,
                                CardView cardView2,
                                CardView cardView3) {
        this.isConnectInterface = listener;
        this.connect = checkBox1;
        this.book = checkBox2;
        this.connectContact = cardView1;
        this.connectDeadline = cardView2;
        this.bookContact = cardView3;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton,
                                 boolean checked) {
        if (compoundButton == connect && checked) {
            isConnectInterface.onConnectSelected(true);
            book.setChecked(false);
            bookContact.setVisibility(View.GONE);
            connectDeadline.setVisibility(View.VISIBLE);
            connectContact.setVisibility(View.VISIBLE);
        } else if (compoundButton == book && checked) {
            isConnectInterface.onConnectSelected(false);
            connect.setChecked(false);
            bookContact.setVisibility(View.VISIBLE);
            connectDeadline.setVisibility(View.GONE);
            connectContact.setVisibility(View.GONE);
        }
    }
}
