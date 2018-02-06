package io.sourcy.retention

import arrow.core.Either
import arrow.data.Try
import mu.KLogging
import kotlin.math.roundToInt

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

        val result = runAllMaybe(retentionInfos)

        logResult(result)

        return result
    }

    private fun runAllMaybe(retentionInfos: Iterable<Either<RetentionLogic.Error, RetentionLogic.Info>>): List<Either<Error, Result>> {
        return if (arguments.dryRun) {
            logger.info("Dry Run. Doing nothing.")
            emptyList()
        } else {
            retentionInfos.flatMap(::runSingleMaybe)
        }
    }

    private fun runSingleMaybe(it: Either<RetentionLogic.Error, RetentionLogic.Info>) =
            it.toOption().toList().map(::runSingle)

    private fun runSingle(retentionInfo: RetentionLogic.Info): Either<Error, Result> =
            Try { Result(retentionInfo, false) }
                    .toEither()
                    .mapLeft { Error(retentionInfo, it) }

    private fun logSummary(errors: List<RetentionLogic.Error>, files: List<RetentionLogic.Info>, expired: List<RetentionLogic.Info>, percentageToDelete: Int) {
        logger.info {
            """
              |
              |----------------------------------------------------------------------------------------------------
              |File-Lookup Summary:
              |----------------------------------------------------------------------------------------------------
              |Number of errors: ${errors.size}
              |Number of files: ${files.size}
              |Number of files to delete: ${expired.size}
              |Percentage of files to delete: $percentageToDelete
              |----------------------------------------------------------------------------------------------------
              |Errors (${errors.size}):
              |----------------------------------------------------------------------------------------------------
              |${errors.map { "File: ${it.file.absolutePath}\nError: ${it.exception.message}" }
                    .joinToString("\n")}
              |----------------------------------------------------------------------------------------------------
              |Files identified for deletion (${expired.size}):
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
              |Retention info for all files (${files.size}):
              |----------------------------------------------------------------------------------------------------
              |${files.joinToString("\n")}
              |----------------------------------------------------------------------------------------------------
            """.trimMargin()
            }
        }
    }

    private fun logResult(results: List<Either<Error, Result>>) {
        val errors = results.flatMap { it.swap().toOption().toList() }
        val files = results.flatMap { it.toOption().toList() }
        val deleted = files.filter(Result::wasDeleted)
        logger.info {
            """
              |
              |----------------------------------------------------------------------------------------------------
              |Deletion Summary:
              |----------------------------------------------------------------------------------------------------
              |Number of errors: ${errors.size}
              |Number of files deletes: ${deleted.size}
              |----------------------------------------------------------------------------------------------------
              |Errors (${errors.size}):
              |----------------------------------------------------------------------------------------------------
              |${errors.map { "File: ${it.sourceInfo.file.absolutePath}\nError: ${it.exception.message}" }
                    .joinToString("\n")}
              |----------------------------------------------------------------------------------------------------
              |Files deleted (${deleted.size}):
              |----------------------------------------------------------------------------------------------------
              |${deleted.joinToString("\n") { it.sourceInfo.file.absolutePath }}
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

    data class Error(val sourceInfo: RetentionLogic.Info,
                     val exception: Throwable)
}