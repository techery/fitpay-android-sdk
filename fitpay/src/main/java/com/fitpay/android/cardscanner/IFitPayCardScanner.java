package com.fitpay.android.cardscanner;

public interface IFitPayCardScanner {
    void startScan();

    void onScanned(ScannedCardInfo cardInfo);

    void onCanceled();
}
