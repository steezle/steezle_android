package com.steezle.e_com.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.iceteck.silicompressorr.SiliCompressor;
import com.steezle.e_com.model.User;
import com.steezle.e_com.R;
import com.steezle.e_com.adapter.SettingPagerAdapter;

import com.steezle.e_com.networking.AppSharedPreferences;
import com.steezle.e_com.services.APIController;
import com.steezle.e_com.services.CustomMultipartRequest;
import com.steezle.e_com.utils.Constant;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONObject;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends SettingPagerAdapter implements View.OnClickListener, Response.ErrorListener,
        Response.Listener<String> {

    private static final String TAG = ProfileFragment.class.getSimpleName();

    private ProgressBar progressBar;
    private View mainFragmentView;
    private EditText edt_fullName, edt_profile_email;
    private RequestQueue requestQueue1;
    private CircleImageView iv_userProfile;
    private User users;
    private LinearLayout rl_btn_update;
    private Uri imageUri=null;
    private MultipartEntity multipartRequest;
    private File profilePicFile = null;

    final int REQUEST_CODE_GALLERY = 1;
    final int REQUEST_IMAGE_CAPTURE = 3;

    private LinearLayout ll_male, ll_female;
    private ImageView iv_male, iv_female;
    private TextView tv_male, tv_female;
    private String selectedGender;

    private static final String USER_DETAIL_URL = Constant.BASE_URL + Constant.EditProfile.EDIT_PROFILE_URL;

    @Override
    protected View initView() {
        return null;
    }



    @Override
    protected View initView(User user) {

        mainFragmentView = View.inflate(mContext, R.layout.act_profile, null);
        requestQueue1 = Volley.newRequestQueue(getActivity());

        //Initlization
        setUpView(mainFragmentView);
        users = AppSharedPreferences.getSharePreference(getActivity()).getUser();
        readDetailUser();


        //Register Button Click Event
        rl_btn_update.setOnClickListener(this);
        iv_userProfile.setOnClickListener(this);
        ll_male.setOnClickListener(this);
        ll_female.setOnClickListener(this);


        String gender = users.getGender();

        if (gender.equals("M")) {

            iv_male.setColorFilter(/*Color.BLACK*/Color.parseColor("#000000"));
            tv_male.setTextColor(/*Color.BLACK*/Color.parseColor("#000000"));
            iv_female.setColorFilter(/*Color.GRAY*/Color.parseColor("#808080"));
            tv_female.setTextColor(/*Color.GRAY*/Color.parseColor("#808080"));

            selectedGender = "M";

        } else if (gender.equals("F")) {

            iv_female.setColorFilter(/*Color.BLACK*/Color.parseColor("#000000"));
            tv_female.setTextColor(/*Color.BLACK*/Color.parseColor("#000000"));
            iv_male.setColorFilter(/*Color.GRAY*/Color.parseColor("#808080"));
            tv_male.setTextColor(/*Color.GRAY*/Color.parseColor("#808080"));

            selectedGender = "F";

        } else if (gender.equals("U")) {

            iv_female.setColorFilter(/*Color.GRAY*/Color.parseColor("#808080"));
            tv_female.setTextColor(/*Color.GRAY*/Color.parseColor("#808080"));
            iv_male.setColorFilter(/*Color.GRAY*/Color.parseColor("#808080"));
            tv_male.setTextColor(/*Color.GRAY*/Color.parseColor("#808080"));

            selectedGender = "U";
        }

        return mainFragmentView;
    }


    private void setUpView(View view) {

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        edt_fullName = (EditText) view.findViewById(R.id.edt_fullName);
        edt_profile_email = (EditText) view.findViewById(R.id.edt_profile_email);
        iv_userProfile = (CircleImageView) view.findViewById(R.id.iv_userProfile);
        rl_btn_update = (LinearLayout) view.findViewById(R.id.rl_btn_update);

        ll_male = (LinearLayout) view.findViewById(R.id.ll_male);
        iv_male = (ImageView) view.findViewById(R.id.iv_male);
        tv_male = (TextView) view.findViewById(R.id.tv_male);
        ll_female = (LinearLayout) view.findViewById(R.id.ll_female);
        iv_female = (ImageView) view.findViewById(R.id.iv_female);
        tv_female = (TextView) view.findViewById(R.id.tv_female);
    }



    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.ll_male:
                iv_male.setColorFilter(Color.BLACK);
                tv_male.setTextColor(Color.BLACK);
                iv_female.setColorFilter(Color.GRAY);
                tv_female.setTextColor(Color.GRAY);
                selectedGender = "M";
                break;

            case R.id.ll_female:
                iv_female.setColorFilter(Color.BLACK);
                tv_female.setTextColor(Color.BLACK);
                iv_male.setColorFilter(Color.GRAY);
                tv_male.setTextColor(Color.GRAY);
                selectedGender = "F";
                break;



            case R.id.iv_userProfile:
                if (requestPermission()) {
                    selectPhotoDialog();
                }
                break;

            case R.id.rl_btn_update:

                if (edt_fullName.getText().toString().length()>0) {
                    updateProfile();
                }
                else{
                    Toast.makeText( getActivity(),"Enter Your Name",Toast.LENGTH_SHORT ).show();
                }

                break;
        }
    }




    private void readDetailUser() {

        edt_fullName.setText(users.getFirst_name());
        edt_profile_email.setText(users.getUser_email());

        if(users.getProfile_pic() != null && users.getProfile_pic().length() > 0) {

            Glide.with(getActivity())
                    .load(users.getProfile_pic())
                    .thumbnail(0.1f)
                    .apply(new RequestOptions()
                    .placeholder(R.drawable.ic_profile_steezle)
                    .error(R.drawable.ic_profile_steezle))
                    .into(iv_userProfile);
        }
    }


    @Override
    protected void initData() {
        super.initData();

    }


    @Override
    public void onResume() {
        super.onResume();
    }



    private void updateProfile() {
        progressBar.setVisibility(View.VISIBLE);
        try {

            multipartRequest = new MultipartEntity();

            try{
                if (imageUri != null && getRealPathFromURI(imageUri) != null && getRealPathFromURI(imageUri).length() > 0) {

                    profilePicFile = new File(SiliCompressor.with(getActivity()).compress(getRealPathFromURI(imageUri), new File(Constant.APP_DIR)));
                    FileBody fileBody = new FileBody(profilePicFile);
                    multipartRequest.addPart(Constant.EditProfile.PROFILE_PIC, fileBody);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            Log.e( "users id",""+users.getUser_id() );

            multipartRequest.addPart(Constant.EditProfile.USER_ID, new StringBody(users.getUser_id()));
            multipartRequest.addPart(Constant.EditProfile.NAME, new StringBody(edt_fullName.getText().toString()));
            multipartRequest.addPart(Constant.EditProfile.CONTACT_NUMBER, new StringBody(""));
            multipartRequest.addPart(Constant.EditProfile.PASSWORD, new StringBody(""));
            multipartRequest.addPart(Constant.EditProfile.GENDER, new StringBody(selectedGender));
            users.setFirst_name(edt_fullName.getText().toString());
            users.setGender(selectedGender);
            try{
                users.setProfile_pic(profilePicFile.toString());
            }catch (Exception e){
                e.printStackTrace();
            }
            AppSharedPreferences.getSharePreference(getContext()).setUser(users);

        } catch (Exception e) {
            e.printStackTrace();
            progressBar.setVisibility(View.GONE);
        }

        CustomMultipartRequest req = new CustomMultipartRequest(
                USER_DETAIL_URL,
                this,
                this,
                multipartRequest);

        req.setShouldCache(false);
        APIController.getInstance(getActivity()).addRequest(req, TAG);


    }



    @Override
    public void onErrorResponse(VolleyError error) {

        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onResponse(String response) {

        progressBar.setVisibility(View.GONE);
        if (response != null) {
            Log.e(TAG, "EditProfileRes--->" + response);

            try {

                JSONObject object = new JSONObject(response);

                if (object.getString(Constant.EditProfile.STATUS).equals("S")) {

                    progressBar.setVisibility(View.GONE);
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setMessage( ""+object.getString(Constant.EditProfile.MESSAGE));
                    alertDialog.setCancelable( false );
                    alertDialog.setTitle( "Steezle" );
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();

                } else {
                    progressBar.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                e.printStackTrace();
                progressBar.setVisibility(View.GONE);
            }


        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean requestPermission() {

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};

        if (!hasPermissions(getActivity(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
            return false;
        }
        return true;
    }


    //select photo dialog
    private void selectPhotoDialog() {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (options[i].equals("Take Photo")) {

                    takePhoto();

                } else if (options[i].equals("Choose from Gallery")) {

                    pickPhoto();

                } else if (options[i].equals("Cancel")) {

                    dialogInterface.dismiss();
                }
            }

        });
        builder.show();
    }

    //take photo from camera
    private void takePhoto() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, ".jpg");
        imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intentPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentPicture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        getActivity().startActivityForResult(intentPicture, REQUEST_IMAGE_CAPTURE);

    }


    //photo from gallery
    private void pickPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        getActivity().startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            if(getActivity() != null) {
                if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {

                    imageUri = data.getData();

                    Log.e( "imageUri","Inside gallery" );
                    Bitmap imageBitmap = SiliCompressor.with(getActivity()).getCompressBitmap(imageUri.toString());
                    iv_userProfile.setImageBitmap(imageBitmap);
//                    iv_userProfile.setImageURI(imageUri);

                } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

                    Log.e( "imageUri","Inside Capture "+imageUri.toString() );
//                    Bitmap imageBitmap = SiliCompressor.with(getActivity()).getCompressBitmap(imageUri.toString());
//                    iv_userProfile.setImageBitmap(imageBitmap);
                    iv_userProfile.setImageURI(imageUri);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public String getRealPathFromURI(Uri contentUri) {
        String path = null;
        String[] proj = {MediaStore.MediaColumns.DATA};
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            path = cursor.getString(column_index);
        }
        cursor.close();
        return path;
    }

}