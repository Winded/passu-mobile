package me.winded.passu;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import me.winded.passu.passu_mobile.PasswordDatabase;
import me.winded.passu.passu_mobile.PasswordEntry;
import me.winded.passu.passu_mobile.PasswordPolicy;

public class ViewEntryActivity extends AppCompatActivity {
    public static final int UPDATE_CODE = 0;

    private class CopyOnClickListener implements DialogInterface.OnClickListener {
        public CopyOnClickListener(Context context, String value) {
            this.context = context;
            this.value = value;
        }

        private Context context;
        private String value;

        @Override
        public void onClick(DialogInterface dialog, int which) {
            ClipboardManager clipboard = (ClipboardManager)context.getSystemService(CLIPBOARD_SERVICE);
            if(clipboard != null) {
                clipboard.setPrimaryClip(ClipData.newPlainText("PASSWORD", value));
                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private PasswordEntry entry;

    private TextView nameText;
    private TextView passwordText;
    private TextView descriptionText;

    private TextView policyLengthText;
    private TextView policyLowercaseText;
    private TextView policyUppercaseText;
    private TextView policyNumbersText;
    private TextView policySpecialText;

    public void onShowPasswordPress(View view) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(entry.getPassword())
                .setPositiveButton(R.string.copy, new CopyOnClickListener(this, entry.getPassword()))
                .setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_entry_option_menu, menu);

        MenuUtil.setMenuIcons(this, menu, new int[]{R.string.fa_pencil_alt_solid, R.string.fa_trash_solid});

        PasswordDatabase db = ((PassuApplication)getApplication()).getPasswordDatabase();
        if(db != null && db.getFileHandle().isReadOnly()) {
            MenuUtil.setIconDisabled(this, menu, 0);
            MenuUtil.setIconDisabled(this, menu, 1);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        switch(id) {
            case R.id.action_edit:
                Intent i = new Intent(this, EditEntryActivity.class);
                i.putExtra("EXTRA_ENTRY_NAME", entry.getName());
                startActivityForResult(i, UPDATE_CODE);
                break;
            case R.id.action_delete:
                deleteEntry();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_entry);

        nameText = findViewById(R.id.name_text);
        passwordText = findViewById(R.id.password_text);
        descriptionText = findViewById(R.id.description_text);

        policyLengthText = findViewById(R.id.policy_length_text);
        policyLowercaseText = findViewById(R.id.policy_lowercase_text);
        policyUppercaseText = findViewById(R.id.policy_uppercase_text);
        policyNumbersText = findViewById(R.id.policy_numbers_text);
        policySpecialText = findViewById(R.id.policy_special_text);
    }

    @Override
    protected void onStart() {
        super.onStart();

        updateData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch(requestCode) {
            case UPDATE_CODE:
                getIntent().putExtra("EXTRA_ENTRY_NAME", data.getStringExtra("EXTRA_ENTRY_NAME"));
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateData() {
        try {
            String entryName = getIntent().getStringExtra("EXTRA_ENTRY_NAME");
            setTitle(entryName);
            loadData(entryName);
        } catch(Exception ex) {
            setTitle("[Invalid entry]");
        }
    }

    private void loadData(String entryName) throws Exception {
        PasswordDatabase db = ((PassuApplication)getApplication()).getPasswordDatabase();
        if(entryName == null || db == null) {
            throw new NullPointerException();
        }

        entry = db.getEntry(entryName);

        nameText.setText(entry.getName());
        passwordText.setText(String.format("(%s characters)", entry.getPassword().length()));
        descriptionText.setText(entry.getDescription());

        PasswordPolicy policy = entry.getPolicy();
        String sTrue = "Yes";
        String sFalse = "No";

        policyLengthText.setText(policy.hasLength() ? String.valueOf(policy.getLength()) : "(default)");
        policyLowercaseText.setText(policy.hasUseLowercase() ? (policy.getUseLowercase() ? sTrue : sFalse) : "(default)");
        policyUppercaseText.setText(policy.hasUseUppercase() ? (policy.getUseUppercase() ? sTrue : sFalse) : "(default)");
        policyNumbersText.setText(policy.hasUseNumbers() ? (policy.getUseNumbers() ? sTrue : sFalse) : "(default)");
        policySpecialText.setText(policy.hasUseSpecial() ? (policy.getUseSpecial() ? sTrue : sFalse) : "(default)");
    }

    private void deleteEntry() {
        final Context ctx = this;

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete " + entry.getName() + "?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            PasswordDatabase db = ((PassuApplication)getApplication()).getPasswordDatabase();
                            db.removeEntry(entry.getName());
                            Toast.makeText(ctx, "Entry removed", Toast.LENGTH_SHORT).show();
                            finish();
                        } catch(Exception ex) {
                            Toast.makeText(ctx, "ERROR: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
