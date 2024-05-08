package com.swolo.daodian.business.order;

import android.os.Bundle;
import android.widget.ListView;

import com.swolo.daodian.R;
import com.swolo.daodian.ui.BaseActivity;

public class OrderListActivity extends BaseActivity {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        mListView = findViewById(R.id.listview);
    }
}
