package im.years.recyclerviewwrappersample.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class MultipleContentMock implements MultiItemEntity {
    public static final int left_type = 10001;
    public static final int right_type = 10002;
    private int type = left_type;
    private String title;
    private String content;

    public MultipleContentMock(int type) {
        this.type = type;
    }

    @Override
    public int getItemType() {
        return this.type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
