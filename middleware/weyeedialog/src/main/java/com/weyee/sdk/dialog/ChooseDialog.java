package com.weyee.sdk.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.*;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;

import java.util.List;

/**
 * @author wuqi by 2019-06-18.
 */
public class ChooseDialog extends BaseDialog {
    private TextView tvTitle;
    private MAdapter adapter;
    private Callback listener;
    private boolean isMultiple; // 是否是多选框


    public ChooseDialog(@NonNull Context context) {
        super(context, R.style.QMUI_ChooseDialog);
        initView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initView() {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.mrmo_choose_dialog, null, false);
        setContentView(inflate);
        setViewLocation();

        tvTitle = inflate.findViewById(R.id.tvTitle);

        ListView listView = inflate.findViewById(R.id.listView);
        adapter = new MAdapter(null);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            dismiss();
            ChooseItemModel model = adapter.getItem(position);
            if (model != null) {
                if (!isMultiple) {
                    // 去除重复点击
                    if (model.choose) {
                        return;
                    }
                    for (ChooseItemModel itemModel : adapter.getList()) {
                        if (itemModel == model) {
                            itemModel.choose = true;
                        } else {
                            itemModel.choose = false;
                        }
                    }
                } else {
                    model.choose = !model.choose;
                }
            }
            if (listener != null) {
                listener.onItemClick(model);
            }
        });
    }

    public void setMultiple(boolean multiple) {
        isMultiple = multiple;
    }

    public void setTitle(String title) {
        if (tvTitle != null) tvTitle.setText(title);
    }

    public void setNewData(List<ChooseItemModel> list) {
        if (adapter != null) adapter.setList(list);
    }

    public void setOnItemClickListener(Callback listener) {
        this.listener = listener;
    }

    @SuppressLint("RtlHardcoded")
    @Override
    protected void setViewLocation() {
        Window window = this.getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
    }

    private class MAdapter extends BaseAdapter {

        private List<ChooseItemModel> mList;

        MAdapter(List<ChooseItemModel> mList) {
            this.mList = mList;
        }

        public void setList(List<ChooseItemModel> mList) {
            this.mList = mList;
            notifyDataSetChanged();
        }

        public List<ChooseItemModel> getList() {
            return mList;
        }

        @Override
        public int getCount() {
            return mList == null ? 0 : mList.size();
        }

        @Override
        public ChooseItemModel getItem(int position) {
            return mList == null ? null : mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mrmo_choose_item, parent, false);
            TextView tvTitle = convertView.findViewById(R.id.tvTitle);
            ImageView ivChoose = convertView.findViewById(R.id.ivChoose);
            ChooseItemModel model = getItem(position);
            tvTitle.setText(model.title);
            ivChoose.setImageResource(model.choose ? (model.chooseIcon == 0 ? R.drawable.mrmo_icon_choose : model.chooseIcon) : (model.unchooseIcon == 0 ? R.drawable.mrmo_icon_unchoose : model.unchooseIcon));
            return convertView;
        }
    }

    public static class ChooseItemModel {
        private int chooseIcon; // 默认 mrmo_icon_choose.png
        private int unchooseIcon;   // 默认 mrmo_icon_unchoose.png

        private boolean choose;
        private String title;

        public ChooseItemModel(boolean choose, String title) {
            this.choose = choose;
            this.title = title;
        }

        public ChooseItemModel(int chooseIcon, int unchooseIcon, boolean choose, String title) {
            this.chooseIcon = chooseIcon;
            this.unchooseIcon = unchooseIcon;
            this.choose = choose;
            this.title = title;
        }

        public boolean isChoose() {
            return choose;
        }

        public String getTitle() {
            return title;
        }
    }

    public interface Callback {
        void onItemClick(ChooseItemModel item);
    }
}
