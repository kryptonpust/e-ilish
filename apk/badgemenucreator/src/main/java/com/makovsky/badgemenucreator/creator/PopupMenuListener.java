package com.makovsky.badgemenucreator.creator;

import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by Denis Makovskyi
 */

public interface PopupMenuListener {

    void onSubmenuInflated(Menu menu);

    void onMenuItemClicked(MenuItem menuItem);
}
