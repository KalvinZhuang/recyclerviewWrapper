package im.years.recyclerviewwrappersample.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import im.years.recyclerviewwrappersample.viewHolder.DataBindBaseViewHolder2;

/**
 * Created by Kalvin.Zhuang on 2019/1/14 9:40.
 */
public class BaseQuickDataBindingAdapter<T, Binding extends ViewDataBinding> extends BaseQuickAdapter<T, DataBindBaseViewHolder2<Binding>> {
    public BaseQuickDataBindingAdapter(int layoutResId, @Nullable List<T> data) {
        super(layoutResId, data);
    }

    public BaseQuickDataBindingAdapter(@Nullable List<T> data) {
        super(data);
    }

    public BaseQuickDataBindingAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(DataBindBaseViewHolder2<Binding> helper, T item) {

    }

    /*不是内部的 view holder 需要重写（防止泛型擦拭） */
    @Override
    protected DataBindBaseViewHolder2<Binding> createBaseViewHolder(View view) {
        return new DataBindBaseViewHolder2<>(view);
    }

    /* 重写并 设置 view Bind */
    @Override
    protected DataBindBaseViewHolder2<Binding> createBaseViewHolder(ViewGroup parent, int layoutResId) {
        Binding viewDataBinding = DataBindingUtil.inflate(mLayoutInflater, layoutResId, parent, false);
        View rootView;
        if (viewDataBinding != null) {
            rootView = viewDataBinding.getRoot();
        } else {
            rootView = getItemView(layoutResId, parent);
        }

        DataBindBaseViewHolder2<Binding> viewDataBindingDataBindBaseViewHolder = new DataBindBaseViewHolder2<>(rootView);
        viewDataBindingDataBindBaseViewHolder.setDataBinding(viewDataBinding);
        return viewDataBindingDataBindBaseViewHolder;
    }
}
