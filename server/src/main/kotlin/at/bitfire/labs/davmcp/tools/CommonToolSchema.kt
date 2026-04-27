import at.bitfire.labs.davmcp.icalendar.simpleEventSchema
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal fun JsonObjectBuilder.collectionIdSchema() {
    put("collectionId", buildJsonObject {
        put("type", "number")
        put(
            "description",
            "Optional ID of the targeted calendar collection. Must be empty (= default calendar) or a collection ID as returned by collections.list."
        )
    })
}

internal fun JsonObjectBuilder.eventListOutputSchema() {
    put("events", buildJsonObject {
        put("type", "array")
        put("items", buildJsonObject {
            put("type", "object")
            put("properties", buildJsonObject {
                put("fileName", buildJsonObject {
                    put("type", "string")
                    put("description", "File name of the event (iCalendar)")
                })
                put("eventData", buildJsonObject {
                    simpleEventSchema()
                })
            })
        })
    })
}