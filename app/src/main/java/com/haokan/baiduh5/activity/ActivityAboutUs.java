package com.haokan.baiduh5.activity;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.haokan.baiduh5.App;
import com.haokan.baiduh5.R;
import com.haokan.baiduh5.util.CommonUtil;
import com.haokan.baiduh5.util.StatusBarUtil;

import java.util.Calendar;

/**
 * 关于我们页面
 */
public class ActivityAboutUs extends ActivityBase implements View.OnClickListener, View.OnLongClickListener {
    private TextView mTvDesc;
    private int mLongClickCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        StatusBarUtil.setStatusBarWhiteBg_BlackText(this);

        TextView tv_copy_right = (TextView) findViewById(R.id.tv_copy_right);
//        tv_copy_right.setText(getString(R.string.app_copyright, Calendar.getInstance().get(Calendar.YEAR)));
        TextView version = (TextView) findViewById(R.id.tv_about_us_version);
        version.setText("v" + App.APP_VERSION_NAME);

        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.iv_about_us_img).setOnLongClickListener(this);
        mTvDesc = (TextView) findViewById(R.id.tv_desc);
    }

    @Override
    public void onClick(View v) {
        onBackPressed();
    }

    @Override
    public boolean onLongClick(View v) {
        if (mLongClickCount < 1) {
            mLongClickCount ++;
            return true;
        }
        if (mLongClickCount > 1) {
            mLongClickCount = 0;
            hideInfoText();
            return true;
        }
        mLongClickCount ++;
        String info = getAboutUsInfo();
        showInfoText(info);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_in_left2right, R.anim.activity_out_left2right);
    }

    public void showInfoText(String info) {
        mTvDesc.setText(info);
        mTvDesc.setVisibility(View.VISIBLE);
    }

    public void hideInfoText() {
        mTvDesc.setVisibility(View.GONE);
    }

    public String getAboutUsInfo() {
        String pid = "";
        try {
            ApplicationInfo info = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            pid = info.metaData.getInt("UMENG_CHANNEL") + "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        String packageName = getPackageName();
        int code = CommonUtil.getLocalVersionCode(this);
        StringBuilder builder = new StringBuilder("packageName:" + packageName + "\npid:" + App.PID + "\npid(real):" + pid
                + "\nversioncode : " + code);
        return builder.toString();
    }
}
