package org.cchao.common.expansion

import android.view.View

/**
 * @author cchen6
 * @Date on 2019/7/1
 * @Description
 */
fun View.clickWithTrigger(onClickListener: View.OnClickListener){
    clickWithTrigger(onClickListener, 300L)
}

fun View.clickWithTrigger(onClickListener: View.OnClickListener, triggerTime: Long) {
    setOnClickListener {
        val clickTime: Long = if (getTag(123456789) == null) 0L else getTag(123456789) as Long
        setTag(123456789, System.currentTimeMillis())
        if (clickTime <= 0L || System.currentTimeMillis() - clickTime > triggerTime) {
            onClickListener.onClick(it)
        }
    }
}