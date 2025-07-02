package com.example.thread

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.thread.navigation.NavGraph
import com.example.thread.ui.theme.ThreadTheme

class MainActivity : ComponentActivity() {

//    val supabase = createSupabaseClient(
//        supabaseUrl = "https://qlnqgpworupkspmkmjeu.supabase.co",
//        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFsbnFncHdvcnVwa3NwbWttamV1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTE0MzMzNzEsImV4cCI6MjA2NzAwOTM3MX0.iTbVpixotI21IP4DilgYhcZT0kmWXQJ-v2a5KQ7FFV4"
//    ) {
//        install(Postgrest)
//    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            ThreadTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(navController = navController)
                }
            }
        }
    }

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }


}