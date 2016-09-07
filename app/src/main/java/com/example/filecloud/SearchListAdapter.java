package com.example.filecloud;

import android.app.LauncherActivity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Developer on 6/27/2016.
 */
public class SearchListAdapter extends BaseAdapter {
    private Context mContext;
    private List<FileInfoClass> listItems;
    public SearchListAdapter() {
    }

    public SearchListAdapter(Context mContext, List<FileInfoClass> listItems) {
        this.mContext = mContext;
        this.listItems = listItems;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setListItems(List<FileInfoClass> listItems) {
        this.listItems = listItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.search_list_item,null);
        TextView itemIcon = (TextView)view.findViewById(R.id.search_list_icon);
        TextView fileName = (TextView)view.findViewById(R.id.text_search_file);
        TextView dirPath = (TextView)view.findViewById(R.id.fullpath_search_file);
        Button goFolderBtn = (Button)view.findViewById(R.id.btn_go_folder);
        FileInfoClass item = listItems.get(position);
        fileName.setText(item.getName());
        dirPath.setText(item.getDirpath());
        if (item.getType().equals("file"))
            itemIcon.setBackgroundResource(R.drawable.icon_file);
        else itemIcon.setBackgroundResource(R.drawable.icon_dir);
        return view;
    }
}
