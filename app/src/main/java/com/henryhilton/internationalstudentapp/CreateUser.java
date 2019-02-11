package com.henryhilton.internationalstudentapp;

import android.Manifest;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class CreateUser extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private Button mHaveAccountButton;
    private ImageView mImageView;

    private EditText mEmailEditText;
    private EditText mNameEditText;
    private EditText mPasswordEditText;
    private EditText mCountryEditText;

    private String mName;
    private String mEmail;
    public String mPassword;
    public String mCountry;

    private Boolean mNameEmpty;
    private Boolean mEmailEmpty;
    private Boolean mPasswordEmpty;
    private Boolean mCountryEmpty;

    public static final int REQUEST_CODE_TAKE_FROM_CAMERA = 0;

    private static final String URI_INSTANCE_STATE_KEY = "saved_uri";

    private Uri mImageCaptureUri;

    private boolean mIsTakenFromCamera;

    //Checks if all fields are empty to chance clear button text (and later function when I have an account is active)
    private void notEmpty() {
        if (!(mNameEmpty && mPasswordEmpty && mEmailEmpty && mCountryEmpty)) {
            mHaveAccountButton.setText("Clear");

        } else {
            mHaveAccountButton.setText("I already have an account");
        }
    }

    //Function of clear button, clears all editTexts
    public void clear(View view) {
        if (mHaveAccountButton.getText().toString().equals("Clear")) {
            mEmailEditText.setText("");
            mNameEditText.setText("");
            mPasswordEditText.setText("");
            mCountryEditText.setText("");
            mImageView.setImageResource(R.drawable.default_profile);
        } else {
            Intent intent = new Intent(this, SignInExample.class);
            startActivity(intent);
            finish();
        }
    }

    //Ensures all camera permissions are allowed
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT < 23)
            return;

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
        }
    }

    //
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) || shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //Show an explanation to the user *asynchronously*
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("This permission is important for the app.")
                            .setTitle("Important permission required");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
                            }

                        }
                    });
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
                } else {
                    //Never ask again and handle your app without permission.
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the image capture uri before the activity goes into background
        outState.putParcelable(URI_INSTANCE_STATE_KEY, mImageCaptureUri);
    }

    public void onSaveClicked(View v) {
        // Save picture
        saveSnap();
        // Close the activity
        finish();
    }

    public void onChangePhotoClicked(View v) {
        // changing the profile image, show the dialog asking the user
        // to choose between taking a picture
        // Go to DialogFragmentHandler for details.
        displayDialog(DialogFragmentHandler.DIALOG_ID_PHOTO_PICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case REQUEST_CODE_TAKE_FROM_CAMERA:
                // Send image taken from camera for cropping
                beginCrop(mImageCaptureUri);
                break;

            case Crop.REQUEST_CROP: //We changed the RequestCode to the one being used by the library.
                // Update image view after image crop
                handleCrop(resultCode, data);

                // Delete temporary image taken by camera after crop.
                if (mIsTakenFromCamera) {
                    File f = new File(mImageCaptureUri.getPath());
                    if (f.exists())
                        f.delete();
                }

                break;
        }
    }

    //displays photo picker fragment
    public void displayDialog(int id) {
        DialogFragment fragment = DialogFragmentHandler.newInstance(id);
        fragment.show(getFragmentManager(),
                "Photo picker");
    }

    public void onPhotoPickerItemSelected(int item) {
        Intent intent;

        switch (item) {

            case DialogFragmentHandler.ID_PHOTO_PICKER_FROM_CAMERA:
                // Take photo from camera，
                // Construct an intent with action
                // MediaStore.ACTION_IMAGE_CAPTURE
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Construct temporary image path and name to save the taken
                // photo
                ContentValues values = new ContentValues(1);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                mImageCaptureUri = getContentResolver()
                        .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);


                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        mImageCaptureUri);
                intent.putExtra("return-data", true);
                try {
                    // Start a camera capturing activity
                    // REQUEST_CODE_TAKE_FROM_CAMERA is an integer tag you
                    // defined to identify the activity in onActivityResult()
                    // when it returns
                    startActivityForResult(intent, REQUEST_CODE_TAKE_FROM_CAMERA);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                mIsTakenFromCamera = true;
                break;

            default:
                return;
        }

    }

    private void loadSnap() {

        // Load profile photo from internal storage
        try {
            FileInputStream fis = openFileInput("profile_photo.png");
            Bitmap bmap = BitmapFactory.decodeStream(fis);
            mImageView.setImageBitmap(bmap);
            fis.close();
        } catch (IOException e) {
            // Default profile photo if no photo saved before.
            mImageView.setImageResource(R.drawable.default_profile);
        }
    }

    private void saveSnap() {

        // Commit all the changes into preference file
        // Save profile image into internal storage.
        mImageView.buildDrawingCache();
        Bitmap bmap = mImageView.getDrawingCache();
        try {
            FileOutputStream fos = openFileOutput(
                    "profile_photo.png", MODE_PRIVATE);
            bmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Method to start Crop activity using the library
     * Earlier the code used to start a new intent to crop the image,
     * but here the library is handling the creation of an Intent, so you don't
     * have to.
     **/
    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            mImageView.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        mAuth = FirebaseAuth.getInstance();

        //lots of initialization of UI elements
        mHaveAccountButton = findViewById(R.id.haveAccountButton);
        Button saveButton = findViewById(R.id.saveButton);

        mEmailEditText = findViewById(R.id.handleEditText);
        mNameEditText = findViewById(R.id.nameEditText);
        mPasswordEditText = findViewById(R.id.passwordEditText);
        mCountryEditText = findViewById(R.id.countryEditText);

        mImageView = findViewById(R.id.imageView);

        mPasswordEmpty = (mPasswordEditText.getText().length() == 0);
        mEmailEmpty = (mEmailEditText.getText().length() == 0);
        mNameEmpty = (mNameEditText.getText().length() == 0);
        mCountryEmpty = (mCountryEditText.getText().length() == 0);

        //Checks to see if any fields have data in, used to decide text of clear button
        notEmpty();

        //checks that all camera permissions are granted
        checkPermissions();

        //retrieves any profile picture that has been previously saved
        if (savedInstanceState != null) {
            mImageCaptureUri = savedInstanceState
                    .getParcelable(URI_INSTANCE_STATE_KEY);
        }

        //Adds text change listener to passwordEditText
        mPasswordEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                //if text changes, password is no longer matched and notEmpty checks to see if the filed is empty to change clear button text
                mPasswordEmpty = (mPasswordEditText.getText().length() == 0);
                notEmpty();
                mPassword = mPasswordEditText.getText().toString();
            }
        });

        //adds text change listener to handleEditText.
        mEmailEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                //checks if field empty
                mEmail = mEmailEditText.getText().toString();
                mEmailEmpty = (mEmailEditText.getText().length() == 0);
                notEmpty();

            }
        });

        //adds text change listener to nameEditText
        mNameEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                mName = mNameEditText.getText().toString();
                mNameEmpty = (mNameEditText.getText().length() == 0);
                notEmpty();

            }
        });

        //adds text change listener to nameEditText
        mCountryEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                mCountry = mCountryEditText.getText().toString();
                mCountryEmpty = (mCountryEditText.getText().length() == 0);
                notEmpty();
            }
        });

    }

    //functionality of save button
    public void save(View view) {

        //checks to see if all data has been entered correctly
        if (mEmailEmpty) Toast.makeText(this, "Please add an email!", Toast.LENGTH_SHORT).show();

        else if (mNameEmpty) Toast.makeText(this, "Please add a name!", Toast.LENGTH_SHORT).show();

        else if (mPasswordEmpty)
            Toast.makeText(this, "Please add a password!", Toast.LENGTH_SHORT).show();

        else if (mCountryEmpty)
            Toast.makeText(this, "Please add a Country of Origin!", Toast.LENGTH_SHORT).show();

        else {
            saveSnap();

            Toast.makeText(this, "Creating Account", Toast.LENGTH_SHORT).show();

            firebaseSignUp(mEmail,mPassword);
        }
    }

    public void firebaseSignUp(String email, String password){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("firebase", "createUserWithEmail:success");

                            FirebaseUser user = mAuth.getCurrentUser();
                           // saveData();
                            updateFirebase();
                            launchIntent();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("firebase", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Cannot Create Account:   " + task.getException(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


//    public void saveData(){
//        //if all data is entered correctly, data is saved to the device
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        SharedPreferences.Editor editor = preferences.edit();
//
//        // Edit the saved preferences
//        editor.putString("Email", mEmail);
//        editor.putString("Name", mName);
//        editor.putString("Password", mPassword);
//        editor.putString("Country", mCountry);
//        editor.commit();
//    }

    public void updateFirebase(){

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference name = database.getReference("profile/fullName");
        name.setValue(mName);

        DatabaseReference email = database.getReference("profile/email");
        email.setValue(mEmail);

        DatabaseReference country = database.getReference("profile/country");
        country.setValue(mCountry);
    }

    public void launchIntent(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}