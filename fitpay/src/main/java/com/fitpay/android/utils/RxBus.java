package com.fitpay.android.utils;

import android.util.Log;

import com.orhanobut.logger.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by Vlad on 28.03.2016.
 */
public class RxBus {

    private static final RxBus sInstance;

    static {
        sInstance = new RxBus();
    }

    public static RxBus getInstance() {
        return sInstance;
    }

    private final Subject<Object, Object> mBus = new SerializedSubject<>(PublishSubject.create());

    public <T> Subscription register(final Class<T> eventClass, Action1<T> onNext) {
        return mBus
                .asObservable()
                .filter(event -> eventClass.isAssignableFrom(event.getClass()))
                .map(obj -> (T) obj)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        onNext,
                        throwable -> Logger.e(throwable.toString() + ", " + getStackTrace(throwable))
                );
    }

    public void post(Object object) {
        Log.d("RxBus", "post event: " + object);
        mBus.onNext(object);
    }

    private String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString(); // stack trace as a string
    }
}
