# RhinoCompiledTest

Performance test of the https://github.com/F43nd1r/rhino-android (JS code, compiled to dalvik `dx` byte code instead of default Rhino's compildation to JVM byte code).

In order to test performance of interpreted _(un-optimized)_ code vs compiled _(optimized)_ code - simply change `context.setOptimizationLevel(-1);` to `context.setOptimizationLevel(1);` and wise-versa in the `MainActivity.onCreate()`. 

In order to install on device: `./gradlew installDebug`

In ordet to un-install from device: `adb uninstall com.thinotest.alex.rhinocompiledtest`

P.S. looks like `rhino-android ` is not very stable yet. 
E.g. When JS function was compiled and then it tries to compile it again after app re-start there are issues about duplicated/ missing jar.
As work-around for testing ** do install**  before new app installation/re-start.
