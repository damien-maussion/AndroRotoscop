package com.example.damien.androrotoscop;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.example.damien.androrotoscop.util.Project;

/**
 * Page de dessin
 *
 * Affiche les calques, l'image de la vidéo ainsi que la zone de dessin.
 * Permet de modifier les outils de dessin et les paramètres.
 */
public class DrawActivity extends Activity {

    //Maximum of layer
    public static int MAX_LAYER = 5;

    //Frequence of layers
    private int pellures_freq;

    //Number of layer displayed
    private int pellures_count;

    //Area where the image of the video will be displayed
    private ImageView displayVid;

    //Take true if the layers should be displayed
    private boolean displayLayers;

    //Area where the layers are displayed
    private ImageView[] layers;

    //The projet used
    private Project project;

    private int currentImage;

    private ImageButton previousButton;
    private ImageButton nextButton;

    private DrawingView drawingView;

    private SeekBar navigationBar;

    private ImageButton buttonPen;
    private ImageButton buttonEraser;

    private ListView actionsProject;

    /** Create the activity
     *
     * Set attributes and listeners.
     * Set images
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        Uri projectURI = Uri.parse(getIntent().getStringExtra(MainActivity.EXTRA_PROJECT_FILE_PATH));
        project = new Project(projectURI, this);

        currentImage = -1;

        pellures_count = 5;
        pellures_freq =1;

        displayVid = (ImageView) findViewById(R.id.DisplayVideo);

        displayLayers = true;
        layers = new ImageView[MAX_LAYER];
        layers[0] = (ImageView) findViewById(R.id.Layer1);
        layers[1] = (ImageView) findViewById(R.id.Layer2);
        layers[2] = (ImageView) findViewById(R.id.Layer3);
        layers[3] = (ImageView) findViewById(R.id.Layer4);
        layers[4] = (ImageView) findViewById(R.id.Layer5);

        previousButton = (ImageButton) findViewById(R.id.previousButton);
        nextButton = (ImageButton) findViewById(R.id.nextButton);
        navigationBar = (SeekBar) findViewById(R.id.navigationBar);
        navigationBar.setMax(project.getFrameCount()-1);
        navigationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                goTo(seekBar.getProgress());
            }
        });

        Spinner size =(Spinner) findViewById(R.id.spinnerSize);
        size.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(parent.getItemAtPosition(position).toString());
                drawingView.changeToolWidth(Integer.parseInt(parent.getItemAtPosition(position).toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        drawingView = (DrawingView) findViewById(R.id.drawingView);
        drawingView.setSize(project.getWidth(), project.getHeight());
        ColorPickerView colorPickerView = (ColorPickerView) findViewById(R.id.color_picker_view);

        colorPickerView.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                clickPen(null);
                drawingView.setColor(color);
            }
        });
        colorPickerView.setColor(getResources().getColor(android.R.color.black));

        buttonPen = (ImageButton) findViewById(R.id.buttonPen);
        buttonEraser = (ImageButton) findViewById(R.id.buttonEraser);

        actionsProject = (ListView) findViewById(R.id.listActionProject);
        actionsProject.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String  itemValue = (String) actionsProject.getItemAtPosition(position);
                switch (itemValue){
                    case "Quitter":
                        System.out.println("quitter");
                        finish();
                        break;
                }
            }
        });
        goTo(0);
    }

    /** Set the pen as tool
     *
     * @param view
     */
    public void clickPen(View view){
        drawingView.setPen();
        buttonPen.setAlpha(1f);
        buttonEraser.setAlpha(.5f);
    }

    /** Set the eraser as tool
     *
     * @param view
     */
    public void clickEraser(View view){
        drawingView.setEraser();
        buttonPen.setAlpha(.5f);
        buttonEraser.setAlpha(1f);
    }

    /** Open a dialog to change some settings
     *
     * @param view
     */
    public void clickButtonSettings(View view) {
        SettingsDialog sd = SettingsDialog.newInstance(pellures_freq, pellures_count,
                displayVid.getVisibility()==View.VISIBLE,
                displayLayers,
                navigationBar.getVisibility()==View.VISIBLE);
        sd.setListener(new SettingsDialog.SettingsDialogListener(){

            @Override
            public void config(int pf, int pc, boolean dV, boolean dL, boolean dN) {
                setLayerFrequence(pf);
                setLayerCount(pc);
                setDisplayVideo(dV);
                setDisplayLayer(dL);
                navigationBar.setVisibility(dN? View.VISIBLE : View.GONE);
            }
        });
        sd.show(getFragmentManager(), "Settings");
    }

