package com.example.calleridv2.model;

import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class CallLogModel {
//    public TextView name;
//    public TextView number;
//    public TextView type;
//    public TextView duration;
//    public ImageButton addImageButton;
//    public Boolean isUnknown;
    String name;
    String number;
    String type;
    String duration;
    int btn;
    Boolean isKnown;

    public CallLogModel(String name, String number, String type, String duration, int btn, Boolean isKnown) {
        this.name = name;
        this.number = number;
        this.type = type;
        this.duration = duration;
        this.btn = btn;
        this.isKnown = isKnown;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getType() {
        return type;
    }

    public String getDuration() {
        return duration;
    }

    public int getBtn() {
        return btn;
    }

    public Boolean getIsKnown() {
        return isKnown;
    }

    @NonNull
    @Override
    public String toString() {
        return name+" "+type+" "+duration+" "+number+" "+isKnown;
    }
}
