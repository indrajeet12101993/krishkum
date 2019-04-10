package `in`.krishkam.pojo.otp

data class Result(
        val id: String,
        val name: String,
        val state: String,
        val city: String,
        val mobile: String,
        val village: String,
        val otp: String,
        val image: String,
        val is_blocked: String,
        val created: String
)