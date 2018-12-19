package id.firdausns.test_firdaus.Config;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import id.firdausns.test_firdaus.R;

public class MyAdapterListRecord extends RecyclerView.Adapter<MyAdapterListRecord.ViewHolder>{
    private ArrayList<ItemRecord> itemRecords;
    private Context context;
    OnItemClickListener mItemClickListener;

    public MyAdapterListRecord(final ArrayList<ItemRecord> itemRecords,Context context){
        this.itemRecords=itemRecords;
        this.context=context;
    }

    @Override
    public MyAdapterListRecord.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_record,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyAdapterListRecord.ViewHolder holder, int position) {
        holder.tv_nama.setText(itemRecords.get(position).getFilename());
        holder.tv_sample.setText(Config.getSample(itemRecords.get(position).getUri())+" kHz");
    }

    @Override
    public int getItemCount() {
        return itemRecords.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_nama,tv_sample;
        CardView card;

        public ViewHolder(View view){
            super(view);
            tv_nama = (TextView) view.findViewById(R.id.tv_nama);
            tv_sample = (TextView) view.findViewById(R.id.tv_sample);
            card = (CardView) view.findViewById(R.id.card);

            card.setOnClickListener(this);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getPosition(),itemRecords.get(getPosition()).uri,itemRecords.get(getPosition()).filename);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view , int position,String uri, String filename);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}
