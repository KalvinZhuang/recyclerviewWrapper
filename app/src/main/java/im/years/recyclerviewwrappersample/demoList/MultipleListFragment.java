package im.years.recyclerviewwrappersample.demoList;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import im.years.recyclerviewwrapper.NewListFragment;
import im.years.recyclerviewwrapper.view.ListEmptyView;
import im.years.recyclerviewwrappersample.R;
import im.years.recyclerviewwrappersample.databinding.ItemHelloListBinding;
import im.years.recyclerviewwrappersample.databinding.ItemListLefBinding;
import im.years.recyclerviewwrappersample.databinding.ItemListRightBinding;
import im.years.recyclerviewwrappersample.model.MultipleContentMock;
import im.years.recyclerviewwrappersample.viewHolder.DataBindBaseViewHolder;
import im.years.recyclerviewwrappersample.viewHolder.DataBindBaseViewHolder2;

public class MultipleListFragment extends NewListFragment<MultipleContentMock, DataBindBaseViewHolder<ItemHelloListBinding>> {
    // 模拟要请求的页面
    private int testRequestPage = 1;
    private int refreshTimes = 1;
    private SampleListAdapter sampleListAdapter;

    @Override
    protected void initViews() {
        super.initViews();

        // 每次初始化的时候，都要在 on create View 内部还原，在 Tab 等内部 当前 fragment 可能没有被销毁数据保留
        sampleListAdapter = new SampleListAdapter(null);
        this.setAdapter(sampleListAdapter);

        // 开启加载
        enableRefresh();
        enableLoadMore();
        setEmptyView(new ListEmptyView(getContext()));
        setListDivider(R.color.list_divider);
        onRefresh();
    }

    @Override
    protected void onRefresh() {
        super.onRefresh();
        getTestDate(true);
    }

    @Override
    protected void onLoadMore() {
        super.onLoadMore();
        getTestDate(false);
    }

    private void getTestDate(final boolean refresh) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean success = true;
                List<MultipleContentMock> contentMockList = new ArrayList<>();
                if (refresh) {// 刷新
                    if (refreshTimes % 3 == 0) {
                        contentMockList = mockDate(getPageSize() - 1);
                    } else if (refreshTimes % 5 == 0) {
                        contentMockList.clear();
                    } else {
                        contentMockList = mockDate(getPageSize());
                    }
                    refreshTimes++;
                } else {// 加载
                    if (testRequestPage % 3 == 0) {
                        success = false;
                    } else if (testRequestPage % 5 == 0) {
                        contentMockList = mockDate(getPageSize() - 1);
                    } else {
                        contentMockList = mockDate(getPageSize());
                    }
                    testRequestPage++;
                }

                endLoading(success, !refresh, contentMockList);
            }
        }, 1000);
    }

    private List<MultipleContentMock> mockDate(int size) {
        List<MultipleContentMock> contentMockList = new ArrayList<>();
        int totalSize = sampleListAdapter.getItemCount();
        for (int i = 0; i < size; i++) {
            int index = totalSize + i;
            MultipleContentMock contentMock;
            if (i > 1 && i % 3 == 0) {
                contentMock = new MultipleContentMock(MultipleContentMock.right_type);
            } else {
                contentMock = new MultipleContentMock(MultipleContentMock.left_type);
            }
            contentMock.setTitle("" + index);
            contentMockList.add(contentMock);
        }
        return contentMockList;
    }


    @Override
    protected void onItemChildClick(View clickedItemView, int position) {
        super.onItemChildClick(clickedItemView, position);
        Toast.makeText(getContext(), "Click Title: " + getItem(position).getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onItemClick(View clickedView, int position) {
        super.onItemClick(clickedView, position);
        Toast.makeText(getContext(), getItem(position).getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onItemLongClick(View clickedView, int position) {
        Toast.makeText(getContext(), "Long Click: " + getItem(position).getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onItemChildLongClick(View clickedItemView, int position) {
        super.onItemChildLongClick(clickedItemView, position);
        Toast.makeText(getContext(), "Long Click Title: " + getItem(position).getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected int getPageSize() {
        return 5;
    }

    @Override
    protected int getPreLoadNumber() {
        return 2;
    }

    private class SampleListAdapter extends BaseMultiItemQuickAdapter<MultipleContentMock, DataBindBaseViewHolder2<ViewDataBinding>> {

        SampleListAdapter(List<MultipleContentMock> data) {
            super(data);
            addItemType(MultipleContentMock.left_type, R.layout.item_list_lef);
            addItemType(MultipleContentMock.right_type, R.layout.item_list_right);
        }

        @Override
        protected void convert(DataBindBaseViewHolder2<ViewDataBinding> helper, MultipleContentMock item) {
            switch (item.getType()) {
                case MultipleContentMock.left_type: {
                    ItemListLefBinding dataBinding = (ItemListLefBinding) helper.getDataBinding();
                    dataBinding.setItemData(item);
                }
                break;
                case MultipleContentMock.right_type: {
                    ItemListRightBinding dataBinding = (ItemListRightBinding) helper.getDataBinding();
                    dataBinding.setItemData(item);
                }
                break;
            }
        }

        /*不是内部的 view holder 需要重写（防止泛型擦拭） */
        @Override
        protected DataBindBaseViewHolder2<ViewDataBinding> createBaseViewHolder(View view) {
            return new DataBindBaseViewHolder2<>(view);
        }

        /* 重写并 设置 view Bind */
        @Override
        protected DataBindBaseViewHolder2<ViewDataBinding> createBaseViewHolder(ViewGroup parent, int layoutResId) {
            ViewDataBinding viewDataBinding = DataBindingUtil.inflate(mLayoutInflater, layoutResId, parent, false);
            View rootView;
            if (viewDataBinding != null) {
                rootView = viewDataBinding.getRoot();
            } else {
                rootView = getItemView(layoutResId, parent);
            }

            DataBindBaseViewHolder2<ViewDataBinding> viewDataBindingDataBindBaseViewHolder = new DataBindBaseViewHolder2<>(rootView);
            viewDataBindingDataBindBaseViewHolder.setDataBinding(viewDataBinding);
            return viewDataBindingDataBindBaseViewHolder;
        }
    }

}
