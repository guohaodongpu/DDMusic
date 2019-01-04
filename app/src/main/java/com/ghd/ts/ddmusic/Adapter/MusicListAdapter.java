package com.ghd.ts.ddmusic.Adapter;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ghd.ts.ddmusic.R;
import com.ghd.ts.ddmusic.entity.MusicList;

import java.util.List;

public class MusicListAdapter extends ArrayAdapter<MusicList> {

    private  int resourceId;

    public MusicListAdapter(Context context, int textViewResourceId, List<MusicList> objecets){

        super(context, textViewResourceId,objecets);
        resourceId=textViewResourceId;
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent) {
        MusicList musicList=getItem(position);
        View view=LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        ImageView musicListImage=(ImageView) view.findViewById(R.id.music_list_image);
        TextView musicListName=(TextView) view.findViewById(R.id.music_list_name);
        TextView musicListDescription=view.findViewById(R.id.music_list_description);
        musicListImage.setImageResource(musicList.getImageId());
        musicListName.setText(musicList.getName());
        musicListDescription.setText(musicList.getDescription());
        return view;
    }
}
