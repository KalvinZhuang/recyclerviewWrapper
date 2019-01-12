package im.years.recyclerviewwrappersample.viewHolder;

import android.view.View;

import com.chad.library.adapter.base.BaseViewHolder;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

public class DataBindBaseViewHolder<T extends ViewDataBinding> extends BaseViewHolder {
    private T dataBinding;

    public DataBindBaseViewHolder(View view) {
        super(view);

        T bind = DataBindingUtil.bind(view);
    }

    public T getDataBinding() {
        return dataBinding;
    }
}
