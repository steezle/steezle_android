package com.steezle.e_com.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;

import com.bumptech.glide.request.RequestOptions;
import com.steezle.e_com.R;
import com.steezle.e_com.model.SubCategoryItem;
import com.steezle.e_com.utils.ProjectUtility;
import com.steezle.e_com.view.Productlist;
import com.steezle.e_com.view.TabActivity;


public class SubCategoryExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<SubCategoryItem> _listDataHeader;
    private HashMap<String, List<SubCategoryItem>> _listDataChild;
    private ImageLoader imageLoader;
    int Count=0;

    public SubCategoryExpandableListAdapter(
            Context context,
            List<SubCategoryItem> listDataHeader,
            HashMap<String, List<SubCategoryItem>> listChildData) {

        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public SubCategoryItem getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition).getId()).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final SubCategoryItem childText = (SubCategoryItem) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.sub_category_item, null);
        }
        TextView tv_productName = (TextView) convertView.findViewById(R.id.tv_productName);

        String s = ProjectUtility.toCamelCaseWord(childText.getSubName());
        tv_productName.setText(s);

        convertView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e( "SubCatExpandListAdp","convertview" );
                        Intent i=new Intent( _context, Productlist.class );

                        i.putExtra("ids", childText.getSubId());
                        i.putExtra("id", childText.getSubId());
                        i.putExtra("productname", childText.getSubName());
                        _context.startActivity( i );
                    }
                });

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        Log.e( "Inside","groupPosition: "+groupPosition );
        if (this._listDataChild.get(this._listDataHeader.get(groupPosition).getId()).size() > 0) {

            return this._listDataChild.get(this._listDataHeader.get(groupPosition).getId()).size();
        }

        else
            {
                Log.e( "SubCatExpandListAdp","else" );
                Intent i=new Intent( _context,Productlist.class );
                boolean s = true;
                i.putExtra("s", s);
                i.putExtra( "productname", this._listDataHeader.get(groupPosition).getName());
                i.putExtra("id", this._listDataHeader.get(groupPosition).getId());
                i.putExtra("ids", this._listDataHeader.get(groupPosition).getId());
                if (Count>0){
                    i.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_CLEAR_TASK );
                }

                _context.startActivity( i );
                Count++;
                return 0;
        }

    }

    @Override
    public SubCategoryItem getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        //Toast.makeText(_context,"getGroupView", Toast.LENGTH_LONG).show();

        SubCategoryItem headerTitle = (SubCategoryItem) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.sub_category_list_item, null);
        }

        ImageView iv_subListImage = (ImageView) convertView.findViewById(R.id.iv_subListImage);

        Glide.with(_context)
                .load(headerTitle.getImage())
                .thumbnail(1.0f)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.empty_menu))
                .into(iv_subListImage);


        TextView tv_brandName = (TextView) convertView.findViewById(R.id.tv_brandName);
        ImageView iv_groupIndicator = (ImageView) convertView.findViewById(R.id.iv_groupIndicator);

        Log.e("groupPosition",""+groupPosition);
//        if (headerTitle.getName().equals("VIEW ALL")) {
            if (groupPosition==0) {
            iv_groupIndicator.setImageResource(R.drawable.next);
        } else {
            iv_groupIndicator.setImageResource(R.drawable.down);
        }

        //tv_brandName.setTypeface(null, Typeface.BOLD);
        tv_brandName.setTextSize(14);
        //String s = ProjectUtility.toCamelCaseWord(headerTitle.getName());
        tv_brandName.setText(headerTitle.getName());

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}