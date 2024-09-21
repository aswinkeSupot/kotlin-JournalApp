# 1. **Firebase Adding**
> **Reference URL -** https://console.firebase.google.com/  
login to firebase and open firebase console page.
```
Create a Project -> "kotlin-JournalApp" -> Continue ->
    Choose or create a Google Analytics account : Default Account for Firebase 
    -> Create porject -> Continue
Open the project "kotlin-JournalApp" in firebase console
Get started by adding Firebase to your app - select 'android' for Add an app to get started 
```
## 1. Register app
```
->  Android package Name : com.aswin.journalapp
    App nickname : Journal App
    Debug signing certificate SHA-1 (Optional) : 
    -> Register App
```
## 2. Download and then add config file
```
Download google.services.json file and Move your downloaded google-services.json file
into your module (app-level) root directory.
-> Click Next
```
## 3. Add Firebase SDK

### 1. Add the plugin as a dependency to your project-level build.gradle.kts file:
- Root-level (project-level) Gradle file (<project>/build.gradle.kts):
```
plugins {
  // Add the dependency for the Google services Gradle plugin
  id("com.google.gms.google-services") version "4.4.2" apply false
}
```
### 2 .Then, in your module (app-level) build.gradle.kts file, add both the google-services plugin and any Firebase SDKs that you want to use in your app:
- Module (app-level) Gradle file (<project>/<app-module>/build.gradle.kts):
```
plugins {
  id("com.android.application")

  // Add the Google services Gradle plugin
  id("com.google.gms.google-services")

  ...
}

dependencies {
  // Import the Firebase BoM
  implementation(platform("com.google.firebase:firebase-bom:33.3.0"))


  // TODO: Add the dependencies for Firebase products you want to use
  // When using the BoM, don't specify versions in Firebase dependencies
  implementation("com.google.firebase:firebase-analytics")


  // Add the dependencies for any other desired Firebase products
  // https://firebase.google.com/docs/android/setup#available-libraries
}
```
Click Next -> Click Continue to the console.


- **Note**  How to Generate the SHA-1 is listed below
 ```
Method 1 :
    Go to Android Studio for -> File -> settings -> Experimental -> 
    UnTick - Do not build Gradle task list during Gradle sync -> Apply -> OK

    ->Build -> Clean and rebuild the project
    -> Sync Project with Gradle Files -> Open 'Gradle' in the right side -> FirebaseApp -> Tasks -> android -> signinReport (RtClick ) 
    -> Run 'FirebaseApp[Sigining..]' -> This will generate SHA1
     
Method 2 : 
    if the above case is note working use programatic Approch:
    fun getSHA1() {
        try {
            val info: PackageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA-1")
                md.update(signature.toByteArray())
                val sha1 = md.digest()

                // Convert byte array to hex string with colons between each byte
                val hexString = sha1.joinToString(separator = ":") { byte ->
                    "%02X".format(byte) // %02X formats to uppercase hex
                }

                Log.d("SHA1", hexString) // Print the SHA-1 hash in the desired format
            }
        } catch (e: Exception) {
            Log.e("SHA1", "Error: ${e.message}")
        }
    }
```
### 3. Add Internet and Access Network State permission in AndroidManifest.xml
```
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

# 2. Creating Firestore Database in Firebase (Cloud Firestore)
> **Reference URL -** [Firebase App Console](https://console.firebase.google.com/project/kotlin-firestoreapp/overview)
```
Open the appliction in Firebase console -> Build (Left side) -> Firestore Database
Create Database ->  
                   location : asia-south1 (Mumbai) -> Next
                   Security rules : Start in test mode - Create  (Your data is open by default to enable quick setup. However, you must update your security rules within 30 days to enable long-term client read/write access. )
 
 This will open a Console for Cloud Firestore
```
 - Check the firestore Rules
```
rules_version = '2';

service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.time < timestamp.date(2024, 10, 19);
    }
  }
}
```
OR
```
rules_version = '2';

