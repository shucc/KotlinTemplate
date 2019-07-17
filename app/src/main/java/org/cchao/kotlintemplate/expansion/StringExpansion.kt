package org.cchao.kotlintemplate.expansion

import org.cchao.kotlintemplate.util.Md5Util

/**
 * @author cchen6
 * @Date on 2019/7/17
 * @Description
 */
fun String.md5(): String {
    return Md5Util.getMd5(this)
}