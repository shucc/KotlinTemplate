package org.cchao.http;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cchen6
 * @Date on 2019/8/2
 * @Description
 */
public abstract class HttpRequestBody {

    @JsonIgnore
    private Long connectTime;

    @JsonIgnore
    private Long readTime;

    @JsonIgnore
    private Long writeTime;

    @JsonIgnore
    private Integer retryCount;

    @JsonIgnore
    private Long retryDelay;

    @JsonIgnore
    private Long retryIncreaseDelay;

    @JsonIgnore
    private Long cookieNetWorkTime;

    @JsonIgnore
    private Long cookieNoNetWorkTime;

    @JsonIgnore
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

    @JsonIgnore
    public abstract Method getMethod();

    @JsonIgnore
    public abstract String getUrl();

    public enum Method {
        POST,
        POST_BODY,
        GET
    }
}

