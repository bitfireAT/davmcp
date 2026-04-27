package at.bitfire.labs.davmcp.tools

import at.bitfire.dav4jvm.ktor.DavCalendar
import at.bitfire.dav4jvm.property.caldav.CalDAV
import at.bitfire.labs.davmcp.HttpClientBuilder
import at.bitfire.labs.davmcp.db.Database
import at.bitfire.labs.davmcp.db.User
import at.bitfire.labs.davmcp.json.McpJson
import collectionIdSchema
import eventListOutputSchema
import io.ktor.http.*
import io.modelcontextprotocol.kotlin.sdk.server.ClientConnection
import io.modelcontextprotocol.kotlin.sdk.types.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import net.fortuna.ical4j.model.Component
import java.time.Instant
import javax.inject.Inject

class QueryEventsByTimeTool @Inject constructor(
    private val database: Database,
    private val httpClientBuilder: HttpClientBuilder,
    private val eventResponseHandler: EventResponseHandler
) : BaseMcpTool() {

    override fun tool() = Tool(
        name = "events.queryByTime",
        description = "Query events by time range",
        inputSchema = ToolSchema(
            properties = buildJsonObject {
                collectionIdSchema()
                put("start", buildJsonObject {
                    put("type", "string")
                    put("format", "date-time")
                    put(
                        "description",
                        "Optional start date-time of the query. Only events with recurrences on or after this timestamp will be returned."
                    )
                })
                put("end", buildJsonObject {
                    put("type", "string")
                    put("format", "date-time")
                    put(
                        "description",
                        "Optional end date-time of the query. Only events with recurrences before this timestamp will be returned."
                    )
                })
            },
            required = listOf()
        ),
        outputSchema = ToolSchema(
            properties = buildJsonObject {
                eventListOutputSchema()
            },
            required = listOf("events")
        ),
        annotations = ToolAnnotations(
            readOnlyHint = true,
            destructiveHint = false
        )
    )

    override suspend fun handle(connection: ClientConnection, user: User, request: CallToolRequest): CallToolResult {
        val input = McpJson.decodeFromJsonElement<InputData>(
            request.arguments ?: throw IllegalArgumentException("Request arguments are required")
        )
        logToolCall("QueryEventsByTimeTool", user, input)

        val service = getCalDavService(database, user)
        val collection = resolveCollection(database, service, input.collectionId)
        val collectionUrl = Url(collection.url)

        httpClientBuilder.buildFromService(service).use { client ->
            val calendar = DavCalendar(client, collectionUrl)

            val start: Instant? = input.start?.let { Instant.parse(it) }
            val end: Instant? = input.end?.let { Instant.parse(it) }

            val events = mutableListOf<EventResponseHandler.EventWithName>()
            calendar.calendarQuery(Component.VEVENT, start, end, setOf(CalDAV.CalendarData)) { response, relation ->
                val eventWithName = eventResponseHandler.processCalendarResponse(response, relation)
                if (eventWithName != null)
                    events += eventWithName
            }
            return CallToolResult(
                content = listOf(TextContent(McpJson.encodeToString(events))),
                isError = false,
                structuredContent = McpJson.encodeToJsonElement(OutputData(events)).jsonObject
            ).also { logger.info("Result: $it") }
        }
    }


    @Serializable
    data class InputData(
        val collectionId: Long? = null,
        val start: String?,
        val end: String?
    )

    @Serializable
    data class OutputData(
        val events: List<EventResponseHandler.EventWithName>
    ) {

    }

}
