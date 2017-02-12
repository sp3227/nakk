# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\sejung\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# 다음지도 proguard 설정
-dontwarn android.opengl.alt.**
-dontwarn net.daum.mf.map.common.**
-dontwarn net.daum.mf.map.n.api.**

-keep class net.daum.mf.map.** {*;}
-keep class net.daum.mf.map.api.** {*;}
-keep class net.daum.android.map.** {*;}