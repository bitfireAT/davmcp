package at.bitfire.labs.davmcp.icalendar

import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.Component
import net.fortuna.ical4j.model.TemporalAdapter
import net.fortuna.ical4j.model.component.VEvent
import java.io.StringReader
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.temporal.Temporal
import kotlin.jvm.optionals.getOrNull

class SimpleConverter {

    fun convert(fileName: String?, iCalendar: String): SimpleEvent? {
        val calendar = CalendarBuilder().build(StringReader(iCalendar))
        val vEvent = calendar.getComponent<VEvent>(Component.VEVENT).getOrNull() ?: return null

        val dtStart: Temporal? = vEvent.getDateTimeStart<Temporal>()?.date
        val dtEnd: Temporal? = vEvent.getEndDate<Temporal>(true)?.getOrNull()?.date

        val startDateTime = if (dtStart != null && TemporalAdapter.isDateTimePrecision(dtStart))
            TemporalAdapter.toLocalTime(dtStart, ZoneOffset.UTC).toInstant()
        else
            null
        val startDate: LocalDate? = dtStart as? LocalDate

        val endDateTime = if (dtEnd != null && TemporalAdapter.isDateTimePrecision(dtStart))
            TemporalAdapter.toLocalTime(dtEnd, ZoneOffset.UTC).toInstant()
        else
            null
        val endDate: LocalDate? = dtEnd as? LocalDate

        return SimpleEvent(
            fileName = fileName,
            iCalendar = iCalendar,
            title = vEvent.summary?.value,
            startDateTime = startDateTime?.toString(),
            startDate = startDate?.toString(),
            endDateTime = endDateTime?.toString(),
            endDate = endDate?.toString()
        )
    }

}