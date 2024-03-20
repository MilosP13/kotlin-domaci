package com.example.kotlinelabapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.kotlinelabapp.R
import com.example.kotlinelabapp.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterFragment : Fragment() {

    private lateinit var auth: FirebaseAuth;
    private lateinit var navController: NavController;
    private lateinit var binding: FragmentRegisterBinding;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        createAccount()
    }

    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
    }

    private fun createAccount() {
        binding.authTextView.setOnClickListener {
            navController.navigate(R.id.action_registerFragment_to_loginFragment)
        }


        binding.btnRegister.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val password = binding.passEt.text.toString().trim()
            val retypedPassword = binding.retypeEt.text.toString().trim()

            if(email.isNotEmpty() && password.isNotEmpty() && retypedPassword.isNotEmpty()) {
                if(password == retypedPassword) {
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if(it.isSuccessful) {
                            Toast.makeText(context, "Account created succesfully!", Toast.LENGTH_SHORT).show()
                            navController.navigate(R.id.action_registerFragment_to_homeFragment)
                        }else {
                            Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

}