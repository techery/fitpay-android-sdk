package com.fitpay.android.utils;

import com.orhanobut.logger.Logger;

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

    public <T> Subscription register(final Class<T> eventClass, Action1<T> onNext) {
        return mBus
                .asObservable()
                .filter(event -> eventClass.isAssignableFrom(event.getClass()))
                .map(obj -> (T) obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.e(throwable.toString());
                    }
                });
    }

    public void post(Object object){
        mBus.onNext(object);
    }
}
