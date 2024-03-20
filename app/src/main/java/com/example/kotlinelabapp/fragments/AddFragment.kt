package com.example.kotlinelabapp.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.kotlinelabapp.R
import com.example.kotlinelabapp.databinding.FragmentAddBinding
import com.example.kotlinelabapp.utilis.Movie
import com.example.kotlinelabapp.utilis.MovieData
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*


class AddFragment : DialogFragment() {

    interface DialogAddBtnClickListener {
        fun onSaveMovie(movieName: String, movieNameEt: TextInputEditText, date: String, movieDateEt: TextInputEditText)
        fun onUpdateMovie(movieData: MovieData, movieNameEt: TextInputEditText, movieDateEt: TextInputEditText )
    }

    private lateinit var binding: FragmentAddBinding
    private lateinit var listener: DialogAddBtnClickListener
    private var movieData: MovieData? = null
    private val myCalendar= Calendar.getInstance()


    fun setListener(listener: DialogAddBtnClickListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        const val TAG = "AddFragment"

        @JvmStatic
        fun newInstance(movieId: String, name: String, date: String) = AddFragment().apply {
            arguments = Bundle().apply {
                putString("movieId", movieId)
                putString("name", name)
                putString("date", date)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(arguments != null) {
            val name = arguments?.getString("name").toString()
            val date = arguments?.getString("date").toString()
            val movie = Movie(name,date)
            movieData = MovieData(arguments?.getString("movieId").toString(), movie)
            binding.movieNameEt.setText(movie.name)
            binding.movieDateEt.setText(movie.date)
        }

        addMovies()
    }



    private fun addMovies() {

        binding.movieDateEt.setOnClickListener {
            val datePicker = DatePickerDialog.OnDateSetListener{
                view, year, month, dayOfMonth ->
                myCalendar.set(Calendar.YEAR,year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLabel(myCalendar)
            }
            DatePickerDialog(requireContext(), datePicker,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.btnAdd.setOnClickListener{
            val newName = binding.movieNameEt.text.toString()
            val newDate = binding.movieDateEt.text.toString()

            if(newName.isNotEmpty() && newDate.isNotEmpty()) {
                if(movieData == null) {
                    listener.onSaveMovie(newName, binding.movieNameEt, newDate ,binding.movieDateEt)
                }else {
                    val newMovie = Movie(newName, newDate)
                    movieData?.movie = newMovie
                    listener.onUpdateMovie(movieData!!, binding.movieNameEt, binding.movieDateEt)
                }
            }else {
                Toast.makeText(context, "All fields required",Toast.LENGTH_SHORT).show()
            }
        }

        binding.close.setOnClickListener {
            dismiss()
        }

    }

    private fun updateLabel(myCalendar: Calendar) {
        val myFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.UK)
        binding.movieDateEt.setText(sdf.format(myCalendar.time))

    }


}