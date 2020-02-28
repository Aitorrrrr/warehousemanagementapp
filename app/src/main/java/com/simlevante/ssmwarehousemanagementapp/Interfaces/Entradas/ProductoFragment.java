package com.simlevante.ssmwarehousemanagementapp.Interfaces.Entradas;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.text.method.MovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.simlevante.ssmwarehousemanagementapp.Helpers.ToastHost;
import com.simlevante.ssmwarehousemanagementapp.Models.Movimiento;
import com.simlevante.ssmwarehousemanagementapp.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class ProductoFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "movPdtes";
    private ArrayList<Movimiento> movPdtes;
    private ProductoAutoCompleteAdaptador movAdapter;

    private AppCompatAutoCompleteTextView codProd;
    private AppCompatEditText descripcion;
    private AppCompatEditText uds;
    private AppCompatEditText incorp;

    private AppCompatImageButton aceptar;
    private AppCompatImageButton cancelar;
    private AppCompatImageButton buscar;

    private int selectedItem;
    private ArrayList<Movimiento> movCreados;

    private int elegidoSelectorDialog = -1;
    private Movimiento movElegidoSelectorDialog;

    public ProductoFragment() {

    }

    public static ProductoFragment newInstance(ArrayList<Movimiento> movPdtes)
    {
        ProductoFragment fragment = new ProductoFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, movPdtes);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            movPdtes = new ArrayList<>(getArguments().getParcelableArrayList(ARG_PARAM1));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_producto, container, false);
        codProd = v.findViewById(R.id.anyadirProducto_codProd_et);
        descripcion = v.findViewById(R.id.anyadirProductos_descripcionProd_et);
        uds = v.findViewById(R.id.anyadirProducto_udsPedido_et);
        incorp = v.findViewById(R.id.anyadirProducto_udsRecibidas_et);

        aceptar = v.findViewById(R.id.anyadirProducto_aceptar_btn);
        cancelar = v.findViewById(R.id.anyadirProducto_cancelar_btn);
        buscar = v.findViewById(R.id.anyadirProducto_buscar_btn);
        aceptar.setOnClickListener(this);
        cancelar.setOnClickListener(this);
        buscar.setOnClickListener(this);

        bloquearViews();

        ArrayList<Movimiento> movAutoComplete = new ArrayList<>(movPdtes);
        movAdapter = new ProductoAutoCompleteAdaptador(getContext(), R.layout.viewholder_autocomplete_product, movAutoComplete);
        codProd.setThreshold(1);
        codProd.setAdapter(movAdapter);

        movCreados = new ArrayList<>();

        // Cambiado por la expresión lambda de abajo, si compilamos un lambda se reutiliza, si metemos la clase la inicializa cada vez!
        /*codProd.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Movimiento mov = (Movimiento) adapterView.getItemAtPosition(i);
                descripcion.setText(mov.getDenominaci());
                uds.setText(Double.toString(mov.getUnidades()));
                desbloquearViews();
            }
        });*/

        codProd.setOnItemClickListener((AdapterView<?> adapterView, View view, int i, long l) ->
        {
                selectedItem = i;
                Movimiento mov = (Movimiento) adapterView.getItemAtPosition(i);
                descripcion.setText(mov.getDenominaci());
                uds.setText(Double.toString(mov.getUnidades()));
                desbloquearViews();
        });

        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void bloquearViews()
    {
        codProd.setEnabled(true);
        codProd.setDropDownHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        uds.setEnabled(false);
        incorp.setEnabled(false);
        aceptar.setEnabled(false);
    }

    private void desbloquearViews()
    {
        descripcion.setEnabled(false);
        incorp.setEnabled(true);
        aceptar.setEnabled(true);
        codProd.setEnabled(false);
        codProd.setDropDownHeight(0);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.anyadirProducto_aceptar_btn:
                if (comprobarTodo())
                {
                    crearMovs();
                }
                break;

            case R.id.anyadirProducto_cancelar_btn:
                getActivity().onBackPressed();
                break;

            case R.id.anyadirProducto_buscar_btn:
                busquedaDescripcionContiene();
                break;
        }
    }

    private boolean comprobarTodo()
    {
        boolean todoOK = true;
        todoOK = comprobarCampos();

        return todoOK;
    }

    private boolean comprobarCampos()
    {
        boolean camposOK = true;
        if (codProd.getText().toString().trim().compareTo("") == 0)
        {
            camposOK = false;
            ((ToastHost) getActivity()).toast("Código del producto vacío.");
            return camposOK;
        }

        String udsIncorp = incorp.getText().toString().trim();

        if (udsIncorp.compareTo("") == 0)
        {
            camposOK = false;
            ((ToastHost) getActivity()).toast("Unidades a incorporar vacías.");
            return camposOK;
        }
        else
        {
            Double udsAux;
            try
            {
                udsAux = Double.valueOf(incorp.getText().toString().trim());
            }
            catch (Exception e)
            {
                camposOK = false;
                ((ToastHost) getActivity()).toast("Formato de las unidades incorrecto.");
                return camposOK;
            }

            if (udsAux <= 0)
            {
                camposOK = false;
                ((ToastHost) getActivity()).toast("Unidades a incorporar menores o iguales a 0.");
                return camposOK;
            }
        }

        return camposOK;
    }

    private void crearMovs()
    {
        Movimiento movAux = new Movimiento(movAdapter.getItem(selectedItem));
        Movimiento movExtra = new Movimiento(movAdapter.getItem(selectedItem));

        if ((movAux.getUnidades() - movAux.getIncorporad()) < Double.valueOf(incorp.getText().toString()))
        {
            AlertDialog.Builder dialogMovs = new AlertDialog.Builder(getContext());
            dialogMovs.setTitle("Unidades incorporadas mayores que las unidades origen. ¿Desea generar 2 movimientos?");
            dialogMovs.setCancelable(false);

            dialogMovs.setPositiveButton("Sí", (DialogInterface dialog, int which) ->
            {
                movAux.setUnidades(movAux.getUnidades() - movAux.getIncorporad());
                movAux.setMoproviene(String.format("%.0f",movAux.getNumero()) + String.format("%.0f",movAux.getPosicion()));
                movCreados.add(movAux);

                movExtra.setIncorporad(0);
                movExtra.setUnidades(Double.valueOf(incorp.getText().toString()) - movAux.getUnidades());
                movExtra.setMoproviene("");
                movCreados.add(movExtra);

                devolverMovs();
            });
            dialogMovs.setNegativeButton("No", (DialogInterface dialog, int which) ->
            {
                ((ToastHost) getTargetFragment().getActivity()).toast("Rectifique unidades a incorporar.");
            });

            /*AlertDialog dialogEnsenyar = dialogMovs.create();
            dialogEnsenyar.show();
            try
            {
                final Button button = dialogEnsenyar.getButton(AlertDialog.BUTTON_POSITIVE);
                LinearLayout linearLayout = (LinearLayout) button.getParent();
                linearLayout.setDividerDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.divider_invisible, null));
                linearLayout.setDividerPadding(4);
            }
            catch(Exception ex)
            {
                //ignore it
            }*/
            AlertDialog dialogMostrar = dialogMovs.create();
            dialogMostrar.setOnShowListener((DialogInterface dialogInterface) ->
            {
                Button posButton = ((AlertDialog) dialogInterface).getButton(DialogInterface.BUTTON_POSITIVE);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                        (
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                params.setMargins(8,0,0,0);
                posButton.setLayoutParams(params);
            });
            dialogMostrar.show();
        }
        else
        {
            movAux.setUnidades(Double.valueOf(incorp.getText().toString()));
            movAux.setMoproviene(String.format("%.0f",movAux.getNumero()) + String.format("%.0f",movAux.getPosicion()));
            movCreados.add(movAux);

            devolverMovs();
        }
    }

    private void devolverMovs()
    {
        Intent intent = new Intent(getContext(), ProductoFragment.class);
        intent.putParcelableArrayListExtra(getResources().getString(R.string.entradas_intercambio_movCreado), movCreados);
        intent.putExtra(getResources().getString(R.string.entradas_intercambio_movBorrar), movAdapter.getItem(selectedItem));
        intent.putExtra(getResources().getString(R.string.entradas_intercambio_movUdsIncorp), Double.valueOf(incorp.getText().toString()));
        getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
        getActivity().onBackPressed();
    }

    private void busquedaDescripcionContiene()
    {
        String aux = descripcion.getText().toString().trim();
        String[] listaStrings = new String[movPdtes.size()];
        ArrayList<Movimiento> listaDescripcion = new ArrayList<>();

        if (aux.compareTo("") != 0)
        {
            int i = 0;
            for (Movimiento movAux: movPdtes)
            {
                if (movAux.getDenominaci().contains(aux))
                {
                    listaStrings[i] = movAux.getCodart();
                    listaDescripcion.add(movAux);
                }

                i++;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Elija producto");

            builder.setSingleChoiceItems(listaStrings, -1, (DialogInterface dialog, int which) ->
            {
               elegidoSelectorDialog = which;
            });

            builder.setPositiveButton("OK", (DialogInterface dialog, int which) ->
            {
                if (elegidoSelectorDialog >= 0)
                {
                    String codArt = listaStrings[elegidoSelectorDialog];
                    for (Movimiento movAux: listaDescripcion)
                    {
                        if (movAux.getCodart().compareTo(codArt) == 0)
                        {
                            movElegidoSelectorDialog = movAux;
                        }
                    }

                    if (movElegidoSelectorDialog != null)
                    {
                        //codProd.setAdapter(null);
                        codProd.setText(movElegidoSelectorDialog.getCodart());
                        descripcion.setText(movElegidoSelectorDialog.getDenominaci());
                        uds.setText(Double.toString(movElegidoSelectorDialog.getUnidades()));
                        desbloquearViews();
                    }
                }
            });
            builder.setNegativeButton("Cancel", (DialogInterface dialog, int which) ->
            {
                elegidoSelectorDialog = -1;
            });

            AlertDialog dialogMostrar = builder.create();
            dialogMostrar.setOnShowListener((DialogInterface dialogInterface) ->
            {
                Button posButton = ((AlertDialog) dialogInterface).getButton(DialogInterface.BUTTON_POSITIVE);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                        (
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                params.setMargins(8,0,0,0);
                posButton.setLayoutParams(params);
            });

            dialogMostrar.show();
        }
    }
}