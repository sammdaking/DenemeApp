package com.example.denemeapp.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.denemeapp.R;
import com.example.denemeapp.databinding.FragmentTakeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TakeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TakeFragment extends Fragment {

    ArrayList<Post> postArrayList;
    private FirebaseStorage firebaseStorage ;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private FragmentTakeBinding bindingTake;
    PostAdapter postAdapter;
    

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TakeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TakeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TakeFragment newInstance(String param1, String param2) {
        TakeFragment fragment = new TakeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postArrayList = new ArrayList<>();
        postAdapter =new PostAdapter(postArrayList);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();

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
                        System.out.println(email+description+salary+title+downloadurl);
                    }

                    postAdapter.notifyDataSetChanged();

                }
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bindingTake = FragmentTakeBinding.inflate(inflater,container,false);
        // Inflate the layout for this fragment
         View view = inflater.inflate(R.layout.fragment_take,container,false);
         return bindingTake.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // DELETE BUTONU
        // Button button = view.findViewById(R.id.buttonDelete);
       /*
        }); */


        bindingTake.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postAdapter = new PostAdapter(postArrayList);
        bindingTake.recyclerView.setAdapter(postAdapter);
        getData();



    }

   public void deleteData(){


    }
}