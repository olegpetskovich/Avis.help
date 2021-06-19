package com.pinus.alexdev.avis.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.enums.SenderChatEnums;
import com.pinus.alexdev.avis.model.ChatLocal;

import java.util.ArrayList;

public class InMessageAdapter extends  ArrayAdapter<ChatLocal> {

    private static final String TAG = InMessageAdapter.class.getSimpleName();

    public InMessageAdapter(@NonNull Context context, @NonNull ArrayList<ChatLocal> review) {
        super(context, 0, review);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ChatLocal msg = getItem(position);
        Log.v(TAG, "Your message: " + msg);

        // Check if an existing view is being reused, otherwise inflate the view
        if(msg.getSender() == SenderChatEnums.CLIENT){
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.conversation_in
                        , parent, false);
            }
            // Lookup view for data population
            Log.v(TAG, msg.getMessage());
            TextView txtMsg = (TextView) convertView.findViewById(R.id.msg);
            if(msg.getMessage()!=null && txtMsg != null)
                txtMsg.setText(msg.getMessage());
        }
        else {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.conversation_out
                            , parent, false);
                }
                // Lookup view for data population
                Log.v(TAG, msg.getMessage());

                TextView txtMsg = (TextView) convertView.findViewById(R.id.msg);
                if(msg.getMessage()!=null && txtMsg != null)
                    txtMsg.setText(msg.getMessage());
        }


        return convertView;
    }

}
