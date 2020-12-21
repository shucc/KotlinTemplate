package org.cchao.http.exception;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * @author cchen6
 * @Date on 2019/8/5
 * @Description
 */
public class RetryWhenNetworkException implements Function<Observable<? extends Throwable>, Observable<?>> {

    //retry次数
    private int count = 3;
    //延迟
    private long delay = 3000;
    //叠加延迟
    private long increaseDelay = 3000;

    public RetryWhenNetworkException() {

    }

    public RetryWhenNetworkException(int count, long delay) {
        this.count = count;
        this.delay = delay;
    }

    public RetryWhenNetworkException(int count, long delay, long increaseDelay) {
        this.count = count;
        this.delay = delay;
        this.increaseDelay = increaseDelay;
    }

    @Override
    public Observable<?> apply(Observable<? extends Throwable> observable) {
        return observable
                .zipWith(Observable.range(1, count + 1), Wrapper::new)
                .flatMap(wrapper -> {
                    if ((wrapper.throwable instanceof ConnectException
                            || wrapper.throwable instanceof SocketTimeoutException
                            || wrapper.throwable instanceof TimeoutException)
                            && wrapper.index < count + 1) {
                        //如果超出重试次数也抛出错误，否则默认是会进入onCompleted
                        return Observable.timer(delay + (wrapper.index - 1) * increaseDelay, TimeUnit.MILLISECONDS);
                    }
                    return Observable.error(wrapper.throwable);
                });
    }

    private static class Wrapper {

        private int index;

        private Throwable throwable;

        Wrapper(Throwable throwable, int index) {
            this.index = index;
            this.throwable = throwable;
        }
    }
}