service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if true;
    }
  }
}
```

# 3. Add the Firestore Database SDK to your app
First go to Documentation for more details
> **Reference URL -** [Firebase Documentation](https://firebase.google.com/docs?authuser=0&hl=en)

Build -> Cloud Firestore/Firestore(From left side) -> Get started
- **build.gradle.kts -**  Module (app-level) Gradle file (<project>/<app-module>/build.gradle.kts):
```
dependencies {
    // Declare the dependency for the Cloud Firestore library
    implementation("com.google.firebase:firebase-firestore")
}
```

# 4. Firebase Authentication
## 1. Adding Authentication SDK to Project
Go to Documentation for more details.
> **Reference URL -** [Firebase Documentation](https://firebase.google.com/docs?authuser=0&hl=en)

Build -> Authentication(From left side) -> Introduction -> We are using "Email and password based authentication" -> Android -> Add the Dependency.
- **build.gradle.kts -**  Module (app-level) Gradle file (<project>/<app-module>/build.gradle.kts):
```
dependencies {
    // Add the dependency for the Firebase Authentication library
    implementation("com.google.firebase:firebase-auth")
}
```
## 2. Enable Authentication (Sign-in Method)
> **Reference URL -** [Firebase App Console](https://console.firebase.google.com/project/kotlin-journalapp/overview)
```
    Build -> Authentication(From left side) -> Get started -> Sign-in method ->
    Sign-in providers: Email/Password (Select) ->
    Email/Password : Enable -> Save
```
## 3. Create a password-based account
Go to Documentation for more details.
> **Reference URL -** [Firebase Documentation](https://firebase.google.com/docs?authuser=0&hl=en)

Build -> Authentication(From left side) -> Android -> Password Authentication -> Create a password-based account
   -  **SignupActivity.kt**
   ```
    class SignupActivity : AppCompatActivity() {

        private lateinit var auth: FirebaseAuth
        lateinit var binding: ActivityMainBinding

        public override fun onStart() {
            super.onStart()
            // Check if user is signed in (non-null) and update UI accordingly.
            val currentUser = auth.currentUser
            if (currentUser != null) {
                reload()
            }
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = DataBindingUtil.setContentView(this,R.layout.activity_signup)

            // Initialize Firebase Auth
            auth = Firebase.auth
        
            binding.btnCreateAccount.setOnClickListener {
                val email = binding.edEmail.text.toString()
                val password = binding.edPassword.text.toString()
            
                CreateUser(email, password)
            }
        }

        fun CreateUser(email: String, password: String) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("SignupActivity", "createUserWithEmail:success")
                        val user = auth.currentUser
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("SignupActivity", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT,).show()
                    }
                }
        }
    }
   ```

# 5. Create JournalListActivity.kt
   design its **activity_journal_list.xml** page 

# 6. Create Recycler Adapter. (JournalRecyclerAdapter.kt)
 Create a model data class **Journal.kt**
 - How to use Databinding in RecyclerAdapter

# 6. Firebase Storage
### 1. Create Firebase Storage
> **Reference URL -** [Firebase App Console](https://console.firebase.google.com/project/kotlin-journalapp/overview)
```
    Build -> Storage(From left side) -> Get started -> 
    Secure rules for Cloud Storage : Start in test mode-> Next ->
    Set Cloud Storage location : nam5(us-central) -> Done
```
### 2. Add Dependency for firebase Storage
> **Reference URL -** [Firebase Documentation](https://firebase.google.com/docs?authuser=0&hl=en)

Build -> Storage(From left side) -> Android -> Get Started -> Add the Cloud Storage SDK to your app
```
dependencies {
    // Add the dependency for the Cloud Storage library
    implementation("com.google.firebase:firebase-storage")
}
```

# 6. Loading image using Glide library with Databinding. (BindingAdapter) 
### 1. Add Kapt in app lever build.gradle.kt
```
plugins {
    id("kotlin-kapt")
}
```

### 2. Add Image loading 3rd party library for load image (Here we are using Glide)
    ```
    dependencies {
        implementation("com.github.bumptech.glide:glide:4.13.2")
        annotationProcessor("com.github.bumptech.glide:compiler:4.13.2")
    }
    ```

### 3. Create **BindingAdapter.kt** kotlin class
    ```
    import android.widget.ImageView
    import androidx.databinding.BindingAdapter
    import com.bumptech.glide.Glide
    import com.bumptech.glide.request.RequestOptions
    
    object BindingAdapters {
    
        @JvmStatic
        @BindingAdapter("imageUrl")
        fun loadImage(view: ImageView, imageUrl: String?) {
            Glide.with(view.context)
                .load(imageUrl)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.placeholder)  // Your placeholder image
                        .error(R.drawable.placeholder)              // Your error image
                )
                .into(view)
        }
    }
    ```

### 4. Loading image into ImageView from String url with Databinding
    ```
        <ImageView
                app:imageUrl="@{journal.imageUrl}"
                android:id="@+id/imgJournalImage"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:adjustViewBounds="true"
                android:cropToPadding="true"
                android:scaleType="fitXY" />
    ```



































