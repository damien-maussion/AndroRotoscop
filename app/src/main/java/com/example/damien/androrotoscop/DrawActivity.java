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


public class DrawActivity extends Activity {

    public static int MAX_LAYER = 5;

    private int pellures_freq;
    private int pellures_count;
    private ImageView displayVid;

    private boolean displayLayers;
    private ImageView[] layers;

    private Project project;
    private int currentImage;

    private ImageButton previousButton;
    private ImageButton nextButton;

    private DrawingView drawingView;

    private SeekBar navigationBar;

    private ImageButton buttonPen;
    private ImageButton buttonEraser;

    private ListView actionsProject;

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
                drawingView.setColor(Integer.parseInt(parent.getItemAtPosition(position).toString()));
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
                drawingView.setColor(color);
            }
        });
        colorPickerView.setColor(getResources().getColor(android.R.color.black));

        buttonPen = (ImageButton) findViewById(R.id.buttonPen);
        buttonEraser = (ImageButton) findViewById(R.id.buttonEraser);

        actionsProject = (ListView) findViewById(R.id.listActionProject);

        /*
        Bitmap b = Bitmap.createBitmap(1200, 720, Bitmap.Config.ARGB_8888);
        b.eraseColor(getResources().getColor(android.R.color.holo_blue_bright));
        layers[4].setBackground(new BitmapDrawable(b));*/

        //layers[4].setBackground(new BitmapDrawable(project.getImage(8)));

        goTo(0);
    }

    public void clickPen(View view){
        drawingView.setPen();
        buttonPen.setAlpha(1f);
        buttonEraser.setAlpha(.5f);
    }

    public void clickEraser(View view){
        drawingView.setEraser();
        buttonPen.setAlpha(.5f);
        buttonEraser.setAlpha(1f);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_draw, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

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

    private void setLayerCount(int pc) {
        if (pellures_count!=pc){
            pellures_count=pc;
            displayLayers();
        }
    }

    private void setLayerFrequence(int pf) {
        if (pellures_freq!=pf){
            pellures_freq = pf;
            changeLayers();
        }
    }

    private void changeLayers() {
        int i =MAX_LAYER-1;
        int imgIndex = currentImage-1;
        while (i>=0) {
            Bitmap b = project.getLayer(imgIndex);
            System.out.println("dL"+i+" L"+imgIndex);
            if (imgIndex>=0 && b != null) {
                System.out.println("imgIndex>=0 && b != null");
                System.out.println("fetch dim"+ b.getWidth()+"-"+ b.getHeight());
                layers[i].setImageBitmap(b);
            }
            else{
                layers[i].setImageBitmap(null);
            }
            i--;
            imgIndex -= pellures_freq;
        }
    }

    public void displayLayers(){
        int visibility = displayLayers ? View.VISIBLE : View.INVISIBLE;
        for (int i=MAX_LAYER-1; i>MAX_LAYER-1-pellures_count; i--){
            layers[i].setVisibility(visibility);
        }
        for (int i=MAX_LAYER-1-pellures_count; i>=0; i--){
            layers[i].setVisibility(View.INVISIBLE);
        }
    }

    private void setDisplayLayer(boolean dL) {
        displayLayers = dL;
        displayLayers();
    }

    private void setDisplayVideo(boolean dV) {
        int visibility = dV ? View.VISIBLE : View.INVISIBLE;
        displayVid.setVisibility(visibility);
    }

    public void goPrevious(View view){
        goTo(currentImage-1);
    }

    public void goNext(View view){
        goTo(currentImage+1);
    }

    //TODO suppress Syso
    public void goTo(int i){
        if (i>=0 && i<project.getFrameCount() && currentImage!=i){
System.out.println("add dim"+ drawingView.getBitmap().getWidth()+"-"+ drawingView.getBitmap().getHeight());
            project.setLayer(currentImage, drawingView.getBitmap());

            currentImage = i;

            displayVid.setBackground(new BitmapDrawable(project.getImage(currentImage)));
            System.out.println("dV I" + currentImage);
            changeLayers();
            previousButton.setVisibility(currentImage == 0 ? View.GONE : View.VISIBLE);
            nextButton.setVisibility(currentImage == (project.getFrameCount() - 1) ? View.GONE : View.VISIBLE);
            navigationBar.setProgress(currentImage);

            drawingView.setBitmap(project.getLayer(currentImage));
            System.out.println("drV L" + currentImage);
        }
    }

    public void displayActionsProject(View view){
        int visibility = (actionsProject.getVisibility()==View.VISIBLE) ? View.GONE : View.VISIBLE;
        actionsProject.setVisibility(visibility);
    }

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
