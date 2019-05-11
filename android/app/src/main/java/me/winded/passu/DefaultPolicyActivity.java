package me.winded.passu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import me.winded.passu.passu_mobile.PasswordDatabase;
import me.winded.passu.passu_mobile.PasswordPolicy;

public class DefaultPolicyActivity extends AppCompatActivity {
    private PasswordPolicy policy;

    private EditText lengthText;
    private Spinner lowercaseSpinner;
    private Spinner uppercaseSpinner;
    private Spinner numbersSpinner;
    private Spinner specialSpinner;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_entry_option_menu, menu);

        MenuUtil.setMenuIcons(this, menu, new int[]{R.string.fa_check_solid, R.string.fa_times_solid});

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
            case R.id.action_save:
                try {
                    saveData();
                    Toast.makeText(this, "Default policy updated", Toast.LENGTH_LONG).show();
                    finish();
                } catch(Exception ex) {
                    Toast.makeText(this, "ERROR: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.action_cancel:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_policy);

        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(this, R.array.policy_spinner_options,
                        android.R.layout.simple_spinner_item);
        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        lengthText = findViewById(R.id.length_text);
        lowercaseSpinner = findViewById(R.id.lowercase_spinner);
        uppercaseSpinner = findViewById(R.id.uppercase_spinner);
        numbersSpinner = findViewById(R.id.numbers_spinner);
        specialSpinner = findViewById(R.id.special_spinner);

        lowercaseSpinner.setAdapter(staticAdapter);
        uppercaseSpinner.setAdapter(staticAdapter);
        numbersSpinner.setAdapter(staticAdapter);
        specialSpinner.setAdapter(staticAdapter);

        PasswordDatabase db = ((PassuApplication)getApplication()).getPasswordDatabase();
        if(db != null) {
            policy = db.getDefaultPolicy();
        }

        loadData();
    }

    private void updateSpinner(Spinner spinner, boolean hasValue, boolean value) {
        if(hasValue) {
            spinner.setSelection(value ? 0 : 1);
        } else {
            spinner.setSelection(2);
        }
    }

    private void loadData() {
        if(policy.hasLength()) {
            lengthText.setText(String.valueOf(policy.getLength()));
        } else {
            lengthText.setText("");
        }

        updateSpinner(lowercaseSpinner, policy.hasUseLowercase(), policy.getUseLowercase());
        updateSpinner(uppercaseSpinner, policy.hasUseUppercase(), policy.getUseUppercase());
        updateSpinner(numbersSpinner, policy.hasUseNumbers(), policy.getUseNumbers());
        updateSpinner(specialSpinner, policy.hasUseSpecial(), policy.getUseSpecial());
    }

    private void saveData() throws Exception {
        String sLength = lengthText.getText().toString();
        if(!sLength.isEmpty()) {
            int length = Integer.parseInt(sLength);
            policy.setLength(length);
        } else {
            policy.resetLength();
        }

        int useLowercase = lowercaseSpinner.getSelectedItemPosition();
        if(useLowercase < 2) {
            policy.setUseLowercase(useLowercase == 0);
        } else if(useLowercase == 2) {
            policy.resetUseLowercase();
        }

        int useUppercase = uppercaseSpinner.getSelectedItemPosition();
        if(useUppercase < 2) {
            policy.setUseUppercase(useUppercase == 0);
        } else if(useUppercase == 2) {
            policy.resetUseUppercase();
        }

        int useNumbers = numbersSpinner.getSelectedItemPosition();
        if(useNumbers < 2) {
            policy.setUseNumbers(useNumbers == 0);
        } else if(useNumbers == 2) {
            policy.resetUseNumbers();
        }

        int useSpecial = specialSpinner.getSelectedItemPosition();
        if(useSpecial < 2) {
            policy.setUseSpecial(useSpecial == 0);
        } else if(useSpecial == 2) {
            policy.resetUseSpecial();
        }

        PasswordDatabase db = ((PassuApplication)getApplication()).getPasswordDatabase();
        db.setDefaultPolicy(policy);
    }
}
