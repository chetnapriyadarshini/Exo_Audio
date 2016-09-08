package com.example.chetna_priya.saitanacyclemodule;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

/**
 * Created by chetna_priya on 9/9/2016.
 */
public class CardWatcher implements TextWatcher {
    int desiredLength=0;
    private static final char space = ' ';
    public CardWatcher(int numSize) {
        desiredLength = numSize+1;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        // Remove spacing char
        if (s.length() > 0 && (s.length() % desiredLength) == 0) {
            final char c = s.charAt(s.length() - 1);
            if (space == c) {
                s.delete(s.length() - 1, s.length());
            }
        }
        // Insert char where needed.
        if (s.length() > 0 && (s.length() % desiredLength) == 0) {
            char c = s.charAt(s.length() - 1);
            // Only if its a digit where there should be a space we insert a space
            if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(space)).length <= 3) {
                s.insert(s.length() - 1, String.valueOf(space));
            }
        }
    }
}
