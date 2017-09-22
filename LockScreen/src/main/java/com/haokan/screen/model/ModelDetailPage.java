package com.haokan.screen.model;

import android.content.Context;
import android.support.annotation.NonNull;

import com.haokan.screen.App;
import com.haokan.screen.bean_old.DataResponse;
import com.haokan.screen.bean_old.MainImageBean;
import com.haokan.screen.bean_old.ResponseBeanCpGridImg;
import com.haokan.screen.bean_old.ResponseBeanTagList;
import com.haokan.screen.bean_old.ResponseBeanZutuImgs;
import com.haokan.screen.bean_old.TagBean;
import com.haokan.screen.http.HttpRetrofitManager;
import com.haokan.screen.http.HttpStatusManager;
import com.haokan.screen.model.interfaces.onDataResponseListener;
import com.haokan.screen.util.LogHelper;
import com.haokan.screen.util.UrlsUtil;
import com.haokan.screen.util.Values;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by wangzixu on 2017/3/21.
 * 获取大图页数据list, 如, 推荐, cp大图, 标签大图, 组图大图灯
 */
public class ModelDetailPage {
    public static void getCpDetailData(Context mContext, @NonNull String cpid, final int pageIndex, @NonNull final onDataResponseListener<List<MainImageBean>> listener) {
        if (!HttpStatusManager.checkNetWorkConnect(mContext)) {
            listener.onNetError();
            return;
        }

        listener.onStart();
        String url = UrlsUtil.getCpImageListUrl(mContext, cpid, pageIndex, Values.PAGE_SIZE);
        Observable<DataResponse<ResponseBeanCpGridImg>> observable = HttpRetrofitManager.getInstance().getRetrofitService().getCpImgListData(url);
        observable
                .map(new Func1<DataResponse<ResponseBeanCpGridImg>, DataResponse<ResponseBeanCpGridImg>>() {
                    @Override
                    public DataResponse<ResponseBeanCpGridImg> call(DataResponse<ResponseBeanCpGridImg> responseBeanZutuImgsDataResponse) {
                        DataResponse<ResponseBeanCpGridImg> response = HttpStatusManager.checkResponseSuccess(responseBeanZutuImgsDataResponse);
                        return response;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DataResponse<ResponseBeanCpGridImg>>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onDataFailed(e.getMessage());
                    }

                    @Override
                    public void onNext(DataResponse<ResponseBeanCpGridImg> objectDataResponse) {
                        if (objectDataResponse.getCode() == 200) {
                            if (objectDataResponse.getData() == null
                                    || objectDataResponse.getData().getList() == null
                                    || objectDataResponse.getData().getList().size() == 0) {
                                listener.onDataEmpty();
                            } else {
                                listener.onDataSucess(objectDataResponse.getData().getList());
                            }
                        } else {
                            listener.onDataFailed(objectDataResponse.getMessage());
                        }
                    }
                });
    }



