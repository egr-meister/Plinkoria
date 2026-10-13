package com.plinkoria.game.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Outbound links open in the user's own browser / Play Store via an Intent.
 * The app itself has NO network access and no INTERNET permission.
 */
object AppLinks {
    const val PRIVACY_POLICY_URL = "https://github.com/egr-meister/Plinkoria/blob/main/PRIVACY.md"
    private const val PACKAGE = "com.plinkoria.game"

    fun openPrivacyPolicy(context: Context): Boolean = open(context, Uri.parse(PRIVACY_POLICY_URL))

    fun openRateApp(context: Context): Boolean =
        open(context, Uri.parse("market://details?id=$PACKAGE")) ||
            open(context, Uri.parse("https://play.google.com/store/apps/details?id=$PACKAGE"))

    private fun open(context: Context, uri: Uri): Boolean = try {
        context.startActivity(Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        true
    } catch (e: ActivityNotFoundException) {
        false
    } catch (e: SecurityException) {
        false
    }
}
