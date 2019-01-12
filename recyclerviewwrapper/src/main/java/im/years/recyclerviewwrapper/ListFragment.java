package im.years.recyclerviewwrapper;

import android.os.Looper;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import im.years.recyclerviewwrapper.decoration.HorizontalDividerItemDecoration;

public abstract class ListFragment extends NewListFragment {

    protected void refresh(@Nullable final List newData) {
        final BaseQuickAdapter mQuickAdapter = getQuickAdapter();
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
}
