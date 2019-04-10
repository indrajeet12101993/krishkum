package `in`.krishkam.utils


import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


object TimeUtils {

    var SECOND_MILLIS: Int = 1000
    var MINUTE_MILLIS: Int = 60 * SECOND_MILLIS
    var HOUR_MILLIS: Int = 60 * MINUTE_MILLIS
    var DAY_MILLIS: Int = 24 * HOUR_MILLIS;


    fun getServerTimeStamp(server_time_string: String): Long {

        val formatter: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date: Date = formatter.parse(server_time_string)
        return (date.time)


    }

    fun getServerTimeStampForComment(server_time_string: String): Long {

        val formatter: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date: Date = formatter.parse(server_time_string)
        return (date.time)


    }


    fun getTimeAgo(time1: Long): String? {
        var time = time1
        if (time < 1000000000000L) {
            time *= 1000
        }


        val now = System.currentTimeMillis()
        if (time > now || time <= 0) {
            return null
        }


        val diff = now - time
        if (diff < MINUTE_MILLIS) {
            return "अभी"
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "एक मिनट पहले"
        } else if (diff < 50 * MINUTE_MILLIS) {
            return (diff / MINUTE_MILLIS).toString() + " मिनटस पहले "
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "एक घंटा पहले"
        } else if (diff < 24 * HOUR_MILLIS) {
            return (diff / HOUR_MILLIS).toString() + " घंटो पहले"
        } else if (diff < 48 * HOUR_MILLIS) {
            return "1 दिन पहले"
        } else if (diff < 720 * HOUR_MILLIS) {
            return "1 महीना पहले "
        } else {
            return (diff / DAY_MILLIS).toString() + " दिन पहले"
        }
    }

    fun currentDate(): Date {
        val calendar = Calendar.getInstance()
        return calendar.time
    }

    fun convertDateTimeWithMonthName(dateStr: String): String? {
        var date: String? = null
        //        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        val dt = SimpleDateFormat("yyyy-MM-dd ")

        try {
            val parsedDate = dt.parse(dateStr)
            val dt1 = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            date = dt1.format(parsedDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return date
    }

    fun getTimeMilliSec(timeStamp: String): Long {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        try {
            val date = format.parse(timeStamp)
            return date.getTime()
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return 0
    }


}