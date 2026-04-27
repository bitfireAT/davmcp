# davmcp – "DAVx⁵ for AI"

An experimental [Model Context Protocol (MCP)](https://modelcontextprotocol.org/) server for CalDAV access.

## ⚠️ Experimental Status

>[!WARNING]
>This project is **experimental** and under active development. The API and functionality may change without notice.
>There are **no automatic database migrations**.

>[!CAUTION]
>Not intended for production use. Only use in test environments.

## What it does

davmcp provides calendar management capabilities through the Model Context Protocol. It acts as a bridge between MCP
clients and CalDAV servers, enabling AI agents and other MCP-compatible tools to manage calendar events.

- **CalDAV Integration**: Connects to standard CalDAV calendar servers
- TODO: CardDAV integration
- TODO: WebDAV integration
- **Events CRUD Operations**: Create, read, update, and delete calendar events
- TODO: CRUD for contacts, tasks, journal entries
- TODO: provide access to WebDAV resources
- **Auxiliary tools**: Query current time and time-based information (needed because LLMs don't have a concept of time)

## Quick Start

These are the steps to manually compile and run davmcp. You can also [use Docker](DOCKER.md) instead.

1. Prepare the required environment: Currently only a JDK is needed. `sqlite3` or a similar tool is required to edit
   the database since there's no configuration UI (yet).
2. Checkout or download davmcp.
2. **Build the server**:
   ```bash
   cd server && ./gradlew build
   ```
3. **Run the server**:
   ```bash
   ./gradlew run --args="3000"
   ```
   (Replace `3000` with your desired port)

   Alternatively, you can build a fat JAR and run it with `java -jar <fat.jar>`.

   The server will start and listen for MCP connections on the specified port.

   If that works, hit Ctrl+C to shut the server down. It should have created a database file named ``data/users.db``.

3. **Add users and server profiles**:

   The AI that acts on your behalf has to authenticate against davmcp. You have to create a token for that
   in the database:
   ```
   <TODO> sqlite3 command to insert a user (see User.sq for table definition)
   ```

4. **Add the MCP connection to your AI model.**

## Configuration

The server requires configuration for your CalDAV server. Refer to the code for available configuration options.

## Development

This project uses Gradle for dependency management and building:

```bash
# Build a fat JAR with all dependencies
./gradlew fatJar

# Run tests
./gradlew test
```

## About MCP

The [Model Context Protocol](https://modelcontextprotocol.org/) is a protocol for AI agents to interact with external tools and services in a structured way.
