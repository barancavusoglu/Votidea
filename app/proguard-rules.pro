# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-dontwarn com.akexorcist.roundcornerprogressbar.TextRoundCornerProgressBar
-dontwarn com.roughike.bottombar.BottomNavigationBehavior
-dontwarn okhttp3.internal.platform.*

-keep class com.bcmobileappdevelopment.votidea.GsonResponse.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.examples.android.model.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer



-keep class com.firebase.** { *; }
-keep class org.apache.** { *; }
-keepnames class com.fasterxml.jackson.** { *; }
-keepnames class javax.servlet.** { *; }
-keepnames class org.ietf.jgss.** { *; }
-dontwarn org.w3c.dom.**
-dontwarn org.joda.time.**
-dontwarn org.shaded.apache.**
-dontwarn org.ietf.jgss.**

#FOR CHATKIT

-keep class * extends com.stfalcon.chatkit.messages.MessageHolders$OutcomingTextMessageViewHolder {
     public <init>(android.view.View, java.lang.Object);
     public <init>(android.view.View);
 }
-keep class * extends com.stfalcon.chatkit.messages.MessageHolders$IncomingTextMessageViewHolder {
     public <init>(android.view.View, java.lang.Object);
     public <init>(android.view.View);
 }
-keep class * extends com.stfalcon.chatkit.messages.MessageHolders$IncomingImageMessageViewHolder {
     public <init>(android.view.View, java.lang.Object);
     public <init>(android.view.View);
 }
-keep class * extends com.stfalcon.chatkit.messages.MessageHolders$OutcomingImageMessageViewHolder {
     public <init>(android.view.View, java.lang.Object);
     public <init>(android.view.View);
 }


 # For Google Play Services
 -keep public class com.google.android.gms.ads.**{
    public *;
 }

#-keep public class com.google.android.gms.oss.**{
#    public *;
# }

 -keep class com.google.** { *; }
 -keep class com.scwang.** { *; }
 -keep class com.shaishavgandhi.** { *; }
 -keep class com.squareup.** { *; }
 -keep class io.fotoapparat.** { *; }
 -keep class com.github.** { *; }
 -keep class com.fxn769.** { *; }
 -keep class com.facebook.** { *; }
 -keep class com.yarolegovich.** { *; }
 -keep class jp.** { *; }
 -keep class com.nostra13.** { *; }
 -keep class com.rengwuxian.** { *; }
 -keep class com.orhanobut.** { *; }

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}
-keep class **.R$*


-keepattributes InnerClasses
 -keep class **.R
 -keep class **.R$* {
    <fields>;
}

#-keep class com.google.android.gms.oss.licenses.** { *; }
#-keep public class * implements com.google.android.gms.oss.licenses { *; }
#-keep public class * extends com.google.android.gms.oss.licenses { *; }
#
#-keep public class com.bcmobileappdevelopment.votidea.OssLicensesMenuActivity
#-keep public class com.bcmobileappdevelopment.votidea.OssLicensesActivity