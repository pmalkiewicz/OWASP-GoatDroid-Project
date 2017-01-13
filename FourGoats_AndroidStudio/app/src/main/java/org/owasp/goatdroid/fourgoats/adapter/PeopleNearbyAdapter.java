package org.owasp.goatdroid.fourgoats.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.owasp.goatdroid.fourgoats.R;

import java.sql.Timestamp;


public class PeopleNearbyAdapter extends ArrayAdapter<String> {
    private final Activity activity;
    private final String[] values;

    static class ViewHolder {
        public TextView name;
        public TextView distance;
        public TextView lastSeen;
    }

    public PeopleNearbyAdapter(Activity activity, String[] values) {
        super(activity, R.layout.people_nearby_item, values);
        this.activity = activity;
        this.values = values;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.people_nearby_item, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.name = (TextView) rowView.findViewById(R.id.name);
            viewHolder.distance = (TextView) rowView.findViewById(R.id.distance);
            viewHolder.lastSeen = (TextView) rowView.findViewById(R.id.lastSeen);
            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        String s = values[position];

        String[] tmp;
        String delimiter = "\n";
        tmp = s.split(delimiter);
        if (tmp.length >= 4) {
            holder.name.setText(tmp[1]);
            holder.distance.setText("Distance: " + convertDistance(tmp[2]));
            holder.lastSeen.setText("Last seen: " + convertTimestampToMinutesAgo(tmp[3])
                    + " minutes ago");
        }

        return rowView;
    }

    private String convertTimestampToMinutesAgo(String str) {
        Timestamp timestamp = Timestamp.valueOf(str);
        long currentTimeMilis = System.currentTimeMillis();
        long timestampMilis = timestamp.getTime();
        long diff = currentTimeMilis - timestampMilis;

        return Long.toString(Math.round(diff / (60 * 1000)));
    }

    private String convertDistance(String str) {
        Double distanceInM = Double.valueOf(str);

        if (distanceInM > 1000) {
            Double distanceInKm = distanceInM/1000;
            return Long.toString(Math.round(distanceInKm)) + " km";
        } else {
            return Long.toString(Math.round(distanceInM)) + " m";
        }
    }
}
