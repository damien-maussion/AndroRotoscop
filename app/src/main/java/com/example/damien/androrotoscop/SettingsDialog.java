package com.example.damien.androrotoscop;

import android.app.Activity;
import android.app.DialogFragment;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.example.damien.androrotoscop.R;

/**
 * Dialog to change some settings (number of layers, display image from video...)
 * Activities that contain this fragment must implement the
 * {@link SettingsDialog.SettingsDialogListener} interface
 * to handle interaction events.
 * Use the {@link SettingsDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsDialog extends DialogFragment {

    public interface SettingsDialogListener{
        public void config(int pf, int pc, boolean displayVid, boolean displayLayers, boolean displayNav);
    }

    private SettingsDialogListener mListener;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PELLURES_FREQ = "pellures_freq";
    private static final String ARG_PELLURES_COUNT = "pellures_count";
    private static final String ARG_DISPLAY_VID = "displayVid";
    private static final String ARG_DISPLAY_LAYERS = "displayLayers";
    private static final String ARG_DISPLAY_NAVIGATION = "displayNav";

    private int pellures_freq;
    private int pellures_count;
    private boolean displayVid;
    private boolean displayLayers;
    private boolean displayNav;

    private RadioGroup sub_freq;
    private Spinner layer;
    private CheckBox ckdv;
    private CheckBox ckdl;
    private CheckBox ckdn;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SettingsDialog.
     */
    public static SettingsDialog newInstance(int pf, int pc, boolean dV, boolean dL, boolean dN) {
        SettingsDialog fragment = new SettingsDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_PELLURES_FREQ, pf);
        args.putInt(ARG_PELLURES_COUNT, pc);
        args.putBoolean(ARG_DISPLAY_VID, dV);
        args.putBoolean(ARG_DISPLAY_LAYERS, dL);
        args.putBoolean(ARG_DISPLAY_NAVIGATION, dN);
        fragment.setArguments(args);
        return fragment;
    }

    public SettingsDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Dialog);
        if (getArguments() != null) {
            pellures_freq = getArguments().getInt(ARG_PELLURES_FREQ);
            pellures_count = getArguments().getInt(ARG_PELLURES_COUNT);
            displayVid = getArguments().getBoolean(ARG_DISPLAY_VID);
            displayLayers = getArguments().getBoolean(ARG_DISPLAY_LAYERS);
            displayNav = getArguments().getBoolean(ARG_DISPLAY_NAVIGATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_settings_dialog, container, false);

        sub_freq = (RadioGroup) v.findViewById(R.id.radioGroupSubFreq);
        sub_freq.check(sub_freq.getChildAt(pellures_freq-1).getId());

        layer = (Spinner) v.findViewById(R.id.spinner_nb_pelure);
        layer.setSelection(pellures_count-1);

        ckdv = (CheckBox) v.findViewById(R.id.checkBoxDisplayVid);
        ckdv.setChecked(displayVid);

        ckdl = (CheckBox) v.findViewById(R.id.checkBoxDisplayLayer);
        ckdl.setChecked(displayLayers);

        ckdn = (CheckBox) v.findViewById(R.id.checkDisplayNav);
        ckdn.setChecked(displayNav);

        Button ok = (Button) v.findViewById(R.id.buttonOkSettings);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int radioButtonID = sub_freq.getCheckedRadioButtonId();
                View radioButton = sub_freq.findViewById(radioButtonID);
                pellures_freq = sub_freq.indexOfChild(radioButton) +1;

                int layer_count = Integer.parseInt(layer.getSelectedItem().toString());

                displayVid = ckdv.isChecked();
                displayLayers = ckdl.isChecked();
                displayNav = ckdn.isChecked();

                mListener.config(pellures_freq, layer_count , displayVid, displayLayers, displayNav);
                dismiss();
            }
        });

        Button cancel = (Button) v.findViewById(R.id.buttonCancelSettings);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return v;
    }

   public void setListener(SettingsDialogListener lis){
       mListener = lis;
   }

}
