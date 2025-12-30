package id.antasari.p7_modern_ui_230104040222.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import id.antasari.p7_modern_ui_230104040222.ui.navigation.AppNavHost

/**
 * Root composable untuk modul SecureAuth.
 *
 * - Menginisialisasi NavController
 * - Meneruskan AuthViewModel ke AppNavHost
 * - Meneruskan callback onBiometricClick dari Activity ke LoginScreen
 */
@Composable
fun SecureAuthApp(
    authViewModel: AuthViewModel,
    onBiometricClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    MaterialTheme {
        Surface(modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .statusBarsPadding()) {
            AppNavHost(
                navController = navController,
                authViewModel = authViewModel,
                onBiometricClick = onBiometricClick
            )
        }
    }
}