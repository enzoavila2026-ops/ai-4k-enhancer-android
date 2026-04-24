package com.tuapp.enhancer4k

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AI4KEnhancerTheme {
                MainScreen()
            }
        }
    }
}

// --- Tema consistente con colors.xml ---
private val BackgroundDark = Color(0xFF0F1219)
private val BackgroundCard = Color(0xFF1A1F2C)
private val BackgroundInput = Color(0xFF1E2430)
private val SurfaceLight = Color(0xFF151B26)
private val AccentPrimary = Color(0xFF2060CF)
private val AccentSecondary = Color(0xFF1A4FB0)
private val AccentGlow = Color(0xFF3F80FF)
private val TextPrimary = Color.White
private val TextSecondary = Color(0xFFB0C8FF)
private val TextTertiary = Color(0xFF50658A)
private val BorderSubtle = Color(0xFF323A4A)
private val BorderActive = Color(0xFF7E9FD9)
private val ProgressTrack = Color(0xFF262F42)

@Composable
fun AI4KEnhancerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = BackgroundDark,
            surface = BackgroundCard,
            primary = AccentPrimary,
            secondary = AccentSecondary,
            onBackground = TextPrimary,
            onSurface = TextPrimary,
            onPrimary = Color.White
        ),
        content = content
    )
}

// --- Pantalla principal ---
@Composable
fun MainScreen() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        
        // Barra de estado simulada
        StatusBar()
        Spacer(modifier = Modifier.height(8.dp))
        
        // Header
        AppHeader()
        Spacer(modifier = Modifier.height(4.dp))
        
        // Créditos
        CreditBadge(available = 12)
        Spacer(modifier = Modifier.height(16.dp))
        
        // Vista previa
        ImagePreview()
        Spacer(modifier = Modifier.height(14.dp))
        
        // Última mejora
        RecentEnhancement()
        Spacer(modifier = Modifier.height(20.dp))
        
        // Panel de controles
        ControlsPanel()
        Spacer(modifier = Modifier.height(24.dp))
        
        // Comparación mock
        ComparisonMock()
        Spacer(modifier = Modifier.height(16.dp))
        
        // Navegación inferior
        BottomNavBar()
        Spacer(modifier = Modifier.height(8.dp))
        
        // Pie
        FootNote()
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun StatusBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("10:28", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Text("📶 🔋 92%", color = TextPrimary, fontSize = 14.sp)
    }
}

@Composable
fun AppHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "AI 4K Enhancer",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge.copy(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFC0E0FF), Color(0xFFA0C8FF))
                )
            ),
            color = Color(0xFFC0E0FF) // fallback
        )
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(30))
                .background(BackgroundInput)
                .border(1.dp, BorderSubtle, RoundedCornerShape(30)),
            contentAlignment = Alignment.Center
        ) {
            Text("⚙️", fontSize = 20.sp)
        }
    }
}

