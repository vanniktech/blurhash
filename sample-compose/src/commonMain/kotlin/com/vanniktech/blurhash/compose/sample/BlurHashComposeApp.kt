package com.vanniktech.blurhash.compose.sample

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import blurhash.sample_compose.generated.resources.Res
import blurhash.sample_compose.generated.resources.blueberries
import org.jetbrains.compose.resources.imageResource
import com.vanniktech.blurhash.BlurHash
import com.vanniktech.blurhash.compose.decodeAsImageBitmap
import com.vanniktech.blurhash.compose.encode

@Composable fun App() = Theme {
  Scaffold { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 64.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
      Spacer(Modifier.weight(1f))

      val bitmap = imageResource(Res.drawable.blueberries)

      Image(
        bitmap = bitmap,
        contentDescription = null,
        contentScale = ContentScale.None,
      )

      val blurHash = BlurHash.encode(bitmap, componentX = 5, componentY = 4)

      Text(
        text = blurHash,
        textAlign = TextAlign.Center,
      )

      val blurred = BlurHash.decodeAsImageBitmap(
        blurHash = blurHash,
        width = bitmap.width / 4,
        height = bitmap.height / 4,
      )

      if (blurred != null) {
        Image(
          modifier = Modifier.height(bitmap.height.pxToDp()).width(bitmap.width.pxToDp()),
          bitmap = blurred,
          contentDescription = null,
          contentScale = ContentScale.Fit,
        )
      }

      Spacer(Modifier.weight(1f))
    }
  }
}

@Composable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }
