package com.gorrotowi.inventoryapp.data;

import android.provider.BaseColumns;

/**
 * Created by Gorro on 09/04/17.
 */

public class InventoryContract {

    public InventoryContract() {
    }

    public static abstract class InventoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "inventory";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_QUANTITY = "quantity";
        public static final String COLUMN_NAME_MAIL = "mail";
        public static final String COLUMN_NAME_PHOTO = "photo";
    }

}
