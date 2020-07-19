package com.projectwork.sediexpress;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class ProceedDialog extends Dialog implements
        android.view.View.OnClickListener {
    private final Activity c;
    public Dialog d;
    public TextView pk, dr, appdistance;
    private NavController navController;
    private PrefManager prefManager;
    private TextView yes, cancel;
    private Spinner spinner;


    public ProceedDialog(Activity context) {
        super(context);
        this.c = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mycustom_dialog);
        navController = Navigation.findNavController(c, R.id.nav_host_fragment);

        prefManager = new PrefManager(c);


        pk = findViewById(R.id.pk);
        dr = findViewById(R.id.dr);
        appdistance = findViewById(R.id.appdistance);

        pk.setText(prefManager.getPickLocation());
        dr.setText(prefManager.getDropLocation());
        appdistance.setText(prefManager.getDistance() + " Km");


        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        prefManager.setWeight((float) 1.5);
                        break;
                    case 1:
                        prefManager.setWeight((float) 2.5);
                        break;
                    case 2:
                        prefManager.setWeight((float) 3.5);
                        break;
                    case 3:
                        prefManager.setWeight((float) 4.5);
                        break;
                    case 4:
                        prefManager.setWeight((float) 5.5);
                        break;
                    case 5:
                        prefManager.setWeight((float) 6.5);
                        break;
                    case 6:
                        prefManager.setWeight((float) 7.5);
                        break;
                    case 7:
                        prefManager.setWeight((float) 8.5);
                        break;
                    case 8:
                        prefManager.setWeight((float) 9.5);
                        break;
                    default:
                        prefManager.setWeight((float) 0.5);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        yes = findViewById(R.id.yes);
        yes.setOnClickListener(this);
        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.yes:

                navController.navigate(R.id.requestActivity);
                break;
            case R.id.cancel:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}