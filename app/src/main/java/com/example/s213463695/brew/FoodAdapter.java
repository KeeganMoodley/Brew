package com.example.s213463695.brew;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static java.security.AccessController.getContext;

/**
 * Created by s214079694 on 2017/06/29.
 */

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView title, price, halaal, quantity;
        ImageView img, imgInfo;
        Button btnInc, btnDec;


        public FoodViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card);
            title = (TextView) itemView.findViewById(R.id.title);
            price = (TextView) itemView.findViewById(R.id.price);
            //halaal = (TextView) itemView.findViewById(R.id.halaal);
            quantity = (TextView) itemView.findViewById(R.id.quantity);
            img = (ImageView) itemView.findViewById(R.id.img);
            btnDec = (Button) itemView.findViewById(R.id.btnDec);
            btnInc = (Button) itemView.findViewById(R.id.btnInc);
            imgInfo = (ImageView) itemView.findViewById(R.id.imgInfo);
        }
    }
    View v;
    private List<Food> foods;
    TotalListener totalListener;

    FoodAdapter(List<Food> foods) {
        this.foods = foods;
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    @Override
    public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_frag, parent, false);
        FoodViewHolder foodViewHolder = new FoodViewHolder(view);
        v = view;
        return foodViewHolder;
    }

    @Override
    public void onBindViewHolder(final FoodViewHolder holder, int position) {
        final Food food = foods.get(position);
        holder.price.setText("R" + String.format("%.2f", food.getPrice()));
        //holder.img.setImageResource(food.getImage());
        //holder.img.setImageBitmap(Bitmap.createScaledBitmap(food.getImage(), holder.img.getWidth(), holder.img.getHeight(), false));
        byte[] bytes = food.getImage();
        Bitmap pic = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        holder.img.setImageBitmap(pic);
        holder.title.setText(food.getTitle());
        /*if (food.isHalaal())
            holder.halaal.setText("Halaal");
        else
            holder.halaal.setText("");*/
        /*totalListener = new TotalListener() {
            @Override
            public void calcTotal() {
                double tot = 0;
                for (Food food : foods)
                    tot += food.getTotal();
            }
        };*/
        final AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
        dialog.setTitle(food.getTitle() + " Ingredients");
        dialog.setMessage(food.nutrition);
        dialog.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        holder.imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
        /*final AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setTitle("Order " + orders.get(position).getOrderNum());
        adb.setMessage("Do You want to withdraw your order?");
        adb.setPositiveButton("Withdraw", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (orders.get(position).getStatus().equals("dispatched")) {
                    Toast.makeText(main, "Sorry the order has been dispatched already!", Toast.LENGTH_LONG).show();
                    dialog.cancel();
                } else {
                    mListener.cancelOrder(orders.get(position));
                }
            }
        });
        adb.setNegativeButton("Cancel", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        adb.show();*/
        holder.btnDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                food.decQuantity();
                holder.quantity.setText(String.valueOf(food.getQuantity()));
                totalListener.calcTotal();
                notifyDataSetChanged();
            }
        });
        holder.btnInc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                food.incQuantity();
                holder.quantity.setText(String.valueOf(food.getQuantity()));
                totalListener.calcTotal();
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void setTotalListener(TotalListener totalListener) {
        this.totalListener = totalListener;
    }
}
