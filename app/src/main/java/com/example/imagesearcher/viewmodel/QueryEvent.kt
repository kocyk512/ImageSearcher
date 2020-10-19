package com.example.imagesearcher.viewmodel

class QueryEvent(
    val data: String,
    val status: QueryStatus
)

sealed class QueryStatus{
    object Valid : QueryStatus()
    object ErrorToShort : QueryStatus()
    object ErrorFormat : QueryStatus()
}