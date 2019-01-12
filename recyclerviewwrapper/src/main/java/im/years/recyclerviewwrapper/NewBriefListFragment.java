package im.years.recyclerviewwrapper;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;

import androidx.annotation.LayoutRes;

public abstract class NewBriefListFragment<T, K extends BaseViewHolder> extends NewListFragment {

    @LayoutRes
    protected abstract int getItemViewRes();

    public abstract void onBindViewItemHolder(K holder, T item);

    @Override
    protected void initViews() {
        super.initViews();

        // 设置 adapter
        setAdapter(sampleListAdapter = new SampleListAdapter());
    }

    protected ArrayList<T> getItems() {
        return (ArrayList<T>) sampleListAdapter.getData();
    }

    protected T getItem(int position) {
        return sampleListAdapter.getItem(position);
    }

    protected void reloadData() {
        sampleListAdapter.notifyDataSetChanged();
    }

    protected SampleListAdapter sampleListAdapter;

    public SampleListAdapter getSampleListAdapter() {
        return sampleListAdapter;
    }

    protected class SampleListAdapter extends BaseQuickAdapter<T, K> {
        protected SampleListAdapter() {
            super(NewBriefListFragment.this.getItemViewRes(), null);
        }

        @Override
        protected void convert(K baseViewHolder, T item) {
            onBindViewItemHolder(baseViewHolder, item);
        }
    }
}
