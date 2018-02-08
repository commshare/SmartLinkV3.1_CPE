package com.alcatel.wifilink.rx.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.rx.adapter.FeedbackPhotoAdapter;
import com.alcatel.wifilink.rx.adapter.FeedbackTypeAdapter;
import com.alcatel.wifilink.rx.bean.FeedbackTypeBean;
import com.alcatel.wifilink.rx.helper.base.FeedbackEnterWatcher;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.utils.FormatTools;
import com.alcatel.wifilink.utils.Logs;
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.pedant.SweetAlert.SweetAlertDialog;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * Created by qianli.ma on 2018/2/6 0006.
 */

public class FeedbackFragment extends Fragment implements FragmentBackHandler {

    @BindView(R.id.iv_feedback_back)
    ImageView iv_back;// 返回键

    @BindView(R.id.rl_feedback_selectType)
    RelativeLayout rlSelectType;// 点击选择类型
    @BindView(R.id.et_feedback_selectType)
    EditText etSelectType;// 已选择类型

    @BindView(R.id.rl_feedback_selectType_choice_list)
    RelativeLayout rlSelectTypeChoiceList_pop;// 选择类型弹框
    @BindView(R.id.iv_feedback_selectType_choice_list_gray)
    ImageView ivSelectTypeChoiceListGray;// 选择类型弹框背景(点击后panel消隐)
    @BindView(R.id.rcv_feedback_selectType_choice_list)
    RecyclerView rcvSelectTypeChoiceList;// 选择类型列表


    @BindView(R.id.et_feedback_enterFeedback)
    EditText etEnterFeedback;// 输入建议
    @BindView(R.id.tv_feedback_stringNum)
    TextView tvStringNum;// 字符个数

    @BindView(R.id.tv_feedback_photo_count)
    TextView tvPhotoCount;// 已经添加的图片数量
    @BindView(R.id.rl_feedback_photo_logo)
    RelativeLayout rlPhotoLogo;// 添加图片按钮
    @BindView(R.id.rcv_feedback_photo)
    RecyclerView rcvPhoto;// 图片列表

    @BindView(R.id.rl_feedback_selectType_submit_ok)
    RelativeLayout rlSelectTypeSubmitOk_pop;// 提交成功面板
    @BindView(R.id.iv_feedback_selectType_submit_ok_gray)
    ImageView ivSelectTypeSubmitOkGray;// 提交成功后背景(点击后panel消隐)
    @BindView(R.id.iv_feedback_selectType_submit_ok_pop)
    RelativeLayout ivSelectTypeSubmitOkPop;// 提交成功后提示框(点击后panel消隐)

    @BindView(R.id.tv_feedback_submit)
    TextView tvSubmit;// 点击提交按钮
    Unbinder unbinder;

