package com.pegasus.pegpay;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zed on 4/25/2016.
 */
public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionHolder> {
    private Context cxt;
    private ArrayList<Transaction> mTransactions;

    public TransactionAdapter(Context context, ArrayList<Transaction> objects){
        cxt = context;
        mTransactions = new ArrayList<>(objects);
    }

    class TransactionHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView titleView;
        TextView subtitleView;
        TextView amountView;

        public TransactionHolder(View v) {
            super(v);
            imageView = (ImageView)v.findViewById(R.id.img_icon);
            titleView = (TextView)v.findViewById(R.id.txt_title);
            subtitleView = (TextView)v.findViewById(R.id.txt_subtitle);
            amountView = (TextView)v.findViewById(R.id.txt_amount);
        }
    }

    @Override
    public int getItemCount() {
        return mTransactions.size();
    }

    public void setTransactions(List<Transaction> transactions) {
        mTransactions = new ArrayList<>(transactions);
    }

    @Override
    public TransactionHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.transaction_item, viewGroup, false);
        TransactionHolder pvh = new TransactionHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final TransactionHolder transactionHolder, int i) {
        final Transaction t = mTransactions.get(i);

        String title = Utils.truncateString(t.title, 40);
        transactionHolder.titleView.setText(title);
        transactionHolder.amountView.setText(t.amountPrefix + " UGX " + t.amount);

        transactionHolder.subtitleView.setText(t.subtitle + " | " + t.date);
        transactionHolder.imageView.setImageResource(t.icon);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public Transaction removeItem(int position) {
        final Transaction transaction = mTransactions.remove(position);
        notifyItemRemoved(position);
        return transaction;
    }

    public void addItem(int position, Transaction transaction) {
        mTransactions.add(position, transaction);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Transaction transaction = mTransactions.remove(fromPosition);
        mTransactions.add(toPosition, transaction);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void animateTo(List<Transaction> transactions) {
        applyAndAnimateRemovals(transactions);
        applyAndAnimateAdditions(transactions);
        applyAndAnimateMovedItems(transactions);
    }

    private void applyAndAnimateRemovals(List<Transaction> newTransactions) {
        for (int i = mTransactions.size() - 1; i >= 0; i--) {
            final Transaction transaction = mTransactions.get(i);
            if (!newTransactions.contains(transaction)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Transaction> newTransactions) {
        for (int i = 0, count = newTransactions.size(); i < count; i++) {
            final Transaction transaction = newTransactions.get(i);
            if (!mTransactions.contains(transaction)) {
                addItem(i, transaction);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Transaction> newTransactions) {
        for (int toPosition = newTransactions.size() - 1; toPosition >= 0; toPosition--) {
            final Transaction transaction = newTransactions.get(toPosition);
            final int fromPosition = mTransactions.indexOf(transaction);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }
}
