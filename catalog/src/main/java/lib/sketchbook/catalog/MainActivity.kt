package lib.sketchbook.catalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import lib.sketchbook.util.setContent

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CatalogApp() }
    }
}
