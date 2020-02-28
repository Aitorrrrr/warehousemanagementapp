package com.simlevante.ssmwarehousemanagementapp.Interfaces.Entradas;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.simlevante.ssmwarehousemanagementapp.Models.Movimiento;
import com.simlevante.ssmwarehousemanagementapp.R;

import java.util.List;

import androidx.appcompat.widget.AppCompatCheckedTextView;
import androidx.appcompat.widget.AppCompatTextView;

public class ProductoSelectorDialogAdaptador extends ArrayAdapter<Movimiento>
{
    private Context mContext;
    private int resourceLayout;
    private InterfazAdaptadorSelectorDialog interfaz;

    public ProductoSelectorDialogAdaptador(Context context, int resource,List<Movimiento> items, ProductoFragment implementar)
    {
        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;

        try
        {
            interfaz = (InterfazAdaptadorSelectorDialog) implementar;
        }
        catch(ClassCastException ex)
        {
            Log.d("Aitor","interfaz mal "+ex.getMessage());
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(resourceLayout, null);
        }

        Movimiento movAux = getItem(position);

        if (movAux != null) {
            AppCompatCheckedTextView ean13 = v.findViewById(R.id.viewholder_anyadirProd_dialog_ean13);
            AppCompatTextView codProd = v.findViewById(R.id.viewholder_anyadirProd_dialog_codProd);
            //RadioButton rb = v.findViewById(R.id.viewholder_anyadirProd_dialog_radioButton);

            if (ean13 != null) {
                ean13.setText(movAux.getEan13());
            }

            if (codProd != null) {
                codProd.setText(movAux.getCodart());
            }

            /*rb.setOnClickListener((View view) ->
            {
                Log.d("Aitor","pulsando... " + rb.isChecked());
                interfaz.seleccionado(getItem(position));
            });*/
        }

        return v;
    }

    public interface InterfazAdaptadorSelectorDialog{

        void seleccionado(Movimiento movElegido);
    }
}
