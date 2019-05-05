package me.winded.passu;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import info.androidhive.fontawesome.FontTextView;
import me.winded.passu.util.StringArray;

public class PasswordEntryAdapter extends RecyclerView.Adapter<PasswordEntryAdapter.ViewHolder> {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public FontTextView itemIconView;
        public TextView itemTextView;

        private ClickListener clickListener;

        public ViewHolder(View v, ClickListener clickListener) {
            super(v);

            itemIconView = v.findViewById(R.id.item_icon);
            itemTextView = v.findViewById(R.id.item_text);
            v.setOnClickListener(this);
            this.clickListener = clickListener;
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    private StringArray dataset;
    private ClickListener clickListener;

    // Provide a suitable constructor (depends on the kind of dataset)
    public PasswordEntryAdapter(StringArray dataset, ClickListener clickListener) {
        this.dataset = dataset;
        this.clickListener = clickListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.icon_text_item, parent, false);
        ViewHolder vh = new ViewHolder(v, clickListener);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // TODO support for folder view
        holder.itemIconView.setText(R.string.fa_file);
        holder.itemTextView.setText(dataset.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return (int)dataset.length();
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }
}
