# Biblioteken (Room, WorkManager, ML Kit, Play Billing, Compose) levererar egna regler.
# Behåll radnummer för läsbara kraschrapporter i Play Console.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Arbetsklasser skapas av WorkManager med namn och måste behållas (säkerhetsnät utöver bibliotekets regler).
-keep class se.tmconnect.garantivalvet.** extends androidx.work.ListenableWorker {
    <init>(android.content.Context, androidx.work.WorkerParameters);
}

# ML Kit (kvittoavläsning) hittar sina komponenter och modeller via namn i manifestet och via reflektion.
# Bibliotekets egna regler räckte inte säkert: avläsningen blev tyst utan resultat i förminskat bygge.
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.internal.mlkit_** { *; }
-keep class com.google.android.odml.** { *; }
-dontwarn com.google.mlkit.**
