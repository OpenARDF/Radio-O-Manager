package org.openardf.radioomanager.shared.event

import org.openardf.radioomanager.shared.alias.AliasRules
import org.openardf.radioomanager.shared.alias.AliasValidationResult
import org.openardf.radioomanager.shared.sportident.SportIdentCodes

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

    /** Returns a copy of the project file with one competitor's validated name changed. */
    fun renameCompetitor(
        projectFile: EventProjectFile,
        competitorId: String,
        firstName: String,
        lastName: String
    ): EventProjectFile {
        val trimmedFirstName = firstName.trim()
        val trimmedLastName = lastName.trim()
        require(trimmedFirstName.isNotEmpty()) {
            "Competitor first name cannot be blank."
        }
        require(trimmedLastName.isNotEmpty()) {
            "Competitor last name cannot be blank."
        }

        var foundCompetitor = false
        val competitorData = projectFile.raceData.competitorData.map { data ->
            val competitorCategory = data.competitorCategory
            val competitor = competitorCategory.competitor
            if (competitor.id == competitorId) {
                foundCompetitor = true
                data.copy(
                    competitorCategory = competitorCategory.copy(
                        competitor = competitor.copy(
                            firstName = trimmedFirstName,
                            lastName = trimmedLastName
                        )
                    )
                )
            } else {
                data
            }
        }
        require(foundCompetitor) {
            "Competitor was not found: $competitorId"
        }

        return projectFile.copy(
            raceData = projectFile.raceData.copy(competitorData = competitorData)
        )
    }

    /** Returns a copy of the project file with one competitor's validated numbers changed. */
    fun updateCompetitorNumbers(
        projectFile: EventProjectFile,
        competitorId: String,
        startNumber: String,
        siNumber: String
    ): EventProjectFile {
        val competitorPosition = projectFile.raceData.competitorData.indexOfFirst {
            it.competitorCategory.competitor.id == competitorId
        }
        require(competitorPosition >= 0) {
            "Competitor was not found: $competitorId"
        }

        val trimmedStartNumber = startNumber.trim()
        require(trimmedStartNumber.isNotEmpty()) {
            "Start number is required."
        }
        val startNumberValue = trimmedStartNumber.toIntOrNull()
            ?: throw IllegalArgumentException("Start number is invalid.")
        require(
            projectFile.raceData.competitorData.noneIndexed { index, data ->
                index != competitorPosition && data.competitorCategory.competitor.startNumber == startNumberValue
            }
        ) {
            "Start number must be unique."
        }

        val trimmedSiNumber = siNumber.trim()
        val siNumberValue = if (trimmedSiNumber.isEmpty()) {
            null
        } else {
            trimmedSiNumber.toIntOrNull()
                ?: throw IllegalArgumentException("SI number is invalid.")
        }
        require(siNumberValue == null || SportIdentCodes.isSINumberValid(siNumberValue)) {
            "SI number is outside the supported SportIdent card range."
        }
        require(
            siNumberValue == null || projectFile.raceData.competitorData.noneIndexed { index, data ->
                index != competitorPosition && data.competitorCategory.competitor.siNumber == siNumberValue
            }
        ) {
            "SI number must be unique."
        }

        val competitorData = projectFile.raceData.competitorData.mapIndexed { index, data ->
            if (index == competitorPosition) {
                val competitorCategory = data.competitorCategory
                data.copy(
                    competitorCategory = competitorCategory.copy(
                        competitor = competitorCategory.competitor.copy(
                            startNumber = startNumberValue,
                            siNumber = siNumberValue
                        )
                    )
                )
            } else {
                data
            }
        }

        return projectFile.copy(
            raceData = projectFile.raceData.copy(competitorData = competitorData)
        )
    }

    /** Returns a copy of the project file with one validated alias changed. */
    fun updateAlias(
        projectFile: EventProjectFile,
        aliasId: String,
        siCode: String,
        name: String
    ): EventProjectFile {
        val aliasPosition = projectFile.raceData.aliases.indexOfFirst { it.id == aliasId }
        require(aliasPosition >= 0) {
            "Alias was not found: $aliasId"
        }

        val trimmedCode = siCode.trim()
        val trimmedName = name.trim()
        val existingCodes = projectFile.raceData.aliases.map { it.siCode }
        val existingNames = projectFile.raceData.aliases.map { it.name }

        require(AliasRules.validateCode(trimmedCode, existingCodes, aliasPosition) == AliasValidationResult.Valid) {
            "Alias SI code is invalid or duplicated."
        }
        require(AliasRules.validateName(trimmedName, existingNames, aliasPosition) == AliasValidationResult.Valid) {
            "Alias name is invalid or duplicated."
        }

        val aliases = projectFile.raceData.aliases.mapIndexed { index, alias ->
            if (index == aliasPosition) {
                alias.copy(siCode = trimmedCode.toInt(), name = trimmedName)
            } else {
                alias
            }
        }

        return projectFile.copy(
            raceData = projectFile.raceData.copy(aliases = aliases)
        )
    }

    /** Returns a copy of the project file with a validated alias appended. */
    fun addAlias(
        projectFile: EventProjectFile,
        aliasId: String,
        siCode: String,
        name: String
    ): EventProjectFile {
        require(aliasId.isNotBlank()) {
            "Alias ID cannot be blank."
        }
        require(projectFile.raceData.aliases.none { it.id == aliasId }) {
            "Alias ID already exists: $aliasId"
        }

        val aliasPosition = projectFile.raceData.aliases.size
        val trimmedCode = siCode.trim()
        val trimmedName = name.trim()
        val existingCodes = projectFile.raceData.aliases.map { it.siCode }
        val existingNames = projectFile.raceData.aliases.map { it.name }

        require(AliasRules.validateCode(trimmedCode, existingCodes, aliasPosition) == AliasValidationResult.Valid) {
            "Alias SI code is invalid or duplicated."
        }
        require(AliasRules.validateName(trimmedName, existingNames, aliasPosition) == AliasValidationResult.Valid) {
            "Alias name is invalid or duplicated."
        }

        val alias = EventAlias(
            id = aliasId,
            raceId = projectFile.raceData.race.id,
            siCode = trimmedCode.toInt(),
            name = trimmedName
        )

        return projectFile.copy(
            raceData = projectFile.raceData.copy(
                aliases = projectFile.raceData.aliases + alias
            )
        )
    }

    /** Returns a copy of the project file with one alias removed. */
    fun removeAlias(projectFile: EventProjectFile, aliasId: String): EventProjectFile {
        require(projectFile.raceData.aliases.any { it.id == aliasId }) {
            "Alias was not found: $aliasId"
        }

        return projectFile.copy(
            raceData = projectFile.raceData.copy(
                aliases = projectFile.raceData.aliases.filterNot { it.id == aliasId }
            )
        )
    }

    private inline fun <T> Iterable<T>.noneIndexed(predicate: (index: Int, T) -> Boolean): Boolean =
        withIndex().none { (index, value) -> predicate(index, value) }
}