@Composable
fun CreditBadge(available: Int) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(40))
            .background(BackgroundInput.copy(alpha = 0.7f))
            .border(1.dp, BorderSubtle, RoundedCornerShape(40))
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            "✨ $available mejoras disponibles",
            color = TextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ImagePreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(BackgroundCard)
            .border(1.dp, BorderSubtle, RoundedCornerShape(32.dp))
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF2A3345), Color(0xFF0B0F16)),
                        center = androidx.compose.ui.geometry.Offset(0.3f, 0.4f)
                    )
                )
                .border(1.dp, BorderSubtle, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🖼️", fontSize = 52.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(60))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .border(1.dp, BorderActive, RoundedCornerShape(60))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        "1920×1080  →  3840×2160 (4K)",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun RecentEnhancement() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(60))
            .background(SurfaceLight.copy(alpha = 0.7f))
            .border(1.dp, BorderSubtle, RoundedCornerShape(60))
            .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(20))
                .background(Color(0xFF3E4B63))
                .border(1.dp, BorderSubtle, RoundedCornerShape(20)),
            contentAlignment = Alignment.Center
        ) {
            Text("🌄", fontSize = 22.sp)
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("Última mejora", color = TextSecondary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text("Paisaje · 12.4 MP → 33.2 MP", color = TextTertiary, fontSize = 12.sp)
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(30))
                .background(Color(0xFF2E3A50))
                .border(1.dp, Color(0xFF5A6F92), RoundedCornerShape(30))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text("Usar", color = Color(0xFFE0EDFF), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun ControlsPanel() {
    var selectedModel by remember { mutableIntStateOf(0) }
    var sharpness by remember { mutableFloatStateOf(0.7f) }
    var selectedFormat by remember { mutableIntStateOf(0) }

    val models = listOf("Rápido" to "2.5s", "Calidad Pro" to "8s", "Arte IA" to "")
    val formats = listOf("PNG", "JPEG 95%", "WebP")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(36.dp))
            .background(SurfaceLight.copy(alpha = 0.85f))
            .border(1.dp, BorderSubtle, RoundedCornerShape(36.dp))
            .padding(20.dp)
    ) {
        Column {
            // Selector de modelo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                models.forEachIndexed { index, (name, time) ->
                    val isActive = selectedModel == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(28))
                            .background(if (isActive) Color(0xFF2E4C7A) else BackgroundInput)
                            .border(
                                1.dp,
                                if (isActive) BorderActive else BorderSubtle,
                                RoundedCornerShape(28)
                            )
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                name,
                                color = if (isActive) Color.White else TextSecondary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (time.isNotEmpty()) {
                                Text(
                                    time,
                                    color = if (isActive) Color.White.copy(alpha = 0.8f) else TextTertiary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Nitidez / Detalle", color = TextSecondary, fontSize = 14.sp)
                    Text("Alta", color = TextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(22.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(10))
                            .background(ProgressTrack)
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .fillMaxWidth(fraction = sharpness)
                            .height(6.dp)
                            .clip(RoundedCornerShape(10))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF5B8CDF), Color(0xFFA3C4FF))
                                )
                            )
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .offset(x = (sharpness * 300).dp) // Simulado
                            .size(22.dp)
                            .clip(RoundedCornerShape(30))
                            .background(Color.White)
                            .border(2.dp, Color(0xFFB5D0FF), RoundedCornerShape(30))
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Formato
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                formats.forEachIndexed { index, fmt ->
                    val isSel = selectedFormat == index
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(30))
                            .background(if (isSel) Color(0xFF2F4770) else BackgroundInput)
                            .border(
                                1.dp,
                                if (isSel) Color(0xFF7DA1E0) else BorderSubtle,
                                RoundedCornerShape(30)
                            )
                            .padding(horizontal = 18.dp, vertical = 8.dp)
                    ) {
                        Text(
                            fmt,
                            color = if (isSel) Color.White else TextSecondary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Botón Procesar
            Button(
                onClick = { /* TODO: procesar */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(60),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentPrimary,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 12.dp
                )
            ) {
                Text(
                    "⚡ Mejorar a 4K",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ComparisonMock() {
    Text(
        "🔍 Comparación antes/después",
        color = Color(0xFFAAC2FF),
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 16.dp)
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFF101622))
            .border(1.dp, BorderSubtle, RoundedCornerShape(28.dp))
            .padding(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(70.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF0D1322))
                        .border(1.dp, Color(0xFF2B3F60), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🌿 Antes", color = Color(0xFFAAC0F0), fontSize = 14.sp)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(70.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF152033))
                        .border(1.dp, Color(0xFF5E7DB0), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✨ Después 4K", color = Color.White, fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .height(2.dp)
                        .weight(1f)
                        .background(Color(0xFF4766A0))
                )
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(30))
                        .background(Color(0xFF2A3A58))
                        .border(1.dp, Color(0xFF7E9AD9), RoundedCornerShape(30)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⇆", fontSize = 22.sp, color = Color(0xFFD6E5FF))
                }
                Box(
                    modifier = Modifier
                        .height(2.dp)
                        .weight(1f)
                        .background(Color(0xFF4766A0))
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Original", color = Color(0xFF8CA1CC), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Text("IA mejorada", color = Color(0xFF8CA1CC), fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun BottomNavBar() {
    val items = listOf("🏠", "🖼️", "⚡", "👤")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        items.forEachIndexed { index, icon ->
            Text(
                icon,
                fontSize = 22.sp,
                color = if (index == 0) TextSecondary else TextTertiary
            )
        }
    }
}

@Composable
fun FootNote() {
    Text(
        "v1.0 · AI Super‑Resolution",
        color = TextTertiary,
        fontSize = 12.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}
