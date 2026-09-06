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

        // 1. 初始化界面布局
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
            text = "\n长按桌面图标可选择快捷方式。\n也可在下方进行系统配置与测试。"
            textSize = 16f
        }
        layout.addView(info)

        // 按钮 1：写入 Settings.Secure 配置 (voice_interaction_service + assistant)
        val btnSetService = Button(this).apply {
            text = "1. 设置 Google 语音服务 (Secure)"
            setOnClickListener {
                setGoogleVoiceService(layout)
            }
        }
        layout.addView(btnSetService)

        // 按钮 2：跳转到系统默认语音助手设置页面
        val btnOpenAssistantSettings = Button(this).apply {
            text = "2. 打开系统“默认助手”设置页"
            setOnClickListener {
                openAssistantSettings()
            }
        }
        layout.addView(btnOpenAssistantSettings)

        // 按钮 3：测试调起 VOICE_COMMAND
        val btnTestCommand = Button(this).apply {
            text = "3. 测试 VOICE_COMMAND"
            setOnClickListener {
                launchVoiceCommand()
            }
        }
        layout.addView(btnTestCommand)

        setContentView(layout)

        // 2. 检测是否通过桌面快捷方式“一键设置并调起”启动[cite: 1]
        if (intent?.action == "com.example.wakegemini.ACTION_SET_AND_VOICE") {
            performSetAndLaunch(layout)
        }
    }

    // 调用系统 API 修改 Secure 配置（写入 voice_interaction_service 和 assistant）
    private fun setGoogleVoiceService(layout: LinearLayout) {
        val targetService = "com.google.android.googlequicksearchbox/com.google.android.voiceinteraction.GsaVoiceInteractionService"
        try {
            // 设置 voice_interaction_service
            val successVoice = Settings.Secure.putString(
                contentResolver,
                "voice_interaction_service",
                targetService
            )

            // 设置 assistant
            val successAssistant = Settings.Secure.putString(
                contentResolver,
                "assistant",
                targetService
            )

            if (successVoice && successAssistant) {
                showLog(layout, "设置成功！voice_interaction_service 与 assistant 均已绑定为 Google 服务。")
            } else {
                showLog(layout, "设置部分失败：voice=$successVoice, assistant=$successAssistant")
            }
        } catch (e: SecurityException) {
            showLog(layout, "权限不足！请在电脑终端执行以下命令授权：\n\nadb shell pm grant $packageName android.permission.WRITE_SECURE_SETTINGS")
        }
    }

    // 跳转到系统的“默认语音助手/默认应用”设置页面
    private fun openAssistantSettings() {
        try {
            // 优先跳转到语音输入与助手设置
            val intent = Intent(Settings.ACTION_VOICE_INPUT_SETTINGS)
            startActivity(intent)
        } catch (e: Exception) {
            try {
                // 部分定制系统可能不支持，尝试降级跳转到默认应用设置页面
                val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
                startActivity(intent)
            } catch (ex: Exception) {
                Toast.makeText(this, "无法自动打开设置界面，请在系统设置中搜索“默认助手”", Toast.LENGTH_LONG).show()
            }
        }
    }

    // 发起 VOICE_COMMAND 调起
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

    // 一键组合逻辑：先修改设置，再拉起语音
    private fun performSetAndLaunch(layout: LinearLayout) {
        setGoogleVoiceService(layout)
        launchVoiceCommand()
    }

    private fun showLog(layout: LinearLayout, msg: String) {
        if (resultTextView == null) {
            resultTextView = TextView(this).apply { textSize = 14f }
            layout.addView(resultTextView)
        }
        resultTextView?.text = "\n$msg"
    }
}