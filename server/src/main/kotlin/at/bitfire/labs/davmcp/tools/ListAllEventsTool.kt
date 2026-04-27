package at.bitfire.labs.davmcp.tools

import at.bitfire.dav4jvm.ktor.DavResource
import at.bitfire.dav4jvm.property.caldav.CalDAV
import at.bitfire.dav4jvm.property.webdav.WebDAV
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
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import javax.inject.Inject

class ListAllEventsTool @Inject constructor(
    private val database: Database,
    private val httpClientBuilder: HttpClientBuilder,
    private val eventResponseHandler: EventResponseHandler
) : BaseMcpTool() {

    override fun tool() = Tool(
        name = "events.listAll",
        description = "List all events in a calendar collection",
        inputSchema = ToolSchema(
            properties = buildJsonObject {
                collectionIdSchema()
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
        logToolCall("ListAllEventsTool", user, input)

        val service = getCalDavService(database, user)
        val collection = resolveCollection(database, service, input.collectionId)
        val collectionUrl = Url(collection.url)

        httpClientBuilder.buildFromService(service).use { client ->
            val davResource = DavResource(client, collectionUrl)

            val events = mutableListOf<EventResponseHandler.EventWithName>()
            davResource.propfind(1, WebDAV.GetETag, CalDAV.CalendarData) { response, relation ->
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
        val collectionId: Long? = null
    )

    @Serializable
    data class OutputData(
        val events: List<EventResponseHandler.EventWithName>
    )

}