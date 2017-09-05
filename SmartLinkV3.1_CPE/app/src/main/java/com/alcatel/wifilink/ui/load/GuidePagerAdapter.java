package com.alcatel.wifilink.ui.load;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

class GuidePagerAdapter extends PagerAdapter {

    private List<View> pagers;

    public GuidePagerAdapter(List<View> pagers) {
        this.pagers = pagers;
    }

    public void notifys(List<View> pagers) {
        this.pagers = pagers;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return pagers == null ? 0 : pagers.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(pagers.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = pagers.get(position);
        container.addView(view);
        return view;
    }
}
