package com.example.model

import androidx.compose.runtime.Immutable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val monthLabelFormatter = object : ThreadLocal<SimpleDateFormat>() {
    override fun initialValue(): SimpleDateFormat {
        return SimpleDateFormat("MMMM yyyy", Locale.FRENCH)
    }
}

/**
 * Modèle immuable représentant un mois spécifique pour la navigation budgétaire.
 *
 * @property year Année (ex: 2026).
 * @property month Index du mois de 0 (Janvier) à 11 (Décembre).
 */
@Immutable
data class YearMonth(
    val year: Int,
    val month: Int
) {
    /**
     * Bornes temporelles (timestamps millisecondes) précalculées pour ce mois.
     */
    val startTimestamp: Long by lazy {
        val cal = Calendar.getInstance()
        cal.clear()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.timeInMillis
    }

    val endTimestamp: Long by lazy {
        val cal = Calendar.getInstance()
        cal.clear()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        cal.timeInMillis
    }

    /**
     * Libellé formaté en français (ex: "Août 2026").
     */
    val displayLabel: String by lazy {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val sdf = monthLabelFormatter.get() ?: SimpleDateFormat("MMMM yyyy", Locale.FRENCH)
        val formatted = sdf.format(Date(cal.timeInMillis))
        formatted.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.FRENCH) else it.toString() }
    }

    /**
     * Retourne le YearMonth précédent.
     */
    fun previous(): YearMonth {
        return if (month == 0) {
            YearMonth(year - 1, 11)
        } else {
            YearMonth(year, month - 1)
        }
    }

    /**
     * Retourne le YearMonth suivant.
     */
    fun next(): YearMonth {
        return if (month == 11) {
            YearMonth(year + 1, 0)
        } else {
            YearMonth(year, month + 1)
        }
    }

    /**
     * Vérifie si un timestamp millisecondes appartient à ce mois précis.
     * Effectue une comparaison O(1) ultra-rapide sans réinstancier Calendar.
     */
    fun containsTimestamp(timestamp: Long): Boolean {
        return timestamp in startTimestamp..endTimestamp
    }

    companion object {
        /**
         * Crée le YearMonth courant.
         */
        fun current(): YearMonth {
            val cal = Calendar.getInstance()
            return YearMonth(
                year = cal.get(Calendar.YEAR),
                month = cal.get(Calendar.MONTH)
            )
        }

        /**
         * Crée le YearMonth correspondant à un timestamp.
         */
        fun fromTimestamp(timestamp: Long): YearMonth {
            val cal = Calendar.getInstance()
            cal.timeInMillis = timestamp
            return YearMonth(
                year = cal.get(Calendar.YEAR),
                month = cal.get(Calendar.MONTH)
            )
        }
    }
}
