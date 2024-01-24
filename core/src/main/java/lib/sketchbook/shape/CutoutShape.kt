package lib.sketchbook.shape

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import lib.sketchbook.shape.CutoutShape.Orientation

class CutoutShape(
    private val cornerSize: CornerSize,
    private val orientation: Orientation
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path()
        val cornerRadius = CornerRadius(cornerSize.toPx(size, density))
        path.addRoundRect(
            RoundRect(
                Rect(Offset.Zero, size),
                cornerRadius,
                CornerRadius.Zero,
                cornerRadius,
                cornerRadius
            )
        )

        val topLeft = inverseCorner(cornerRadius)
        path.op(path, topLeft, PathOperation.Union)

        val bottomRight = inverseCorner2(cornerRadius)
        bottomRight.transform(Matrix().apply {
            rotateZ(90f)
            scale(y = -1f)
        })
        bottomRight.translate(Offset(x = size.width, y = size.height))
        path.op(path, bottomRight, PathOperation.Union)

        when (orientation) {
            Orientation.TopLeft -> path.transform(Matrix().apply {
                scale(x = -1f)
                translate(x = -size.width)
            })

            Orientation.TopRight -> {} // noop
            Orientation.BottomRight -> path.transform(Matrix().apply {
                scale(y = -1f)
                translate(y = -size.height)
            })

            Orientation.BottomLeft -> path.transform(Matrix().apply {
                scale(x = -1f, y = -1f)
                translate(x = -size.width, y = -size.height)
            })
        }

        return Outline.Generic(path)
    }

    private fun inverseCorner(radius: CornerRadius) = Path().apply {
        moveTo(0f, 0f)
        lineTo(radius.x, 0f)
        lineTo(0f, radius.y)
        quadraticBezierTo(0f, 0f, 0f, radius.y)
        quadraticBezierTo(0f, 0f, -radius.x, 0f)
        close()
    }

    private fun inverseCorner2(radius: CornerRadius) = Path().apply {
        moveTo(0f, 0f)
        lineTo(-radius.x, 0f)
        lineTo(0f, -radius.y)
        quadraticBezierTo(0f, 0f, 0f, -radius.y)
        quadraticBezierTo(0f, 0f, radius.x, 0f)
        close()
    }

    enum class Orientation {
        TopLeft, TopRight, BottomRight, BottomLeft
    }

}

@Preview(showBackground = true)
@Composable
private fun CutoutShapePreview(
    @PreviewParameter(OrientationProvider::class)
    orientation: Orientation
) {
    val shape = CutoutShape(
        cornerSize = CornerSize(16.dp),
        orientation = orientation
    )
    Box(
        modifier = Modifier
            .padding(16.dp)
            .size(32.dp)
            .background(Color.Blue, shape)
    )
}

private class OrientationProvider : PreviewParameterProvider<Orientation> {
    override val values = Orientation.entries.asSequence()
}