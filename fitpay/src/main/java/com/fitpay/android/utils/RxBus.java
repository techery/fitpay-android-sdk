package com.fitpay.android.utils;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
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

    public void send(Object o) {
        mBus.onNext(o);
    }

    public Observable<Object> toObserverable() {
        return mBus;
    }

    public <T> Subscription register(final Class<T> eventClass, Action1<T> onNext) {
        return mBus
                .filter(event -> eventClass.isAssignableFrom(event.getClass()))
                .map(obj -> (T) obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext);
    }

    public void post(Object object){
        mBus.onNext(object);
    }
}
