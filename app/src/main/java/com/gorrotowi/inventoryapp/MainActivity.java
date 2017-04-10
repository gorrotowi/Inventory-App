package com.gorrotowi.inventoryapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.gorrotowi.inventoryapp.adapters.AdapterProducts;
import com.gorrotowi.inventoryapp.data.InventoryContract;
import com.gorrotowi.inventoryapp.data.InventoryOpenHelper;
import com.gorrotowi.inventoryapp.entitys.ItemProduct;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    AdapterProducts adapterProducts;
    RecyclerView recyclerView;
    FloatingActionButton fab;
    ViewGroup noDataView;
    SQLiteDatabase db;
    InventoryOpenHelper openHelper;
    private List<ItemProduct> dummyData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.rcInventory);
        fab = (FloatingActionButton) findViewById(R.id.fabAdd);
        noDataView = (ViewGroup) findViewById(R.id.lyNoData);

        openHelper = new InventoryOpenHelper(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

//        adapterProducts = new AdapterProducts(getDummyData());
        recyclerView.setAdapter(adapterProducts);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddProductActivity.class));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        Cursor cursorData = getInventory();
        dummyData.clear();
        try {
            do {
                dummyData.add(new ItemProduct(cursorData.getString(cursorData.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_NAME_NAME)),
                        cursorData.getInt(cursorData.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_NAME_PRICE)),
                        cursorData.getInt(cursorData.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_NAME_QUANTITY)),
                        cursorData.getInt(cursorData.getColumnIndex(InventoryContract.InventoryEntry._ID))));
            } while (cursorData.moveToNext());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursorData.close();
        }

        if (dummyData.size() > 0) {
            noDataView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapterProducts = new AdapterProducts(dummyData);
            recyclerView.setAdapter(adapterProducts);
        } else {
            noDataView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

    }

    public Cursor getInventory() {
        db = openHelper.getReadableDatabase();
        Cursor cursor = db.query(InventoryContract.InventoryEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
        cursor.moveToFirst();
//        openHelper.close();
        return cursor;
    }

}
