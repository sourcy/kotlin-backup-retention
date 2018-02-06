package io.sourcy.retention

import arrow.core.Either
import arrow.core.flatMap
import arrow.data.Try
import arrow.syntax.either.left
import arrow.syntax.either.right
import mu.KLogging
import kotlin.math.roundToInt

// TODO: verbose logging
class RetentionRunner(private val arguments: Arguments,
                      private val settings: Settings) {
    companion object : KLogging()

    fun run(retentionInfos: Iterable<Either<RetentionLogic.Error, RetentionLogic.Info>>): List<Either<Error, Result>> {
        val errors = retentionInfos.flatMap { it.swap().toOption().toList() }
        val files = retentionInfos.flatMap { it.toOption().toList() }
        val expired = files.filter(RetentionLogic.Info::isExpired)
        val percentageToDelete = (expired.size.toFloat() / files.size * 100).roundToInt()

        logSummary(errors, files, expired, percentageToDelete)

        requireForceIfMaxPercentageExceeded(percentageToDelete)

        return if (arguments.dryRun) {
            logger.info("Dry Run. Doing nothing.")
            emptyList()
        } else {
            retentionInfos.map(::runSingle)
        }
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

    private fun logSummary(errors: List<RetentionLogic.Error>, files: List<RetentionLogic.Info>, expired: List<RetentionLogic.Info>, percentageToDelete: Int) {
        logger.info {
            """
              |
              |----------------------------------------------------------------------------------------------------
              |Pre-Run Summary:
              |----------------------------------------------------------------------------------------------------
              |Number or Errors: ${errors.size}
              |Number of Files: ${files.size}
              |Number of Files to delete: ${expired.size}
              |Percentage to delete: $percentageToDelete
              |----------------------------------------------------------------------------------------------------
              |Errors (${errors.size}):
              |----------------------------------------------------------------------------------------------------
              |${errors.joinToString("\n") { """File: ${it.file.absolutePath}""" }
                    .zip(errors.joinToString("\n") { """Error: ${it.exception.message}""" })}
              |----------------------------------------------------------------------------------------------------
              |Files to delete (${expired.size}):
              |----------------------------------------------------------------------------------------------------
              |${expired.joinToString("\n") { it.file.absolutePath }}
              |----------------------------------------------------------------------------------------------------
            """.trimMargin()
        }
        if (arguments.verbose) {
            logger.info {
                """
              |
              |----------------------------------------------------------------------------------------------------
              |Retention Info for all files (${files.size}):
              |----------------------------------------------------------------------------------------------------
              |${files.joinToString("\n")}
              |----------------------------------------------------------------------------------------------------
            """.trimMargin()
            }
        }
    }

    private fun requireForceIfMaxPercentageExceeded(percentageToDelete: Int) {
        if (percentageToDelete > settings.files.maxPercentDelete) {
            logger.warn { "$percentageToDelete% of all files up for deletion, that is more than ${settings.files.maxPercentDelete}% allowed." }
            if (!arguments.useTheForce) {
                throw IllegalArgumentException("Increase max-percent-delete or use --force.")
            }
            logger.warn { "--force specified. Continuing." }
        }
    }

    data class Result(val sourceInfo: RetentionLogic.Info,
                      val wasDeleted: Boolean)

    data class Error(val sourceInfo: Either<RetentionLogic.Error, RetentionLogic.Info>,
                     val exception: Throwable)
}