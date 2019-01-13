package im.years.recyclerviewwrappersample;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import im.years.recyclerviewwrapper.NewListFragment;
import im.years.recyclerviewwrapper.view.ListEmptyView;
import im.years.recyclerviewwrappersample.databinding.ItemHelloListBinding;
import im.years.recyclerviewwrappersample.viewHolder.DataBindBaseViewHolder;

public class NewDataBindListFragment extends NewListFragment<ContentMock, DataBindBaseViewHolder<ItemHelloListBinding>> {
    // 模拟要请求的页面
    private int testRequestPage = 1;
    private int refreshTimes = 1;
    private SampleListAdapter sampleListAdapter = new SampleListAdapter();

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
                List<ContentMock> contentMockList = new ArrayList<>();
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

    private List<ContentMock> mockDate(int size) {
        List<ContentMock> contentMockList = new ArrayList<>();
        int totalSize = sampleListAdapter.getItemCount();
        for (int i = 0; i < size; i++) {
            int index = totalSize + i;
            ContentMock contentMock = new ContentMock("title: index:" + index, "content:" + index);
            contentMockList.add(contentMock);
        }
        return contentMockList;
    }


    @Override
    protected void onItemChildClick(View clickedItemView, int position) {
        super.onItemChildClick(clickedItemView, position);
        Toast.makeText(getContext(), "Click Title: " + getItem(position).title, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onItemClick(View clickedView, int position) {
        super.onItemClick(clickedView, position);
        Toast.makeText(getContext(), getItem(position).title, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onItemLongClick(View clickedView, int position) {
        Toast.makeText(getContext(), "Long Click: " + getItem(position).title, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onItemChildLongClick(View clickedItemView, int position) {
        super.onItemChildLongClick(clickedItemView, position);
        Toast.makeText(getContext(), "Long Click Title: " + getItem(position).title, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected int getPageSize() {
        return 5;
    }

    @Override
    protected int getPreLoadNumber() {
        return 2;
    }

    /*class SampleListAdapter extends BaseQuickAdapter<ContentMock, SampleListAdapter.DateBindViewHolder> {
        SampleListAdapter() {
            super(R.layout.item_hello_list, null);
        }

        @Override
        protected void convert(DateBindViewHolder helper, ContentMock item) {
            ItemHelloListBinding dataBinding = (ItemHelloListBinding) helper.getDataBinding();
            dataBinding.setItemData(item);
        }

        // 你把这个类抽出去就不能使用了（类型转换异常） | 这个类也不能拥有自己的泛型  （你可以自定义自己的 adapter ，和 view Holder）
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
*/

    /*
    * 方式二： 通过重写创建方法
    * */
    class SampleListAdapter extends BaseQuickAdapter<ContentMock, DataBindBaseViewHolder<ItemHelloListBinding>> {
        SampleListAdapter() {
            super(R.layout.item_hello_list, null);
        }

        @Override
        protected void convert(DataBindBaseViewHolder<ItemHelloListBinding> helper, ContentMock item) {
            ItemHelloListBinding dataBinding = helper.getDataBinding();
            dataBinding.setItemData(item);
        }

        @Override
        protected DataBindBaseViewHolder<ItemHelloListBinding> createBaseViewHolder(ViewGroup parent, int layoutResId) {
            View view = getItemView(layoutResId, parent);
            return new DataBindBaseViewHolder<ItemHelloListBinding>(view);
        }

//        // 一定要重写不然创建的不会是 对应的类型
//        @Override
//        protected DataBindBaseViewHolder<ItemHelloListBinding> createBaseViewHolder(View view) {
//            return new DataBindBaseViewHolder<ItemHelloListBinding>(view);
//        }
    }
}
