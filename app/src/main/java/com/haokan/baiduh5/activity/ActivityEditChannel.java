package com.haokan.baiduh5.activity;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Vibrator;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.haokan.baiduh5.R;
import com.haokan.baiduh5.adapter.AdapterEditChannel;
import com.haokan.baiduh5.bean.TypeBean;
import com.haokan.baiduh5.util.CommonUtil;
import com.haokan.baiduh5.util.StatusBarUtil;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by wangzixu on 2017/6/12.
 */
public class ActivityEditChannel extends ActivityBase implements View.OnClickListener {
    public static final String TAG = "ActivityMyCollection";
    private RecyclerView mRecyview;
    private AdapterEditChannel mAdapter;
    public static final String KEY_INTENT_CHANNELS = "keychannel";
    public static final String KEY_INTENT_CURRENTNAME = "keyname";
    public String mCurrentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editchannel);

        StatusBarUtil.setStatusBarBgColor(this, R.color.hong);
        initView();

        mCurrentName = getIntent().getStringExtra(KEY_INTENT_CURRENTNAME);
        ArrayList<TypeBean> list = getIntent().getParcelableArrayListExtra(KEY_INTENT_CHANNELS);
        if (list != null && list.size() > 0) {
            mAdapter.addDataBeans(list);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(KEY_INTENT_CHANNELS, (ArrayList<? extends Parcelable>)mAdapter.getDataBeans());
        intent.putExtra(KEY_INTENT_CURRENTNAME, mCurrentName);
        setResult(RESULT_OK, intent);

        super.onBackPressed();
        overridePendingTransition(R.anim.activity_in_left2right, R.anim.activity_out_left2right);
    }

    public void click(String name) {
        mCurrentName = name;
        onBackPressed();
    }

    private void initView() {
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.confirm).setOnClickListener(this);

        mRecyview = (RecyclerView) findViewById(R.id.recyview);
        final GridLayoutManager manager = new GridLayoutManager(this, 4);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyview.setLayoutManager(manager);
        mRecyview.setHasFixedSize(true);
        mRecyview.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new AdapterEditChannel(this);
        mRecyview.setAdapter(mAdapter);

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                    final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                    final int swipeFlags = 0;
                    return makeMovementFlags(dragFlags, swipeFlags);
                } else {
                    final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                    final int swipeFlags = 0;
                    return makeMovementFlags(dragFlags, swipeFlags);
                }
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //得到当拖拽的viewHolder的Position
                int fromPosition = viewHolder.getAdapterPosition();
                //拿到当前拖拽到的item的viewHolder
                int toPosition = target.getAdapterPosition();
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(mAdapter.getDataBeans(), i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(mAdapter.getDataBeans(), i, i - 1);
                    }
                }
                mAdapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }

            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return false;
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE && viewHolder  != null && viewHolder.itemView != null) {
                    viewHolder.itemView.scrollBy(6, 6);
                    //获取系统震动服务
                    Vibrator vib = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
                    //震动70毫秒
                    vib.vibrate(50);
                    viewHolder.itemView.setSelected(true);
                }
                super.onSelectedChanged(viewHolder, actionState);
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (viewHolder  != null && viewHolder.itemView != null) {
                    viewHolder.itemView.scrollTo(0, 0);
                    viewHolder.itemView.setSelected(false);
                }
                super.clearView(recyclerView, viewHolder);
            }
        });
        helper.attachToRecyclerView(mRecyview);
    }

    @Override
    public void onClick(View v) {
        if (CommonUtil.isQuickClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.back:
                onBackPressed();
                break;
            case R.id.confirm:
                onBackPressed();
                break;
        }
    }
}
