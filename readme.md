# Screen tracker [![](https://jitpack.io/v/catalinghita8/android-screen-tracker.svg)](https://jitpack.io/#catalinghita8/android-screen-tracker)


### Description
Screen tracker overlays on top of the target application the currently visible fragment and its activity host. The library provides insight on what UI components are currently on top of the stacks.

### Usage
1. Import the dependency:

    `build.gradle` (app):

    ``` gradle
    dependencies {
        implementation 'com.github.catalinghita8:android-screen-tracker:0.1.1'
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
    class YourApplication: Application() {
        override fun onCreate() {
            ...
            ScreenTracker.initialize(this)
        }
    }
    ```

### Release notes
Check out release notes [here](releases.md).


