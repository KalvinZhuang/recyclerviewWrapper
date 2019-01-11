package im.years.recyclerviewwrapper;

import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public abstract class NewListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private BaseQuickAdapter mQuickAdapter;

    private View emptyView;
    private int currentPage = 0;
    private boolean isEnabledLoadMore;
    private boolean isEnabledRefresh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // get root view
        View rootView = inflater.inflate(getLayoutRes(), container, false);

        // find views
        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);

        // init pages
        this.initViews();
        this.initSwipeRefreshLayout(mSwipeRefreshLayout);
        this.initRecycleView(mRecyclerView);

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
                if (mQuickAdapter != null && isEnabledLoadMore) {
                    mQuickAdapter.loadMoreComplete();
                }
                NewListFragment.this.onRefresh();
            }
        });
    }

    protected void setAdapter(BaseQuickAdapter adapter) {

        // init adapter config
        mQuickAdapter = adapter;
        mQuickAdapter.isFirstOnly(true);
        mQuickAdapter.setNotDoAnimationCount(getPageSize());
        mRecyclerView.setAdapter(mQuickAdapter);
        mQuickAdapter.setPreLoadNumber(getPreLoadNumber());

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

    protected void enableRefresh() {
        if (mSwipeRefreshLayout == null) {
            throw new RuntimeException("Did you add SwipeRefreshLayout in your layout?");
        }

        isEnabledRefresh = true;
        mSwipeRefreshLayout.setEnabled(isEnabledRefresh);
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
        mQuickAdapter.setPreLoadNumber(getPageSize());
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

    protected void refresh(@Nullable final List newData) {
        if (mQuickAdapter == null) {
            throw new RuntimeException("Please call setAdapter first.");
        }

        if (getActivity() == null || newData == null) {
            return;
        }

        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            mQuickAdapter.setNewData(newData);
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mQuickAdapter.setNewData(newData);
                }
            });
        }
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

    private void endLoadingOnUiThread(boolean success, boolean isMore, @NonNull List newData) {
        if (success) {
            int newDataSize = newData.size();

            if (isMore) { // load more
                currentPage++;
                if (newDataSize == 0) {
                    mQuickAdapter.loadMoreEnd();
                } else {
                    mQuickAdapter.loadMoreComplete();
                }

                mQuickAdapter.addData(newData);
            } else { //refresh
                currentPage = 0;

                if (newDataSize == 0 && emptyView != null) {
                    if (emptyView.getParent() != null) {
                        ((ViewGroup) emptyView.getParent()).removeView(emptyView);
                    }
                    mQuickAdapter.setEmptyView(emptyView);
                    mQuickAdapter.setHeaderFooterEmpty(true, false);
                }

                if (isEnabledLoadMore && newDataSize >= getPageSize()) {
                    realEnableLoadMore();
                } else {
                    realDisableLoadMore();
                }
                mQuickAdapter.setNewData(newData);
            }
        } else {
            if (isMore) {
                mQuickAdapter.loadMoreFail();
            }
        }

        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
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
