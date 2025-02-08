package com.example.smarthome.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthome.R;
import com.example.smarthome.adapter.ProductAdapter;
import com.example.smarthome.data.model.Product;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rcvProduct;
    private View mView;
    private MainActivity mainActivity;
    private ProductAdapter productAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home, container, false);

        mainActivity = (MainActivity) getActivity();

        rcvProduct = mView.findViewById(R.id.rcv_product);

        GridLayoutManager gridLayoutManager  = new GridLayoutManager(mainActivity, 2);
        rcvProduct.setLayoutManager(gridLayoutManager);

        productAdapter = new ProductAdapter();

        productAdapter.setData(getListProduct(), new ProductAdapter.IClickAddToCartListener() {
            @Override
            public void onClickAddToCart(ImageView imgAddToCart, Product product) {
                Toast.makeText(mainActivity, product.getName() + " đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
            }
        });

        rcvProduct.setAdapter(productAdapter);

        return mView;
    }

    private List<Product> getListProduct() {
        List<Product> list = new ArrayList<>();

        list.add(new Product(R.drawable.img_1, "Product name 1", "This is des1"));
        list.add(new Product(R.drawable.img_1, "Product name 2", "This is des2"));
        list.add(new Product(R.drawable.img_1, "Product name 3", "This is des3"));
        list.add(new Product(R.drawable.img_1, "Product name 4", "This is des3"));
        list.add(new Product(R.drawable.img_1, "Product name 5", "This is des3"));
        list.add(new Product(R.drawable.img_1, "Product name 6", "This is des3"));

        return list;
    }
}
