package kolskypavel.ardfmanager.backend.files.processors;

import android.content.Context
import kolskypavel.ardfmanager.backend.room.entity.Category
import java.io.IOException

object TemplateProcessor {

    @Throws(IOException::class)
    fun loadTemplate(templateName: String, context: Context): String {
        return context.assets.open(templateName).readAllBytes().toString()

    }

    fun processTemplate(template: String, params: HashMap<String, String>): String {
        var output = template

        for (par in params) {
            output = output.replace(par.key, par.value)
        }

        return output
    }
}
