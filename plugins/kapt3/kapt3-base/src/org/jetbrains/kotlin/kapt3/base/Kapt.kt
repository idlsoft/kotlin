/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.kapt3.base

import com.sun.tools.javac.util.Context
import org.jetbrains.kotlin.kapt3.base.util.KaptLogger
import org.jetbrains.kotlin.kapt3.base.util.WriterBackedKaptLogger
import org.jetbrains.kotlin.kapt3.base.util.info
import kotlin.system.measureTimeMillis

object Kapt {
    private const val JAVAC_CONTEXT_CLASS = "com.sun.tools.javac.util.Context"

    @JvmStatic
    @Suppress("unused")
    fun kapt(
        paths: KaptPaths,
        isVerbose: Boolean,
        mapDiagnosticLocations: Boolean,
        annotationProcessorFqNames: List<String>,
        processorOptions: Map<String, String>,
        javacOptions: Map<String, String>
    ) {
        val logger = WriterBackedKaptLogger(isVerbose)

        if (!Kapt.checkJavacComponentsAccess(logger)) {
            return
        }

        val kaptContext = KaptContext(paths, false, logger, mapDiagnosticLocations, processorOptions, javacOptions)

        logger.info { "Kapt3 is enabled (stand-alone mode)." }
        logger.info { "Map diagnostic locations: $mapDiagnosticLocations" }
        paths.log(logger)
        logger.info { "Javac options: $javacOptions" }
        logger.info { "AP options: $processorOptions" }

        val javaSourceFiles = paths.collectJavaSourceFiles()
        logger.info { "Java source files: " + javaSourceFiles.joinToString { it.canonicalPath } }

        val processorLoader = ProcessorLoader(paths, annotationProcessorFqNames, logger)

        processorLoader.use {
            val processors = processorLoader.loadProcessors(findClassLoaderWithJavac())

            val annotationProcessingTime = measureTimeMillis {
                kaptContext.doAnnotationProcessing(javaSourceFiles, processors)
            }

            logger.info { "Annotation processing took $annotationProcessingTime ms" }
        }
    }

    fun checkJavacComponentsAccess(logger: KaptLogger): Boolean {
        try {
            Class.forName(JAVAC_CONTEXT_CLASS)
            return true
        } catch (e: ClassNotFoundException) {
            logger.error("'$JAVAC_CONTEXT_CLASS' class can't be found ('tools.jar' is absent in the plugin classpath). Kapt won't work.")
            return false
        }
    }

    private fun findClassLoaderWithJavac(): ClassLoader {
        fun Class<*>.toClassFilePath() = name.replace('.', '/') + ".class"

        val javacContextPath = Context::class.java.toClassFilePath()
        val kaptPath = Kapt::class.java.toClassFilePath()

        fun findRightClassLoader(current: ClassLoader): ClassLoader? {
            if (current.getResource(javacContextPath) != null && current.getResource(kaptPath) == null) {
                return current
            }

            val parent = current.parent ?: return null
            return findRightClassLoader(parent)
        }

        val kaptClassLoader = Kapt::class.java.classLoader
        return findRightClassLoader(kaptClassLoader) ?: kaptClassLoader
    }
}