package com.pegasus.pegpay;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.AQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zed on 5/19/2016.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {
    private Context cxt;
    private ArrayList<Message> mMessages;
    static OnItemClickListener clickListener;

    public MessageAdapter(Context context, ArrayList<Message> objects){
        cxt = context;
        mMessages = new ArrayList<>(objects);
    }

    class MessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        View view;

        public MessageHolder(View v) {
            super(v);
            view = v;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(view, getPosition());
        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public void setTransactions(List<Message> messages) {
        mMessages = new ArrayList<>(messages);
    }

    @Override
    public MessageHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_item, viewGroup, false);
        MessageHolder pvh = new MessageHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final MessageHolder messageHolder, int i) {
        final Message m = mMessages.get(i);

        String title = Utils.truncateString(m.subject, 40);
        AQuery aq = new AQuery(messageHolder.view);
        aq.id(R.id.txt_subject).text(title).getTextView().setTypeface(null, m.read ? Typeface.NORMAL : Typeface.BOLD);
        aq.id(R.id.txt_date).text(m.date);
        aq.id(R.id.txt_content).text(Utils.truncateString(m.content, 80));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public Message removeItem(int position) {
        final Message message = mMessages.remove(position);
        notifyItemRemoved(position);
        return message;
    }

    public void addItem(int position, Message message) {
        mMessages.add(position, message);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Message message = mMessages.remove(fromPosition);
        mMessages.add(toPosition, message);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void animateTo(List<Message> messages) {
        applyAndAnimateRemovals(messages);
        applyAndAnimateAdditions(messages);
        applyAndAnimateMovedItems(messages);
    }

    private void applyAndAnimateRemovals(List<Message> newTransactions) {
        for (int i = mMessages.size() - 1; i >= 0; i--) {
            final Message transaction = mMessages.get(i);
            if (!newTransactions.contains(transaction)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Message> newTransactions) {
        for (int i = 0, count = newTransactions.size(); i < count; i++) {
            final Message message = newTransactions.get(i);
            if (!mMessages.contains(message)) {
                addItem(i, message);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Message> newMessages) {
        for (int toPosition = newMessages.size() - 1; toPosition >= 0; toPosition--) {
            final Message message = newMessages.get(toPosition);
            final int fromPosition = mMessages.indexOf(message);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }
}
