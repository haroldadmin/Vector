# Credits to MvRx's proguard rules for helping me create this file

# Keep the Companion object of classes extending VectorViewModel
-keepclassmembers class ** extends com.haroldadmin.vector.VectorViewModel {
    ** Companion;
}

# Classes extending VectorViewModel are recreated using reflection, which assumes that a one argument
# constructor accepting a data class holding the state exists. Need to make sure to keep the constructor
# around. Additionally, a static create / inital state method will be generated in the case a
# companion object factory is used with JvmStatic. This is accessed via reflection.
-keepclassmembers class ** extends com.haroldadmin.vector.VectorViewModel {
    public <init>(...);
    public static *** create(...);
    public static *** initialState(...);
}

# If a VectorViewModel is used without JvmStatic, keep create and initalState methods which
# are accessed via reflection.
-keepclassmembers class ** implements com.haroldadmin.vector.VectorViewModelFactory {
     public <init>(...);
     public *** create(...);
     public *** initialState(...);
}

# VectorViewModelFactory is referenced via reflection using the Companion class name.
-keepnames class * implements com.haroldadmin.vector.VectorViewModelFactory


# Members of the Kotlin data classes used as the state in Vector are read via Kotlin reflection which cause trouble
# with Proguard if they are not kept.
-keepclassmembers,includedescriptorclasses class ** implements com.haroldadmin.vector.VectorState {
   *;
}

# The MvRxState object and the names classes that implement the MvRxState interfrace need to be
# kept as they are accessed via reflection.
-keepnames class com.haroldadmin.vector.VectorState
-keepnames class * implements com.haroldadmin.vector.VectorState