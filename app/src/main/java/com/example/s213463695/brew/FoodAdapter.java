package com.example.s213463695.brew;

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
        ImageView img;
        Button btnInc, btnDec;


        public FoodViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card);
            title = (TextView) itemView.findViewById(R.id.title);
            price = (TextView) itemView.findViewById(R.id.price);
            halaal = (TextView) itemView.findViewById(R.id.halaal);
            quantity = (TextView) itemView.findViewById(R.id.quantity);
            img = (ImageView) itemView.findViewById(R.id.img);
            btnDec = (Button) itemView.findViewById(R.id.btnDec);
            btnInc = (Button) itemView.findViewById(R.id.btnInc);
        }
    }

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
        return foodViewHolder;
    }

    @Override
    public void onBindViewHolder(final FoodViewHolder holder, int position) {
        final Food food = foods.get(position);
        holder.price.setText("R" + String.valueOf(food.getPrice()));
        holder.img.setImageResource(food.getImage());
        holder.title.setText(food.getTitle());
        if (food.isHalaal())
            holder.halaal.setText("Halaal");
        else
            holder.halaal.setText("");
        /*totalListener = new TotalListener() {
            @Override
            public void calcTotal() {
                double tot = 0;
                for (Food food : foods)
                    tot += food.getTotal();
            }
        };*/
        holder.btnDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                food.decQuantity();
                holder.quantity.setText(String.valueOf(food.getQuantity()));
                //totalListener.calcTotal();
            }
        });
        holder.btnInc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                food.incQuantity();
                holder.quantity.setText(String.valueOf(food.getQuantity()));
                //totalListener.calcTotal();
            }
        });

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
