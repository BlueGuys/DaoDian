package com.swolo.daodian.business.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.swolo.daodian.R;

import java.util.ArrayList;

public class OrderListAdapter extends BaseAdapter {

    private ArrayList<NewOrderResult.Order> orderArrayList = new ArrayList<>();

    public void notify(ArrayList<NewOrderResult.Order> orders) {
        if (orders.isEmpty()) {
            orderArrayList.clear();
        } else {
            orderArrayList.clear();
            orderArrayList.addAll(orders);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return orderArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return orderArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, null);
        }
        TextView tvGoodsName = convertView.findViewById(R.id.order_goods_name);
        TextView tvGoodsNumber = convertView.findViewById(R.id.order_number);
        TextView tvGoodsQuantity = convertView.findViewById(R.id.order_goods_standard);
        TextView tvGoodsRemark = convertView.findViewById(R.id.order_goods_remark);
        NewOrderResult.Order order = (NewOrderResult.Order) getItem(position);
        tvGoodsName.setText(order.nxCommunityGoodsEntity.nxCgGoodsName);
        tvGoodsNumber.setText(order.nxCospPickUpCode);
        tvGoodsQuantity.setText(order.nxCospQuantity);
        tvGoodsRemark.setText(order.nxCospRemark);
        return convertView;
    }
}
