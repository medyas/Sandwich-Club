package com.udacity.sandwichclub;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    boolean sort = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] sandwiches = getResources().getStringArray(R.array.sandwich_names);
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, sandwiches);

        // Simplification: Using a ListView instead of a RecyclerView
        final ListView listView = findViewById(R.id.sandwiches_listview);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                launchDetailActivity(position);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.app_bar_search:
                return true;
            case R.id.app_bar_sort:
                adapter.sort(new Comparator<String>() {
                    @Override
                    public int compare(String s, String t1) {
                        if(sort) {
                            //sort = false;
                            return s.substring(0, 1).compareToIgnoreCase(t1.substring(0, 1));
                        }
                        else {
                            //sort = true;
                            return t1.substring(0, 1).compareToIgnoreCase(s.substring(0, 1));
                        }
                    }
                });

                adapter.notifyDataSetChanged();
                return true;
            case R.id.app_bar_reset:
                String[] sandwiches = getResources().getStringArray(R.array.sandwich_names);
                adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1, sandwiches);
                adapter.notifyDataSetChanged();
                return true;
            case R.id.app_bar_settings:
                Snackbar mySnackbar = Snackbar.make(findViewById(R.id.sandwiches_listview),
                        "Settings Activity not yet implemented!", Snackbar.LENGTH_SHORT);
                mySnackbar.show();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void launchDetailActivity(int position) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_POSITION, position);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent,
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            startActivity(intent);
        }


    }


}
