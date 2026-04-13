package com.fatec.merge_skills_kmp.routes

import com.fatec.merge_skills_kmp.domain.models.ApiError
import com.fatec.merge_skills_kmp.domain.models.UploadResponse
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.application.call
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import java.util.UUID

fun Route.uploadRoutes(supabase: SupabaseClient) {
    route("/upload") {
        post("/avatar") {
            val multipart = call.receiveMultipart()
            var fileBytes: ByteArray? = null
            var fileName: String? = null
            var userId: String? = null
            
            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        fileBytes = part.streamProvider().readBytes()
                        val extension = part.originalFileName?.substringAfterLast('.', "jpg") ?: "jpg"
                        fileName = "${UUID.randomUUID()}.$extension"
                    }
                    is PartData.FormItem -> {
                        if (part.name == "userId") {
                            userId = part.value
                        }
                    }
                    else -> {}
                }
                part.dispose()
            }

            if (fileBytes != null && fileName != null && userId != null) {
                try {
                    val bucket = supabase.storage.from("avatars")
                    val path = "profile/$userId/$fileName"
                    
                    bucket.upload(path, fileBytes!!, upsert = true)
                    
                    val publicUrl = bucket.publicUrl(path)
                    
                    call.respond(HttpStatusCode.OK, UploadResponse(url = publicUrl))
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond(
                        HttpStatusCode.InternalServerError, 
                        ApiError(code = "UPLOAD_FAILED", message = "Upload failed: ${e.message}")
                    )
                }
            } else {
                val errorMsg = if (userId == null) "Missing userId" else "No file uploaded"
                call.respond(
                    HttpStatusCode.BadRequest, 
                    ApiError(code = "BAD_REQUEST", message = errorMsg)
                )
            }
        }
    }
}
