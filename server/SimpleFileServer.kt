import com.sun.net.httpserver.HttpServer
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpExchange
import java.io.File
import java.net.InetSocketAddress
import java.nio.file.Files

fun main() {
    val port = 8080
    val rootDir = File("public") // directory to serve files from
    if (!rootDir.exists()) rootDir.mkdirs()

    val server = HttpServer.create(InetSocketAddress(port), 0)
    server.createContext("/") { exchange ->
        val path = exchange.requestURI.path.removePrefix("/")
        val file = File(rootDir, path)

        if (file.exists() && file.isFile) {
            val content = Files.readAllBytes(file.toPath())
            exchange.sendResponseHeaders(200, content.size.toLong())
            exchange.responseBody.use { it.write(content) }
        } else {
            val response = "File not found"
            exchange.sendResponseHeaders(404, response.toByteArray().size.toLong())
            exchange.responseBody.use { it.write(response.toByteArray()) }
        }
    }

    server.executor = null // single-threaded
    server.start()
    println("File server started at http://localhost:8080/index.html")
}
