package com.gorrotowi.inventoryapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gorrotowi.inventoryapp.data.InventoryContract;
import com.gorrotowi.inventoryapp.data.InventoryOpenHelper;
import com.gorrotowi.inventoryapp.utils.Utils;

public class DetailProductActivity extends AppCompatActivity {

    private static final String TAG = DetailProductActivity.class.getSimpleName();
    InventoryOpenHelper openHelper;
    SQLiteDatabase db;

    TextView txtName;
    TextView txtPrice;
    TextView txtQuantity;
    TextView txtDesc;

    EditText edtxAdd;
    EditText edtxRemove;

    ImageView imgProduct;

    Button btnAdd;
    Button btnRemove;
    private int mquantity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);

        txtName = (TextView) findViewById(R.id.txtDetailProductName);
        txtPrice = (TextView) findViewById(R.id.txtDetailProductPrice);
        txtQuantity = (TextView) findViewById(R.id.txtDetailProductQuantity);
        txtDesc = (TextView) findViewById(R.id.txtDetailDescr);

        edtxAdd = (EditText) findViewById(R.id.edtxDetailAdd);
        edtxRemove = (EditText) findViewById(R.id.edtxDetailRemove);

        imgProduct = (ImageView) findViewById(R.id.imgDetailProduct);

        btnAdd = (Button) findViewById(R.id.btnDetailAdd);
        btnRemove = (Button) findViewById(R.id.btnDetailRemove);

        openHelper = new InventoryOpenHelper(this);

        if (getIntent().getExtras() != null) {
            int id = getIntent().getExtras().getInt("id");
            retriveData(id);
        } else {
            finish();
            Toast.makeText(this, "Something went wrong, try again please", Toast.LENGTH_SHORT).show();
        }

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItems();
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeItems();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_product_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.detail_action_req_more:
                requestForMore();
                break;
            case R.id.detail_action_delete:
                deleteItem();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void addItems() {
        if (!edtxAdd.getText().toString().isEmpty()) {
            try {
                int newquantity = Integer.parseInt(edtxAdd.getText().toString());
                Log.e(TAG, "addItems: " + newquantity);
                Log.e(TAG, "addItems: " + mquantity);
                updateQuantity(newquantity + mquantity);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Please, introduce a valid number*", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please, introduce a valid number", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeItems() {
        if (!edtxRemove.getText().toString().isEmpty()) {
            try {
                int newquantity = Integer.parseInt(edtxRemove.getText().toString());
                Log.e(TAG, "addItems: " + newquantity);
                Log.e(TAG, "addItems: " + mquantity);
                if ((mquantity - newquantity) < 0) {
                    Toast.makeText(this, "You can not remove more than you have", Toast.LENGTH_SHORT).show();
                } else {
                    updateQuantity(mquantity - newquantity);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Please, introduce a valid number*", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please, introduce a valid number", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestForMore() {
        String[] TO = {txtDesc.getText().toString()};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "We need more" + txtName.getText().toString());
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateQuantity(int quantityFinal) {
        String[] args = {getIntent().getExtras().getInt("id") + ""};
        ContentValues contentValues = new ContentValues();
        contentValues.put(InventoryContract.InventoryEntry.COLUMN_NAME_QUANTITY, quantityFinal);
        db = openHelper.getWritableDatabase();
        int result = db.update(InventoryContract.InventoryEntry.TABLE_NAME, contentValues, InventoryContract.InventoryEntry._ID + "=?", args);
        if (result != 0) {
            txtQuantity.setText(String.format(getString(R.string.formated_quantity), quantityFinal));
            mquantity = quantityFinal;
            edtxAdd.setText(null);
            edtxRemove.setText(null);
        } else {
            Toast.makeText(this, "Something went wrong, try again please", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    private void deleteItem() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Are you sure to delete this item?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deletefromDB();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        alertDialog.show();
    }

    private void deletefromDB() {
        db = openHelper.getWritableDatabase();
        String[] args = {getIntent().getExtras().getInt("id") + ""};
        int result = db.delete(InventoryContract.InventoryEntry.TABLE_NAME, InventoryContract.InventoryEntry._ID + "=?", args);
        if (result > 0) {
            finish();
        } else {
            Toast.makeText(this, "Error trying to delete this item", Toast.LENGTH_SHORT).show();
        }
    }

    private void retriveData(int id) {
        db = openHelper.getReadableDatabase();
        String[] matchs = {id + ""};
        Cursor cursor = db.query(InventoryContract.InventoryEntry.TABLE_NAME,
                null,
                InventoryContract.InventoryEntry._ID + "=?",
                matchs,
                null,
                null,
                null);
        cursor.moveToFirst();
        try {
            do {
                String name = cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_NAME_NAME));
                int price = cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_NAME_PRICE));
                mquantity = cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_NAME_QUANTITY));
                String mail = cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_NAME_MAIL));
                Bitmap bitmap = Utils.getImage(cursor.getBlob(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_NAME_PHOTO)));

                txtName.setText(name);
                txtPrice.setText(String.format(getString(R.string.formated_price), price));
                txtQuantity.setText(String.format(getString(R.string.formated_quantity), mquantity));
                txtDesc.setText(mail);

                imgProduct.setImageBitmap(bitmap);


            } while (cursor.moveToNext());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }
    }
}
