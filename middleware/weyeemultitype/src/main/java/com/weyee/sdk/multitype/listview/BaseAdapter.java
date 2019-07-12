package com.weyee.sdk.multitype.listview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.weyee.sdk.multitype.AdapterHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * List View 的Holder基类
 *
 * @author wuqi by 2019/5/29.
 */
public abstract class BaseAdapter<T> extends android.widget.BaseAdapter implements AdapterHelper<T> {
    private List<T> mList;

    public BaseAdapter(@Nullable List<T> datas) {
        this.mList = datas == null ? new ArrayList<>() : datas;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public T getItem(int position) {
        return mList == null ? null : (position >= mList.size() || position < 0 ? null : mList.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(getLayoutId(position), parent, false);
            viewHolder = new BaseHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (BaseHolder) convertView.getTag();
        }

        convert(viewHolder, getItem(position), position);
        return convertView;
    }

    protected abstract int getLayoutId(int position);

    protected abstract void convert(@NonNull BaseHolder viewHolder, T item, int position);

    @Override
    public boolean addAll(@Nullable List<T> list) {
        return addAll(list, false);
    }

    @Override
    public boolean addAll(@Nullable List<T> list, boolean clear) {
        if (list == null) return false;
        if (clear)
            mList.clear();
        boolean result = mList.addAll(list);
        notifyDataSetChanged();
        return result;
    }

    @Override
    public boolean addAll(int position, @Nullable List<T> list) {
        if (list == null) return false;
        if (position < 0 || position >= mList.size()) return false;
        boolean result = mList.addAll(position, list);
        notifyDataSetChanged();
        return result;
    }

    @Override
    public void add(@Nullable T data) {
        if (data == null) return;
        mList.add(data);
        notifyDataSetChanged();
    }

    @Override
    public void add(int position, @Nullable T data) {
        if (data == null) return;
        if (position < 0 || position >= mList.size()) return;
        mList.add(position, data);
        notifyDataSetChanged();
    }

    @Override
    public void clear() {
        mList.clear();
    }

    @Override
    public void clearAll() {
        mList.clear();
        notifyDataSetChanged();
    }

    @Override
    public boolean contains(T data) {
        return mList.contains(data);
    }

    @Override
    public T getData(int index) {
        if (index < 0 || index >= mList.size()) return null;
        return mList.get(index);
    }

    /**
     * 返回数据集合
     *
     * @return 数据集合
     */
    @Override
    public List<T> getAll() {
        return mList;
    }

    @Override
    public void modify(@Nullable T oldData, @Nullable T newData, @Nullable Object object) {
        modify(mList.indexOf(oldData), newData);
    }

    @Override
    public void modify(int index, @Nullable T newData) {
        if (index < 0 || index >= mList.size()) return;
        mList.set(index, newData);
        notifyDataSetChanged();
    }

    @Override
    public void remove(@Nullable T data) {
        if (data == null) return;
        mList.remove(data);
        notifyDataSetChanged();
    }

    @Override
    public void remove(int index, @Nullable Object object) {
        if (index < 0 || index >= mList.size()) return;
        mList.remove(index);
        notifyDataSetChanged();
    }

    @Override
    public void removeAll(@Nullable List<T> list) {
        if (list != null && mList.containsAll(list)) {
            mList.removeAll(list);
            notifyDataSetChanged();
        }
    }
}
