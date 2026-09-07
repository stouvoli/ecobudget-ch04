package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Transaction
import com.example.ui.theme.DarkCardBadge
import com.example.ui.theme.DarkOutline
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.DarkTextSecondary
import ecobudget.shared.generated.resources.Res
import ecobudget.shared.generated.resources.content_desc_delete_format
import ecobudget.shared.generated.resources.content_desc_edit
import ecobudget.shared.generated.resources.currency_fcfa
import ecobudget.shared.generated.resources.date_today
import ecobudget.shared.generated.resources.date_yesterday
import org.jetbrains.compose.resources.stringResource

/**
 * Composant atomique réutilisable pour afficher chaque dépense.
 * Cliquer sur la carte déclenche [onClick] pour ouvrir le formulaire pré-rempli d'édition.
 *
 * @param transaction La transaction immuable à afficher.
 * @param onClick Callback déclenché au clic sur la carte (modification).
 * @param onDelete Callback déclenché pour supprimer la transaction.
 * @param modifier Modificateur Compose optionnel.
 */
@Composable
fun TransactionCard(
    transaction: Transaction,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryName = stringResource(transaction.category.labelResId)
    val todayText = stringResource(Res.string.date_today)
    val yesterdayText = stringResource(Res.string.date_yesterday)
    val currencyFcfa = stringResource(Res.string.currency_fcfa)

    val formattedDate = remember(transaction.date, todayText, yesterdayText) {
        formatRelativeDate(transaction.date, todayText, yesterdayText)
    }

    val formattedAmount = remember(transaction.amount, currencyFcfa) {
        val formattedNumber = transaction.amount.toLong()
            .toString()
            .reversed()
            .chunked(3)
            .joinToString(" ")
            .reversed()
        "$formattedNumber $currencyFcfa"
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(width = 1.dp, color = DarkOutline, shape = RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .testTag("transaction_card_${transaction.id}"),
        color = DarkSurfaceVariant,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Badge d'icône/emoji avec fond violet sombre contrasté
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkCardBadge),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = transaction.category.emoji,
                    fontSize = 22.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Intitulé en Blanc pur et métadonnées en Gris clair hautement lisible
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = transaction.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = Color.White,
                        maxLines = 1,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(Res.string.content_desc_edit),
                        tint = DarkTextSecondary.copy(alpha = 0.5f),
                        modifier = Modifier.size(13.dp)
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "$categoryName • $formattedDate",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = DarkTextSecondary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Montant en Blanc Pur et bouton de suppression
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "-$formattedAmount",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("delete_transaction_${transaction.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(Res.string.content_desc_delete_format, transaction.title),
                        tint = DarkTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private val MONTHS_FR = listOf(
    "janv.", "févr.", "mars", "avr.", "mai", "juin",
    "juil.", "août", "sept.", "oct.", "nov.", "déc."
)

/**
 * Formate un timestamp (epoch millis) en libellé "d MMM" (ex: "14 févr.") en pur Kotlin.
 */
fun formatRelativeDate(
    timestamp: Long,
    todayString: String = "Aujourd'hui",
    yesterdayString: String = "Hier"
): String {
    // Calcul calendaire civil universel à partir de l'epoch (sans SimpleDateFormat)
    val days = timestamp / 86_400_000L
    val z = days + 719468
    val era = (if (z >= 0) z else z - 146096) / 146097
    val doe = (z - era * 146097).toInt()
    val yoe = (doe - doe / 1024 + doe / 1461 - doe / 146096) / 365
    val doy = doe - (365 * yoe + yoe / 4 - yoe / 100)
    val mp = (5 * doy + 2) / 153
    val day = doy - (153 * mp + 2) / 5 + 1
    val month = mp + (if (mp < 10) 3 else -9) // Mois de 1 à 12

    val monthLabel = MONTHS_FR.getOrElse(month - 1) { "" }
    return "$day $monthLabel"
}