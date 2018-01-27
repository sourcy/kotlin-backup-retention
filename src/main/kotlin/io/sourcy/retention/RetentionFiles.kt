package io.sourcy.retention

import java.io.File

class RetentionFiles(private val directories: Sequence<File>) {
    fun allMatchingFiles(pattern: String) =
        directories.flatMap { it.walkTopDown().filter { it.exists() } }
}