package com.haokan.screen.lockscreen.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.haokan.lockscreen.R;
import com.haokan.screen.activity.ActivityBase;
import com.haokan.screen.bean.LockImageBean;
import com.haokan.screen.lockscreen.adapter.AdapterMyDCIM2;
import com.haokan.screen.bean.BeanDCIM;
import com.haokan.screen.lockscreen.model.ModelLockImage;
import com.haokan.screen.model.interfaces.onDataResponseListener;
import com.haokan.screen.util.DisplayUtil;
import com.haokan.screen.util.LogHelper;
import com.haokan.screen.util.Values;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class ActivityMyDCIM2 extends ActivityBase implements View.OnClickListener{

    private RecyclerView recyclerView;
    private ArrayList<BeanDCIM> list = new ArrayList<>();
    private AdapterMyDCIM2 mAdapter;
    private TextView edit;
    private boolean isDelete=false;
    private ImageView mIvBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_dcim2);
        recyclerView= (RecyclerView) findViewById(R.id.recycleView);
        edit= (TextView) findViewById(R.id.edit);
        edit.setOnClickListener(this);
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mIvBack.setOnClickListener(this);
        GridLayoutManager manager=new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(manager);
        final int divider = DisplayUtil.dip2px(this, 5f);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0,divider,divider,0);
            }
        });

//        String path = Environment.getExternalStorageDirectory().toString() + Values.Path.PATH_DCIM_PIC;
//        File dir = new File(path);
//        if (dir.exists()) {//读取保存相册的数据
//            File[] files=dir.listFiles(new FilenameFilter() {
//                @Override
//                public boolean accept(File dir, String name) {
//                    if (name.startsWith(".img")) {
//                        return true;
//                    }
//                    return false;
//                }
//            });
//
//            if (files != null) {
//                for (int i=0;i<files.length;i++){
//                    BeanDCIM beanDCIM=new BeanDCIM();
//                    beanDCIM.setId(files[i].getAbsolutePath());
//                    beanDCIM.setPath(files[i].getAbsolutePath());
//                    list.add(beanDCIM);
//                }
//            }
//        }
//        mAdapter= new AdapterMyDCIM2(list, this);
//        recyclerView.setAdapter(mAdapter);
        LogHelper.e("times","-----onCreate");

        loadDate();
    }

    private  void loadDate(){
        String path = Environment.getExternalStorageDirectory().toString() + Values.Path.PATH_DCIM_PIC;
        File dir = new File(path);
        if (dir.exists()) {//读取保存相册的数据
            File[] files=dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (name.startsWith(".img")) {
                        return true;
                    }
                    return false;
                }
            });

            if (files != null) {
                list.clear();
                for (int i=0;i<files.length;i++){
                    BeanDCIM beanDCIM=new BeanDCIM();
                    beanDCIM.setId(files[i].getAbsolutePath());
                    beanDCIM.setPath(files[i].getAbsolutePath());
                    list.add(beanDCIM);
                }
            }
        }
        mAdapter= new AdapterMyDCIM2(list, this);
        recyclerView.setAdapter(mAdapter);
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        LogHelper.e("times","-----onRestart");

        loadDate();

    }

    @Override
    public void onClick(View view) {
        if(view==edit){
            if(isDelete) {
                if(mAdapter.hasDeleteList()){
                    showDeleteDialog();
                }else {
                    mAdapter.delete(false);
                    edit.setText(getString(R.string.edit));
                    isDelete=false;
                    mAdapter.notifyDataSetChanged();
                }
            }else{
                mAdapter.delete(true);
                edit.setText(getString(R.string.delete));
                isDelete=true;
                mAdapter.notifyDataSetChanged();
            }
        }else if(view==mIvBack){
            onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode!=RESULT_OK){
            return;
        }
        if(requestCode==100){
            if(data!=null) {
                jumpCrop(data.getData());
            }
        }else if(requestCode==200){
            String path=data.getStringExtra("path");
            BeanDCIM beanDCIM=new BeanDCIM();
            beanDCIM.setPath(path);
            beanDCIM.setId(path);
            list.add(beanDCIM);
            mAdapter.notifyDataSetChanged();
        }else if(requestCode==300){

            //通知锁屏删除了图片
            Intent intent1 = new Intent();
            intent1.setAction(Values.Action.RECEIVER_UPDATA_LOCAL_IMAGE);
            sendBroadcast(intent1);

            list.remove(data.getIntExtra("position",0));
            mAdapter.notifyDataSetChanged();
        }
    }

    private void jumpCrop(Uri uri){
        Intent intent = new Intent(this,ActivityCropPicture.class);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 200);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_in_left2right, R.anim.activity_out_left2right);
    }
    private void showDeleteDialog() {
        final Dialog mDialog = new Dialog(this, R.style.dialog);
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_layout_setting, null);
        TextView title = (TextView) v.findViewById(R.id.tv_dialog_title);
        TextView desc = (TextView) v.findViewById(R.id.tv_dialog_desc);
        TextView cancel = (TextView) v.findViewById(R.id.cancel);
        TextView unbind = (TextView) v.findViewById(R.id.unbind);

        final CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkbox);
        checkBox.setVisibility(View.VISIBLE);
        checkBox.setText(R.string.dialog_check_txt);

        title.setText(R.string.dialog_title);
        desc.setText(R.string.delete_dcim_hint);
        unbind.setText(R.string.ok);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        unbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAction(checkBox.isChecked());
                mDialog.dismiss();

            }
        });
        mDialog.setCancelable(false);
        mDialog.setContentView(v);
        mDialog.show();

        refreshLockImageData();//偷偷请求锁定图片数据
    }
    private void deleteAction(boolean isChecked){
        if(isChecked&&!TextUtils.isEmpty(mLockImageBeanUri)){
            mAdapter.deleteLockedFile(mLockImageBeanUri);
        }
        mAdapter.delete(false);
        edit.setText(getString(R.string.edit));
        isDelete=false;
        mAdapter.notifyDataSetChanged();
    }
    private  String mLockImageBeanUri;
    protected void refreshLockImageData() {
        ModelLockImage.getLockImage(new onDataResponseListener<LockImageBean>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onDataSucess(LockImageBean lockImageBean) {
                if(lockImageBean!=null&& !TextUtils.isEmpty(lockImageBean.image_url)) {
                    mLockImageBeanUri = lockImageBean.image_url;
                }
            }

            @Override
            public void onDataEmpty() {
            }

            @Override
            public void onDataFailed(String errmsg) {

            }

            @Override
            public void onNetError() {
            }
        });
    }
}