    private HomeRxActivity activity;
    private View inflate;
    private FeedbackPhotoAdapter feedbackAdapter;
    private LinearLayoutManager lm_photo;
    private String[] feedbackTypes;
    private List<FeedbackTypeBean> feedbackTypeBeans;
    private FeedbackTypeAdapter feedbackTypeAdapter;
    private LinearLayoutManager lm_selectType;
    private boolean isPopShow = false;// 弹窗显示标记位
    private int REQUEST_IMAGE = 123;// 请求本地图片约定码

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (HomeRxActivity) getActivity();
        inflate = View.inflate(getActivity(), R.layout.fra_feedback_rx, null);
        unbinder = ButterKnife.bind(this, inflate);
        initRes();
        resetUi();
        initAdapter();
        initListener();
        return inflate;
    }

    /**
     * 初始化资源
     */
    private void initRes() {
        feedbackTypes = getResources().getStringArray(R.array.feedbacktype);
        feedbackTypeBeans = setFeedbackTypeBeans(feedbackTypes);
    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        // 1.初始化selectType适配器
        feedbackTypeAdapter = new FeedbackTypeAdapter(getActivity(), feedbackTypeBeans);
        feedbackTypeAdapter.setOnSelectTypeListener(attr -> {
            rlSelectTypeChoiceList_pop.setVisibility(View.GONE);// 0.列表界面隐藏
            feedbackTypeBeans = attr;// 1.更新数据
            for (FeedbackTypeBean ftb : feedbackTypeBeans) {
                if (ftb.isSelected()) {
                    etSelectType.setText(ftb.getTypeName());// 2.显示数据
                    break;
                }
            }
            // 4.恢复标记位
            isPopShow = false;

        });
        lm_selectType = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        rcvSelectTypeChoiceList.setLayoutManager(lm_selectType);
        rcvSelectTypeChoiceList.setAdapter(feedbackTypeAdapter);


        // TOGO 2018/2/7 0007 模拟数据
        List<Bitmap> bitmaps = new ArrayList<Bitmap>();
        for (int i = 0; i < 5; i++) {
            Drawable drawable = getActivity().getResources().getDrawable(R.drawable.test_feedback);
            Bitmap bitmap = FormatTools.getInstance().drawable2Bitmap(drawable);
            bitmaps.add(bitmap);
        }
        feedbackAdapter = new FeedbackPhotoAdapter(activity, bitmaps);
        lm_photo = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        rcvPhoto.setLayoutManager(lm_photo);
        rcvPhoto.setAdapter(feedbackAdapter);
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        // 添加字符数监听器
        etEnterFeedback.addTextChangedListener(new FeedbackEnterWatcher() {
            @Override
            public void getCurrentLength(int length) {
                tvStringNum.setText(String.valueOf(length + "/2000"));
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            resetUi();
        } else {
            isPopShow = false;
        }
    }

    private void resetUi() {
        // 1.初始化导航栏等
        if (activity == null) {
            activity = (HomeRxActivity) getActivity();
        }
        activity.tabFlag = Cons.TAB_FEEDBACK;
        activity.llNavigation.setVisibility(View.GONE);
        activity.rlBanner.setVisibility(View.GONE);
        // 2.初始化控件值
        // TODO: 2018/2/8 0008
    }

    @Override
    public boolean onBackPressed() {
        back();
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.iv_feedback_back,// 返回
                     R.id.rl_feedback_selectType,// 选择类型
                     R.id.rl_feedback_photo_logo,// 选择图片
                     R.id.tv_feedback_submit,// 提交
                     R.id.iv_feedback_selectType_choice_list_gray,// 选择类型列表背景
                     R.id.iv_feedback_selectType_submit_ok_gray,// 提交成功背景
                     R.id.iv_feedback_selectType_submit_ok_pop})// 提交类型弹窗
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_feedback_back:// 返回
                back();
                break;
            case R.id.rl_feedback_selectType:// 选择类型
                clickSelectType(true);
                break;
            case R.id.rl_feedback_photo_logo:// 选择图片
                clickSelectPhoto();
                break;
            case R.id.tv_feedback_submit:// 提交
                // TODO: 2018/2/7 0007
                break;
            case R.id.iv_feedback_selectType_choice_list_gray:// 选择类型列表背景
                clickSelectType(false);
                break;
            case R.id.iv_feedback_selectType_submit_ok_gray:// 提交成功背景
            case R.id.iv_feedback_selectType_submit_ok_pop:// 提交类型弹窗
                sumbitOkPop(false);
                break;
        }
    }

    /**
     * 点击选择照片
     */
    private void clickSelectPhoto() {
        MultiImageSelector.create(getActivity()).showCamera(true) // 是否显示相机. 默认为显示
                .count(5) // 最大选择图片数量, 默认为9. 只有在选择模式为多选时有效
                .multi() // 多选模式, 默认模式;
                .start(this, REQUEST_IMAGE);// 注意:第一个参数写this即可(是fragment就写framgent,activity就写activity)
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                // 获取返回的图片列表
                List<String> pics = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                // 处理你自己的逻辑 ....
                Logs.t("ma_photo").ii("pics: " + pics.size());
            }
        }
    }

    /**
     * 提交成功弹框
     *
     * @param isShow
     */
    private void sumbitOkPop(boolean isShow) {
        isPopShow = isShow;
        rlSelectTypeSubmitOk_pop.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    /**
     * 弹出选择类型
     */
    private void clickSelectType(boolean isShow) {
        isPopShow = isShow;
        rlSelectTypeChoiceList_pop.setVisibility(isShow ? View.VISIBLE : View.GONE);
        feedbackTypeAdapter.notifys(feedbackTypeBeans);
    }

    /**
     * 回退
     */
    private void back() {
        if (isPopShow) {
            rlSelectTypeChoiceList_pop.setVisibility(View.GONE);
            rlSelectTypeSubmitOk_pop.setVisibility(View.GONE);
            isPopShow = false;
        } else {
            showExitDialog();
        }

    }

    private void showExitDialog() {
        // 重启设备
        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)// 类型
                .setTitleText(getString(R.string.return_feedback_title))// 警告
                .setContentText(getString(R.string.return_feedback_des))// 描述
                .setConfirmText(getString(R.string.ok))// 确定
                .setConfirmClickListener(dialog -> {
                    dialog.dismiss();// 对话框消失
                    activity.fraHelpers.transfer(activity.clazz[Cons.TAB_SETTING]);
                })// 设置确定监听
                .showCancelButton(true)// 显示取消按钮
                .setCancelText(getString(R.string.cancel))// 取消文本
                .show();
    }

    /**
     * 封装类型对象
     *
     * @param feedbackTypes
     * @return
     */
    private List<FeedbackTypeBean> setFeedbackTypeBeans(String[] feedbackTypes) {
        List<FeedbackTypeBean> fts = new ArrayList<>();
        for (int i = 0; i < feedbackTypes.length; i++) {
            FeedbackTypeBean ft = new FeedbackTypeBean();
            ft.setTypeName(feedbackTypes[i]);
            ft.setSelected(i == 0 ? true : false);
            fts.add(ft);
        }
        return fts;
    }
}
