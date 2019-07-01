package org.cchao.kotlintemplate.expansion

/**
 * @author cchen6
 * @Date on 2019/6/19
 * @Description 屏幕信息
 */
data class ScreenData(val px: Int, val density: Float) {

    var dp: Float = 0f

    init {
        dp = px / density
    }
}