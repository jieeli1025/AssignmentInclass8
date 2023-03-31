package com.example.assignment_8;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment implements View.OnClickListener{

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private Button buttonRegister;
    private FirebaseAuth mAuth;
    private IloginFragmentActions mlistener;
    private String userEmail;
    private String userPassword;
    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
           mAuth = FirebaseAuth.getInstance();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle("Login");
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        editTextEmail = rootView.findViewById(R.id.editTextEmail);
        editTextPassword = rootView.findViewById(R.id.editTextPassword);
        buttonLogin = rootView.findViewById(R.id.loginButton);
        buttonRegister = rootView.findViewById(R.id.registerButton);
        buttonLogin.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);
        return rootView;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof IloginFragmentActions) {
            mlistener = (IloginFragmentActions) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement IloginFragmentActions");
        }
    }

    @Override
    public void onClick(View view) {

            if(view.getId() == R.id.loginButton) {
                Log.d("loginFragment", "onClick: Login button clicked");
                userEmail = editTextEmail.getText().toString().trim();
                userPassword = editTextPassword.getText().toString().trim();
                if(userEmail.isEmpty() || userPassword.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter email and password", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Toast.makeText(getContext(), "Login successfully", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Login failed", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {
                                        mlistener.populateMainFragment(mAuth.getCurrentUser());
                                    }
                                }
                            });



                }
            } else if(view.getId() == R.id.registerButton) {
                Log.d("loginFragment", "onClick:  register clicked");
                mlistener.populateRegisterFragment();
            }
        }



    public interface IloginFragmentActions {
        void populateMainFragment(FirebaseUser user);
        void populateRegisterFragment();


    }
}