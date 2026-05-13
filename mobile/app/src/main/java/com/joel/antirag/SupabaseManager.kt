package com.joel.antirag

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.realtime.Realtime

import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json

object SupabaseManager {
    private const val SUPABASE_URL = "https://bmtslqeyilbxtyjshncd.supabase.co"
    private const val SUPABASE_KEY = "REPLACE_WITH_YOUR_KEY" // Move to local.properties or Secrets.kt

    val client: SupabaseClient by lazy {
        createSupabaseClient(SUPABASE_URL, SUPABASE_KEY) {
            install(Postgrest)
            install(Storage)
            install(Realtime)
            defaultSerializer = KotlinXSerializer(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
                encodeDefaults = true
            })
        }
    }
}
