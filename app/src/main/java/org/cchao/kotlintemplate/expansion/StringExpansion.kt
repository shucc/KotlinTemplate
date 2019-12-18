package org.cchao.kotlintemplate.expansion

import org.cchao.common.utils.Md5Utils

/**
 * @author cchen6
 * @Date on 2019/7/17
 * @Description
 */
fun String.md5(): String {
    return Md5Utils.getMd5(this)
}