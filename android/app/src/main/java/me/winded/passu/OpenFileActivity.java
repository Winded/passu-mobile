package me.winded.passu;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

import me.winded.passu.passu_mobile.Passu_mobile;
import me.winded.passu.passu_mobile.PasswordDatabase;

public class OpenFileActivity extends AppCompatActivity {

    private static final int FILE_SELECT_CODE = 0;
    private static final int FILE_CREATE_CODE = 1;

    private TextView fileBrowseText;
    private EditText passwordInput;
    private CheckBox readOnlyCheckBox;

    private Uri fileUri;

    public void onFileBrowsePress(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select Password file"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void onFileOpenPress(View view) {
        if(fileUri == null) {
            Toast.makeText(this, "Select a file first", Toast.LENGTH_SHORT).show();
            return;
        }

        String password = passwordInput.getText().toString();
        boolean readOnly = readOnlyCheckBox.isChecked();

        try {
            PasswordDatabase db = Passu_mobile.passwordDatabaseFromFile(new FileHandle(getContentResolver(), fileUri, readOnly), password);
            ((PassuApplication)getApplication()).setPasswordDatabase(db);
            passwordInput.setText("");
            startActivity(new Intent(this, ListEntriesActivity.class));
        } catch(Exception ex) {
            AlertBox.display(getSupportFragmentManager(),"ERROR: " + ex.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.open_file_option_menu, menu);

        MenuUtil.setMenuIcons(this, menu, new int[]{R.string.fa_plus_solid});

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        switch(id) {
            case R.id.action_new_file:
                try {
                    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                    intent.setType("*/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(
                            Intent.createChooser(intent, "Enter file name"),
                            FILE_CREATE_CODE);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(this, "Please install a File Manager.",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_file);

        fileBrowseText = findViewById(R.id.file_browse_label);
        passwordInput = findViewById(R.id.file_password);
        readOnlyCheckBox = findViewById(R.id.readonly_checkbox);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
        }

        switch (requestCode) {
            case FILE_SELECT_CODE:
                fileUri = data.getData();

                try {
                    fileBrowseText.setText(FileUtils.getFileNameFromPath(fileUri.getPath()));
                } catch(UnsupportedEncodingException ex) {
                    fileBrowseText.setText("[Invalid file]");
                }
                break;
            case FILE_CREATE_CODE:
                Intent i = new Intent(this, NewFileActivity.class);
                i.setData(data.getData());
                startActivity(i);
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
