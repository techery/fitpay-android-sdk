# FitPay Android SDK - README.md

## Building the app

### Building using Android Studio
```
mkdir fitpay  
cd fitpay
git clone git@github.com:fitpay/fitpay-android-sdk.git  
cd fitpay-android-sdk
studio.sh (or double click your android studio)
Open an existing Android Studio project
/home/yourname/fitpay/fitpay-android-sdk
Click on Project (topleft), select Java, test, UserTest - right click and select "Run UserTest"
```



Fit-Pay also utilizes a continuous integration system (travis) to build and test. Current Develop Branch Status: [![Build Status](https://travis-ci.org/fitpay/fitpay-android-sdk.svg?branch=develop)](https://travis-ci.org/fitpay/fitpay-android-sdk)

### Building from the commandline
Ensure you have the Android SDK installed on your machine. http://developer.android.com/sdk/index.html  
  
Gradle also automagically runs the tests when you build.  

```
export ANDROID_HOME=/home/myname/Android/Sdk
cd ~
mkdir fitpay  
cd fitpay
git clone git@github.com:fitpay/fitpay-android-sdk.git  
cd fitpay-android-sdk  
./gradlew clean build  
```

### Problems with the tests?
Some versions of Java come with an encryption distribution that may not be up to modern standards. If you experience problems with SSL connections, please download the [Java Cryptography Extension](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html).

### Running code coverage
You can run code coverage in the android SDK (to highlight individual file results) or manually. To run manually, run:  
gradlew testDebugUnitTestCoverage  
(Filtered) results can be found in fitpay/build/reports/jacoco/testDebugUnitTestCoverage/html. You can also see per line information in fitpay/build/reports/jacoco/testDebugUnitTestCoverage/testDebugUnitTestCoverage.xml.  
  
## Contributing to the SDK
We welcome contributions to the SDK. For your first few contributions please fork the repo, make your changes and submit a pull request. Internally we branch off of develop, test, and PR-review the branch before merging to develop (moderately stable). Releases to Master happen less frequently, undergo more testing, and can be considered stable. For more information, please read:  [http://nvie.com/posts/a-successful-git-branching-model/](http://nvie.com/posts/a-successful-git-branching-model/)

## Using a pre-built version of the SDK as a dependency for your build:
Pre-built versions of the Android SDK are hosted on jcenter(). To use in your project:
* make sure jcenter() is listed in the ```repositories``` closure of your application's build.gradle file. 
* add the pre-built SDK dependency, for example to use v0.1.0 of the SDK: ```compile 'com.fitpay.android:fitpay:0.1.0'``` dependency to the ```dependencies``` closure of the build.gradle file.

## License
This code is licensed under the MIT license. More information can be found in the [LICENSE](LICENSE) file contained in this repository.

## Questions? Comments? Concerns?
Please contact the team via a github issue, OR, feel free to email us: sdk@fit-pay.com

