package org.cchao.kotlintemplate.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.cchao.common.ui.base.BaseFragment
import org.cchao.common.utils.L
import org.cchao.kotlintemplate.R

class MainFragment : BaseFragment() {

    companion object {
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }

    override fun bindLayout() = R.layout.fragment_main

    override fun initData() {

    }

    override fun bindEvent() {
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        L.d("Fragment-->onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        L.d("Fragment-->onCreate")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        L.d("Fragment-->onActivityCreated")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        L.d("Fragment-->onCreateView")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        L.d("Fragment-->onViewCreated")
    }

    override fun onStart() {
        super.onStart()
        L.d("Fragment-->onStart")
    }

    override fun onResume() {
        super.onResume()
        L.d("Fragment-->onResume")
    }

    override fun onPause() {
        super.onPause()
        L.d("Fragment-->onPause")
    }

    override fun onStop() {
        super.onStop()
        L.d("Fragment-->onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        L.d("Fragment-->onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        L.d("Fragment-->onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        L.d("Fragment-->onDetach")
    }
}