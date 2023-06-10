package com.example.eHandel.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class exampleBottomSheet(layoutMostrar: Int): BottomSheetDialogFragment() {

    var layoutBS = layoutMostrar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(layoutBS, container, false)

    }
}