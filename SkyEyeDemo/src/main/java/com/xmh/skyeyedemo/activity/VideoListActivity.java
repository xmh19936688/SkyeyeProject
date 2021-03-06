package com.xmh.skyeyedemo.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.xmh.skyeyedemo.R;
import com.xmh.skyeyedemo.adapter.VideoListAdapter;
import com.xmh.skyeyedemo.base.BaseActivity;
import com.xmh.skyeyedemo.bean.FileBmobBean;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by mengh on 2016/3/9 009.
 */
public class VideoListActivity extends BaseActivity{

    public static final String EXTRA_TAG_EYENAME="eyeUsername";
    private static final int ITEM_COUNT_PER_PAGE= Integer.MAX_VALUE;

    @Bind(R.id.rv_video_list)RecyclerView rvVideo;
    @Bind(R.id.tv_empty_log)TextView tvEmptyLog;
    @Bind(R.id.tv_back)TextView tvBack;
    @Bind(R.id.tv_title)TextView tvTitle;
    @Bind(R.id.tv_menu)TextView tvRefresh;
    @Bind(R.id.cl_snackbar)CoordinatorLayout snackbarContainer;

    private VideoListAdapter mVideoAdapter;
    private ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);
        ButterKnife.bind(this);

        initView();
        requestVideoList(false);

    }

    private void requestVideoList(final boolean needFeedback) {
        loadingDialog.show();
        tvRefresh.setClickable(false);
        String eyeUsername = getIntent().getStringExtra(EXTRA_TAG_EYENAME);
        BmobQuery<FileBmobBean> query = new BmobQuery<>();
        query.addWhereEqualTo("username", eyeUsername);
        query.setLimit(ITEM_COUNT_PER_PAGE);
        query.order("-updatedAt");//按更新时间降序排列
        query.findObjects(this, new FindListener<FileBmobBean>() {
            @Override
            public void onSuccess(List<FileBmobBean> list) {
                tvRefresh.setClickable(true);
                loadingDialog.dismiss();
                if (list != null && !list.isEmpty()) {
                    mVideoAdapter.setVideoList(list);
                    tvEmptyLog.setVisibility(View.GONE);
                } else {
                    tvEmptyLog.setText(R.string.none_video);
                }
                if(needFeedback) {
                    Snackbar.make(snackbarContainer, R.string.refresh_success, Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(int i, String s) {
                tvRefresh.setClickable(true);
                loadingDialog.dismiss();
                if(needFeedback) {
                    Snackbar.make(snackbarContainer, R.string.refresh_fail, Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initView() {
        initTitle();
        //初始化dialog
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setMessage(getString(R.string.loading_video_list));
        //初始化list
        rvVideo.setLayoutManager(new LinearLayoutManager(this));
        mVideoAdapter=new VideoListAdapter(this,snackbarContainer);
        rvVideo.setAdapter(mVideoAdapter);
    }

    private void initTitle() {
        tvTitle.setText(R.string.video_list);
        tvRefresh.setText(R.string.refresh);
        tvBack.setVisibility(View.VISIBLE);
        tvRefresh.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.tv_back)
    void onBackClick(){
        super.onBackPressed();
    }

    @OnClick(R.id.tv_menu)
    void onRefreshClick(){
        requestVideoList(true);
    }
}
