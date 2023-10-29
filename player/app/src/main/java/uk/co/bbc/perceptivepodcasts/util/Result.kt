package uk.co.bbc.perceptivepodcasts.util

sealed class Result<out Payload, out Error> {
    data class Success<out Payload>(val item: Payload) : Result<Payload, Nothing>()
    data class Error<out Error>(val error: Error) : Result<Nothing, Error>()

    companion object {
        val Error = Error(null)
    }
}
