package com.gorrotowi.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.gorrotowi.inventoryapp.data.InventoryContract;
import com.gorrotowi.inventoryapp.data.InventoryOpenHelper;
import com.gorrotowi.inventoryapp.utils.Utils;

public class AddProductActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    SQLiteDatabase db;
    InventoryOpenHelper openHelper;
    byte[] imgCode;
    private ImageView imageView;
    private EditText edtxName;
    private EditText edtxDescr;
    private EditText edtxPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        imageView = (ImageView) findViewById(R.id.imgAddProduct);
        edtxName = (EditText) findViewById(R.id.edtxAddName);
        edtxDescr = (EditText) findViewById(R.id.edtxAddDescrip);
        edtxPrice = (EditText) findViewById(R.id.edtxAddPrice);
        Button btnPhoto = (Button) findViewById(R.id.btnAddPhoto);

        openHelper = new InventoryOpenHelper(this);

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } else {
                    Toast.makeText(AddProductActivity.this, "Error trying to open camera app", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_product_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_action_addproduct:
                if (validateForm()) {
                    addProduct();
                } else {
                    Toast.makeText(this, "Verify the information, please", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            imgCode = Utils.getBytes(imageBitmap);
        }
    }

    private void addProduct() {
        db = openHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(InventoryContract.InventoryEntry.COLUMN_NAME_NAME, edtxName.getText().toString());
        contentValues.put(InventoryContract.InventoryEntry.COLUMN_NAME_PRICE, Integer.parseInt(edtxPrice.getText().toString()));
        contentValues.put(InventoryContract.InventoryEntry.COLUMN_NAME_MAIL, edtxDescr.getText().toString());
        contentValues.put(InventoryContract.InventoryEntry.COLUMN_NAME_QUANTITY, 0);
        contentValues.put(InventoryContract.InventoryEntry.COLUMN_NAME_PHOTO, imgCode);
        db.insert(InventoryContract.InventoryEntry.TABLE_NAME, null, contentValues);
        db.close();
        finish();
    }

    private boolean validateForm() {
        return !edtxName.getText().toString().isEmpty() && !edtxPrice.getText().toString().isEmpty() && !edtxDescr.getText().toString().isEmpty() && imageView.getDrawable() != null;
    }

}
