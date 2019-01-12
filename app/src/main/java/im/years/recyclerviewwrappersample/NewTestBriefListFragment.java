package im.years.recyclerviewwrappersample;

import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import im.years.recyclerviewwrapper.NewBriefListFragment;
import im.years.recyclerviewwrapper.view.ListEmptyView;

public class NewTestBriefListFragment extends NewBriefListFragment<ContentMock, BaseViewHolder> {
    // 模拟要请求的页面
    private int testRequestPage = 1;
    private int refreshTimes = 1;

    @Override
    protected int getItemViewRes() {
        return R.layout.item_hello_list;
    }

    @Override
    public void onBindViewItemHolder(BaseViewHolder holder, ContentMock item) {
        holder.setText(R.id.textView, item.title);
    }

    @Override
    protected void initViews() {
        super.initViews();

        // 开启加载
        enableRefresh();
        enableLoadMore();
        setEmptyView(new ListEmptyView(getContext()));
        setListDivider(R.color.list_divider);
        onRefresh();
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
        int totalSize = this.getItems().size();
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
}
