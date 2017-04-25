package construction.thesquare.worker.myaccount.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.shared.data.model.Invoice;
import construction.thesquare.shared.utils.DateUtils;

/**
 * Created by maizaga on 5/11/16.
 *
 */

public class MyAccountInvoicesAdapter extends RecyclerView.Adapter {

    private Context context;

    private enum ItemType {
        HEADER(0), ITEM(1);

        private int code;

        ItemType(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        static ItemType getItemType(int code) {
            for (ItemType type : values()) {
                if (type.getCode() == code) return type;
            }

            return null;
        }
    }

    private List<Object> items;
    private List<Invoice> invoices;

    private TreeSet<Integer> headers = new TreeSet<>();
    private TreeSet<Integer> invoiceIndexes = new TreeSet<>();
    private boolean hasApproved = false;
    private boolean hasPaid = false;

    public MyAccountInvoicesAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemType type = ItemType.getItemType(viewType);

        if (type != null)
            switch (type) {
                case HEADER:
                    View headerView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.list_item_header, parent, false);

                    return new HeaderViewHolder(headerView);
                case ITEM:
                    View itemView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.list_item_invoices, parent, false);

                    return new ItemViewHolder(itemView);
            }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemType type = ItemType.getItemType(getItemViewType(position));

        if (type != null)
            switch (type) {
                case HEADER:
                    String text = (String) items.get(position);
                    ((HeaderViewHolder)holder).text.setText(text);
                    break;
                case ITEM:
                    Invoice invoice = (Invoice) items.get(position);
                    ItemViewHolder itemViewHolder = (ItemViewHolder)holder;
                    itemViewHolder.companyName.setText(invoice.getTimesheet().getCompanyName());
                    itemViewHolder.daysWorked.setText(DateUtils.formatWeekFromDayToDay(invoice.getTimesheet().getFrom(), invoice.getTimesheet().getTo()));
                    switch (invoice.getStatus()) {
                        case APPROVED:
                            itemViewHolder.buttonText.setText("");
                            break;
                        case PAID:
                            itemViewHolder.buttonText.setText(R.string.paid);
                            itemViewHolder.buttonText.setTextColor(Color.GREEN);
                            break;
                    }
                    itemViewHolder.daysWorkedAmount.setText(invoice.getDaysWorkedAmout());
            }
    }

    @Override
    public int getItemViewType(int position) {
        if (invoiceIndexes.contains(position)) {
            return ItemType.ITEM.getCode();
        } else if (headers.contains(position)) {
            return ItemType.HEADER.getCode();
        }

        return -1;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setInvoices(List<Invoice> invoices) {
        if (invoices == null) {
            this.invoices = new ArrayList<>();
        } else {
            this.invoices = invoices;
        }

        initializeIndexes();
    }

    private void initializeIndexes() {
        if (items == null) {
            items = new ArrayList<>();
        } else {
            items.clear();
        }

        headers.clear();
        invoiceIndexes.clear();

        if (invoices.size() > 0) {
            int index = 0;
            for (Invoice invoice : invoices) {
                switch (invoice.getStatus()) {
                    case PAID:
                        if (!hasPaid) {
                            hasPaid = true;
                            items.add(context.getString(R.string.paid_header));
                            headers.add(index);
                            index++;
                        }
                        items.add(invoice);
                        invoiceIndexes.add(index);
                        break;
                    case APPROVED:
                        if (!hasApproved) {
                            hasApproved = true;
                            items.add(context.getString(R.string.approved_to_be_paid_header));
                            headers.add(index);
                            index++;
                        }
                        items.add(invoice);
                        invoiceIndexes.add(index);
                        break;
                }
                index++;
            }
        }

        notifyDataSetChanged();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text)
        TextView text;

        HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.invoice_item_company_name) TextView companyName;
        @BindView(R.id.invoice_item_days_worked) TextView daysWorked;
        @BindView(R.id.invoice_item_days_worked_amount) TextView daysWorkedAmount;
        @BindView(R.id.invoice_item_button) TextView buttonText;

        ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
