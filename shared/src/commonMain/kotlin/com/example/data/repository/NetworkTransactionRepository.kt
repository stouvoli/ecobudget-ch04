package com.example.data.repository

import com.example.model.Transaction
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class NetworkTransactionRepository(
    private val client: HttpClient,
    private val studentId: String
) : TransactionRepository {

    private val baseUrl = "https://ecobudget-api-950033221721.europe-west9.run.app"

    override suspend fun getTransactions(): List<Transaction> {
        return client.get("$baseUrl/transactions") {
            parameter("studentId", studentId)
        }.body()
    }

    override suspend fun addTransaction(transaction: Transaction): Transaction {
        return client.post("$baseUrl/transactions") {
            parameter("studentId", studentId)
            contentType(ContentType.Application.Json)
            setBody(transaction)
        }.body()
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        client.put("$baseUrl/transactions/${transaction.id}") {
            parameter("studentId", studentId)
            contentType(ContentType.Application.Json)
            setBody(transaction)
        }
    }

    override suspend fun deleteTransaction(id: String) {
        client.delete("$baseUrl/transactions/$id") {
            parameter("studentId", studentId)
        }
    }
}