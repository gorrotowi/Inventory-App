package com.gorrotowi.inventoryapp.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gorrotowi.inventoryapp.DetailProductActivity;
import com.gorrotowi.inventoryapp.R;
import com.gorrotowi.inventoryapp.data.InventoryContract;
import com.gorrotowi.inventoryapp.data.InventoryOpenHelper;
import com.gorrotowi.inventoryapp.entitys.ItemProduct;

import java.util.List;

/**
 * Created by Gorro on 09/04/17.
 */

public class AdapterProducts extends RecyclerView.Adapter<AdapterProducts.ViewHolder> {

    private static final String TAG = AdapterProducts.class.getSimpleName();
    private List<ItemProduct> itemProducts;

    public AdapterProducts(List<ItemProduct> itemProducts) {
        this.itemProducts = itemProducts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventory, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Context ctx = holder.itemView.getContext();
        final ItemProduct item = itemProducts.get(position);

        holder.txtName.setText(item.getName());
        holder.txtPrice.setText(String.format(ctx.getString(R.string.formated_price), item.getPrice()));
        holder.txtQuantity.setText(String.format(ctx.getString(R.string.formated_quantity), item.getQuantity()));

        holder.btnSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sellItem(ctx, item, holder);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ctx, DetailProductActivity.class);
                intent.putExtra("id", item.getId());
                ctx.startActivity(intent);
            }
        });

    }

    private void sellItem(Context ctx, ItemProduct item, ViewHolder holder) {

        InventoryOpenHelper openHelper = new InventoryOpenHelper(ctx);
        SQLiteDatabase db = openHelper.getWritableDatabase();
        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_NAME_QUANTITY
        };
        String[] matchs = {
                item.getId() + ""
        };
        Cursor cursor = db.query(InventoryContract.InventoryEntry.TABLE_NAME,
                projection,
                InventoryContract.InventoryEntry._ID + "=?",
                matchs,
                null,
                null,
                null);

        cursor.moveToFirst();
        try {
            do {
                int quantity = cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_NAME_QUANTITY));
                if (quantity == 0) {
                    Toast.makeText(ctx, "Sold out, please requeste for more", Toast.LENGTH_SHORT).show();
                    db.close();
                } else {
                    int newQuantity = quantity - 1;
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(InventoryContract.InventoryEntry.COLUMN_NAME_QUANTITY, newQuantity);
                    db.update(InventoryContract.InventoryEntry.TABLE_NAME, contentValues, InventoryContract.InventoryEntry._ID + "=?", matchs);
                    holder.txtQuantity.setText(String.format(ctx.getString(R.string.formated_quantity), newQuantity));
                    db.close();
                }
            } while (cursor.moveToNext());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }

    }

    @Override
    public int getItemCount() {
        return itemProducts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName;
        TextView txtPrice;
        TextView txtQuantity;

        Button btnSell;

        ViewHolder(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.txtItemProduct);
            txtPrice = (TextView) itemView.findViewById(R.id.txtItemPrice);
            txtQuantity = (TextView) itemView.findViewById(R.id.txtItemQuantity);
            btnSell = (Button) itemView.findViewById(R.id.btnItemSell);
        }
    }

}
