package com.udacity.sandwichclub;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.transition.Explode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;
import com.udacity.sandwichclub.model.Sandwich;
import com.udacity.sandwichclub.utils.JsonUtils;


import java.io.IOException;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "extra_position";
    private static final int DEFAULT_POSITION = -1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    TextView known_as, description, origin, ingred;
    ImageView ingredientsIv;
    Bitmap img;
    Sandwich sandwich;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Explode());
            getWindow().setExitTransition(new Explode());
        } else {
            // Swap without transition
        }

        setContentView(R.layout.activity_detail);

        if (getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        known_as = (TextView) findViewById(R.id.also_known_tv);
        description = (TextView) findViewById(R.id.description_tv);
        ingred = (TextView) findViewById(R.id.ingredients_tv);
        origin = (TextView) findViewById(R.id.origin_tv);
        ingredientsIv = (ImageView) findViewById(R.id.image_iv);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        int position = intent.getIntExtra(EXTRA_POSITION, DEFAULT_POSITION);
        if (position == DEFAULT_POSITION) {
            // EXTRA_POSITION not found in intent
            closeOnError();
            return;
        }

        String[] sandwiches = getResources().getStringArray(R.array.sandwich_details);
        String json = sandwiches[position];
        sandwich = JsonUtils.parseSandwichJson(json);
        if (sandwich == null) {
            // Sandwich data unavailable
            closeOnError();
            return;
        }

        populateUI(sandwich);
        Picasso.with(this)
                .load(sandwich.getImage())
                .into(ingredientsIv, new Callback() {
                    @Override
                    public void onSuccess() {
                        BitmapDrawable drawable = (BitmapDrawable) ingredientsIv.getDrawable();
                        img = drawable.getBitmap();
                    }

                    @Override
                    public void onError() {

                    }
                });

        setTitle(sandwich.getMainName());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.app_bar_fav:
                Snackbar mySnackbar = Snackbar.make(findViewById(R.id.arcView),
                        "Added to Favorite", Snackbar.LENGTH_SHORT);
                mySnackbar.setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(), "Removed from Favorite !", Toast.LENGTH_LONG).show();
                    }
                });
                mySnackbar.show();
                return true;
            case R.id.app_bar_share:
                requestRead();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    private void populateUI(Sandwich sandwich) {
        if(sandwich != null) {
            known_as.setText(convertList(sandwich.getAlsoKnownAs()));
            description.setText(sandwich.getDescription());
            ingred.setText(convertList(sandwich.getIngredients()));
            origin.setText(sandwich.getPlaceOfOrigin());
        }
        else {
            Toast.makeText(getApplicationContext(), "Sandwich object is null", Toast.LENGTH_LONG);
        }

    }

    private String convertList(List<String> lst) {
        String r="";
        for(String str: lst) {
            r += str;
        }
        return r;
    }


    /**
     * requestPermissions and do something
     *  solution obtained from https://stackoverflow.com/a/47735380/8738574
     *
     */
    public void requestRead() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            shareSandwich();
        }
    }


    public void shareSandwich() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpg");
        String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), img, sandwich.getMainName(), null);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(bitmapPath));
        shareIntent.putExtra(Intent.EXTRA_TITLE, sandwich.getMainName());
        shareIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(new StringBuilder()
                .append("<p><b>"+sandwich.getMainName()+"</b> "+(sandwich.getAlsoKnownAs().equals("")?"":"also known as "+sandwich.getAlsoKnownAs())+"</p>")
                .append("<p>"+sandwich.getDescription()+"</p>")
                .append("<p>Ingredients are "+sandwich.getIngredients().toString()+"</p>")
                .append("<small><p>It comes from "+(sandwich.getPlaceOfOrigin().equals("")?"Unknown":sandwich.getPlaceOfOrigin())+"</p></small>")
                .toString()));
        startActivity(Intent.createChooser(shareIntent, "Share Sandwich using"));
    }

    /**
     * onRequestPermissionsResult
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                shareSandwich();
            } else {
                // Permission Denied
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
