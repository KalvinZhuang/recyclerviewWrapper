package im.years.recyclerviewwrappersample.viewHolder;

import android.view.View;

import com.chad.library.adapter.base.BaseViewHolder;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

public class DataBindBaseViewHolder<Binding extends ViewDataBinding> extends BaseViewHolder {
    private Binding dataBinding;

    public DataBindBaseViewHolder(View view) {
        super(view);
        dataBinding = DataBindingUtil.bind(view);
    }

    public Binding getDataBinding() {
        return dataBinding;
    }
}
