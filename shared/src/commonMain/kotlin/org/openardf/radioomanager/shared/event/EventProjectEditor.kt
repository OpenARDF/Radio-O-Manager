package org.openardf.radioomanager.shared.event

/** Shared event-project editing helpers used by desktop and future non-Android flows. */
object EventProjectEditor {
    /** Returns a copy of the project file with a validated race name. */
    fun renameRace(projectFile: EventProjectFile, name: String): EventProjectFile {
        val trimmedName = name.trim()
        require(trimmedName.isNotEmpty()) {
            "Race name cannot be blank."
        }

        return projectFile.copy(
            raceData = projectFile.raceData.copy(
                race = projectFile.raceData.race.copy(name = trimmedName)
            )
        )
    }

    /** Returns a copy of the project file with one validated category name changed. */
    fun renameCategory(
        projectFile: EventProjectFile,
        categoryId: String,
        name: String
    ): EventProjectFile {
        val trimmedName = name.trim()
        require(trimmedName.isNotEmpty()) {
            "Category name cannot be blank."
        }
        require(
            projectFile.raceData.categories.none {
                it.category.id != categoryId && it.category.name == trimmedName
            }
        ) {
            "Category name must be unique."
        }

        var foundCategory = false
        val categories = projectFile.raceData.categories.map { categoryData ->
            if (categoryData.category.id == categoryId) {
                foundCategory = true
                categoryData.copy(category = categoryData.category.copy(name = trimmedName))
            } else {
                categoryData
            }
        }
        require(foundCategory) {
            "Category was not found: $categoryId"
        }

        return projectFile.copy(
            raceData = projectFile.raceData.copy(categories = categories)
        )
    }
}
