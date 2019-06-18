package org.cchao.kotlintemplate.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * @author cchen6
 * @Date on ${Date}
 * @Description
 */
abstract class BaseFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = LayoutInflater.from(container!!.context).inflate(bindLayout(), container, false)
        initData()
        bindEvent()
        return view
    }

    abstract fun bindLayout(): Int

    abstract fun initData()

    abstract fun bindEvent()
}