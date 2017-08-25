package com.fitpay.android.cardscanner;

public interface IFitPayCardScanner {
    void startScan(String callbackId);

    void onScanned(ScannedCardInfo cardInfo);

    void onCanceled();
}
