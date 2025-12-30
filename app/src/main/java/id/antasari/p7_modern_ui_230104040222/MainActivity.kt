package id.antasari.p7_modern_ui_230104040222

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import id.antasari.p7_modern_ui_230104040222.ui.auth.AuthViewModel
import id.antasari.p7_modern_ui_230104040222.ui.auth.SecureAuthApp
import id.antasari.p7_modern_ui_230104040222.ui.theme.P7ModernUiTheme

class MainActivity : FragmentActivity() {

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private val authViewModel: AuthViewModel by viewModels()

    private var lastBackgroundAt: Long? = null
    private val APP_LOCK_TIMEOUT_MS = 30_000L // 30 detik

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // --- Sembunyikan System Bars ---
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        // Sembunyikan Status Bar dan Navigation Bar
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        // 1. Cek biometric
        val isBiometricReady = BiometricUtils.isBiometricReady(this)
        authViewModel.setBiometricAvailability(isBiometricReady)

        // 2. Load akun dari storage (jika ada)
        val storedAccount = AccountStorage.loadAccount(this)
        if (storedAccount != null) {
            authViewModel.restoreAccountFromStorage(
                account = storedAccount,
                biometricAvailable = isBiometricReady
            )
        }

        // 3. Siapkan BiometricPrompt
        val executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    authViewModel.onBiometricAuthenticated()

                    val current = authViewModel.uiState.value
                    if (!current.registeredEmail.isNullOrBlank()) {
                        AccountStorage.saveAccount(
                            context = this@MainActivity,
                            name = current.name,
                            email = current.registeredEmail ?: current.email,
                            password = current.registeredPassword ?: current.password,
                            biometricEnabled = current.isBiometricEnabled,
                            isDarkTheme = current.isDarkTheme,
                            appLockEnabled = current.appLockEnabled,
                            loginAlertsEnabled = current.loginAlertsEnabled,
                            newDeviceAlertsEnabled = current.newDeviceAlertsEnabled,
                            publicWifiWarningEnabled = current.publicWifiWarningEnabled
                        )
                    }

                    Toast.makeText(
                        this@MainActivity,
                        "Login dengan biometrik berhasil",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    authViewModel.onBiometricError(errString.toString())
                    Toast.makeText(
                        this@MainActivity,
                        "Biometric error: $errString",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        this@MainActivity,
                        "Biometrik tidak dikenali",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

        // 4. Konfigurasi prompt info
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.BIOMETRIC_WEAK

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Login dengan Biometrik")
            .setSubtitle("Gunakan sidik jari / wajah yang sudah terdaftar")
            .setNegativeButtonText("Gunakan password")
            .setAllowedAuthenticators(authenticators)
            .build()

        // 5. Jalankan Compose dengan theme yang mengikuti state
        setContent {
            val uiState by authViewModel.uiState.collectAsState()

            P7ModernUiTheme(darkTheme = uiState.isDarkTheme) {
                SecureAuthApp(
                    authViewModel = authViewModel,
                    onBiometricClick = {
                        val state = authViewModel.uiState.value
                        if (isBiometricReady && state.isBiometricEnabled) {
                            biometricPrompt.authenticate(promptInfo)
                        } else {
                            Toast.makeText(
                                this,
                                "Biometrik belum siap atau belum diaktifkan.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )
            }
        }
    }

    // App Lock: catat kapan ke background
    override fun onStop() {
        super.onStop()
        val state = authViewModel.uiState.value
        if (state.isSignedIn && state.appLockEnabled) {
            lastBackgroundAt = System.currentTimeMillis()
        }
    }

    override fun onStart() {
        super.onStart()
        maybeLockApp()
    }

    private fun maybeLockApp() {
        val last = lastBackgroundAt ?: return
        val state = authViewModel.uiState.value

        if (!state.appLockEnabled || !state.isSignedIn) return

        val elapsed = System.currentTimeMillis() - last
        if (elapsed >= APP_LOCK_TIMEOUT_MS) {
            // Lock session
            authViewModel.forceLock()
            lastBackgroundAt = null

            // Jika biometric aktif & ready, langsung minta fingerprint
            if (state.isBiometricEnabled && BiometricUtils.isBiometricReady(this)) {
                biometricPrompt.authenticate(promptInfo)
            }
        }
    }
}