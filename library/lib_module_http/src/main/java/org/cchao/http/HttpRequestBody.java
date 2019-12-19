package org.cchao.http;

import androidx.annotation.Keep;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cchen6
 * @Date on 2019/8/2
 * @Description
 */
@Keep
public abstract class HttpRequestBody {

    @JSONField(serialize = false)
    private Long connectTime;

    @JSONField(serialize = false)
    private Long readTime;

    @JSONField(serialize = false)
    private Long writeTime;

    @JSONField(serialize = false)
    private Integer retryCount;

    @JSONField(serialize = false)
    private Long retryDelay;

    @JSONField(serialize = false)
    private Long retryIncreaseDelay;

    @JSONField(serialize = false)
    private Long cookieNetWorkTime;

    @JSONField(serialize = false)
    private Long cookieNoNetWorkTime;

    @JSONField(serialize = false)
    private Map<String, Object> customHeader;

    public Long getConnectTime() {
        return (long) (10 * 1000);
    }

    public Long getReadTime() {
        return (long) (10 * 1000);
    }

    public Long getWriteTime() {
        return (long) (10 * 1000);
    }

    public Integer getRetryCount() {
        return 3;
    }

    public Long getRetryDelay() {
        return 1000L;
    }

    public Long getRetryIncreaseDelay() {
        return 1000L;
    }

    public Long getCookieNetWorkTime() {
        return (long) (24 * 60 * 60 * 30);
    }

    public Long getCookieNoNetWorkTime() {
        return (long) (24 * 60 * 60 * 30);
    }

    public Map<String, Object> getCustomHeader() {
        return new HashMap<>();
    }

    @JSONField(serialize = false)
    public abstract Method getMethod();

    @JSONField(serialize = false)
    public abstract String getUrl();

    public enum Method {
        POST,
        POSTBODY,
        GET
    }
}

