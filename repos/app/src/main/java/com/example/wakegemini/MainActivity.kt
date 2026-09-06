package com.example.wakegemini

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var resultTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
        }

        val title = TextView(this).apply {
            text = "MagicKey 测试"
            textSize = 24f
        }
        layout.addView(title)

        val info = TextView(this).apply {
            text = "\n长按桌面图标弹出“语音指令”。\n也可在下方直接配置与测试。"
            textSize = 16f
        }
        layout.addView(info)

        // 按钮 1：配置系统 Voice Interaction Service
        val btnSetService = Button(this).apply {
            text = "1. 设置 Google 语音服务"
            setOnClickListener {
                setGoogleVoiceService(layout)
            }
        }
        layout.addView(btnSetService)

        // 按钮 2：测试调起 VOICE_COMMAND
        val btnTestCommand = Button(this).apply {
            text = "2. 测试 VOICE_COMMAND"
            setOnClickListener {
                val intent = Intent(Intent.ACTION_VOICE_COMMAND).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    showLog(layout, "调起失败：\n$e")
                }
            }
        }
        layout.addView(btnTestCommand)

        setContentView(layout)
    }

    // 调用系统 API 修改 secure 配置
    private fun setGoogleVoiceService(layout: LinearLayout) {
        val targetService = "com.google.android.googlequicksearchbox/com.google.android.voiceinteraction.GsaVoiceInteractionService"
        try {
            val success = Settings.Secure.putString(
                contentResolver,
                "voice_interaction_service",
                targetService
            )
            if (success) {
                showLog(layout, "设置成功！当前语音服务已绑定为 Google Quick Search Box。")
            } else {
                showLog(layout, "设置失败：系统未接受此修改。")
            }
        } catch (e: SecurityException) {
            showLog(layout, "权限不足！请在电脑终端执行以下命令授权：\n\nadb shell pm grant $packageName android.permission.WRITE_SECURE_SETTINGS")
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