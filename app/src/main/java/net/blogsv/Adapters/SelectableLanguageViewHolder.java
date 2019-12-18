package net.blogsv.Adapters;

import android.view.View;
import android.widget.CheckedTextView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import net.blogsv.model.Language;
import net.blogsv.ntsmxh.R;

/**
 * Created by Tamim on 29/06/2018.
 */


public class SelectableLanguageViewHolder extends RecyclerView.ViewHolder {

    public static final int MULTI_SELECTION = 2;
    public static final int SINGLE_SELECTION = 1;
    CheckedTextView textView;
    TextView text_view_item_category_item_select;
    CardView card_view_category_item_select;

    Language mItem;
    OnItemSelectedListener itemSelectedListener;

    public SelectableLanguageViewHolder(View view, OnItemSelectedListener listener) {
        super(view);
        itemSelectedListener = listener;
        card_view_category_item_select = (CardView) view.findViewById(R.id.card_view_category_item_select);
        textView = (CheckedTextView) view.findViewById(R.id.checked_text_item);
        text_view_item_category_item_select = (TextView) view.findViewById(R.id.text_view_item_category_item_select);
        card_view_category_item_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItem.isSelected() && getItemViewType() == MULTI_SELECTION) {
                    setChecked(false);
                } else {
                    setChecked(true);
                }
                itemSelectedListener.onItemSelected(mItem);

            }
        });
    }

    public void setChecked(boolean value) {
        if (value) {
            //card_view_category_item_select.setCardBackgroundColor(Color.parseColor("#FF007E71"));
        } else {
            // card_view_category_item_select.setCardBackgroundColor(Color.parseColor("#a7164f"));
        }
        mItem.setSelected(value);
        textView.setChecked(value);
    }

    public interface OnItemSelectedListener {

        void onItemSelected(Language item);
    }

}