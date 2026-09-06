package com.example.wakegemini

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private var resultTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 96, 48, 96)
        }

        val title = TextView(this).apply {
            text = "MagicKey 测试"
            textSize = 24f
        }
        layout.addView(title)

        val info = TextView(this).apply {
            text = "\n长按桌面图标可选择“一键设置并调起”。\n也可在下方进行系统配置与测试。"
            textSize = 16f
        }
        layout.addView(info)

        // 按钮 1：写入 Settings.Secure 配置
        val btnSetService = Button(this).apply {
            text = "1. 设置 Google 语音服务 (Secure)"
            setOnClickListener { setGoogleVoiceService(layout) }
        }
        layout.addView(btnSetService)

        // 按钮 2：打开默认助手设置
        val btnOpenAssistantSettings = Button(this).apply {
            text = "2. 打开系统“默认助手”设置页"
            setOnClickListener { openAssistantSettings() }
        }
        layout.addView(btnOpenAssistantSettings)

        // 按钮 3：测试调起 VOICE_COMMAND
        val btnTestCommand = Button(this).apply {
            text = "3. 测试 VOICE_COMMAND"
            setOnClickListener { launchVoiceCommand() }
        }
        layout.addView(btnTestCommand)

        setContentView(layout)
    }

    private fun setGoogleVoiceService(layout: LinearLayout) {
        val targetService = "com.google.android.googlequicksearchbox/com.google.android.voiceinteraction.GsaVoiceInteractionService"
        try {
            val successVoice = Settings.Secure.putString(contentResolver, "voice_interaction_service", targetService)
            val successAssistant = Settings.Secure.putString(contentResolver, "assistant", targetService)

            if (successVoice && successAssistant) {
                showLog(layout, "设置成功！voice_interaction_service 与 assistant 均已绑定为 Google 服务。")
            } else {
                showLog(layout, "设置部分失败：voice=$successVoice, assistant=$successAssistant")
            }
        } catch (e: SecurityException) {
            showLog(layout, "权限不足！请在电脑终端执行以下命令授权：\n\nadb shell pm grant $packageName android.permission.WRITE_SECURE_SETTINGS")
        }
    }

    private fun openAssistantSettings() {
        try {
            startActivity(Intent(Settings.ACTION_VOICE_INPUT_SETTINGS))
        } catch (e: Exception) {
            try {
                startActivity(Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS))
            } catch (ex: Exception) {
                Toast.makeText(this, "无法自动打开设置界面，请手动搜索“默认助手”", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun launchVoiceCommand() {
        val intent = Intent(Intent.ACTION_VOICE_COMMAND).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "调起失败：\n$e", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLog(layout: LinearLayout, msg: String) {
        if (resultTextView == null) {
            resultTextView = TextView(this).apply { textSize = 14f }
            layout.addView(resultTextView)
        }
        resultTextView?.text = "\n$msg"
    }
}