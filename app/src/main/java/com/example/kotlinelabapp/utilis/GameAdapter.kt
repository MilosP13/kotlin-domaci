package com.example.kotlinelabapp.utilis

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinelabapp.databinding.EachGameItemBinding

class GameAdapter(private val list: MutableList<GameData>):
    RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    private var listener: GameAdapterClicksInterface? = null

    fun setListener(listener: GameAdapterClicksInterface) {
        this.listener = listener
    }

    interface GameAdapterClicksInterface{
        fun onDeleteGameBtnCLicked(gameData: GameData)
        fun onEditGameBtnClicked(gameData: GameData)
    }

    inner class GameViewHolder(val binding: EachGameItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val binding = EachGameItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GameViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.game.text = this.game!!.name
                binding.watchDate.text = this.game!!.date

                binding.deleteGame.setOnClickListener {
                    listener?.onDeleteGameBtnCLicked(this)
                }

                binding.editGame.setOnClickListener {
                    listener?.onEditGameBtnClicked(this)
                }
            }
        }
    }


}