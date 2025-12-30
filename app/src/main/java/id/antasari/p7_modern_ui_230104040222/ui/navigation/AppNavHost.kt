package id.antasari.p7_modern_ui_230104040222.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import id.antasari.p7_modern_ui_230104040222.AccountStorage
import id.antasari.p7_modern_ui_230104040222.CreateAccountScreen
import id.antasari.p7_modern_ui_230104040222.HomeScreen
import id.antasari.p7_modern_ui_230104040222.LoginScreen
import id.antasari.p7_modern_ui_230104040222.SecurityDetailsScreen
import id.antasari.p7_modern_ui_230104040222.SettingsScreen
import id.antasari.p7_modern_ui_230104040222.ui.auth.AuthViewModel

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val SECURITY_DETAILS = "security_details"
    const val SETTINGS = "settings"
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    onBiometricClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by authViewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Begitu isSignedIn = true -> pindah ke HOME
    LaunchedEffect(uiState.isSignedIn) {
        if (uiState.isSignedIn) {
            navController.navigate(Routes.HOME) {
                popUpTo(Routes.LOGIN) { inclusive = true }
            }
        } else {
            // Jika tidak signed in, pastikan stack kembali ke LOGIN
            navController.navigate(Routes.LOGIN) {
                popUpTo(Routes.LOGIN) { inclusive = true }
            }
        }
    }

    val displayName = uiState.name.ifBlank { "User" }

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN,
        modifier = modifier
    ) {
        // LOGIN
        composable(Routes.LOGIN) {
            LoginScreen(
                uiState = uiState,
                onEmailChange = authViewModel::onEmailChange,
                onPasswordChange = authViewModel::onPasswordChange,
                onSignInClick = { authViewModel.signInWithPassword(onSuccess = {}) },
                onForgotPasswordClick = { /* TODO */ },
                onCreateAccountClick = { navController.navigate(Routes.REGISTER) },
                onBiometricClick = { onBiometricClick() }
            )
        }

        // REGISTER
        composable(Routes.REGISTER) {
            CreateAccountScreen(
                onSignUpClick = { name, email, password ->
                    authViewModel.onNameChange(name)
                    authViewModel.onEmailChange(email)
                    authViewModel.onPasswordChange(password)
                    authViewModel.onConfirmPasswordChange(password)

                    val success = authViewModel.createAccount()
                    if (success) {
                        val current = authViewModel.uiState.value
                        AccountStorage.saveAccount(
                            context = context,
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
                        // --- Setelah sukses simpan, pindah ke LOGIN ---
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.REGISTER) { inclusive = true }
                        }
                    }
                },
                // --- Aksi navigasi pada tombol back/sign-in ---
                onSignInClick = {
                    navController.popBackStack()
                }
            )
        }

        // HOME
        composable(Routes.HOME) {
            HomeScreen(
                userName = displayName,
                onLogoutClick = { authViewModel.signOut() },
                onOpenSecurityDetailsClick = { navController.navigate(Routes.SECURITY_DETAILS) },
                onOpenSettingsClick = { navController.navigate(Routes.SETTINGS) }
            )
        }

        // SECURITY DETAILS
        composable(Routes.SECURITY_DETAILS) {
            SecurityDetailsScreen(
                userName = displayName,
                onBackClick = { navController.popBackStack() }
            )
        }

        // SETTINGS
        composable(Routes.SETTINGS) {
            SettingsScreen(
                userName = displayName,
                isBiometricEnabled = uiState.isBiometricEnabled,
                appLockEnabled = uiState.appLockEnabled,
                isDarkTheme = uiState.isDarkTheme,
                loginAlertsEnabled = uiState.loginAlertsEnabled,
                newDeviceAlertsEnabled = uiState.newDeviceAlertsEnabled,
                publicWifiWarningEnabled = uiState.publicWifiWarningEnabled,
                onBiometricToggle = { enabled ->
                    authViewModel.setBiometricEnabled(enabled)
                    val current = authViewModel.uiState.value
                    if (!current.registeredEmail.isNullOrBlank()) {
                        AccountStorage.saveAccount(
                            context = context,
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
                    if (enabled) { onBiometricClick() }
                },
                onAppLockToggle = { enabled ->
                    authViewModel.setAppLockEnabled(enabled)
                    val current = authViewModel.uiState.value
                    if (!current.registeredEmail.isNullOrBlank()) {
                        AccountStorage.saveAccount(
                            context = context,
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
                },
                onDarkThemeToggle = { enabled ->
                    authViewModel.setDarkTheme(enabled)
                    val current = authViewModel.uiState.value
                    if (!current.registeredEmail.isNullOrBlank()) {
                        AccountStorage.saveAccount(
                            context = context,
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
                },
                onLoginAlertsToggle = { enabled ->
                    authViewModel.setLoginAlertsEnabled(enabled)
                    val current = authViewModel.uiState.value
                    if (!current.registeredEmail.isNullOrBlank()) {
                        AccountStorage.saveAccount(
                            context = context,
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
                },
                onNewDeviceAlertsToggle = { enabled ->
                    authViewModel.setNewDeviceAlertsEnabled(enabled)
                    val current = authViewModel.uiState.value
                    if (!current.registeredEmail.isNullOrBlank()) {
                        AccountStorage.saveAccount(
                            context = context,
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
                },
                onPublicWifiWarningToggle = { enabled ->
                    authViewModel.setPublicWifiWarningEnabled(enabled)
                    val current = authViewModel.uiState.value
                    if (!current.registeredEmail.isNullOrBlank()) {
                        AccountStorage.saveAccount(
                            context = context,
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
                },
                onDeleteAccountClick = {
                    authViewModel.clearAccount()
                    AccountStorage.clearAccount(context)
                    navController.navigate(Routes.REGISTER) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}