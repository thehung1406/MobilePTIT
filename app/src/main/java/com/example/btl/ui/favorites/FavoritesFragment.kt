package com.example.btl.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.btl.R

class FavoritesFragment : Fragment() {

    private lateinit var favoritesRecyclerView: RecyclerView
    private lateinit var emptyFavoritesText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)
        favoritesRecyclerView = view.findViewById(R.id.favorites_recycler_view)
        emptyFavoritesText = view.findViewById(R.id.empty_favorites_text)

        // TODO: Thay thế phần này bằng logic lấy dữ liệu thật
        val favoritesListIsEmpty = true // Giả định danh sách trống

        if (favoritesListIsEmpty) {
            favoritesRecyclerView.visibility = View.GONE
            emptyFavoritesText.visibility = View.VISIBLE
        } else {
            favoritesRecyclerView.visibility = View.VISIBLE
            emptyFavoritesText.visibility = View.GONE
            // TODO: Cập nhật adapter cho RecyclerView ở đây
        }

        return view
    }
}