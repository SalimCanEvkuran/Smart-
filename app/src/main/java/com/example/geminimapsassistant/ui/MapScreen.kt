package com.example.geminimapsassistant.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.geminimapsassistant.data.RouteInfo
import com.example.geminimapsassistant.ui.theme.*
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

// osmdroid'in kendi TileSourceFactory'sinde koyu mod karosu bulunmuyor,
// bu yüzden CartoDB'nin ücretsiz "Dark Matter" karolarını özel bir kaynak olarak tanımlıyoruz.
private val DARK_TILE_SOURCE = XYTileSource(
    "CartoDBDarkMatter",
    0, 20, 256, ".png",
    arrayOf(
        "https://a.basemaps.cartocdn.com/dark_all/",
        "https://b.basemaps.cartocdn.com/dark_all/",
        "https://c.basemaps.cartocdn.com/dark_all/"
    )
)

/**
 * OpenStreetMap tabanlı harita ekranı. Google Maps yerine ücretsiz osmdroid kullanır,
 * API key veya faturalandırma gerektirmez.
 */
@Composable
fun MapScreen(
    userLocation: GeoPoint?,
    route: RouteInfo?,
    onStartNavigation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val defaultLocation = userLocation ?: GeoPoint(41.0082, 28.9784) // İstanbul

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                MapView(context).apply {
                    setTileSource(DARK_TILE_SOURCE) // Koyu mod harita karoları (CartoDB)
                    setMultiTouchControls(true)
                    controller.setZoom(if (route != null) 13.0 else 14.0)
                }
            },
            update = { mapView ->
                mapView.overlays.clear()

                val target = route?.destination ?: defaultLocation
                mapView.controller.setCenter(target)

                userLocation?.let { loc ->
                    val marker = Marker(mapView)
                    marker.position = loc
                    marker.title = "Konumunuz"
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    mapView.overlays.add(marker)
                }

                route?.let { r ->
                    val destMarker = Marker(mapView)
                    destMarker.position = r.destination
                    destMarker.title = r.destinationLabel
                    destMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    mapView.overlays.add(destMarker)

                    val line = Polyline(mapView)
                    line.setPoints(listOf(r.origin, r.destination))
                    line.outlinePaint.color = android.graphics.Color.parseColor("#4C7CF3")
                    line.outlinePaint.strokeWidth = 10f
                    mapView.overlays.add(line)
                }

                mapView.invalidate()
            }
        )

        if (route != null) {
            RouteInfoCard(
                route = route,
                onStartNavigation = onStartNavigation,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun RouteInfoCard(
    route: RouteInfo,
    onStartNavigation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(SurfaceDark, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "${route.durationText} (${route.distanceText})",
            color = TextPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(StatusGreen, shape = CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = route.trafficStatus, color = TextSecondary, fontSize = 13.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onStartNavigation,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
        ) {
            Text("Yol Tarifi Başla", color = TextPrimary)
        }
    }
}
