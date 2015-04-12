package com.example.damien.androrotoscop.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.net.Uri;
import android.os.Environment;

import com.example.damien.androrotoscop.R;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Damien on 09/04/2015.
 */
public class Project {

    public static String CONFIG_FILE_EXTENSION = "properties";
    public static String IMAGE_SOURCE_NAME = "image-%09d.png";
    public static String IMAGE_OUTPUT_NAME = "output-%09d.png";

    int fps;

    //File dirSource;
    //File dirOutput;

    //private List<Drawable> imagesSource;
    //private List<Drawable> imagesOutput;

    private Map<Integer, Bitmap> layers;

    Context context;
    private List<Integer> imagesSourceRessource;
    private List<Integer> imagesOutputRessource;

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    private int width, height;

    /** Test if the uri correspond to a valid project file
     *
     * @param configFilePath : the path to test
     * @return true if the uri correspond to a valid project file
     */
    public static boolean isValidProject(Uri configFilePath){
        String file = configFilePath.toString();
        if(file.contains(".") && (file.substring(file.lastIndexOf(".")) == CONFIG_FILE_EXTENSION)) {
            //TODO other verifications

            return true;
        }
        return false;
    }

    /** Build a projet and return the path to the file describing the project
     *
     * @param videoPath: video source
     * @param framerate: output project framerate
     * @return uri to the file describing the project
     */
    public static Uri build(Uri videoPath, int framerate){
        return Uri.parse("."+ CONFIG_FILE_EXTENSION);
    }

    public Bitmap getImage(int i){
        Drawable d = context.getResources().getDrawable(imagesSourceRessource.get(i));
        /*ScaleDrawable sd = new ScaleDrawable(d, 0, 480,800);
        return sd.getDrawable();*/
        return ((BitmapDrawable) d).getBitmap();
    }

    public Bitmap getLayer(int i){
        //return context.getResources().getDrawable(imagesOutputRessource.get(i));
        if (layers.containsKey(i)){
            return layers.get(i);
        }
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    }

    public int getFrameCount(){
        return imagesSourceRessource.size();
    }

    public int getFramerate(){
        return fps;
    }

    public void setLayer(int i, Bitmap b){
        if (b!=null)
            layers.put(i, b);
    }

    public Project(Uri configFilePath,Context c){
        fps = 1;

        context=c;

        imagesSourceRessource = new ArrayList<Integer>();
        imagesSourceRessource.add(R.drawable.image_00001);
        imagesSourceRessource.add(R.drawable.image_00002);
        imagesSourceRessource.add(R.drawable.image_00003);
        imagesSourceRessource.add(R.drawable.image_00004);
        imagesSourceRessource.add(R.drawable.image_00005);
        imagesSourceRessource.add(R.drawable.image_00006);
        imagesSourceRessource.add(R.drawable.image_00007);
        imagesSourceRessource.add(R.drawable.image_00008);
        imagesSourceRessource.add(R.drawable.image_00009);
        imagesSourceRessource.add(R.drawable.image_00010);
        imagesSourceRessource.add(R.drawable.image_00011);
        imagesSourceRessource.add(R.drawable.image_00012);
        imagesSourceRessource.add(R.drawable.image_00013);
        imagesSourceRessource.add(R.drawable.image_00014);

        Bitmap b = getImage(0);
        width = b.getWidth();
        height = b.getHeight();
        System.out.println(width+"-"+height);

        layers = new HashMap<Integer, Bitmap>();

        imagesOutputRessource = new ArrayList<Integer>();
        imagesOutputRessource.add(R.drawable.layer_00001);
        imagesOutputRessource.add(R.drawable.layer_00002);
        imagesOutputRessource.add(R.drawable.layer_00003);
        imagesOutputRessource.add(R.drawable.layer_00004);
        imagesOutputRessource.add(R.drawable.layer_00005);
        imagesOutputRessource.add(R.drawable.layer_00006);
        imagesOutputRessource.add(R.drawable.layer_00007);
        imagesOutputRessource.add(R.drawable.layer_00008);
        imagesOutputRessource.add(R.drawable.layer_00009);
        imagesOutputRessource.add(R.drawable.layer_00010);
        imagesOutputRessource.add(R.drawable.layer_00011);
        imagesOutputRessource.add(R.drawable.layer_00012);
        imagesOutputRessource.add(R.drawable.layer_00013);
        imagesOutputRessource.add(R.drawable.layer_00014);
    }
}
