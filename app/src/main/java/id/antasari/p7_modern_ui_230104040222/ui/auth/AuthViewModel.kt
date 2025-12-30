package id.antasari.p7_modern_ui_230104040222.ui.auth

import androidx.lifecycle.ViewModel
import id.antasari.p7_modern_ui_230104040222.StoredAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * State utama untuk Layar autentikasi (Login & Create Account)
 */
data class AuthUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val isSignedIn: Boolean = false,

    // Biometric
    val isBiometricAvailable: Boolean = false,
    val isBiometricEnabled: Boolean = false,

    // Kredensial terdaftar (hasil CreateAccount / Load storage)
    val registeredEmail: String? = null,
    val registeredPassword: String? = null,

    // Theme
    val isDarkTheme: Boolean = false,

    // App Lock
    val appLockEnabled: Boolean = true,

    // Security alert toggles
    val loginAlertsEnabled: Boolean = true,
    val newDeviceAlertsEnabled: Boolean = true,
    val publicWifiWarningEnabled: Boolean = true,

    // Pesan error terakhir (jika ada)
    val lastErrorMessage: String? = null
)

/**
 * ViewModel untuk mengelola Logika autentikasi dan pengaturan keamanan
 */
class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // --------- Helper internal ---------
    private fun updateState(block: (AuthUiState) -> AuthUiState) {
        _uiState.value = block(_uiState.value)
    }

    // --------- Input handler (dipanggil dari UI) ---------
    fun onNameChange(newName: String) {
        updateState { it.copy(name = newName, lastErrorMessage = null) }
    }

    fun onEmailChange(newEmail: String) {
        updateState { it.copy(email = newEmail, lastErrorMessage = null) }
    }

    fun onPasswordChange(newPassword: String) {
        updateState { it.copy(password = newPassword, lastErrorMessage = null) }
    }

    fun onConfirmPasswordChange(newPassword: String) {
        updateState { it.copy(confirmPassword = newPassword, lastErrorMessage = null) }
    }

    fun clearError() {
        updateState { it.copy(lastErrorMessage = null) }
    }

    // --------- Login dengan password ---------
    fun signInWithPassword(onSuccess: () -> Unit) {
        val current = _uiState.value

        // 1. Harus ada akun terdaftar
        if (current.registeredEmail.isNullOrBlank() || current.registeredPassword.isNullOrBlank()) {
            updateState {
                it.copy(
                    isLoading = false,
                    lastErrorMessage = "Belum ada akun terdaftar. Silakan buat akun terlebih dahulu."
                )
            }
            return
        }

        // 2. Input tidak boleh kosong
        if (current.email.isBlank() || current.password.isBlank()) {
            updateState {
                it.copy(
                    isLoading = false,
                    lastErrorMessage = "Email dan password tidak boleh kosong."
                )
            }
            return
        }

        // 3. Validasi: hanya akun yang terdaftar yang boleh login
        if (current.email != current.registeredEmail || current.password != current.registeredPassword) {
            updateState {
                it.copy(
                    isLoading = false,
                    lastErrorMessage = "Email atau password tidak sesuai dengan akun yang terdaftar."
                )
            }
            return
        }

        // 4. Sukses login
        updateState {
            it.copy(
                isLoading = false,
                isSignedIn = true,
                lastErrorMessage = null
            )
        }
        onSuccess()
    }

    /**
     * Create account:
     * - validasi nama, email, password, konfirmasi
     * - menyimpan ke state
     * - mengaktifkan biometric untuk akun ini (flag)
     * - menyimpan registeredEmail & registeredPassword (untuk validasi login)
     *
     * return true jika registrasi valid, false jika error.
     */
    fun createAccount(): Boolean {
        val current = _uiState.value

        if (
            current.name.isBlank() ||
            current.email.isBlank() ||
            current.password.isBlank() ||
            current.confirmPassword.isBlank()
        ) {
            updateState {
                it.copy(lastErrorMessage = "Nama, email, password, dan konfirmasi password wajib diisi.")
            }
            return false
        }

        if (current.password != current.confirmPassword) {
            updateState {
                it.copy(lastErrorMessage = "Konfirmasi password tidak sama.")
            }
            return false
        }

        updateState {
            it.copy(
                isSignedIn = false,
                isBiometricEnabled = true,
                lastErrorMessage = null,
                registeredEmail = current.email,
                registeredPassword = current.password
            )
        }
        return true
    }

    // --------- Logout & Clear Account ---------
    fun signOut() {
        updateState {
            it.copy(
                isSignedIn = false,
                password = "",
                confirmPassword = "",
                lastErrorMessage = null
                // registeredEmail & registeredPassword dibiarkan,
                // supaya akun tetap ada selama belum di-delete dari storage
            )
        }
    }

    fun clearAccount() {
        updateState {
            AuthUiState(
                isBiometricAvailable = it.isBiometricAvailable // tetap baca dari device
            )
        }
    }

    // --------- Biometric: state & callback ---------
    fun setBiometricAvailability(available: Boolean) {
        updateState { it.copy(isBiometricAvailable = available) }
    }

    fun setBiometricEnabled(enabled: Boolean) {
        updateState { it.copy(isBiometricEnabled = enabled) }
    }

    fun onBiometricAuthenticated() {
        updateState {
            it.copy(
                isSignedIn = true,
                lastErrorMessage = null
            )
        }
    }

    fun onBiometricError(message: String) {
        updateState { it.copy(lastErrorMessage = message) }
    }

    // --------- App Lock ---------
    fun setAppLockEnabled(enabled: Boolean) {
        updateState { it.copy(appLockEnabled = enabled) }
    }

    fun forceLock() {
        updateState { it.copy(isSignedIn = false) }
    }

    // --------- Theme ---------
    fun setDarkTheme(enabled: Boolean) {
        updateState { it.copy(isDarkTheme = enabled) }
    }

    // --------- Security Alerts ---------
    fun setLoginAlertsEnabled(enabled: Boolean) {
        updateState { it.copy(loginAlertsEnabled = enabled) }
    }

    fun setNewDeviceAlertsEnabled(enabled: Boolean) {
        updateState { it.copy(newDeviceAlertsEnabled = enabled) }
    }

    fun setPublicWifiWarningEnabled(enabled: Boolean) {
        updateState { it.copy(publicWifiWarningEnabled = enabled) }
    }

    // --------- Restore dari storage ---------
    fun restoreAccountFromStorage(account: StoredAccount, biometricAvailable: Boolean) {
        updateState {
            it.copy(
                name = account.name,
                email = account.email,
                registeredEmail = account.email,
                registeredPassword = account.password,
                isBiometricEnabled = account.biometricEnabled && biometricAvailable,
                isDarkTheme = account.isDarkTheme,
                appLockEnabled = account.appLockEnabled,
                loginAlertsEnabled = account.loginAlertsEnabled,
                newDeviceAlertsEnabled = account.newDeviceAlertsEnabled,
                publicWifiWarningEnabled = account.publicWifiWarningEnabled
            )
        }
    }
}