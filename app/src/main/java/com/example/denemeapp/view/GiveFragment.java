package com.example.denemeapp.view;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.denemeapp.R;
import com.example.denemeapp.databinding.FragmentGiveBinding;
import com.example.denemeapp.databinding.FragmentTakeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GiveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GiveFragment extends Fragment {
    ArrayList<Post> postArrayList;
    private FirebaseStorage firebaseStorage ;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    PostAdapter postAdapter;


    private FragmentGiveBinding binding;
    Uri imageData;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GiveFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GiveFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GiveFragment newInstance(String param1, String param2) {
        GiveFragment fragment = new GiveFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerLauncher();
        postArrayList = new ArrayList<>();
        postAdapter =new PostAdapter(postArrayList);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

 //       binding = FragmentGiveBinding.inflate(getLayoutInflater());
   //     View view = binding.getRoot();



        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();
        getData();

    }

    private void getData() {

        firebaseFirestore.collection("Post").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error != null){

                        Toast.makeText(getContext(),error.getLocalizedMessage(),Toast.LENGTH_LONG).show();

                    }


                    if (value != null){
                        for( DocumentSnapshot snapshot : value.getDocuments()){
                            Map<String,Object> data = snapshot.getData();
                            String description = (String) data.get("description");
                            String downloadurl = (String) data.get("downloadurl");
                            String email = (String) data.get("email");
                            String salary = (String) data.get("salary");
                            String title = (String) data.get("title");
                            Post post = new Post(description,downloadurl,email,salary,title);
                            postArrayList.add(post);


                        }

                        postAdapter.notifyDataSetChanged();

                    }
            }
        });


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentGiveBinding.inflate(inflater,container,false);
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_give, container, false);
        return binding.getRoot();


    }





    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button button = view.findViewById(R.id.btn_setData);
        ImageView imageView = view.findViewById(R.id.setImageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setImage(view);
            }
        });


            button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // push tuşuna basınca firebase e verileri kaydeden fonksiyon
                if(imageData!= null){
                    UUID uuid = UUID.randomUUID();
                    String imageName = "images/" + uuid + ".png";

                    storageReference.child(imageName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Download url alma
                                StorageReference newStorageReference = firebaseStorage.getReference(imageName);
                                newStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();
                                    String tittle = binding.edtexTittle.getText().toString();
                                    String description = binding.edtexDescription.getText().toString();
                                    // salary string olarak tutuluyo hatalı
                                    String salary = binding.edtexSalary.getText().toString();
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    String email = user.getEmail();

                                    HashMap<String,Object> postData = new HashMap<>();
                                    postData.put("title",tittle);
                                    postData.put("description",description);
                                    postData.put("salary",salary);
                                    postData.put("downloadurl",downloadUrl);
                                    postData.put("email",email);
                                    postData.put("date", FieldValue.serverTimestamp());



                                    firebaseFirestore.collection("Post").add(postData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Toast.makeText(getContext(),"Added Data on Firebase",Toast.LENGTH_SHORT).show();



                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            });


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }

                //    DocumentReference documentReference = firebaseFirestore.collection("Post").document();


                DocumentReference docRef = firebaseFirestore.collection("cities").document("SF");
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });

            }
        });





    }

   // private  void getData{



    //}

    private void registerLauncher(){
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK)
                    {
                        Intent intentFromResult = result.getData();
                        if(intentFromResult != null)
                        {
                             imageData =intentFromResult.getData();
                             binding.setImageView.setImageURI(imageData); // First method

                        }
                    }

            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                 if(result){
                    Intent intentToGalery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGalery);
                 }
                 else {
                     // get context i chech et
                     Toast.makeText(getContext(),"Permission Needed", Toast.LENGTH_SHORT).show();
                 }
            }
        });
    }

     // Imageview a basınca galeriden fotoğraf almayı sağlayan izin kodları"
    public  void setImage (View view){
    // buraya dikkat
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_DENIED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                Snackbar.make(view,"Permision needed galeri",Snackbar.LENGTH_INDEFINITE).setAction("Give permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // ask permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();
            }       else {// ask permission
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

        } else {
            Intent intentToGalery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGalery);

        }

    }

}