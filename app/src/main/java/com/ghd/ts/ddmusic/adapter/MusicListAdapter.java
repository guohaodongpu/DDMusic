package com.ghd.ts.ddmusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ghd.ts.ddmusic.R;
import com.ghd.ts.ddmusic.entity.SongSheet;

import java.util.List;

public class MusicListAdapter extends ArrayAdapter<SongSheet> {

    private int mResourceId;

    public MusicListAdapter(Context context, int textViewResourceId, List<SongSheet> objecets) {

        super(context, textViewResourceId, objecets);
        mResourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SongSheet musicList = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(mResourceId,
                parent, false);
        ImageView musicListImage = view.findViewById(R.id.music_list_image);
        TextView musicListName = view.findViewById(R.id.music_list_name);
        TextView musicListDescription = view.findViewById(R.id.music_list_description);
        musicListImage.setImageResource(musicList.getImageId());
        musicListName.setText(musicList.getName());
        musicListDescription.setText(musicList.getDescription());
        return view;
    }
}
