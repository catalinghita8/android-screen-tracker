# Screen tracker [![](https://jitpack.io/v/catalinghita8/android-screen-tracker.svg)](https://jitpack.io/#catalinghita8/android-screen-tracker)


### Overview
Screen Tracker provides insight on what UI components are currently on top of the stacks for the target app. More precisely, it detects the currently visible fragment and its activity host. The components are displayed as an overlay on the screen.

![](https://i.imgur.com/bUJ0Ulp.png)
![](https://i.imgur.com/OZChXcZ.png)


### Usage
1. Import the dependency:

    `build.gradle` (app):

    ``` gradle
    dependencies {
        implementation 'com.github.catalinghita8:android-screen-tracker:0.2.3-beta'
    }
    ```

    `build.gradle` (project):

    ``` gradle
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
    ```
2. Initialize the library in your application class:

    ``` kotlin
    class MyApplication: Application() {
        override fun onCreate() {
            ...
            ScreenTracker.initialize(this)
        }
    }
    ```

### Release notes
Check out release notes [here](releases.md).


