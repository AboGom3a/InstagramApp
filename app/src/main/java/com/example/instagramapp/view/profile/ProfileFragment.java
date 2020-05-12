package com.example.instagramapp.view.profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagramapp.R;
import com.example.instagramapp.model.Post;
import com.example.instagramapp.model.User;
import com.example.instagramapp.utils.Utilities;
import com.example.instagramapp.view.auth.LoginActivity;
import com.example.instagramapp.view.splash.SplashActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {
    private ImageView imgProfile;
    private TextView tvName;
    private TextView tvEmail;
    private Button btnLogOut,checkimg;
    private ImageButton addimage;
    private Uri imageUri;
    private Bitmap selectedImage;

    private static final int GALLERY_PICK = 100;
    private static final int GALLERY_PERMISSION = 200;


    private StorageReference mStorageRef;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private NavController navController;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view =inflater.inflate(R.layout.fragment_profile, container, false);
        setupView(view);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        setupView(view);
    }

    private void setupView(View view) {
        imgProfile = view.findViewById(R.id.profile_image);
        tvName = view.findViewById(R.id.tv_name);
        tvEmail = view.findViewById(R.id.tv_Email);
        btnLogOut = view.findViewById(R.id.btn_logout);
        addimage = view.findViewById(R.id.btn_add_image);
        checkimg = view.findViewById(R.id.btn_edit);


        btnLogOut.setOnClickListener(this);
        addimage.setOnClickListener(this);
        checkimg.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users").child(user.getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User userObject = dataSnapshot.getValue(User.class);
                tvName.setText(userObject.getName());
                tvEmail.setText(userObject.getEmail());
              /* Picasso.get()
                        .load(userObject.getImage())
                        .placeholder(R.drawable.img_placeholder)
                        .into(imgProfile);
                 */

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "" + error.toException(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_logout:
                mAuth.signOut();
                startActivity(new Intent(getActivity(), SplashActivity.class));
                break;
            case R.id.btn_add_image:
                //saveImage();
                break;
            case R.id.btn_edit:
               // checkAccessImagesPermission();
                break;
        }
    }

    private void saveImage() {
        final String imagePath = UUID.randomUUID().toString() + ".jpg";

        mStorageRef.child("postImages").child(imagePath).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mStorageRef.child("postImages").child(imagePath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageURL = uri.toString();

                        User user = new User();
                        user.setImage(imageURL);
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        saveImageToDB(currentUser.getUid(),user);
                    }
                });
            }
        });
    }

    private void saveImageToDB(String id,User user) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(id).child("image");

        myRef.setValue(user);
        Toast.makeText(getActivity(), "image added!", Toast.LENGTH_SHORT).show();

    }

    private void checkAccessImagesPermission() {
        int permission = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERMISSION);
        } else {
            getImageFromGallery();
        }
    }

    private void getImageFromGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_PICK);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == GALLERY_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getImageFromGallery();
            } else {
                Toast.makeText(getActivity(), "permission_denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                imageUri = data.getData();
                assert imageUri != null;
                InputStream imageStream = requireActivity().getContentResolver().openInputStream(imageUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);

                // base64
                // String imageBase64 = getResizedBase64(selectedImage, 100, 100);

                imgProfile.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "something_went_wrong", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getActivity(), "you_havent_picked_image", Toast.LENGTH_LONG).show();
        }
    }
}
