package com.ragul.notetaking

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ragul.notetaking.Authentication.LoginScreen
import com.ragul.notetaking.Authentication.SignupScreen
import com.ragul.notetaking.ui.viewmodel.NoteViewModel
import com.ragul.notetaking.ui.screens.HomeScreen
import com.ragul.notetaking.ui.screens.NoteEditorScreen
import com.ragul.notetaking.ui.theme.NoteTakingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val noteViewModel: NoteViewModel by viewModels()
        setContent {
            NoteTakingTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Router.Signup) {
                    composable(Router.Signup) {
                        SignupScreen(navController)
                    }
                    composable(Router.Login) {
                        LoginScreen(navController)
                    }
                    composable(Router.Home) {
                        HomeScreen(navController)
                    }
                    composable(Router.CreateNote) {
                        NoteEditorScreen(navController, null)
                    }
                    composable(
                        route = Router.EditNote,
                        arguments = listOf(navArgument("noteId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val noteId = backStackEntry.arguments?.getInt("noteId")
                        NoteEditorScreen(navController, noteId)
                    }
                }
            }
        }
    }
}