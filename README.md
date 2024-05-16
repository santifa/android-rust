# Android Rust interface example

This is an example app which demonstrates the interfacing between Rust and an Android application.
This shows only the user side and ignores mostly the setup needed from the Rust perspective.
The companion project is the [android_rust_library](https://github.com/santifa/android-rust-lib)
which shows the other side needed for a nice and
smooth interfacing. As this is only an experiment for demonstration, the code is not very well-written.

## Android perspective

Android is based roughly on the JVM as it uses its own implementation. But this offers us the standard `JNI` mechanics
to integrate libraries written in other languages. The Android side is fairly easy but uses Java classes for the interop
instead of Kotlin. Mostly because I didn't know Kotlin well enough.

To integrate a library the following steps are necessary:

+ The project is a basic layout template from android studio without heavy modifications.
   + The main code is written in Kotlin and the JNI part is written in Java
   + The SDK extensions `NDK`, `build-tools` and `platforms` are needed.
   + These should be available in the command-line Path
     + Set `ANDROID_HOME` if not available
     + `export PATH=$PATH:${ANDROID_HOME}/cmdline-tools/cmdline-tools/bin:${ANDROID_HOME}/platform-tools`
     + `export PATH=$PATH:${ANDROID_HOME}/ndk/<version>`
     + `sdkmanager --list_installed` can show the SDK extensions (broken with Linux Mint)
     + _Caution_: The `build-tools` version and `gradle` version are connected through the mention in `build.gradle`. 
     Thus, try an older version if `gradle` complains about missing `build-tools`. To further escalate this, the platform
     SDK version and the `gradle` version are linked together.
  + The JNI integration is done via linking the four possible architectures to the folder `app/src/main/jniLibs`.
  See [here](https://developer.android.com/ndk/guides/abis#sa) for the four Android ABI types.

From the folder `app/src/main` run
```shell
mkdir jniLibs
mkdir jniLibs/arm64
mkdir jniLibs/armeabi
mkdir jniLibs/x86
```
and
```shell
ln -snf <rust_library>/target/aarch64-linux-android/release/libgreetings.so jniLibs/arm64-v8a/libandroid_rust.so
ln -snf <rust_library>/target/armv7-linux-androideabi/release/libgreetings.so jniLibs/armeabi-v7a/libandroid_rust.so
ln -snf <rust_library>/target/i686-linux-android/release/libgreetings.so jniLibs/x86/libandroid_rust.so
ln -snf <rust_library>/target/x64_64-linux-android/release/libgreetings.so jniLibs/x86_64/libandroid_rust.so
```

This populates the library in all four architecture options for the JVM. The JVM loads the appropriate shared library
for the architecture it is running.

The Java side integration is done by the class `NativeLibrary.java`:

```java
public class NativeLibrary {
    static {
        // Note: No lib or .so are required to find the shared library.
        System.loadLibrary("android_rust");
    }
    
    // Define the native method calls on the Java side
    public static native String helloWorld();
    public static native int add(int left, int right);
    public static native String perfectNumbers(int input);
}
```

This is everything needed from the Android side to integrate a shared library.
Nevertheless, the ABI should be matching and the symbols provided by the shared library should
match a given format but this is a subject for the implementer of the native library on the rust side.

## Demonstration Cases

The example implements some basic demonstration cases.

1. The first case is a simple call to the Rust native library and returns a JVM string with the architecture
2. The second use case is a simple addition with left and right hand side.
3. The third use case is a more complex operation printing the first perfect numbers up-to the input number. 
See [this](https://rosettacode.org/wiki/Perfect_numbers) one for more informations

## References

The following references were used:
* [Rust Library Android](https://gendignoux.com/blog/2022/10/24/rust-library-android.html)
* [Rust JNI](https://docs.rs/jni/latest/jni/)
* [Rust on Android](https://mozilla.github.io/firefox-browser-architecture/experiments/2017-09-21-rust-on-android.html)
* [Building Rust Modules](https://source.android.com/docs/setup/build/rust/building-rust-modules/overview?hl=de)