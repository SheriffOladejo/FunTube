package com.funtube.Adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.funtube.R;
import com.funtube.model.Transaction;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class Transactiondapter extends RecyclerView.Adapter<Transactiondapter.TransactionHolder> {
    private List<Transaction> transactionList= new ArrayList<>();
    private Context context;
    public Transactiondapter(List<Transaction> transactionList, Context context){
        this.context=context;
        this.transactionList=transactionList;
    }
    @Override
    public TransactionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewHolder= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_earning, null, false);
        viewHolder.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new Transactiondapter.TransactionHolder(viewHolder);
    }

    @Override
    public void onBindViewHolder(TransactionHolder holder,final int position) {
            holder.text_view_points_transaction_item.setText(transactionList.get(position).getPoints());
            holder.text_view_amount_transaction_item.setText(transactionList.get(position).getAmount());
            holder.text_view_created_transaction_item.setText(transactionList.get(position).getCreated());
            holder.text_view_label_transaction_item.setText(transactionList.get(position).getLabel());
        Picasso.with(context).load(transactionList.get(position).getImage()).placeholder(context.getResources().getDrawable(R.drawable.placeholder)).error(context.getResources().getDrawable(R.drawable.image_quote)).into(holder.image_view_item_earning);
    }
    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class TransactionHolder extends RecyclerView.ViewHolder {

        private final TextView text_view_amount_transaction_item;
        private final TextView text_view_points_transaction_item;
        private final TextView text_view_label_transaction_item;
        private final TextView text_view_created_transaction_item;
        private final CircleImageView image_view_item_earning;

        public TransactionHolder(View itemView) {
            super(itemView);
            this.text_view_amount_transaction_item = (TextView) itemView.findViewById(R.id.text_view_amount_transaction_item);
            this.text_view_points_transaction_item = (TextView) itemView.findViewById(R.id.text_view_points_transaction_item);
            this.text_view_label_transaction_item = (TextView) itemView.findViewById(R.id.text_view_label_transaction_item);
            this.text_view_created_transaction_item = (TextView) itemView.findViewById(R.id.text_view_created_transaction_item);
            this.image_view_item_earning = (CircleImageView) itemView.findViewById(R.id.image_view_item_earning);
        }
    }
}