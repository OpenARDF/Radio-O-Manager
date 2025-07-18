package kolskypavel.ardfmanager.backend.files.json.temps

data class ResultPunchJson(
    val code: String,
    val control_type: String,
    val punch_status: String,
    val split_time: String
)