package im.years.recyclerviewwrapper;

import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

/**
 * Created by alvinzeng on 19/10/2016.
 */

public abstract class ListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private BaseQuickAdapter mQuickAdapter;

    View emptyView;

    Integer currentPage = 0;
    boolean isEnabledLoadMore;
    boolean isEnabledRefresh;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(getLayoutRes(), container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        mRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this.getActivity()));

        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setEnabled(false);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (mQuickAdapter != null && isEnabledLoadMore) {
                        mQuickAdapter.loadComplete();
                    }
                    ListFragment.this.onRefresh();
                }
            });

            mSwipeRefreshLayout.setColorSchemeResources(
                    android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
        }

        this.initViews();

        return v;
    }

    protected void setAdapter(BaseQuickAdapter adapter) {
        mQuickAdapter = adapter;
        mRecyclerView.setAdapter(mQuickAdapter);

        mRecyclerView.addOnItemTouchListener(new SimpleClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ListFragment.this.onItemClick(view, position);
            }

            @Override
            public void onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                ListFragment.this.onItemLongClick(view, position);
            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                ListFragment.this.onItemChildClick(view, position);
            }

            @Override
            public void onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {
                ListFragment.this.onItemChildLongClick(view, position);
            }
        });
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
        if (mQuickAdapter == null) {
            throw new RuntimeException("Please call setAdapter first.");
        }
        isEnabledLoadMore = true;
        mQuickAdapter.openLoadMore(getPageSize());
        mQuickAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setEnabled(false);
                }
                onLoadMore();
            }
        });
    }

    protected void disableLoadMore() {
        if (mQuickAdapter == null) {
            throw new RuntimeException("Please call setAdapter first.");
        }
        isEnabledLoadMore = false;

        mQuickAdapter.openLoadMore(-1);
        mQuickAdapter.setOnLoadMoreListener(null);
        mQuickAdapter.notifyItemChanged(mRecyclerView.getChildCount());
    }

    protected void refresh(final List newData) {
        if (mQuickAdapter == null) {
            throw new RuntimeException("Please call setAdapter first.");
        }
        if (getActivity() == null) {
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

    protected void endLoading(final boolean success, final boolean isMore, final List newData) {
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

    private void endLoadingOnUiThread(boolean success, boolean isMore, List newData) {
        if (success) {
            int newDataSize = newData == null ? 0 : newData.size();

            if (isMore) {
                currentPage++;
                if (newDataSize == 0) {
                    mQuickAdapter.loadComplete();
                }

                mQuickAdapter.addData(newData);
            } else {
                currentPage = 1;

                if (newDataSize == 0 && emptyView != null && mQuickAdapter.getEmptyView() != emptyView) {
                    mQuickAdapter.setEmptyView(true, false, emptyView);
                }

                if (isEnabledLoadMore) {
                    enableLoadMore();
                }
                mQuickAdapter.setNewData(newData);
            }
        } else {
            if (isMore) {
                mQuickAdapter.showLoadMoreFailedView();
            }
        }

        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
            if (isEnabledRefresh) {
                mSwipeRefreshLayout.setEnabled(true);
            }
        }
    }

    protected void setListDivider(@ColorRes int color) {
        mRecyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(getActivity())
                        .colorResId(color)
                        .build());
    }

    protected void setEmptyView(View view) {
        emptyView = view;
    }

    protected void addHeaderView(View header) {
        if (mQuickAdapter == null) {
            throw new RuntimeException("Please call setAdapter first.");
        }

        mQuickAdapter.addHeaderView(header);
    }

    protected void addFooterView(View footer) {
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

    protected
    @LayoutRes
    int getLayoutRes() {
        return R.layout.az_list_fragment;
    }

    protected int getPageSize() {
        return -1;
    }

    protected RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public BaseQuickAdapter getQuickAdapter() {
        return mQuickAdapter;
    }
}