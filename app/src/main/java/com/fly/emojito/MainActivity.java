package com.fly.emojito;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    static AlertDialog dialog;
    static ImageView mImageView;
    static final int PICK_FROM_CAMERA = 1;
    static final int CROP_FROM_CAMERA = 2;
    static final int PICK_FROM_FILE = 3;
    static Uri mImageCaptureUri;
    static Bitmap profileImgBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        captureImageInitialization();

        mImageView = (ImageView) findViewById(R.id.profilePic);
        //mImageView.setOnClickListener(new View.OnClickListener() {
         //   @Override
         //   public void onClick(View v) {
         //       dialog.show();
         //   }
        //});

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                dialog.show();


            }
        });
















        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /*Image*/
    private void captureImageInitialization()
    {
        /**
         * a selector dialog to display two image source options, from camera
         * �Take from camera� and from existing files �Select from gallery�
         */

        LayoutInflater li = LayoutInflater.from(this);
        final View myView = li.inflate(R.layout.myprofile_addimage, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(myView);

        Button btnGallery = (Button) myView.findViewById(R.id.btn_select_gallery);
        Button btnCamera = (Button) myView.findViewById(R.id.btn_take_camera);
        Button btnRemove = (Button) myView.findViewById(R.id.btn_remove_img);

        dialog = builder.create();

        btnRemove.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {

                // Intent intent = new Intent();
                // intent.setType("image/*");
                // intent.setAction(Intent.ACTION_GET_CONTENT);
                // startActivityForResult(Intent.createChooser(intent,
                // "Complete action using"), PICK_FROM_FILE);
                // dialog.dismiss();
                //MyProfileSoap.editUserProfiletwo(MyProfileView.this, onTaskStartEditImg, onTaskCompleteImg, sessionId, deviceId, editImg);
                //profilePic.setImageDrawable(MyProfileView.this.getResources().getDrawable(R.drawable.add_image3));
                dialog.dismiss();

            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Pick"), PICK_FROM_FILE);
                dialog.dismiss();

            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
                intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

                try
                {
                    intent.putExtra("return-data", true);

                    startActivityForResult(intent, PICK_FROM_CAMERA);
                    dialog.dismiss();

                }
                catch (ActivityNotFoundException e)
                {
                    e.printStackTrace();
                }
            }
        });

        dialog = builder.create();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = 1070;
        dialog.getWindow().setAttributes(lp);

    }

    /* __________Crop Adapter______________*/
    public class CropOptionAdapter extends ArrayAdapter<CropOption>
    {
        private ArrayList<CropOption> mOptions;
        private LayoutInflater mInflater;

        public CropOptionAdapter(Context context, ArrayList<CropOption> options)
        {
            super(context, R.layout.crop_selector, options);
            mOptions = options;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup group)
        {
            if (convertView == null)
                convertView = mInflater.inflate(R.layout.crop_selector, null);

            CropOption item = mOptions.get(position);

            if (item != null)
            {
                ((ImageView) convertView.findViewById(R.id.iv_icon)).setImageDrawable(item.icon);
                ((TextView) convertView.findViewById(R.id.tv_name)).setText(item.title);

                return convertView;
            }

            return null;
        }
    }

    public class CropOption
    {
        public CharSequence title;
        public Drawable icon;
        public Intent appIntent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode)
        {
            case PICK_FROM_CAMERA:
                /**
                 * After taking a picture, do the crop
                 */
                doCrop();

                break;

            case PICK_FROM_FILE:
                /**
                 * After selecting image from files, save the selected path
                 */
                mImageCaptureUri = data.getData();

                doCrop();

                break;

            case CROP_FROM_CAMERA:
                Bundle extras = data.getExtras();
                /**
                 * After cropping the image, get the bitmap of the cropped image
                 * and
                 * display it on imageview.
                 */
                if (extras != null)
                {
                    Bitmap photo = extras.getParcelable("data");
                    mImageView.setImageBitmap(photo);
                    profileImgBitmap = photo;

					/* Execute edit profile soap */

                    //editImg.setProfileImg(Utils.bitmapToBase64(photo));

                }

                File f = new File(mImageCaptureUri.getPath());
                // Log.e("Path",mImageCaptureUri.getPath());

                if (f.exists())
                    f.delete();


                break;

        }
    }

    private void doCrop()
    {
        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();
        /**
         * Open image crop app by starting an intent
         * �com.android.camera.action.CROP�.
         */
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        /**
         * Check if there is image cropper app installed.
         */
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
        /**
         * Specify the image path, crop dimension and scale
         */
        intent.setData(mImageCaptureUri);
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);

        Intent i = new Intent(intent);
        ResolveInfo res = list.get(0);

        i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

        startActivityForResult(i, CROP_FROM_CAMERA);
        /**
         * There is posibility when more than one image cropper app exist, so we
         * have to check for it first. If there is only one app, open then app.
         */

        CropOptionAdapter adapter = new CropOptionAdapter(getApplicationContext(), cropOptions);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {

                if (mImageCaptureUri != null)
                {
                    mImageCaptureUri = null;
                }
            }
        });

        // AlertDialog alert = builder.create();
        // alert.show();

    }
    /*Image*/

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
