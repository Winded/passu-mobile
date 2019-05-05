package me.winded.passu;

import android.app.Application;

import me.winded.passu.passu_mobile.PasswordDatabase;

public class PassuApplication extends Application {
    private PasswordDatabase passwordDatabase;

    public PasswordDatabase getPasswordDatabase() {
        return passwordDatabase;
    }
    public void setPasswordDatabase(PasswordDatabase passwordDatabase) {
        this.passwordDatabase = passwordDatabase;
    }
}
