package im.years.recyclerviewwrappersample.viewHolder;

import android.view.View;

import com.chad.library.adapter.base.BaseViewHolder;

import androidx.databinding.ViewDataBinding;

public class DataBindBaseViewHolder2<Binding extends ViewDataBinding> extends BaseViewHolder {
    private Binding dataBinding;

    public DataBindBaseViewHolder2(View view) {
        super(view);
    }

    public Binding getDataBinding() {
        return dataBinding;
    }

    public void setDataBinding(Binding dataBinding) {
        this.dataBinding = dataBinding;
    }
}
