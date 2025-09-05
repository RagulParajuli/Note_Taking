package com.ragul.notetaking

object Router {
    const val Signup = "signup"
    const val Login = "login"
    const val ForgotPassword = "forgot_password"
    const val Home = "home"
    const val CreateNote = "create_note"
    const val EditNote = "edit_note/{noteId}"
    const val Splash = "splash"
    
    // Helper function to create the edit note route with a specific ID
    fun editNoteRoute(noteId: Int): String = "edit_note/$noteId"
}