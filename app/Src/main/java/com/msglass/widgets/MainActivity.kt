package com.ms.glasswidgets

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.provider.Settings
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {
    private val bg = Color.rgb(13, 23, 41)
    private val card = Color.rgb(35, 48, 72)
    private val pale = Color.rgb(241, 245, 255)
    private val blue = Color.rgb(177, 202, 255)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        val scroll = ScrollView(this)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(26), dp(20), dp(28))
            setBackgroundColor(bg)
        }
        scroll.addView(root)
        root.addView(TextView(this).apply {
            text = "MS Glass Widgets"
            textSize = 29f
            setTextColor(pale)
            typeface = Typeface.DEFAULT_BOLD
        })
        root.addView(TextView(this).apply {
            text = "Widget kính mờ • Ưu tiên nhạc và âm lượng"
            textSize = 14f
            setTextColor(blue)
            setPadding(0, dp(6), 0, dp(20))
        })
        addCard(root, "01  •  Trình phát nhạc", "Tự nhận phiên phát nhạc Android tương thích, ảnh bìa, tên bài hát, nghệ sĩ, tiến trình và nút điều khiển.", "Cấp quyền nhận diện nhạc") {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }
        addCard(root, "02  •  Media Stage", "Widget lớn: ảnh bìa, tên bài hát, thanh tiến trình và nút bài trước / phát-tạm dừng / bài tiếp.", "Cách thêm widget") { showWidgetHelp("Media Stage") }
        addCard(root, "03  •  Media Book", "Widget ngang gọn hơn, đặt ảnh bìa cạnh thông tin bài hát và nút điều khiển.", "Cách thêm widget") { showWidgetHelp("Media Book") }
        addCard(root, "04  •  Media Wave", "Thanh điều khiển nhạc nhỏ, phù hợp đặt ở cạnh dưới màn hình chính.", "Cách thêm widget") { showWidgetHelp("Media Wave") }
        addCard(root, "05  •  Âm lượng kính", "Widget riêng có mức âm lượng, nút tắt tiếng, giảm và tăng âm lượng đa phương tiện.", "Cách thêm widget") { showWidgetHelp("Âm lượng") }
        root.addView(TextView(this).apply {
            text = "Lưu ý: để nhận bài hát từ ứng dụng khác, hãy bật quyền thông báo cho MS Glass Widgets. Sau đó thêm widget qua màn hình chính: nhấn giữ vùng trống → Widget → MS Glass Widgets."
            setTextColor(Color.rgb(190, 203, 225))
            textSize = 13f
            setPadding(dp(4), dp(8), dp(4), dp(12))
        })
        setContentView(scroll)
    }

    private fun addCard(root: LinearLayout, title: String, desc: String, buttonText: String, action: () -> Unit) {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(15), dp(16), dp(15))
            background = getDrawable(R.drawable.bg_glass)
        }
        box.addView(TextView(this).apply {
            text = title; textSize = 18f; setTextColor(pale); typeface = Typeface.DEFAULT_BOLD
        })
        box.addView(TextView(this).apply {
            text = desc; textSize = 14f; setTextColor(Color.rgb(205, 217, 238)); setPadding(0, dp(7), 0, dp(12))
        })
        box.addView(Button(this).apply {
            text = buttonText; setTextColor(Color.rgb(19, 38, 68)); setBackgroundColor(blue); setOnClickListener { action() }
        })
        root.addView(box, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { bottomMargin = dp(13) })
    }

    private fun showWidgetHelp(label: String) {
        android.app.AlertDialog.Builder(this)
            .setTitle(label)
            .setMessage("Nhấn giữ vùng trống trên màn hình chính → Widget → MS Glass Widgets → chọn “$label” rồi kéo ra màn hình. Nếu chưa thấy, đóng và mở lại danh sách widget sau khi cài ứng dụng.")
            .setPositiveButton("Đã hiểu", null).show()
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
}
