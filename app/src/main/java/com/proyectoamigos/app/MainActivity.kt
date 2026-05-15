package com.proyectoamigos.app

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.proyectoamigos.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val homeUrl = "https://proyectoamigos.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        // Splash screen API (Android 12+ nativo, retro-compat para más viejos)
        val splash = installSplashScreen()

        // Edge-to-edge para que la web aproveche toda la pantalla
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mantén el splash visible hasta el primer paint de la web
        var contentReady = false
        splash.setKeepOnScreenCondition { !contentReady }

        // Insets — empuja la WebView dentro del área segura
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or
                WindowInsetsCompat.Type.displayCutout()
            )
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        setupWebView { contentReady = true }
        setupPullToRefresh()
        setupBackNavigation()

        if (savedInstanceState == null) {
            binding.webview.loadUrl(homeUrl)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(onFirstPaint: () -> Unit) {
        val web = binding.webview

        web.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            cacheMode = WebSettings.LOAD_DEFAULT
            mediaPlaybackRequiresUserGesture = false
            useWideViewPort = true
            loadWithOverviewMode = true
            setSupportZoom(false)
            builtInZoomControls = false
            displayZoomControls = false
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            userAgentString = "${userAgentString} ProyectoAmigosApp/${BuildConfig.VERSION_NAME}"
        }

        // Fondo transparente para que el splash y la app coincidan visualmente
        web.setBackgroundColor(Color.parseColor("#06070A"))

        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(web, true)

        web.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                val url = request.url
                return when {
                    // Mantener navegación dentro del dominio en la app
                    url.host?.endsWith("proyectoamigos.com") == true -> false
                    // mailto, tel, intents → app externa
                    url.scheme in listOf("mailto", "tel", "sms", "geo", "intent") ->
                        openExternal(url)
                    // Cualquier otro http(s) → navegador del sistema
                    url.scheme in listOf("http", "https") -> openExternal(url)
                    else -> false
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.swipeRefresh.isRefreshing = false
                onFirstPaint()
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: android.webkit.WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                // Solo trata como error si falla el frame principal
                if (request?.isForMainFrame == true) {
                    onFirstPaint() // libera el splash aunque haya fallado
                }
            }
        }

        web.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                binding.progress.progress = newProgress
                binding.progress.visibility =
                    if (newProgress in 1..99) View.VISIBLE else View.GONE
            }
        }
    }

    private fun setupPullToRefresh() {
        binding.swipeRefresh.setColorSchemeColors(
            Color.parseColor("#7A5CFF"),
            Color.parseColor("#00C8FF"),
            Color.parseColor("#00FFB2")
        )
        binding.swipeRefresh.setProgressBackgroundColorSchemeColor(
            Color.parseColor("#111318")
        )
        binding.swipeRefresh.setOnRefreshListener {
            binding.webview.reload()
        }
    }

    private fun setupBackNavigation() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.webview.canGoBack()) {
                    binding.webview.goBack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun openExternal(uri: Uri): Boolean {
        return try {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
            true
        } catch (_: ActivityNotFoundException) {
            false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.webview.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.webview.restoreState(savedInstanceState)
    }

    override fun onPause() {
        super.onPause()
        binding.webview.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.webview.onResume()
    }

    override fun onDestroy() {
        binding.webview.apply {
            stopLoading()
            loadUrl("about:blank")
            clearHistory()
            destroy()
        }
        super.onDestroy()
    }
}
