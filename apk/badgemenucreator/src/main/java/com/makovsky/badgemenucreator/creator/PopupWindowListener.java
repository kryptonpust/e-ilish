package com.makovsky.badgemenucreator.creator;

import android.view.View;

/**
 * Created by Denis Makovskyi
 */

public interface PopupWindowListener {

    void onItemViewInflated(View itemView);

    void onItemViewClicked(int id);
}
