package org.cchao.kotlintemplate.ui.activity

import kotlinx.android.synthetic.main.activity_main.*
import org.cchao.kotlintemplate.R
import org.cchao.kotlintemplate.ui.base.BaseActivity

/**
 * @author cchen6
 * @Date on 2019/6/19
 * @Description
 */
class SecondActivity : BaseActivity() {

    override fun bindLayout(): Int {
        return R.layout.activity_main
    }

    override fun initData() {

    }

    override fun bindEvent() {
        text_test.setOnClickListener {
            showLoading("喝喝会...")
        }
    }
}