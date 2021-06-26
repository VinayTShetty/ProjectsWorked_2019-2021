package com.benjaminshamoilia.trackerapp.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.benjaminshamoilia.trackerapp.R;
import com.shuhart.stickyheader.StickyAdapter;
import java.util.ArrayList;

public class SectionAdapter extends StickyAdapter<RecyclerView.ViewHolder, RecyclerView.ViewHolder> {

    private ArrayList<Section> sectionArrayList;
    private static final int LAYOUT_HEADER= 0;
    private static final int LAYOUT_CHILD= 1;

    public SectionAdapter(Context context, ArrayList<Section> sectionArrayList){

        // inflater = LayoutInflater.from(context);
        // this.context = context;
        this.sectionArrayList = sectionArrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == LAYOUT_HEADER ) {
            return new HeaderViewholder(inflater.inflate(R.layout.recycler_view_header_item, parent, false));
        }else {
            return new ItemViewHolder(inflater.inflate(R.layout.recycler_view_item, parent, false));
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        if (sectionArrayList.get(position).isHeader()) {
            ((HeaderViewholder) holder).textView.setText( sectionArrayList.get(position).getName());
        } else {
            ((ItemViewHolder) holder).textView.setText(sectionArrayList.get(position).getName());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(sectionArrayList.get(position).isHeader()) {
            return LAYOUT_HEADER;
        }else
            return LAYOUT_CHILD;
    }

    @Override
    public int getItemCount() {
        return sectionArrayList.size();
    }

    @Override
    public int getHeaderPositionForItem(int itemPosition) {
        Log.d("seeee",""+itemPosition+"......"+sectionArrayList.get(itemPosition).sectionPosition());
        return sectionArrayList.get(itemPosition).sectionPosition();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int headerPosition) {
        ((HeaderViewholder) holder).textView.setText( sectionArrayList.get(headerPosition).getName());
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return createViewHolder(parent, LAYOUT_HEADER);
    }

    public static class HeaderViewholder extends RecyclerView.ViewHolder {
        TextView textView;

        HeaderViewholder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_view);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ItemViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_view);
        }
    }
}
