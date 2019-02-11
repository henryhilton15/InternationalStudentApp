package com.henryhilton.internationalstudentapp;

/**
 * Created by Alex W on 02/10/2017.
 * Adapted from CS65 sample project
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

//Adapted from CS65 sample project:
//https://github.com/mishravarun/CS65-Samples/tree/master/Camera

public class DialogFragmentHandler extends DialogFragment {

    // Different dialog IDs
    public static final int DIALOG_ID_ERROR = -1;
    public static final int DIALOG_ID_PHOTO_PICKER = 1;

    // For photo picker selection:
    public static final int ID_PHOTO_PICKER_FROM_CAMERA = 0;

    private static final String DIALOG_ID_KEY = "dialog_id";

    public static DialogFragmentHandler newInstance(int dialog_id) {
        DialogFragmentHandler frag = new DialogFragmentHandler();
        Bundle args = new Bundle();
        args.putInt(DIALOG_ID_KEY, dialog_id);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int dialog_id = getArguments().getInt(DIALOG_ID_KEY);

        final Activity parent = getActivity();

        // Setup dialog appearance and onClick Listeners
        switch (dialog_id) {
            case DIALOG_ID_PHOTO_PICKER:
                // Build picture picker dialog for choosing from camera or gallery
                AlertDialog.Builder builder = new AlertDialog.Builder(parent);
                builder.setTitle("Pick");
                // Set up click listener, firing intents open camera
                DialogInterface.OnClickListener dlistener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // Item is ID_PHOTO_PICKER_FROM_CAMERA
                        // Call the onPhotoPickerItemSelected in the parent
                        // activity, i.e., CreateUser in this case
                        ((CreateUser) parent)
                                .onPhotoPickerItemSelected(item);
                    }
                };
                // Set the item/s to display and create the dialog
                builder.setItems(R.array.ui_profile_photo_picker_items, dlistener);
                return builder.create();
            default:
                return null;
        }
    }
}