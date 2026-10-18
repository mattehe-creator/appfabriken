# Behåll radnummer för läsbara kraschrapporter i Play Console.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.CoroutineWorker
-keep class se.tmconnect.flextid.paminnelse.PaminnelseWorker { *; }
