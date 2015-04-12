package com.example.damien.androrotoscop;

import android.app.DialogFragment;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link FramerateDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FramerateDialog extends DialogFragment {

    public interface FramerateChangedListener{
        public void FramerateChangedListener(int f);
    }


    // the fragment initialization parameters
    private static final String ARG_FPS = "fps";

    private Spinner fps;

    private FramerateChangedListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FramerateDialog.
     */
    public static FramerateDialog newInstance(int frame) {
        FramerateDialog fragment = new FramerateDialog();
        return fragment;
    }

    public FramerateDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_framerate_dialog, container, false);

        Button ok = (Button) v.findViewById(R.id.FramerateButtonOK);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.FramerateChangedListener(Integer.parseInt(fps.getSelectedItem().toString()));
                dismiss();
            }
        });

        Button cancel = (Button) v.findViewById(R.id.FramerateButtonCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        fps = (Spinner) v.findViewById(R.id.spinnerFPS);

        return v;
    }

    public void setFramerateChangedListener(FramerateChangedListener lis){
        mListener = lis;
    }

}
