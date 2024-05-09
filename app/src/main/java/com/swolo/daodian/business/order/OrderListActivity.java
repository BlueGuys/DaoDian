package com.swolo.daodian.business.order;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.swolo.daodian.R;
import com.swolo.daodian.ui.ActivityUtil;
import com.swolo.daodian.ui.BaseActivity;

public class OrderListActivity extends BaseActivity {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        mListView = findViewById(R.id.listview);
    }

    public void goBack(View view) {
        ActivityUtil.goBack(this);
    }
}
