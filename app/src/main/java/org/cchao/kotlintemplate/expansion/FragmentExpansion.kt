package org.cchao.kotlintemplate.expansion

import androidx.fragment.app.Fragment

/**
 * @author cchen6
 * @Date on 2019/6/19
 * @Description
 */
fun Fragment.showLoading() {
    showLoading(null)
}

fun Fragment.showLoading(content: String?) {
    activity!!.showLoading(content)
}

fun Fragment.hideLoading() {
    activity!!.hideLoading()
}

fun Fragment.clearLoading() {
    activity!!.clearLoading()
}