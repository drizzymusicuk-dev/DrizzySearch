package com.memeitizer.search;

import android.content.Context;
import android.view.*;
import android.widget.*;
import java.util.*;

public class SearchAdapter extends BaseAdapter {
    private final Context ctx;
    private final List<Map<String, String>> data;

    public SearchAdapter(Context c, List<Map<String,String>> d) {
        this.ctx = c; this.data = d;
    }

    public int getCount() { return data.size(); }
    public Object getItem(int i) { return data.get(i); }
    public long getItemId(int i) { return i; }

    public View getView(int i, View v, ViewGroup parent) {
        if (v == null)
            v = LayoutInflater.from(ctx).inflate(R.layout.item_result, parent, false);
        Map<String,String> r = data.get(i);
        ((TextView)v.findViewById(R.id.title)).setText(r.get("title"));
        ((TextView)v.findViewById(R.id.url)).setText(r.get("url"));
        ((TextView)v.findViewById(R.id.snippet)).setText(r.get("snippet"));
        return v;
    }
}
