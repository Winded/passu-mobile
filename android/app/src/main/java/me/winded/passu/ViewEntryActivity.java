package me.winded.passu;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import info.androidhive.fontawesome.FontDrawable;
import me.winded.passu.passu_mobile.PasswordDatabase;
import me.winded.passu.passu_mobile.PasswordEntry;
import me.winded.passu.passu_mobile.PasswordPolicy;

public class ViewEntryActivity extends AppCompatActivity {

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
                // TODO
                break;
            case R.id.action_delete:
                // TODO
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

        String entryName = getIntent().getStringExtra("EXTRA_ENTRY_NAME");
        PasswordDatabase db = ((PassuApplication)getApplication()).getPasswordDatabase();
        if(entryName == null || db == null) {
            throw new NullPointerException();
        }

        try {
            entry = db.getEntry(entryName);
            updateTexts();
        } catch(Exception ex) {
            setTitle("[Invalid entry]");
        }
    }

    private void updateTexts() {
        setTitle(entry.getName());

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
}
