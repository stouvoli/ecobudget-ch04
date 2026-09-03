package com.example.data.repository

import com.example.model.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * Interface du dépôt pour la gestion des transactions d'EcoBudget.
 * Définit le contrat d'accès asynchrone et réactif aux données.
 */
interface TransactionRepository {
    suspend fun getTransactions(): List<Transaction>
    suspend fun addTransaction(transaction: Transaction): Transaction
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun deleteTransaction(id: String)
}
