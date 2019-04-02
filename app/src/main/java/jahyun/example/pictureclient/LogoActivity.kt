package jahyun.example.pictureclient

import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.Process
import android.support.v7.app.AlertDialog
import kotlinx.android.synthetic.main.activity_logo.*

class LogoActivity : AppCompatActivity() {
    private lateinit var mWifiManager: WifiManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logo)

        mWifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        mWifiManager.isWifiEnabled = true
    }

    private inner class WifiStateThread : Thread() {
        override fun run() {
            val start = System.currentTimeMillis()
            try {
                mWifiStateHandler?.sendEmptyMessage(0)
                Thread.sleep(1500)
                while (true) {
                    if (mWifiManager.connectionInfo.rssi > -70) {
                        mWifiStateHandler?.sendEmptyMessage(1)
                        break
                    } else {
                        mWifiStateHandler?.sendEmptyMessage(0)
                    }

                    if (System.currentTimeMillis() - start >= 10000) {
                        mWifiStateHandler?.sendEmptyMessage(2)
                        break
                    }

                    Thread.sleep(100)
                }
            } catch (e: Exception) {
                
            }
        }
    }

    internal inner class WifiStateHandler : Handler() {
        override fun handleMessage(msg: Message?) {
            if (msg?.what == 0) {
                if (wifi_state_info.text.length == 0 || wifi_state_info.text.length >= 15) {
                    wifi_state_info.text = "Wi-Fi 연결 시도 중"
                } else {
                    wifi_state_info.append(".")
                }
            } else if (msg?.what == 1) {
                wifi_state_info.text = "Wi-Fi 연결 완료"
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                wifi_state_info.text = "Wi-Fi 연결 실패"
                val digBuilder = AlertDialog.Builder(this@LogoActivity)
                    .setTitle("Wifi Error")
                    .setMessage("연결할 수 있는 Wi-Fi가 없습니다.\n" + "애플리케이션을 종료합니다.")
                    .setPositiveButton("확인") { dialog, which ->
                        Process.killProcess(Process.myPid())
                    }
                    .setIcon(R.mipmap.ic_launcher)
                val dig = digBuilder.create()
                dig.show()
            }
        }
    }
}
