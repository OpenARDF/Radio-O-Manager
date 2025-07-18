package kolskypavel.ardfmanager.backend.files.json.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import kolskypavel.ardfmanager.backend.room.entity.Category

class CategoryJsonAdapter {
    @ToJson
    fun toJson(category: Category): String {
        var res = ""

        return res
    }

    @FromJson
    fun fromJson(jsonString: String):Category{
        val parsed = Category();

        return parsed
    }
}