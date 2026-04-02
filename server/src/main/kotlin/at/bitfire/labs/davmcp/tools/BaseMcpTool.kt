package at.bitfire.labs.davmcp.tools

import at.bitfire.labs.davmcp.db.Collection
import at.bitfire.labs.davmcp.db.Database
import at.bitfire.labs.davmcp.db.Service
import at.bitfire.labs.davmcp.db.User
import io.modelcontextprotocol.kotlin.sdk.server.ClientConnection
import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.TextContent
import java.io.PrintWriter
import java.io.StringWriter

abstract class BaseMcpTool : McpTool {

    /**
     * Resolves the calendar collection to use for an operation.
     *
     * Resolution order:
     * 1. If [collectionId] is provided, that specific collection is used.
     * 2. If the service has a [Service.defaultCollectionId] configured, that collection is used.
     * 3. Otherwise, silently falls back to the first collection found for the service,
     *    assuming it is the only one.
     *
     * @param database database instance used to query collections and services
     * @param service the service whose collections are searched
     * @param collectionId optional explicit collection ID requested by the caller
     * @return the resolved [Collection]
     * @throws IllegalArgumentException if [collectionId] is given but no matching collection exists
     * @throws IllegalStateException if no collection exists for the service at all
     */
    protected fun resolveCollection(database: Database, service: Service, collectionId: Long?): Collection {
        if (collectionId != null)
            return database.collectionQueries.getById(collectionId).executeAsOneOrNull()
                ?: throw IllegalArgumentException("Collection with id=$collectionId not found")

        database.serviceQueries.getDefaultCollection(service.id).executeAsOneOrNull()?.let { return it }

        return database.collectionQueries.getByService(service.id).executeAsList().firstOrNull()
            ?: throw IllegalStateException("No calendar collection found for service id=${service.id}.")
    }



    abstract suspend fun handle(connection: ClientConnection, user: User, request: CallToolRequest): CallToolResult

    override suspend fun handler(connection: ClientConnection, user: User, request: CallToolRequest): CallToolResult =
        try {
            handle(connection, user, request)
        } catch (e: Exception) {
            val stringWriter = StringWriter()
            val printWriter = PrintWriter(stringWriter)

            printWriter.println(e.message ?: e.javaClass.name)
            printWriter.println()
            printWriter.println("-----")
            printWriter.println()
            e.printStackTrace(printWriter)

            CallToolResult(
                content = listOf(TextContent(stringWriter.toString())),
                isError = true
            )
        }

}