    /** Change the number of layer and change the display
     *
     * @param pc : number of layer used
     */
    private void setLayerCount(int pc) {
        if (pellures_count!=pc){
            pellures_count=pc;
            displayLayers();
        }
    }

    /** Change the frequence of layers and modify the images used by the array layer
     *
     * @param pf : the new frequence
     */
    private void setLayerFrequence(int pf) {
        if (pellures_freq!=pf){
            pellures_freq = pf;
            changeLayers();
        }
    }

    /**
     * Modify images in layer[] according to the frequence and the current image index
     */
    private void changeLayers() {
        int i =MAX_LAYER-1;
        int imgIndex = currentImage-1;
        while (i>=0) {
            Bitmap b = project.getLayer(imgIndex);
            if (imgIndex>=0 && b != null) {
                layers[i].setImageBitmap(b);
            }
            else{
                layers[i].setImageBitmap(null);
            }
            i--;
            imgIndex -= pellures_freq;
        }
    }

    /**
     * Display layer as wanted (pellures_count or none)
     */
    public void displayLayers(){
        int visibility = displayLayers ? View.VISIBLE : View.INVISIBLE;
        for (int i=MAX_LAYER-1; i>MAX_LAYER-1-pellures_count; i--){
            layers[i].setVisibility(visibility);
        }
        for (int i=MAX_LAYER-1-pellures_count; i>=0; i--){
            layers[i].setVisibility(View.INVISIBLE);
        }
    }

    /** Change the display of the layers
     *
     * @param dL : true to display, false if not
     */
    private void setDisplayLayer(boolean dL) {
        displayLayers = dL;
        displayLayers();
    }

    /** Change the display of the image of the video
     *
     * @param dV : true to display, false if not
     */
    private void setDisplayVideo(boolean dV) {
        int visibility = dV ? View.VISIBLE : View.INVISIBLE;
        displayVid.setVisibility(visibility);
    }

    /** Move to the previous image
     *
     * @param view
     */
    public void goPrevious(View view){
        goTo(currentImage-1);
    }

    /** Move to next image
     *
     * @param view
     */
    public void goNext(View view){
        goTo(currentImage+1);
    }

    /** Move to the image passed as parameter
     *
     * @param i : the new image index
     */
    public void goTo(int i){
        if (i>=0 && i<project.getFrameCount() && currentImage!=i){

            //save the drawing in project
            project.setLayer(currentImage, drawingView.getBitmap());

            currentImage = i;

            //modify all displayed images
            displayVid.setBackground(new BitmapDrawable(project.getImage(currentImage)));
            changeLayers();
            previousButton.setVisibility(currentImage == 0 ? View.GONE : View.VISIBLE);
            nextButton.setVisibility(currentImage == (project.getFrameCount() - 1) ? View.GONE : View.VISIBLE);
            navigationBar.setProgress(currentImage);

            drawingView.setBitmap(project.getLayer(currentImage));
        }
    }

    /** Toogle the visibility of the actions of the project (Save...)
     *
     * @param view
     */
    public void displayActionsProject(View view){
        int visibility = (actionsProject.getVisibility()==View.VISIBLE) ? View.GONE : View.VISIBLE;
        actionsProject.setVisibility(visibility);
    }

    /** Toogle the display of the drawing tools area
     *
     * @param view
     */
    public void clickDrawingTools(View view){
        RelativeLayout l = (RelativeLayout) findViewById(R.id.layoutTools);
        ImageButton button = (ImageButton) findViewById(R.id.buttonDrawingTools);
        if (l.getVisibility()== View.VISIBLE) {
            l.setVisibility(View.GONE);
            button.setImageResource(android.R.drawable.arrow_down_float);
        }
        else{
            l.setVisibility(View.VISIBLE);
            button.setImageResource(android.R.drawable.arrow_up_float);
        }
    }
}
