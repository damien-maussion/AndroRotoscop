package com.example.damien.androrotoscop;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;

import com.example.damien.androrotoscop.util.Project;


public class MainActivity extends Activity {

    static final int REQUEST_VIDEO_CAPTURE = 1;
    static final int REQUEST_FILE_PROJECT = 2;

    public static final String EXTRA_PROJECT_FILE_PATH = "com.example.damien.androrotoscop.PROJECT_FILE_PATH";

    Uri projectUri, videoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        projectUri=Uri.parse("");
        videoUri=Uri.parse("");
    }

    /** Launch the capture of a video, the ask the framerate and launch the drawActivity
     *
     * @param view
     */
    public void clickButtonNewProject(View view) {
        /*
        FramerateDialog fd = FramerateDialog.newInstance(1);
        fd.setFramerateChangedListener(new FramerateDialog.FramerateChangedListener() {
            @Override
            public void FramerateChangedListener(int fps) {
                System.out.println(fps);
                launchDraw();
            }
        });
        fd.show(getFragmentManager(), "FPS");*/

        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    /** Launch the file explorer to find the configuration file of a project then launch the drawActivity
     *
     * @param view
     */
    public void clickButtonOpenProject(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        startActivityForResult(Intent.createChooser(intent, "Choose file explorer"), REQUEST_FILE_PROJECT);
    }

    @Override
    /**
     * Called when intent result is available (video or project config file)
     * Open the drawActivity at the end
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == REQUEST_VIDEO_CAPTURE) {
                videoUri = data.getData();

                FramerateDialog fd = FramerateDialog.newInstance(1);
                fd.setFramerateChangedListener(new FramerateDialog.FramerateChangedListener() {

                    @Override
                    public void FramerateChangedListener(int fps) {
                        projectUri = Project.build(videoUri, fps);
                        launchDraw();
                    }
                });
                fd.show(getFragmentManager(), "FPS");
            }
            else if (requestCode == REQUEST_FILE_PROJECT){
                projectUri = data.getData();
                launchDraw();
            }
        }
    }

    /**
     * Launch the drawActivity
     */
    public void launchDraw() {
        //if (Project.isValidProject(projectUri)) {
            Intent i = new Intent(this, DrawActivity.class);
            i.putExtra(EXTRA_PROJECT_FILE_PATH, projectUri.toString());
            startActivity(i);
            finish();
        //}
    }

}
