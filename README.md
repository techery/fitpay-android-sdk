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
### Gotchas

#### Use [retrolambda](https://github.com/evant/android-retrolambda-lombok) over the [jack toolchain](http://tools.android.com/tech-docs/jackandjill).

If you see an error similar to the following, it's due to the use of the use of the [jack toolchain](http://tools.android.com/tech-docs/jackandjill) instead of [retrolambda](https://github.com/evant/android-retrolambda-lombok).

```
com.fitpay.android.api.ApiManager.com_fitpay_android_api_ApiManager_lambda$createUser$0(com.fitpay.android.api.models.user.UserCreateRequest, com.fitpay.android.api.callbacks.ApiCallback)' was expected to be of type direct but instead was found to be of type virtual
```

Add `classpath 'me.tatarka.retrolambda.projectlombok:lombok.ast:0.2.3.a2'` to your dependencies and remove the jack toolchain.

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
* add the pre-built SDK dependency, for example to use v0.5.0 of the SDK: ```compile 'com.fitpay.android:android_sdk:0.5.0'``` dependency to the ```dependencies``` closure of the build.gradle file.
    * Grab via Maven:

        ```xml
        <dependency>
        <groupId>com.fitpay.android</groupId>
        <artifactId>android_sdk</artifactId>
        <version>0.5.0</version>
        </dependency>
        ```
    * or Gradle:

        ```groovy
        compile 'com.fitpay.android:android_sdk:0.5.0'
        ```

## Using local version of the SDK as a dependency for your build:
In order to use a local version of the SDK in your project, you need to first build the local repository by using the Gradle task ```uploadArchives``` that's included in the FitPay Android SDK. After running this task, the compiled SDK will be outputted to a local folder on your computer ("LocalRepository") and will be available for use in your project.

You can run the Gradle task and build the repository from Android Studio or from the commandline.

* Build using Android Studio

    ```
    Open the FitPay Android SDK in Android Studio

    /home/yourname/fitpay/fitpay-android-sdk

    Click on Gradle (topright), select fitpay-android->Tasks->upload, uploadArchives - right click and select "Run fitpay-android[uploadArchives]"
    ```

* Build from the commandline
    ```
    Open the FitPay Android SDK in your commandline

    cd fitpay-android-sdk
    ./gradlew clean uploadArchives
    ```

Now that you've built the repository, you need to tell your Android project where it is located and that it needs to be included in your project. Open your Android project, and do the following:

1. Add the local repository's location to the top-level build.gradle file
    ```
    def localMavenRepository = 'file://' + new File(System.getProperty('user.home'), 'LocalRepository').absolutePath
    def String pbwURL = 'http://'
    def String metaDataURL = 'http://artifactory.fpctrl.com:8080/artifactory/repo/fitpay/pagare/maven-metadata.xml'
    ```

2. Include ```localMavenRepository()``` in the ```repositories``` closure of the same top-level build.gradle file.
    ```
    allprojects {
        repositories {
            maven { url localMavenRepository }
        }
    }
     ```
3. Add the repository as a dependency to the module-level build.gradle file of your project.
    ```
    dependencies {
        compile fileTree(dir: 'libs', include: ['*.jar'])
        compile 'com.fitpay.android:android_sdk:0.4.20'
    }
    ```

That's it! You are now able to build from your local repository.

## Contributing to the SDK
We welcome contributions to the SDK. For your first few contributions please fork the repo, make your changes and submit a pull request. Internally we branch off of develop, test, and PR-review the branch before merging to develop (moderately stable). Releases to Master happen less frequently, undergo more testing, and can be considered stable. For more information, please read:  [http://nvie.com/posts/a-successful-git-branching-model/](http://nvie.com/posts/a-successful-git-branching-model/)

## Release Steps

This instructions are for only those that have the credentials for pushing public FitPay Android SDK releases.

* Create `release-X.X.X` branch.
* Set release version in `fitpay/build.gradle`, commit/push the change.
* Update `bintray.properties` with release credentials, **don't commit into git**.
* Run `./gradlew bintrayUpload`.
* Authenticate to bintray.com and publish the new artifact so it's publicly accessible.
* Merge `release-X.X.X` branch into `develop` and `master`
* Create tagged release on github with general release notes.
* Delete `release-X.X.X` branch
* Update `fitpay/build.gradle` with next development version and commit/push the change.

## License
This code is licensed under the MIT license. More information can be found in the [LICENSE](LICENSE) file contained in this repository.

## Questions? Comments? Concerns?
Please contact the team via a github issue, OR, feel free to email us: sdk@fit-pay.com
