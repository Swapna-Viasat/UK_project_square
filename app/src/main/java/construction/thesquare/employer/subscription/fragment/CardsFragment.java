package construction.thesquare.employer.subscription.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.employer.subscription.StripeActivity;
import construction.thesquare.employer.subscription.adapter.CardsAdapter;
import construction.thesquare.employer.subscription.model.CardsResponse;
import construction.thesquare.employer.subscription.model.CreditCard;
import construction.thesquare.employer.subscription.model.RemoveCardResponse;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gherg on 12/29/2016.
 */

public class CardsFragment extends Fragment implements CardsAdapter.CardListener {

    @BindView(R.id.cards_rv)
    RecyclerView cardsRv;
    public static final String TAG = "CardsFragment";
    private List<CreditCard> cards = new ArrayList<>();
    private static CardsFragment cardsFragment;
    private CardsAdapter adapter;

    public static CardsFragment newInstance() {
        if (cardsFragment == null) {
            cardsFragment = new CardsFragment();
        }
        return cardsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cards, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new CardsAdapter(this, cards);
        cardsRv.setLayoutManager(new LinearLayoutManager(getContext()));
        cardsRv.setAdapter(adapter);

        HttpRestServiceConsumer.getBaseApiClient()
                .fetchCards().enqueue(new Callback<CardsResponse>() {
            @Override
            public void onResponse(Call<CardsResponse> call,
                                   Response<CardsResponse> response) {
                if (null != response) {
                    if (null != response.body()) {
                        if (null != response.body().response) {
                            if (!cards.isEmpty()) cards.clear();
                            cards.addAll(response.body().response);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CardsResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onRemove(final CreditCard card) {
        new AlertDialog.Builder(getActivity())
                .setMessage(String.format(getString(R.string.employer_payments_sure),
                        String.valueOf(card.last4)))
                .setCancelable(false)
                .setNegativeButton(getString(R.string.employer_payments_cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .setPositiveButton(getString(R.string.employer_payments_yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                remove(card);
                                dialog.dismiss();
                            }
                        })
                .show();
    }

    private void remove(final CreditCard card) {
        HttpRestServiceConsumer.getBaseApiClient()
                .removeCard(card.id).enqueue(new Callback<RemoveCardResponse>() {
            @Override
            public void onResponse(Call<RemoveCardResponse> call,
                                   Response<RemoveCardResponse> response) {
                if (response.code() == 204) {
                    cards.remove(card);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<RemoveCardResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onCardPicked(CreditCard card) {
        //
    }

    @OnClick(R.id.action1)
    public void add() {
        Intent intent = new Intent(getActivity(), StripeActivity.class);
        getActivity().startActivity(intent);
    }
}
