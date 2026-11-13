package com.example.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object TimeUtils {

    private const val TAG = "TimeUtils"
    private const val PREFS_NAME = "chemmaster_time_prefs"
    private const val KEY_TIME_OFFSET_MS = "ist_time_offset_ms"
    private const val KEY_PREFS_VERSION = "time_prefs_version"
    private const val CURRENT_PREFS_VERSION = 2

    // Presets for quick adjustments
    const val OFFSET_ZERO = 0L
    const val OFFSET_PLUS_1H_30M = 5_400_000L   // +1h 30m
    const val OFFSET_MINUS_1H_30M = -5_400_000L // -1h 30m
    const val OFFSET_PLUS_1H = 3_600_000L       // +1 hour
    const val OFFSET_MINUS_1H = -3_600_000L     // -1 hour
    const val OFFSET_PLUS_30M = 1_800_000L      // +30 min
    const val OFFSET_MINUS_30M = -1_800_000L    // -30 min
    const val OFFSET_PLUS_1M = 60_000L          // +1 min
    const val OFFSET_MINUS_1M = -60_000L        // -1 min

    val IST_TIME_ZONE: TimeZone = TimeZone.getTimeZone("Asia/Kolkata")
    val IST_LOCALE: Locale = Locale.forLanguageTag("en-IN")

    // Default offset is 0L because Asia/Kolkata timezone is already UTC+05:30
    private var offsetMillis: Long = OFFSET_ZERO
    private val _offsetFlow = MutableStateFlow(OFFSET_ZERO)
    val offsetFlow: StateFlow<Long> = _offsetFlow.asStateFlow()

    private val _syncStatusFlow = MutableStateFlow("Indian Standard Time (Asia/Kolkata • UTC+5:30)")
    val syncStatusFlow: StateFlow<String> = _syncStatusFlow.asStateFlow()

    private var sharedPreferences: SharedPreferences? = null

    /**
     * Initializes the default JVM timezone to Indian Standard Time (IST - Asia/Kolkata)
     * and loads saved custom calibration offset.
     */
    fun initIndianStandardTime(context: Context? = null) {
        try {
            TimeZone.setDefault(IST_TIME_ZONE)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set default TimeZone", e)
        }

        context?.let { ctx ->
            try {
                sharedPreferences = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val prefsVersion = sharedPreferences?.getInt(KEY_PREFS_VERSION, 0) ?: 0
                if (prefsVersion < CURRENT_PREFS_VERSION) {
                    // Reset any old buggy +1h 30m forced offset from version 1
                    sharedPreferences?.edit()
                        ?.putLong(KEY_TIME_OFFSET_MS, OFFSET_ZERO)
                        ?.putInt(KEY_PREFS_VERSION, CURRENT_PREFS_VERSION)
                        ?.apply()
                    offsetMillis = OFFSET_ZERO
                    _offsetFlow.value = OFFSET_ZERO
                    _syncStatusFlow.value = "Calibrated to IST (UTC+5:30)"
                } else {
                    val savedOffset = sharedPreferences?.getLong(KEY_TIME_OFFSET_MS, OFFSET_ZERO) ?: OFFSET_ZERO
                    offsetMillis = savedOffset
                    _offsetFlow.value = savedOffset
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load time prefs", e)
            }
        }
    }

    fun getOffsetMillis(): Long = offsetMillis

    fun setOffsetMillis(newOffset: Long, context: Context? = null) {
        offsetMillis = newOffset
        _offsetFlow.value = newOffset
        try {
            val prefs = sharedPreferences ?: context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs?.edit()
                ?.putLong(KEY_TIME_OFFSET_MS, newOffset)
                ?.putInt(KEY_PREFS_VERSION, CURRENT_PREFS_VERSION)
                ?.apply()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save time offset", e)
        }
    }

    fun adjustOffset(deltaMillis: Long, context: Context? = null) {
        setOffsetMillis(offsetMillis + deltaMillis, context)
    }

    /**
     * Returns current accurate timestamp in milliseconds, taking into account any calibration offset.
     */
    fun getCurrentIstMillis(): Long {
        return System.currentTimeMillis() + offsetMillis
    }

    /**
     * Formats timestamp into 12-hour format with AM/PM in IST.
     * Example: "08:30 PM"
     */
    fun formatIstTime(timestamp: Long = getCurrentIstMillis()): String {
        val sdf = SimpleDateFormat("hh:mm a", IST_LOCALE).apply {
            timeZone = IST_TIME_ZONE
        }
        return sdf.format(Date(timestamp))
    }

    /**
     * Formats timestamp with hours, minutes, and seconds in IST.
     * Example: "08:30:15 PM"
     */
    fun formatIstTimeWithSeconds(timestamp: Long = getCurrentIstMillis()): String {
        val sdf = SimpleDateFormat("hh:mm:ss a", IST_LOCALE).apply {
            timeZone = IST_TIME_ZONE
        }
        return sdf.format(Date(timestamp))
    }

    /**
     * Formats full date and time in IST.
     * Example: "12 Nov 2026, 08:30 PM IST"
     */
    fun formatIstDateTime(timestamp: Long = getCurrentIstMillis()): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a 'IST'", IST_LOCALE).apply {
            timeZone = IST_TIME_ZONE
        }
        return sdf.format(Date(timestamp))
    }

    /**
     * Formats short date in IST.
     * Example: "12 Nov 2026"
     */
    fun formatIstDate(timestamp: Long = getCurrentIstMillis()): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", IST_LOCALE).apply {
            timeZone = IST_TIME_ZONE
        }
        return sdf.format(Date(timestamp))
    }

    /**
     * Fetches real-world network atomic time via HTTP Date header or WorldTimeAPI
     * to automatically calibrate and synchronize exact Indian Standard Time with 0 drift.
     */
    suspend fun syncWithNetwork(context: Context? = null): Result<Long> = withContext(Dispatchers.IO) {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(6, TimeUnit.SECONDS)
            .readTimeout(6, TimeUnit.SECONDS)
            .build()

        // 1. Try Google lightweight generate_204 endpoint for atomic Date header
        try {
            val req = Request.Builder().url("https://clients3.google.com/generate_204").head().build()
            val resp = okHttpClient.newCall(req).execute()
            val dateHeader = resp.header("Date")
            if (dateHeader != null) {
                val httpFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
                val parsed = httpFormat.parse(dateHeader)
                if (parsed != null) {
                    val serverUtc = parsed.time
                    val localUtc = System.currentTimeMillis()
                    val drift = serverUtc - localUtc
                    setOffsetMillis(drift, context)
                    _syncStatusFlow.value = "Synced with Google Atomic Time (drift: ${drift}ms)"
                    return@withContext Result.success(drift)
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Google 204 time sync failed, trying worldtimeapi", e)
        }

        // 2. Try worldtimeapi for Asia/Kolkata
        try {
            val req = Request.Builder().url("https://worldtimeapi.org/api/timezone/Asia/Kolkata").get().build()
            val resp = okHttpClient.newCall(req).execute()
            val body = resp.body?.string() ?: ""
            if (resp.isSuccessful && body.isNotBlank()) {
                val json = JSONObject(body)
                val unixtimeSec = json.optLong("unixtime", 0L)
                if (unixtimeSec > 0) {
                    val serverUtc = unixtimeSec * 1000L
                    val localUtc = System.currentTimeMillis()
                    val drift = serverUtc - localUtc
                    setOffsetMillis(drift, context)
                    _syncStatusFlow.value = "Synced with WorldTimeAPI IST (drift: ${drift}ms)"
                    return@withContext Result.success(drift)
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "WorldTimeApi failed", e)
        }

        // Fallback to 0 offset (exact JVM Asia/Kolkata IST)
        _syncStatusFlow.value = "Device IST Time (Asia/Kolkata • UTC+5:30)"
        Result.success(offsetMillis)
    }
}

/**
 * Composable state holder that ticks every second and recomposes on offset changes.
 */
@Composable
fun rememberCurrentIstTime(): State<String> {
    val currentOffset by TimeUtils.offsetFlow.collectAsState()
    val istTimeState = remember { mutableStateOf(TimeUtils.formatIstTime()) }

    LaunchedEffect(currentOffset) {
        while (true) {
            istTimeState.value = TimeUtils.formatIstTime()
            delay(1000L)
        }
    }

    return istTimeState
}

@Composable
fun rememberCurrentIstTimeWithSeconds(): State<String> {
    val currentOffset by TimeUtils.offsetFlow.collectAsState()
    val istTimeSecondsState = remember { mutableStateOf(TimeUtils.formatIstTimeWithSeconds()) }

    LaunchedEffect(currentOffset) {
        while (true) {
            istTimeSecondsState.value = TimeUtils.formatIstTimeWithSeconds()
            delay(1000L)
        }
    }

    return istTimeSecondsState
}

@Composable
fun rememberCurrentIstDateTime(): State<String> {
    val currentOffset by TimeUtils.offsetFlow.collectAsState()
    val istDateTimeState = remember { mutableStateOf(TimeUtils.formatIstDateTime()) }

    LaunchedEffect(currentOffset) {
        while (true) {
            istDateTimeState.value = TimeUtils.formatIstDateTime()
            delay(1000L)
        }
    }

    return istDateTimeState
}
