package com.example.kotlinelabapp.fragments

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinelabapp.R
import com.example.kotlinelabapp.databinding.FragmentHomeBinding
import com.example.kotlinelabapp.utilis.*
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class HomeFragment : Fragment(), AddFragment.DialogAddBtnClickListener, GameAdapter.GameAdapterClicksInterface{

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var navController: NavController
    private lateinit var binding: FragmentHomeBinding
    private var popUpFragment: AddFragment? = null
    private lateinit var adapter: GameAdapter
    private lateinit var mList: MutableList<GameData>
    val CHANNEL_ID = "channelID"
    val CHANNEL_NAME = "channelName"
    private lateinit var manager: NotificationManager
    val NOTIFICATION_ID = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        init(view)
        getDataFromFirebase()
        registerEvents()
        loadData()
        binding.btnSave.setOnClickListener {
            saveData()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.logout) {
            auth.signOut()
            navController.navigate(R.id.action_homeFragment_to_loginFragment)
            return true
        }
        return true
    }

    private fun saveData() {
        val insertedText: String = binding.etText.text.toString()
        binding.gameRec.text = insertedText

        val sharedPreferences : SharedPreferences = requireContext().getSharedPreferences("sharedPrefs",
            Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = sharedPreferences.edit()
        editor.apply{
            putString("STRING_KEY", insertedText)
        }.apply()
        Toast.makeText(context,"Data saved",Toast.LENGTH_SHORT).show()
        binding.etText.setText("")
    }

    private fun loadData() {
        val sharedPreferences : SharedPreferences = requireContext().getSharedPreferences("sharedPrefs",
            Context.MODE_PRIVATE)
        val savedString: String? = sharedPreferences.getString("STRING_KEY", null)
        binding.gameRec.text = savedString
    }

    private fun getDataFromFirebase() {
        databaseRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                for (gameSnapshot in snapshot.children) {
                    val gameData = gameSnapshot.key?.let {
                        GameData(it, gameSnapshot.getValue(Game::class.java)!!)
                    }
                    val game = Game(gameData?.game!!.name, gameData.game.date)
                    if(game!=null) {
                        mList.add(gameData!!)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference.child("Games").child(auth.currentUser?.uid.toString())
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        mList = mutableListOf()
        adapter = GameAdapter(mList)
        adapter.setListener(this)
        binding.recyclerView.adapter = adapter
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT).apply {
                lightColor = Color.YELLOW
                enableLights(true)
            }
            manager = requireContext().getSystemService<NotificationManager>()!!
            manager.createNotificationChannel(channel)
        }
    }

    override fun onSaveGame(
        gameName: String,
        gameNameEt: TextInputEditText,
        date: String,
        gameDateEt: TextInputEditText
    ) {
        val game: Game = Game(gameName, date)
        databaseRef.push().setValue(game).addOnCompleteListener {

            if(it.isSuccessful) {
                val notification = NotificationCompat.Builder(requireContext(),CHANNEL_ID)
                    .setContentTitle("Game notification")
                    .setContentText("Game '${gameName}' is added")
                    .setSmallIcon(R.drawable.baseline_notifications_24)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build()

                val notificationManager = NotificationManagerCompat.from(requireContext())
                notificationManager.notify(NOTIFICATION_ID, notification)

            }else {
                Toast.makeText(context, it.exception?.message,Toast.LENGTH_SHORT).show()
            }
            gameNameEt.text = null
            gameDateEt.text = null
            popUpFragment!!.dismiss()
        }
    }

    override fun onUpdateGame(gameData: GameData, gameNameEt: TextInputEditText, gameDateEt: TextInputEditText) {
        val map = HashMap<String, Any>()
        map[gameData.gameId] = gameData.game
        databaseRef.updateChildren(map).addOnCompleteListener{
            if(it.isSuccessful) {
                Toast.makeText(context, "Game updated successfully",Toast.LENGTH_SHORT).show()
            }else {
                Toast.makeText(context, it.exception?.message,Toast.LENGTH_SHORT).show()
            }
            gameNameEt.text = null
            gameDateEt.text = null
            popUpFragment!!.dismiss()
        }
    }


    private fun registerEvents() {
        binding.addBtnHome.setOnClickListener{

            if(popUpFragment != null){
                childFragmentManager.beginTransaction().remove(popUpFragment!!).commit()
            }

            popUpFragment = AddFragment()
            popUpFragment!!.setListener(this)
            popUpFragment!!.show(childFragmentManager,AddFragment.TAG)
        }
    }


    override fun onDeleteGameBtnCLicked(gameData: GameData) {
        databaseRef.child(gameData.gameId).removeValue().addOnCompleteListener {
            if(it.isSuccessful) {
                val notification = NotificationCompat.Builder(requireContext(),CHANNEL_ID)
                    .setContentTitle("Game notification")
                    .setContentText("Game deleted")
                    .setSmallIcon(R.drawable.baseline_notifications_24)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build()

                val notificationManager = NotificationManagerCompat.from(requireContext())
                notificationManager.notify(NOTIFICATION_ID, notification)
            }else {
                Toast.makeText(context, it.exception?.message,Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEditGameBtnClicked(gameData: GameData) {
        if(popUpFragment != null)
            childFragmentManager.beginTransaction().remove(popUpFragment!!).commit()
        popUpFragment = AddFragment.newInstance(gameData.gameId, gameData.game.name!!, gameData.game.date!!)
        popUpFragment!!.setListener(this)
        popUpFragment!!.show(childFragmentManager, AddFragment.TAG)
    }




}