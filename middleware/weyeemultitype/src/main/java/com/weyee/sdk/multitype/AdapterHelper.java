package com.weyee.sdk.multitype;

import androidx.annotation.Nullable;

import java.util.List;

/**
 * @author wuqi by 2019/3/21.
 */
public interface AdapterHelper<T> {

    boolean addAll(@Nullable List<T> list);

    boolean addAll(@Nullable List<T> list, boolean clear);

    boolean addAll(int position, @Nullable List<T> list);

    void add(@Nullable T data);

    void add(int position, @Nullable T data);

    void clear();

    void clearAll();

    boolean contains(@Nullable T data);

    T getData(int index);

    List<T> getAll();

    void modify(@Nullable T oldData, @Nullable T newData, @Nullable Object object);

    void modify(int index, @Nullable T newData);

    void remove(@Nullable T data);

    void remove(int index, @Nullable Object object);

    void removeAll(@Nullable List<T> list);
}

