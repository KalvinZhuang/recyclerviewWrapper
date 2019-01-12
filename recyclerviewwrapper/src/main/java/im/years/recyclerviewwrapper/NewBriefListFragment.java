package im.years.recyclerviewwrapper;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidx.annotation.LayoutRes;

public abstract class NewBriefListFragment<T> extends NewListFragment {

    @LayoutRes
    protected abstract int getItemViewRes();

    public abstract void onBindViewItemHolder(BaseViewHolder holder, T item);

    @Override
    protected void initViews() {
        super.initViews();

        // 设置 adapter
        setAdapter(sampleListAdapter = new SampleListAdapter());
    }

    protected List<T> getItems() {
        return (List<T>) sampleListAdapter.getData();
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

    protected class SampleListAdapter extends BaseQuickAdapter<T, BaseViewHolder> {
        protected SampleListAdapter() {
            super(NewBriefListFragment.this.getItemViewRes(), null);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, T item) {
            onBindViewItemHolder(baseViewHolder, item);
        }
    }
}
