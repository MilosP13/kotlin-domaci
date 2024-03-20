package com.example.kotlinelabapp.utilis

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinelabapp.databinding.EachMovieItemBinding
import com.example.kotlinelabapp.fragments.HomeFragment

class MovieAdapter (private val list: MutableList<MovieData>):
    RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    private var listener: MovieAdapterClicksInterface? = null

    fun setListener(listener: MovieAdapterClicksInterface) {
        this.listener = listener
    }

    interface MovieAdapterClicksInterface{
        fun onDeleteMovieBtnCLicked(movieData: MovieData)
        fun onEditMovieBtnClicked(movieData: MovieData)
    }

    inner class MovieViewHolder(val binding: EachMovieItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = EachMovieItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.movie.text = this.movie!!.name
                binding.watchDate.text = this.movie!!.date

                binding.deleteMovie.setOnClickListener {
                    listener?.onDeleteMovieBtnCLicked(this)
                }

                binding.editMovie.setOnClickListener {
                    listener?.onEditMovieBtnClicked(this)
                }
            }
        }
    }


}