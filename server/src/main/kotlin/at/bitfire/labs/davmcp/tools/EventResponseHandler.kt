package at.bitfire.labs.davmcp.tools

import at.bitfire.dav4jvm.ktor.Response
import at.bitfire.dav4jvm.property.caldav.CalendarData
import at.bitfire.labs.davmcp.icalendar.SimpleEvent
import at.bitfire.labs.davmcp.icalendar.SimpleEventConverter
import kotlinx.serialization.Serializable
import javax.inject.Inject

class EventResponseHandler @Inject constructor(
    private val simpleEventConverter: SimpleEventConverter
) {

    fun processCalendarResponse(response: Response, relation: Response.HrefRelation): EventWithName? {
        if (relation != Response.HrefRelation.MEMBER)
            return null

        val calendarData = response[CalendarData::class.java]?.iCalendar ?: return null
        val event = simpleEventConverter.fromICalendar(calendarData)
        return event?.let {
            EventWithName(
                fileName = response.hrefName(),
                eventData = it
            )
        }
    }

    @Serializable
    data class EventWithName(
        val fileName: String,
        val eventData: SimpleEvent
    )

}