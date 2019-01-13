package im.years.recyclerviewwrapper;

import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidx.annotation.ColorRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import im.years.recyclerviewwrapper.decoration.HorizontalDividerItemDecoration;

public abstract class NewListFragment<T, K extends BaseViewHolder> extends Fragment {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private BaseQuickAdapter<T, K> mQuickAdapter;

    private View emptyView;
    protected int currentPage = 0;
    private boolean isEnabledLoadMore;
    private boolean isEnabledRefresh;
    private boolean isLoadEndGone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // get root view
        View rootView = inflater.inflate(getLayoutRes(), container, false);

        // find views
        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);

        this.initSwipeRefreshLayout(mSwipeRefreshLayout);
        this.initRecycleView(mRecyclerView);

        // init pages
        this.initViews();
        return rootView;
    }

    protected void initRecycleView(RecyclerView mRecyclerView) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    protected void initSwipeRefreshLayout(@Nullable SwipeRefreshLayout mSwipeRefreshLayout) {
        if (mSwipeRefreshLayout == null) {
            return;
        }

        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mSwipeRefreshLayout.setEnabled(isEnabledRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 刷新先禁用加载
                mQuickAdapter.setEnableLoadMore(false);
                NewListFragment.this.onRefresh();
            }
        });
    }

    protected void setListDivider(@ColorRes int color) {
        mRecyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(getActivity())
                        .colorResId(color)
                        .build());
    }

    protected void setAdapter(BaseQuickAdapter adapter) {

        // init adapter config
        mQuickAdapter = adapter;
        mQuickAdapter.isFirstOnly(true);
        mQuickAdapter.setNotDoAnimationCount(getPageSize()); // 第一页无动画
        mQuickAdapter.setPreLoadNumber(getPreLoadNumber()); // 倒数第几个开始加载
        mRecyclerView.setAdapter(mQuickAdapter);

        // bind Event
        mQuickAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                NewListFragment.this.onItemChildClick(view, position);
            }
        });

        mQuickAdapter.setOnItemChildLongClickListener(new BaseQuickAdapter.OnItemChildLongClickListener() {
            @Override
            public boolean onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {
                NewListFragment.this.onItemChildLongClick(view, position);
                return true;
            }
        });

        mQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                NewListFragment.this.onItemClick(view, position);
            }
        });

        mQuickAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                NewListFragment.this.onItemLongClick(view, position);
                return true;
            }
        });
    }

    protected List<T> getItems() {
        return (List<T>) mQuickAdapter.getData();
    }

    protected T getItem(int position) {
        return mQuickAdapter.getItem(position);
    }

    public void setLoadEndGone(boolean isGon) { // 不显示 加载完成（没有更多数据）
        isLoadEndGone = isGon;
    }

    protected void enableRefresh() {
        if (mSwipeRefreshLayout == null) {
            throw new RuntimeException("Did you add SwipeRefreshLayout in your layout?");
        }

        isEnabledRefresh = true;
        mSwipeRefreshLayout.setEnabled(true);
    }

    protected void disableRefresh() {
        if (mSwipeRefreshLayout == null) {
            throw new RuntimeException("Did you add SwipeRefreshLayout in your layout?");
        }

        isEnabledRefresh = false;
        mSwipeRefreshLayout.setEnabled(false);
    }

    protected void enableLoadMore() {
        isEnabledLoadMore = true;
        realEnableLoadMore();
    }

    protected void disableLoadMore() {
        isEnabledLoadMore = false;
        realDisableLoadMore();
    }

    private void realEnableLoadMore() {
        if (mQuickAdapter == null) {
            throw new RuntimeException("Please call setAdapter first.");
        }

        mQuickAdapter.setEnableLoadMore(true);
        mQuickAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setEnabled(false);
                }
                onLoadMore();
            }
        }, mRecyclerView);
    }

    private void realDisableLoadMore() {
        if (mQuickAdapter == null) {
            throw new RuntimeException("Please call setAdapter first.");
        }

        mQuickAdapter.setEnableLoadMore(false);
        mQuickAdapter.setOnLoadMoreListener(null, mRecyclerView);
        mQuickAdapter.notifyItemChanged(mRecyclerView.getChildCount());
    }

    protected void endLoading(final boolean success, final boolean isMore, @NonNull final List newData) {
        if (getActivity() == null) {
            return;
        }

        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            endLoadingOnUiThread(success, isMore, newData);
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    endLoadingOnUiThread(success, isMore, newData);
                }
            });
        }
    }

    protected void endLoadingOnUiThread(boolean success, boolean isMore, @NonNull List newData) {
        if (success) {
            int newDataSize = newData.size();

            if (isMore) { // load more
                mQuickAdapter.addData(newData);
            } else { //refresh
                mQuickAdapter.setNewData(newData);
            }

            if (newDataSize < getPageSize()) {
                mQuickAdapter.loadMoreEnd(!isMore || isLoadEndGone);
            } else {
                mQuickAdapter.loadMoreComplete();
            }
        } else {
            mQuickAdapter.loadMoreFail();
        }

        currentPage = isMore ? ++currentPage : 0; // 当前是第几页（从0 开始）
        mQuickAdapter.setEnableLoadMore(this.isEnabledLoadMore); // 恢复加载

        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setEnabled(this.isEnabledRefresh);// 恢复刷新（加载，刷新不能同时进行）
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
        mQuickAdapter.setEmptyView(emptyView);
        mQuickAdapter.setHeaderFooterEmpty(true, false);
    }

    public void addHeaderView(View header) {
        if (mQuickAdapter == null) {
            throw new RuntimeException("Please call setAdapter first.");
        }

        mQuickAdapter.addHeaderView(header);
    }

    public void addFooterView(View footer) {
        if (mQuickAdapter == null) {
            throw new RuntimeException("Please call setAdapter first.");
        }

        mQuickAdapter.addFooterView(footer);
    }

    protected void initViews() {
        //Empty
    }

    protected void onLoadMore() {
    }

    protected void onRefresh() {
        this.mSwipeRefreshLayout.setRefreshing(true);
    }

    protected void onItemClick(View clickedView, int position) {
    }

    protected void onItemLongClick(View clickedView, int position) {
    }

    protected void onItemChildClick(View clickedItemView, int position) {
    }

    protected void onItemChildLongClick(View clickedItemView, int position) {
    }

    @LayoutRes
    protected int getLayoutRes() {
        return R.layout.az_list_fragment;
    }

    protected int getPageSize() {
        return 20;
    }

    protected int getPreLoadNumber() {
        return 5;
    }

    protected RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public BaseQuickAdapter getQuickAdapter() {
        return mQuickAdapter;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return mSwipeRefreshLayout;
    }
}
