package com.example.wakegemini

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var resultTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. 创建主容器（垂直线性布局）
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
        }

        // 2. 标题
        val title = TextView(this).apply {
            text = "MagicKey 测试"
            textSize = 24f
        }
        layout.addView(title)

        // 3. 说明文案（对应 shortcuts.xml 中的“语音指令”）
        val info = TextView(this).apply {
            text = "\n长按桌面上的本 App 图标，应该出现“语音指令”。\n\n点击下面按钮也可以直接测试 VOICE_COMMAND。"
            textSize = 16f
        }
        layout.addView(info)

        // 4. 测试按钮
        val button = Button(this).apply {
            text = "测试 VOICE_COMMAND"
            setOnClickListener {
                val intent = Intent(Intent.ACTION_VOICE_COMMAND).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    // 启动失败时，动态添加/更新错误提示
                    if (resultTextView == null) {
                        resultTextView = TextView(this@MainActivity).apply {
                            textSize = 16f
                        }
                        layout.addView(resultTextView)
                    }
                    resultTextView?.text = "\n启动失败：\n$e"
                }
            }
        }
        layout.addView(button)

        // 5. 设置视图
        setContentView(layout)
    }
}