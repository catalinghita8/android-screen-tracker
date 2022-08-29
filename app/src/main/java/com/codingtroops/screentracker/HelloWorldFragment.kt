package com.codingtroops.screentracker

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button


class HelloWorldFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hello_world, container, false)

        view.findViewById<Button>(R.id.btnOpenActivity).setOnClickListener {
            startActivity(Intent(requireActivity(), MyFragmentActivity::class.java))
        }

        return view
    }

}