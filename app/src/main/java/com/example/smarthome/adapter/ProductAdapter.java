package com.example.smarthome.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthome.R;
import com.example.smarthome.data.model.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> mListProduct;
    private IClickAddToCartListener iClickAddToCartListener;

    public interface IClickAddToCartListener {
        void onClickAddToCart(ImageView imgAddToCart, Product product);
    }

    // cập nhật danh sách sản phẩm mới vào adapter
    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Product> list, IClickAddToCartListener listener) {
        this.mListProduct = list;
        this.iClickAddToCartListener = listener;
        //RecyclerView cập nhật lại dữ liệu và hiển thị danh sách mới.
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = mListProduct.get(position);

        if (product == null) return;

        holder.imgProduct.setImageResource(product.getResourceId());
        holder.tvProductName.setText(product.getName());
        holder.tvDes.setText(product.getDes());

        holder.imgAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickAddToCartListener.onClickAddToCart(holder.imgAddToCart, product);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mListProduct != null) return mListProduct.size();
        return 0;
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgProduct;
        private TextView tvProductName;
        private TextView tvDes;
        private ImageView imgAddToCart;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProduct = itemView.findViewById(R.id.img_product);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvDes = itemView.findViewById(R.id.tv_des);
            imgAddToCart = itemView.findViewById(R.id.img_add_to_cart);
        }
    }
}
