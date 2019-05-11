package me.winded.passu;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import info.androidhive.fontawesome.FontDrawable;
import me.winded.passu.passu_mobile.IFileHandle;
import me.winded.passu.passu_mobile.IWriter;
import me.winded.passu.passu_mobile.PasswordDatabase;
import me.winded.passu.util.StringArray;

public class ListEntriesActivity extends AppCompatActivity implements PasswordEntryAdapter.ClickListener {
    private RecyclerView recyclerView;
    private PasswordEntryAdapter mAdapter;

    private StringArray dataset;

    public void onAddEntryPress(View view) {
        Intent i = new Intent(this, NewEntryActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_entries_option_menu, menu);

        MenuUtil.setMenuIcons(this, menu, new int[]{R.string.fa_search_solid, R.string.fa_save_solid, R.string.fa_times_solid});

        PasswordDatabase db = ((PassuApplication)getApplication()).getPasswordDatabase();
        if(db != null && db.getFileHandle().isReadOnly()) {
            MenuUtil.setIconDisabled(this, menu, 1);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        switch(id) {
            case R.id.action_search:
                // TODO
                break;
            case R.id.action_save:
                PasswordDatabase db = ((PassuApplication)getApplication()).getPasswordDatabase();
                try {
                    db.save();
                    Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show();
                } catch(Exception ex) {
                    Toast.makeText(this, "ERROR: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.action_close:
                closeApp();
                break;
            case R.id.action_default_policy:
                Intent i = new Intent(this, DefaultPolicyActivity.class);
                startActivity(i);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            closeApp();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onItemClick(int position, View v) {
        if(position >= dataset.length()) {
            return;
        }

        String entryName = dataset.get(position);
        Intent i = new Intent(this, ViewEntryActivity.class);
        i.putExtra("EXTRA_ENTRY_NAME", entryName);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_entries);

        recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected void onStart() {
        super.onStart();

        dataset = null;
        PasswordDatabase db = ((PassuApplication)getApplication()).getPasswordDatabase();
        if(db != null) {
            dataset = db.getEntryNames("");
        }
        mAdapter = new PasswordEntryAdapter(dataset, this);
        recyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = findViewById(R.id.add_button);
        if(db.getFileHandle().isReadOnly()) {
            ((View)fab).setVisibility(View.GONE);
        } else {
            FontDrawable drawable = new FontDrawable(this, R.string.fa_plus_solid, true, false);
            drawable.setTextColor(ContextCompat.getColor(this, R.color.icon_color_light));
            fab.setImageDrawable(drawable);
        }
    }

    private void closeApp() {
        PasswordDatabase db = ((PassuApplication)getApplication()).getPasswordDatabase();
        if(db == null || !db.isModified()) {
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
            return;
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Exit?");
        alertDialogBuilder
                .setMessage("You have unsaved changes. Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(0);
                            }
                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
