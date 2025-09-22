#####################################
# General Android / Kotlin / Compose
#####################################

# Keep attributes for reflection & serialization
-keepattributes Signature, *Annotation*, EnclosingMethod, InnerClasses

# Keep Kotlin metadata (needed for reflection, serialization, Compose)
-keep class kotlin.Metadata { *; }

# Jetpack Compose (previews & runtime reflection)
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Keep Compose @Composable functions
-keep class androidx.compose.runtime.** { *; }

#####################################
# Firebase
#####################################

# Keep Firebase Realtime DB model classes (no-arg constructors & public fields)
-keepclassmembers class com.example.thread.model.** {
    public <init>();
    <fields>;
}

# Keep Firebase SDK classes
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Needed for Firebase Messaging
-keep class com.google.firebase.messaging.FirebaseMessagingService { *; }
-keep class com.google.firebase.iid.FirebaseInstanceIdService { *; }

#####################################
# Coil / Coil3
#####################################
-keep class coil.** { *; }
-keep class coil3.** { *; }
-dontwarn coil.**
-dontwarn coil3.**

#####################################
# Cloudinary
#####################################
-keep class com.cloudinary.** { *; }
-dontwarn com.cloudinary.**

# Ignore Cloudinary's optional Glide integration
-dontwarn com.cloudinary.android.download.glide.**
-dontwarn com.bumptech.glide.**

# Ignore Cloudinary's optional Picasso integration
-dontwarn com.cloudinary.android.download.picasso.**
-dontwarn com.squareup.picasso.**

#####################################
# Google Play Services / Maps
#####################################
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

#####################################
# Gson / Moshi / JSON
#####################################
-keep class com.google.gson.** { *; }
-keep class com.squareup.moshi.** { *; }
-dontwarn com.google.gson.**
-dontwarn com.squareup.moshi.**

#####################################
# Retrofit / OkHttp / Ktor
#####################################
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

#####################################
# Lottie
#####################################
-keep class com.airbnb.lottie.** { *; }
-dontwarn com.airbnb.lottie.**

#####################################
# Room
#####################################
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

#####################################
# Your App Classes / Activities / ViewModels
#####################################
-keep class com.example.thread.viewModel.** { *; }
-keep class com.example.thread.screens.** { *; }
-keep class com.example.thread.application.** { *; }
-keep class com.example.thread.utils.** { *; }
-keep class com.example.thread.navigation.** { *; }
-keep class com.example.thread.itemView.** { *; }
-keep class com.example.thread.ui.** { *; }
-keep class com.example.thread.MainActivity { *; }

#####################################
# Firebase dynamic fields / maps
#####################################
-keepclassmembers class * {
    @com.google.firebase.database.PropertyName <fields>;
}
