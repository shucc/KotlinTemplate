package org.cchao.http;

import org.cchao.common.utils.GsonUtils;
import org.cchao.common.utils.Md5Utils;

import io.reactivex.functions.Consumer;

/**
 * @author cchen6
 * @Date on 2019/8/5
 * @Description
 */
public class CacheConsumer implements Consumer<HttpResponseModel<Object>> {

    private HttpRequestBody httpRequestBody;

    private boolean isCache;

    public CacheConsumer(HttpRequestBody httpRequestBody, boolean isCache) {
        this.httpRequestBody = httpRequestBody;
        this.isCache = isCache;
    }

    @Override
    public void accept(HttpResponseModel<Object> objectHttpResponseModel) throws Exception {
        if (!isCache || null == objectHttpResponseModel || !objectHttpResponseModel.isSuccess()) {
            return;
        }
        String key = Md5Utils.getMd5(GsonUtils.toString(httpRequestBody));
    }
}
