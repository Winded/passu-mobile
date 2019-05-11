package me.winded.passu;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;

import me.winded.passu.passu_mobile.PasswordDatabase;

public class NewFileActivity extends AppCompatActivity {
    private TextView fileLabel;

    private EditText passwordText;
    private EditText repeatPasswordText;

    private Uri fileUri;

    public void onFileCreatePress(View view) {
        String password = passwordText.getText().toString();
        String repeatPassword = repeatPasswordText.getText().toString();

        if(password.isEmpty() || !password.equals(repeatPassword)) {
            AlertBox.display(getSupportFragmentManager(), "Passwords don't match");
            return;
        }

        try {
            PasswordDatabase db = new PasswordDatabase(new FileHandle(getContentResolver(), fileUri, false), password);
            db.save();
            ((PassuApplication)getApplication()).setPasswordDatabase(db);
            Intent i = new Intent(this, ListEntriesActivity.class);
            startActivity(i);
        } catch(Exception ex) {
            AlertBox.display(getSupportFragmentManager(), "ERROR: " + ex.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_file);

        fileLabel = findViewById(R.id.file_browse_label);

        passwordText = findViewById(R.id.file_password);
        repeatPasswordText = findViewById(R.id.file_password_repeat);
    }

    @Override
    protected void onStart() {
        super.onStart();

        fileUri = getIntent().getData();
        try {
            fileLabel.setText(FileUtils.getFileNameFromPath(fileUri.getPath()));
        } catch(UnsupportedEncodingException ex) {
            fileLabel.setText("[Invalid file]");
        }
    }
}
