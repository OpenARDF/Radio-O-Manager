package kolskypavel.ardfmanager.backend.files.constants

object FileConstants {
    const val OCM_START_CSV_COLUMNS = 3
    const val CATEGORY_CSV_COLUMNS = 11

    const val TEMPLATE_TEXT = "textResultTemplate.tmpl"
    const val TEMPLATE_TEXT_CATEGORY = "textResCatHeader.tmpl"
    const val TEMPLATE_HTML = "htmlResultTtemplate.tmpl"

    //TEMPLATE RACE KEYS
    const val KEY_TITLE_RESULTS = "\${title_results}"
    const val KEY_TITLE_RACE_NAME = "\${title_race_name}"
    const val KEY_TITLE_RACE_DATE = "\${title_race_date}"
    const val KEY_TITLE_RACE_START_TIME = "\${title_race_start_time}"
    const val KEY_TITLE_RACE_LEVEL = "\${title_race_level}"
    const val KEY_TITLE_RACE_BAND = "\${title_race_band}"
    const val KEY_TITLE_LIMIT = "\${title_limit}"
    const val KEY_TITLE_LENGTH = "\${title_length}"
    const val KEY_TITLE_CONTROLS = "\${title_controls}"

    const val KEY_RACE_NAME = "\${race_name}"
    const val KEY_RACE_DATE = "\${race_date}"
    const val KEY_RACE_START_TIME = "\${race_start_time}"
    const val KEY_RACE_LEVEL = "\${race_level}"
    const val KEY_RACE_BAND = "\${race_band}"
    const val KEY_RACE_RESULTS = "\${race_results}"

    // TEMPLATE CATEGORY KEYS
    const val KEY_CAT_NAME = "\${cat_name}"
    const val KEY_CAT_LIMIT = "\${cat_limit}"
    const val KEY_CAT_LENGTH = "\${length}"
    const val KEY_CAT_CONTROLS = "\${cat_controls}"

    // TEMPLATE RESULT KEYS
    const val KEY_TITLE_CLUB = "\${title_club}"

    const val KEY_TITLE_GENERATED_WITH = "\${title_generated_with}"
    const val KEY_GENERATED_WITH = "\${generated_with}"
    const val KEY_VERSION = "\${software_version}"


}