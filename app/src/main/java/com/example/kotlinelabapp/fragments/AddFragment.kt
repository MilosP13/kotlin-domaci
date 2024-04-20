package com.example.kotlinelabapp.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.kotlinelabapp.databinding.FragmentAddBinding
import com.example.kotlinelabapp.utilis.Game
import com.example.kotlinelabapp.utilis.GameData
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*


class AddFragment : DialogFragment() {

    interface DialogAddBtnClickListener {
        fun onSaveGame(gameName: String, gameNameEt: TextInputEditText, date: String, gameDateEt: TextInputEditText)
        fun onUpdateGame(gameData: GameData, gameNameEt: TextInputEditText, gameDateEt: TextInputEditText )
    }

    private lateinit var binding: FragmentAddBinding
    private lateinit var listener: DialogAddBtnClickListener
    private var gameData: GameData? = null
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
        fun newInstance(gameId: String, name: String, date: String) = AddFragment().apply {
            arguments = Bundle().apply {
                putString("gameId", gameId)
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
            val game = Game(name,date)
            gameData = GameData(arguments?.getString("gameId").toString(), game)
            binding.gameNameEt.setText(game.name)
            binding.gameDateEt.setText(game.date)
        }

        addGames()
    }



    private fun addGames() {

        binding.gameDateEt.setOnClickListener {
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
            val newName = binding.gameNameEt.text.toString()
            val newDate = binding.gameDateEt.text.toString()

            if(newName.isNotEmpty() && newDate.isNotEmpty()) {
                if(gameData == null) {
                    listener.onSaveGame(newName, binding.gameNameEt, newDate ,binding.gameDateEt)
                }else {
                    val newGame = Game(newName, newDate)
                    gameData?.game = newGame
                    listener.onUpdateGame(gameData!!, binding.gameNameEt, binding.gameDateEt)
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
        binding.gameDateEt.setText(sdf.format(myCalendar.time))

    }


}