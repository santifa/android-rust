package com.santifa.android_rust;

public class NativeLibrary {
    // Load the native Rust library
    static {
        System.loadLibrary("android_rust");
    }

    /**
     *  A simple example function which only produces a string.
     * @return A hello world string with the native architecture.
     */
    public static native String helloWorld();

    /**
     * A simple example function for passing arguments.
     * @param left The left hand operand.
     * @param right The right hand operand.
     * @return THe left and right hand added together.
     */
    public static native int add(int left, int right);
}
