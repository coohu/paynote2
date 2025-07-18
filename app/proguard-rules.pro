# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\user\AppData\Local\Android\Sdk\tools\proguard\proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If you use reflection, typically to load classes dynamically, you need
# to tell ProGuard what not to remove.
#-keep public class com.example.myapplicatoin.MyClass
#-keepclassmembers class com.example.myapplication.MyClass {
#   public <init>();
#}

# If you use native code, you might want to keep the native interfaces.
#-keepclasseswithmembernames,includedescriptorclasses class * {
#    native <methods>;
#}
#-keepclassmembers,allowoptimization enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}

-keep class androidx.core.app.CoreComponentFactory { *; }
-keep class androidx.core.app.CoreComponentFactory { *; }
