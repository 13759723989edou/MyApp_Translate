package com.androidstudio_2024_vision.myapp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.View
import retrofit2.Retrofit
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.androidstudio_2024_vision.myapp.databinding.ActivityMainBinding
import com.androidstudio_2024_vision.myapp.databinding.AppBarMainBinding
import com.androidstudio_2024_vision.myapp.databinding.ContentMainBinding
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Response
import java.net.URL
import retrofit2.Callback
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Field
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.security.MessageDigest
import java.util.LinkedList
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    var fromLanguage = "aoto" //目标语言
    var toLanguage = "auto" //翻译语言
    val appId = "20240826002132755"
    val key = "uUnQQAsgQKUFPPZL3HZH"
    var myClipboard: ClipboardManager? = null // 复制文本
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var appBarMainBinding: AppBarMainBinding
    private lateinit var contentMainBinding: ContentMainBinding
    private lateinit var tvTranslation: TextView
    private lateinit var edContent: EditText
    private lateinit var iVClear: ImageView
    private lateinit var result_lay: View
    private lateinit var tv_result: TextView
    private lateinit var iv_clear_tx:ImageView
    private lateinit var baiduTranslateService: BaiduTranslateService
    private val TAG = "MainActivity"
    private lateinit var  iv_copy_tx :ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
//        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化 ActivityMainBinding、AppBarMainBinding、ContentMainBinding
        activityMainBinding = binding
        appBarMainBinding = activityMainBinding.appBarMain
        // 初始化ContentMain 中的控件
        contentMainBinding = appBarMainBinding.contentMain
        tvTranslation = contentMainBinding.tvTranslation
        edContent = contentMainBinding.edContent
        iVClear = contentMainBinding.ivClearTx
        result_lay = contentMainBinding.resultLay
        tv_result = contentMainBinding.tvResult
        iv_clear_tx = contentMainBinding.ivClearTx

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        onClick()
        //输入框监听
        editTextListener()

        // 初始化Retrofit配置
        val retrofitBaidu = Retrofit.Builder()
            .baseUrl("https://fanyi-api.baidu.com/api/trans/vip/")
            .addConverterFactory(GsonConverterFactory.create())  //添加Gson 转换器
            .build()

        baiduTranslateService = retrofitBaidu.create(BaiduTranslateService::class.java)

    }

    /**
     * 输入框监听
     */
    private fun editTextListener() {
        edContent.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                iVClear.visibility = View.VISIBLE
                val content = edContent.text.toString().trim()
                if (content.isEmpty()) {
                    result_lay.visibility = View.GONE
                    tvTranslation.visibility = View.VISIBLE
//                    before_lay.visibility = View.VISIBLE
//                    after_lay.visibility = View.GONE
                    iVClear.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })
    }

    /**
     * 点击
     */
    private fun onClick() {
        tvTranslation.setOnClickListener { //翻译
            transition()
        }
        iv_clear_tx.setOnClickListener {//清空输入框
            edContent.text.clear()
        }
        iv_copy_tx = contentMainBinding.ivCopyTx
        val myClipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        iv_copy_tx.setOnClickListener {//复制文本
            val result = tv_result.text.toString()
            myClipboard!!.setPrimaryClip(ClipData.newPlainText("text", result))
            showMsg("已复制")
        }
    }
    private fun transition() {
        val word = edContent.text.toString().trim() // 需查询的单词 q
        val from = "auto" // 源语种 en 英语 zh 中文
        var to = "en" // 目标语种，根据实际情况设置
        val appid = "20240826002132755" // 百度创建的应用的翻译API的appid
        val salt = (Math.random() * 100 + 1).toInt() // 随机数这里范围是[0,100]整数无强制要求
        val key = "uUnQQAsgQKUFPPZL3HZH" // 百度翻译API的密钥
        val secretKey = "$appid$word$salt$key" // 拼接的密钥
        val sign = MD5Utils.md5(secretKey) // MD5加密

        Log.d(TAG, "secretKey: $secretKey")
        Log.d(TAG, "sign: $sign")

        val call = baiduTranslateService.translate(word, from, to, appid, salt.toString(), sign)

        call.enqueue(object : Callback<TranslateResult> {
            override fun onResponse(call: Call<TranslateResult>, response: Response<TranslateResult>) {

                // 打印原始的未处理响应
                Log.d(TAG, "原始响应内容: ${response.raw()}")

                // 继续解析响应
                Log.d(TAG, "完整响应内容: ${response.body()}")

                if (response.isSuccessful) {
                    val result = response.body()?.trans_result?.get(0)?.dst
                    runOnUiThread {
//                        tvTranslation.text = "翻译结果：$result"
                        tvTranslation.visibility = View.GONE
                        //显示翻译的结果
                        tv_result.text = response.body()?.trans_result!![0].dst
                        result_lay.visibility = View.VISIBLE
                    }
                    Log.d(TAG, "翻译结果: $result")
                } else {  //请求不成功，状态码非 2xx
                    runOnUiThread {
                        tvTranslation.text = "翻译失败：${response.errorBody()?.string()}"
                    }
                    result_lay.visibility = View.VISIBLE
                    Log.d(TAG, "请求失败: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<TranslateResult>, t: Throwable) {
                Log.d(TAG, "请求失败: ${t.message}")
            }
        })
    }

    /**
     * Toast提示
     * @param msg 提示内容
     */
    private fun showMsg(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }


}