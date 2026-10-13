package com.plinkoria.game.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.annotation.RawRes
import com.plinkoria.game.R
import java.util.Collections

enum class Sfx(@RawRes val resId: Int) {
    Click(R.raw.sfx_click),
    Correct(R.raw.sfx_correct),
    Wrong(R.raw.sfx_wrong),
    Match(R.raw.sfx_match),
    Win(R.raw.sfx_win),
    Lose(R.raw.sfx_lose)
}

/**
 * Short sound effects via SoundPool. Created once in the Application.
 * [enabled] is kept in sync with the persisted Settings by the Application.
 * All failures are swallowed — audio must never crash the game.
 */
class SoundManager(context: Context) {

    @Volatile
    var enabled: Boolean = true

    private val loaded: MutableSet<Int> = Collections.synchronizedSet(HashSet())
    private val soundIds = HashMap<Sfx, Int>()
    private val pool: SoundPool? = try {
        SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .build()
    } catch (e: Exception) {
        null
    }

    init {
        pool?.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) loaded.add(sampleId)
        }
        val appContext = context.applicationContext
        Sfx.entries.forEach { sfx ->
            try {
                pool?.load(appContext, sfx.resId, 1)?.let { soundIds[sfx] = it }
            } catch (_: Exception) {
            }
        }
    }

    fun play(sfx: Sfx) {
        if (!enabled) return
        val p = pool ?: return
        val id = soundIds[sfx] ?: return
        if (id !in loaded) return
        try {
            p.play(id, 0.9f, 0.9f, 1, 0, 1f)
        } catch (_: Exception) {
        }
    }
}
