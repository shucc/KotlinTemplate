package org.cchao.kotlintemplate.expansion

import android.app.Activity
import android.widget.Toast

fun Activity.showToast(content: String) {
    Toast.makeText(this, content, Toast.LENGTH_SHORT).show()
}

fun Activity.showLoading() {
    showLoading(null)
}

fun Activity.showLoading(content: String?) {

}