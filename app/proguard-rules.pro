# Add project specific ProGuard rules
# Keep Supabase classes
-keep class io.github.supabase.** { *; }
-keep class io.ktor.** { *; }

# Keep model classes for serialization
-keepclassmembers class com.guidetradeai.domain.model.** {
    *;
}

# Keep enum values
-keepclassmembers enum com.guidetradeai.domain.model.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep data class constructors
-keepclassmembers class com.guidetradeai.data.** {
    <init>(...);
}
