package xyz.ariefbayu.xyzbarcodescanner.ui.main

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.scan_image_fragment.*
import xyz.ariefbayu.xyzbarcodescanner.R

class ScanResultsFragment : Fragment() {

    companion object {
        fun newInstance() = ScanResultsFragment()
    }

    private lateinit var viewModel: ScanViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.scan_image_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ScanViewModel::class.java)
        // TODO: Use the ViewModel
       // var scantext = viewModel.getScanText()
       // message.text = scantext

        //Get Argument that passed from activity in "data" key value
        val getArgument = arguments!!.getString("scantext")
       // message.setText(getArgument) //set string over textview
    }

}