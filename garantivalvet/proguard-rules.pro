# Biblioteken (Room, WorkManager, ML Kit, Play Billing, Compose) levererar egna regler.
# Behåll radnummer för läsbara kraschrapporter i Play Console.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Arbetsklasser skapas av WorkManager med namn och måste behållas (säkerhetsnät utöver bibliotekets regler).
-keep class se.tmconnect.garantivalvet.** extends androidx.work.ListenableWorker {
    <init>(android.content.Context, androidx.work.WorkerParameters);
}
