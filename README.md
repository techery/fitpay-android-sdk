# FitPay Android SDK - README.md

## Building the library

### Building using Android Studio

```
mkdir fitpay  
cd fitpay
git clone git@github.com:fitpay/fitpay-android-sdk.git  
cd fitpay-android-sdk
studio.sh (or double click your android studio)
Open an existing Android Studio project
/home/yourname/fitpay/fitpay-android-sdk
Click on Gradle (topright), select fitpay-android->Tasks->build, build - right click and select "Run fitpay-android[build]"
```

Fit-Pay also utilizes a continuous integration system (travis) to build and test.
Current Develop Branch Status: [![Build Status](https://travis-ci.org/fitpay/fitpay-android-sdk.svg?branch=develop)](https://travis-ci.org/fitpay/fitpay-android-sdk)

### Building from the commandline

Ensure you have the Android SDK installed on your machine. http://developer.android.com/sdk/index.html

Gradle also automatically runs the tests when you build.

```
export ANDROID_HOME=/home/myname/Android/Sdk
cd ~
mkdir fitpay  
cd fitpay
git clone git@github.com:fitpay/fitpay-android-sdk.git  
cd fitpay-android-sdk  
./gradlew clean build  
```

### Running tests using Android Studio

```
Open an existing Android Studio project
/home/yourname/fitpay/fitpay-android-sdk
Click on Gradle (topright), select fitpay-android->Tasks->verification, test - right click and select "Run fitpay-android[test]"
```

### Running tests from the commandline

```
cd fitpay-android-sdk
./gradlew clean test
```

### Problems with the tests?

Some versions of Java come with an encryption distribution that may not be up to modern standards. If you experience problems with SSL connections, please download the [Java Cryptography Extension](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html).

### Running code coverage

You can run code coverage in the android SDK (to highlight individual file results) or manually. To run manually, run:  
gradlew testDebugUnitTestCoverage  
(Filtered) results can be found in fitpay/build/reports/jacoco/testDebugUnitTestCoverage/html. You can also see per line information in fitpay/build/reports/jacoco/testDebugUnitTestCoverage/testDebugUnitTestCoverage.xml.  

## Using a pre-built version of the SDK as a dependency for your build:

Pre-built versions of the Android SDK are hosted on jcenter(). To use in your project:

* make sure jcenter() is listed in the ```repositories``` closure of your application's build.gradle file.

    ```
    buildscript {
        repositories {
            jcenter()
        }
    }
    ```
* add the pre-built SDK dependency, for example to use v0.4.15 of the SDK: ```compile 'com.fitpay.android:android_sdk:0.4.15'``` dependency to the ```dependencies``` closure of the build.gradle file.
    * Grab via Maven:

        ```xml
        <dependency>
        <groupId>com.fitpay.android</groupId>
        <artifactId>android_sdk</artifactId>
        <version>0.4.15</version>
        </dependency>
        ```
    * or Gradle:

        ```groovy
        compile 'com.fitpay.android:android_sdk:0.4.15'
        ```

## Using local version of the SDK as a dependency for your build:
You can build and use SDK from the local repository. So there are few ways how to do that:

* Uploading using Android Studio

    ```
    Open an existing Android Studio project
    /home/yourname/fitpay/fitpay-android-sdk
    Click on Gradle (topright), select fitpay-android->Tasks->upload, uploadArchives - right click and select "Run fitpay-android[uploadArchives]"
    ```

* Uploading from from the commandline
    ```
    Open an existing Android Studio project
    cd fitpay-android-sdk
    ./gradlew clean uploadArchives
    ```

Now in your child project you should import the library from the local storage.

* First step. Add local repository to the top-level build.gradle
    ```
    def localMavenRepository = 'file://' + new File(System.getProperty('user.home'), 'LocalRepository').absolutePath
    def String pbwURL = 'http://'
    def String metaDataURL = 'http://artifactory.fpctrl.com:8080/artifactory/repo/fitpay/pagare/maven-metadata.xml'
    ```

* Second step. Make sure localMavenRepository() is listed in the ```repositories``` closure of your application's build.gradle file.
    ```
    allprojects {
        repositories {
            maven { url localMavenRepository }
        }
    }
     ```

## Contributing to the SDK
We welcome contributions to the SDK. For your first few contributions please fork the repo, make your changes and submit a pull request. Internally we branch off of develop, test, and PR-review the branch before merging to develop (moderately stable). Releases to Master happen less frequently, undergo more testing, and can be considered stable. For more information, please read:  [http://nvie.com/posts/a-successful-git-branching-model/](http://nvie.com/posts/a-successful-git-branching-model/)

## License
This code is licensed under the MIT license. More information can be found in the [LICENSE](LICENSE) file contained in this repository.

## Questions? Comments? Concerns?
Please contact the team via a github issue, OR, feel free to email us: sdk@fit-pay.com

