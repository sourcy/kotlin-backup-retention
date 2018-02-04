package io.sourcy.retention

import arrow.core.Either
import arrow.core.flatMap
import arrow.data.Try
import arrow.syntax.either.left
import arrow.syntax.either.right
import mu.KLogging

// TODO: verbose logging
class RetentionRunner(private val arguments: Arguments) {
    companion object : KLogging()

    fun run(retentionInfos: Iterable<Either<RetentionLogic.Error, RetentionLogic.Info>>): List<Either<Error, Result>> =
            if (arguments.dryRun) {
                emptyList()
            } else {
                // TODO: check for maxDelete & force argument
                retentionInfos.map(::runSingle)
            }

    private fun runSingle(retentionInfo: Either<RetentionLogic.Error, RetentionLogic.Info>): Either<Error, Result> =
            retentionInfo
                    .mapLeft(::convertError)
                    .flatMap(::executeRetention)

    private fun convertError(error: RetentionLogic.Error): Error =
            Error(error.left(), error.exception)

    private fun executeRetention(retentionInfo: RetentionLogic.Info): Either<Error, Result> =
            Try { Result(retentionInfo, false) }
                    .toEither()
                    .mapLeft { Error(retentionInfo.right(), it) }

    data class Result(val sourceInfo: RetentionLogic.Info,
                      val wasDeleted: Boolean)

    data class Error(val sourceInfo: Either<RetentionLogic.Error, RetentionLogic.Info>,
                     val exception: Throwable)
}