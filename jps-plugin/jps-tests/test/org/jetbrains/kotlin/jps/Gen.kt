/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.jps

import org.jetbrains.jps.builders.JpsBuildTestCase
import org.jetbrains.kotlin.jps.build.dependeciestxt.actualizeMppJpsIncTestCaseDirs

class Gen : JpsBuildTestCase() {
    fun testGen() {
        actualizeMppJpsIncTestCaseDirs("/Users/jetbrains/tasks/jpsmpp/jps-plugin/testData", "incremental/multiplatform/multiModule")
    }
}