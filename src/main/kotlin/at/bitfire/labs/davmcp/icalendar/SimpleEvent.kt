package at.bitfire.labs.davmcp.icalendar

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@Serializable
data class SimpleEvent(
    val fileName: String?,
    val iCalendar: String,
    val title: String?,
    val startDateTime: String?,
    val startDate: String?,
    val endDateTime: String?,
    val endDate: String?
)

fun JsonObjectBuilder.simpleEventSchema() {
    put("type", "object")
    put("properties", buildJsonObject {
        put("fileName", buildJsonObject {
            put("type", "string")
            put("description", "Name of the calendar file")
        })
        put("iCalendar", buildJsonObject {
            put("type", "string")
            put("description", "Original iCalendar data as string")
        })
        put("title", buildJsonObject {
            put("type", "string")
            put("description", "Event title (SUMMARY)")
        })
        put("startDateTime", buildJsonObject {
            put("type", "string")
            put("format", "date-time")
            put("description", "Start date-time of the event")
        })
        put("startDate", buildJsonObject {
            put("type", "string")
            put("format", "date")
            put("description", "Start date of the event")
        })
        put("endDateTime", buildJsonObject {
            put("type", "string")
            put("format", "date-time")
            put("description", "End date-time of the event")
        })
        put("endDate", buildJsonObject {
            put("type", "string")
            put("format", "date")
            put("description", "End date of the event")
        })
    })
    //put("required", json.encodeToJsonElement(listOf("fileName", "iCal")))
}