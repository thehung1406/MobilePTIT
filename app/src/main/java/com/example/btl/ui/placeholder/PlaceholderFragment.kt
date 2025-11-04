package com.example.btl.ui.placeholder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.btl.R

class PlaceholderFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_placeholder, container, false)
        val textView = view.findViewById<TextView>(R.id.placeholder_text)
        // The fragment label is passed as an argument
        textView.text = arguments?.getString("android:label")
        return view
    }
}
