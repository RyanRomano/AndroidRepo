package com.digitalcitadel.ryan.geostash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.digitalcitadel.ryan.geostash.DatabaseHelper;
import com.digitalcitadel.ryan.geostash.R;
import com.digitalcitadel.ryan.geostash.Stash;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by ryan on 3/8/16.
 */
public class StashAdapter extends ArrayAdapter<Stash>{
    Activity mActivity;


    public StashAdapter(Activity activity, List<Stash> objects) {
        super(activity, 0, objects);
        mActivity = activity;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        TextView descriptionValue;
        TextView latitudeValue;
        TextView longitudeValue;
        Button buttonFindStash;
        Button buttonDeleteStash;


        final Stash stashInList = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_stash, parent, false);
        }
        descriptionValue = (TextView) convertView.findViewById(R.id.stash_description);
        latitudeValue = (TextView) convertView.findViewById(R.id.latitude_value);
        longitudeValue = (TextView) convertView.findViewById(R.id.longitude_value);
        buttonFindStash = (Button) convertView.findViewById(R.id.find_a_stash_button);
        buttonDeleteStash = (Button) convertView.findViewById(R.id.delete_stash_button);
        descriptionValue.setText(stashInList.getId()+"");
        latitudeValue.setText(stashInList.getLatitude());
        longitudeValue.setText(stashInList.getLongitude());

        final double latitude = Double.parseDouble(stashInList.getLatitude());
        final double longitude = Double.parseDouble(stashInList.getLongitude());

        buttonFindStash.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CompassActivity.start(mActivity, latitude, longitude);
            }
        });

        buttonDeleteStash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(stashInList);
                DatabaseHelper db = new DatabaseHelper(getContext());
                db.removeData(stashInList.getId());
            }
        });

        return convertView;
    }
}
