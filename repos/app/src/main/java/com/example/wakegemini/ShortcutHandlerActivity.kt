package com.example.wakegemini

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Toast

class ShortcutHandlerActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val targetService = "com.google.android.googlequicksearchbox/com.google.android.voiceinteraction.GsaVoiceInteractionService"

        val currentVoice = Settings.Secure.getString(contentResolver, "voice_interaction_service")
        val currentAssistant = Settings.Secure.getString(contentResolver, "assistant")

        val isAlreadySet = (currentVoice == targetService && currentAssistant == targetService)

        if (!isAlreadySet) {
            setGoogleVoiceService(targetService)

            Handler(Looper.getMainLooper()).postDelayed({
                launchVoiceCommand()
                finishWithNoAnim()
            }, 250)
        } else {
            launchVoiceCommand()
            finishWithNoAnim()
        }
    }

    private fun setGoogleVoiceService(targetService: String) {
        try {
            Settings.Secure.putString(contentResolver, "voice_interaction_service", targetService)
            Settings.Secure.putString(contentResolver, "assistant", targetService)
        } catch (e: SecurityException) {
            Toast.makeText(this, "修改语音服务失败：缺失 WRITE_SECURE_SETTINGS 权限", Toast.LENGTH_SHORT).show()
        }
    }

    private fun launchVoiceCommand() {
        val intent = Intent(Intent.ACTION_VOICE_COMMAND).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "调起语音助手失败：$e", Toast.LENGTH_SHORT).show()
        }
    }

    // 兼顾全版本的无动画退出逻辑
    private fun finishWithNoAnim() {
        finish()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 14 (API 34) 及以上使用新 API
            // 参数 1: OVERRIDE_TRANSITION_CLOSE 代表关闭 Activity 时的动画 override
            // 参数 2 & 3: 0 代表无进入和退出动画
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0)
        } else {
            // Android 13 及以下继续使用旧 API
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }
    }
}