    /**
     * 把获取的tag大图中, 目标tag置于首位
     */
    protected static void processTagPositon(List<MainImageBean> list, String tagId) {
        if (list == null || list.size() == 0) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            MainImageBean bean = list.get(i);
            List<TagBean> tag_info = bean.getTag_info();
            TagBean tagBean = null;
            for (int j = 0; j < tag_info.size(); j++) {
                if (tag_info.get(j).getTag_id().equals(tagId)) {
                    tagBean = tag_info.get(j);
                    break;
                }
            }
            if (tagBean != null) {
                tag_info.remove(tagBean);
                tag_info.add(0,tagBean);
            }
        }
    }

    public static void getTagDetailData(Context mContext, @NonNull final String tagId, final int pageIndex, @NonNull final onDataResponseListener listener) {
        if (listener == null) {
            return;
        }
        if (!HttpStatusManager.checkNetWorkConnect(mContext)) {
            listener.onNetError();
            return;
        }

        String url = UrlsUtil.getTagImageListUrl(mContext, tagId, pageIndex, Values.PAGE_SIZE);
        Observable<DataResponse<ResponseBeanTagList>> observable = HttpRetrofitManager.getInstance().getRetrofitService().getTagImgListData(url);
        observable
                .map(new Func1<DataResponse<ResponseBeanTagList>, DataResponse<ResponseBeanTagList>>() {
                    @Override
                    public DataResponse<ResponseBeanTagList> call(DataResponse<ResponseBeanTagList> responseBeanZutuImgsDataResponse) {
                        DataResponse<ResponseBeanTagList> response = HttpStatusManager.checkResponseSuccess(responseBeanZutuImgsDataResponse);
                        if (response.getCode() == 200) {
                            if (response.getData() != null
                                    && response.getData().getList() != null
                                    && response.getData().getList().size() > 0) {
                                //处理标签的位置,把当前的标签放在第一个位置
                                List<MainImageBean> list = response.getData().getList();
                                processTagPositon(list, tagId);
                            }
                        }
                        return response;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DataResponse<ResponseBeanTagList>>() {
                    @Override
                    public void onStart() {
                        listener.onStart();
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onDataFailed(e.getMessage());
                    }

                    @Override
                    public void onNext(DataResponse<ResponseBeanTagList> objectDataResponse) {
                        if (objectDataResponse.getCode() == 200) {
                            if (objectDataResponse.getData() == null
                                    || objectDataResponse.getData().getList() == null
                                    || objectDataResponse.getData().getList().size() == 0) {
                                listener.onDataEmpty();
                            } else {
                                listener.onDataSucess(objectDataResponse.getData().getList());
                            }
                        } else {
                            listener.onDataFailed(objectDataResponse.getMessage());
                        }
                    }
                });
    }

    public static void getZutuData(Context context, @NonNull String albumId, @NonNull final onDataResponseListener<List<MainImageBean>> listener) {
        if (!HttpStatusManager.checkNetWorkConnect(context)) {
            LogHelper.d("wangzixu", "getZutuData net error");
            listener.onNetError();
            return;
        }
        String url = UrlsUtil.getAlbumDetailUrl(context, albumId, App.sBigImgSize, App.sLoadingImgSize);
        Observable<DataResponse<ResponseBeanZutuImgs>> observable = HttpRetrofitManager.getInstance().getRetrofitService().getAlbumData1(url);
        observable
                .map(new Func1<DataResponse<ResponseBeanZutuImgs>, DataResponse<ResponseBeanZutuImgs>>() {
                    @Override
                    public DataResponse<ResponseBeanZutuImgs> call(DataResponse<ResponseBeanZutuImgs> responseBeanZutuImgsDataResponse) {
                        DataResponse<ResponseBeanZutuImgs> response = HttpStatusManager.checkResponseSuccess(responseBeanZutuImgsDataResponse);
                        return response;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DataResponse<ResponseBeanZutuImgs>>() {
                    @Override
                    public void onStart() {
                        listener.onStart();
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onDataFailed(e.getMessage());
                        e.printStackTrace();
                        LogHelper.d("wangzixu", "getZutuData onError");
                    }

                    @Override
                    public void onNext(DataResponse<ResponseBeanZutuImgs> objectDataResponse) {
                        LogHelper.d("wangzixu", "getZutuData onNext");
                        if (objectDataResponse.getCode() == 200) {
                            if (objectDataResponse.getData() == null
                                    || objectDataResponse.getData().getList() == null
                                    || objectDataResponse.getData().getList().size() == 0) {
                                listener.onDataEmpty();
                            } else {
                                listener.onDataSucess(objectDataResponse.getData().getList());
                            }
                        } else {
                            listener.onDataFailed(objectDataResponse.getMessage());
                        }
                    }
                });
    }

    /**
     * tag页的组图
     */
    public static void getZutuDataForTagPage(Context context, @NonNull String albumId, final String tagId, @NonNull final onDataResponseListener<List<MainImageBean>> listener) {
        if (!HttpStatusManager.checkNetWorkConnect(context)) {
            listener.onNetError();
            return;
        }

        String url = UrlsUtil.getAlbumDetailUrl(context, albumId, App.sBigImgSize, App.sLoadingImgSize);
        Observable<DataResponse<ResponseBeanZutuImgs>> observable = HttpRetrofitManager.getInstance().getRetrofitService().getAlbumData1(url);
        observable
                .map(new Func1<DataResponse<ResponseBeanZutuImgs>, DataResponse<ResponseBeanZutuImgs>>() {
                    @Override
                    public DataResponse<ResponseBeanZutuImgs> call(DataResponse<ResponseBeanZutuImgs> responseBeanZutuImgsDataResponse) {
                        DataResponse<ResponseBeanZutuImgs> response = HttpStatusManager.checkResponseSuccess(responseBeanZutuImgsDataResponse);
                        if (response.getCode() == 200) {
                            if (response.getData() != null
                                    && response.getData().getList() != null
                                    && response.getData().getList().size() > 0) {
                                //处理标签的位置,把当前的标签放在第一个位置
                                List<MainImageBean> list = response.getData().getList();
                                processTagPositon(list, tagId);
                            }
                        }
                        return response;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DataResponse<ResponseBeanZutuImgs>>() {
                    @Override
                    public void onStart() {
                        listener.onStart();
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onDataFailed(e.getMessage());
                    }

                    @Override
                    public void onNext(DataResponse<ResponseBeanZutuImgs> objectDataResponse) {
                        if (objectDataResponse.getCode() == 200) {
                            if (objectDataResponse.getData() == null
                                    || objectDataResponse.getData().getList() == null
                                    || objectDataResponse.getData().getList().size() == 0) {
                                listener.onDataEmpty();
                            } else {
                                listener.onDataSucess(objectDataResponse.getData().getList());
                            }
                        } else {
                            listener.onDataFailed(objectDataResponse.getMessage());
                        }
                    }
                });
    }
}
