package org.cchao.kotlintemplate.expansion

import android.content.Context
import android.text.TextUtils

fun Context.showToast(content: String?) {
    if (TextUtils.isEmpty(content)) {
        return
    }
    if (System.currentTimeMillis() - ExpansionObject.showToastTime < 1000) {
        return
    }

}