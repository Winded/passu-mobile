package me.winded.passu;

import android.content.Context;
import android.support.annotation.Dimension;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;

import info.androidhive.fontawesome.FontDrawable;

public class MenuUtil {
    public static void setMenuIcons(Context context, Menu menu, int[] icons) {
        int textSize = (int)(context.getResources().getDimension(R.dimen.icon_option_size) / context.getResources().getDisplayMetrics().density);

        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            if (!menuItem.hasSubMenu() && i < icons.length) {
                FontDrawable drawable = new FontDrawable(context, icons[i], true, false);
                drawable.setTextColor(ContextCompat.getColor(context, R.color.icon_color_light));
                drawable.setTextSize(textSize);
                menu.getItem(i).setIcon(drawable);
            }
        }
    }

    public static void setIconDisabled(Context context, Menu menu, int itemId) {
        MenuItem item = menu.getItem(itemId);
        item.setEnabled(false);
        FontDrawable drawable = (FontDrawable)item.getIcon();
        drawable.setTextColor(ContextCompat.getColor(context, R.color.icon_color_disabled));
    }
}
