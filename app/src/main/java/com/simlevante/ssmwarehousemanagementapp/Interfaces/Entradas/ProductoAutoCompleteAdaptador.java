package com.simlevante.ssmwarehousemanagementapp.Interfaces.Entradas;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import com.simlevante.ssmwarehousemanagementapp.Models.Movimiento;
import com.simlevante.ssmwarehousemanagementapp.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class ProductoAutoCompleteAdaptador extends ArrayAdapter<Movimiento>
{
    private Context context;
    private int resourceId;
    private List<Movimiento> items, tempItems, suggestions;

    public ProductoAutoCompleteAdaptador(@NonNull Context context, int resourceId, ArrayList<Movimiento> items) {
        super(context, resourceId, items);
        this.items = items;
        this.context = context;
        this.resourceId = resourceId;
        tempItems = new ArrayList<>(items);
        suggestions = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        try {
            if (convertView == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                view = inflater.inflate(resourceId, parent, false);
            }
            Movimiento movAux = getItem(position);
            AppCompatTextView ean13 = view.findViewById(R.id.viewholder_anyadirProd_ean13);
            AppCompatTextView codProd = view.findViewById(R.id.viewholder_anyadirProd_codProd);

            ean13.setText(movAux.getEan13());
            codProd.setText(movAux.getCodart());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    @Nullable
    @Override
    public Movimiento getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getCount() {
        //Log.d("Aitor", Integer.toString(items.size()));
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return movFilter;
    }

    private Filter movFilter = new Filter()
    {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            Movimiento mov = (Movimiento) resultValue;
            return mov.getCodart();
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            if (charSequence != null)
            {
                suggestions.clear();
                for (Movimiento mov: tempItems)
                {
                    if (mov.getCodart().toLowerCase().startsWith(charSequence.toString().toLowerCase()))
                    {
                        suggestions.add(mov);
                    }
                    else if (mov.getEan13().toLowerCase().startsWith(charSequence.toString().toLowerCase()))
                    {
                        suggestions.add(mov);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            }
            else
            {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            ArrayList<Movimiento> tempValues = (ArrayList<Movimiento>) filterResults.values;
            if (filterResults != null && filterResults.count > 0)
            {
                clear();
                for (Movimiento movObj : tempValues)
                {
                    add(movObj);
                }
                notifyDataSetChanged();
            }
            else
            {
                clear();
                notifyDataSetChanged();
            }
        }
    };
}
