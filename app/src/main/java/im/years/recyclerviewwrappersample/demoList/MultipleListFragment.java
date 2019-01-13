package im.years.recyclerviewwrappersample.demoList;

import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

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

public class MultipleListFragment extends NewListFragment<MultipleContentMock, DataBindBaseViewHolder<ItemHelloListBinding>> {
    // 模拟要请求的页面
    private int testRequestPage = 1;
    private int refreshTimes = 1;
    private SampleListAdapter sampleListAdapter = new SampleListAdapter(null);

    @Override
    protected void initViews() {
        super.initViews();

        this.setAdapter(sampleListAdapter);

        // 开启加载
        enableRefresh();
        enableLoadMore();
        setEmptyView(new ListEmptyView(getContext()));
        setListDivider(R.color.list_divider);
        onRefresh();

        // 关闭加载完成
        setLoadEndGone(true);
    }

    @Override
    protected void onRefresh() {
        testRequestPage = 1;
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
        }, 1500);
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

    private class SampleListAdapter extends BaseMultiItemQuickAdapter<MultipleContentMock, SampleListAdapter.DateBindViewHolder> {

        SampleListAdapter(List<MultipleContentMock> data) {
            super(data);
            addItemType(MultipleContentMock.left_type, R.layout.item_list_lef);
            addItemType(MultipleContentMock.right_type, R.layout.item_list_right);
        }

        @Override
        protected void convert(DateBindViewHolder helper, MultipleContentMock item) {
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


        // view holder 需要在内部定义否则无效 (需要定义并重写 内部的 BaseViewHolder )
        public class DateBindViewHolder extends BaseViewHolder {
            private ViewDataBinding dataBind;

            public DateBindViewHolder(View view) {
                super(view);
                dataBind = DataBindingUtil.bind(view);
            }

            public ViewDataBinding getDataBinding() {
                return dataBind;
            }
        }
    }
}
