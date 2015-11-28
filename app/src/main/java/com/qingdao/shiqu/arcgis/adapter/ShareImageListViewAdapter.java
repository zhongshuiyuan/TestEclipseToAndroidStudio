package com.qingdao.shiqu.arcgis.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qingdao.shiqu.arcgis.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015-11-26.
 */
public class ShareImageListViewAdapter extends BaseAdapter {

    public static final int WECHAT = 0;
    public static final int WECHAT_TIMELINE = 1;
    public static final int QQ = 2;
    public static final int EMAIL = 3;
    public static final int DATABASE = 4;
    public static final int OTHER = 5;

    private Context mContext;

    private ArrayList<ListItem> mItems;

    public ShareImageListViewAdapter(Context context) {
        super();

        mItems = createItems();

        mContext = context;
    }

    private ArrayList<ListItem> createItems() {
        ArrayList<ListItem> items = new ArrayList<>();

        ListItem item0 = new ListItem();
        item0.id = WECHAT;
        item0.title = "发送给微信好友";
        item0.iconResId = R.drawable.ic_wechat;
        items.add(item0);

        ListItem item1 = new ListItem();
        item1.id = WECHAT_TIMELINE;
        item1.title = "分享到微信朋友圈";
        item1.iconResId = R.drawable.ic_wechat_discover;
        items.add(item1);

        ListItem item2 = new ListItem();
        item2.id = QQ;
        item2.title = "通过QQ发送到电脑";
        item2.iconResId = R.drawable.ic_qq;
        items.add(item2);

        ListItem item3 = new ListItem();
        item3.id = EMAIL;
        item3.title = "通过E-mail发送";
        item3.iconResId = R.drawable.ic_email;
        items.add(item3);

        ListItem item4 = new ListItem();
        item4.id = DATABASE;
        item4.title = "发送到数据库";
        item4.iconResId = R.drawable.ic_database_plus;
        items.add(item4);

        ListItem item5 = new ListItem();
        item5.id = OTHER;
        item5.title = "通过其他方式分享";
        item5.iconResId = R.drawable.ic_share_variant;
        items.add(item5);

        return items;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return ((ListItem) getItem(position)).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ListItem item = mItems.get(position);
        ImageView icon;
        TextView title;

        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.share_image_list_view_item, null);
            //item = new ListItem();
        } else {
            view = convertView;
            //item = (ListItem) view.getTag();
        }

        icon = (ImageView) view.findViewById(R.id.share_image_icon);
        icon.setImageResource(item.iconResId);
        title = (TextView) view.findViewById(R.id.share_image_title);
        title.setText(item.title);

        view.setTag(item);

        return view;
    }

    public class ListItem {
        public int id;
        public String title;
        public int iconResId;
    }
}
