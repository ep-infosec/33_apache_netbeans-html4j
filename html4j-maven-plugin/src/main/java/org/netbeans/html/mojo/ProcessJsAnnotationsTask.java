/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.netbeans.html.mojo;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Project;

public class ProcessJsAnnotationsTask extends DefaultTask {
    void processJsAnnotations(final Project p) {
        final Set<?> allSourceSets = (Set<?>) p.property("sourceSets");
        if (allSourceSets == null) {
            throw new GradleException("Cannot find sourceSets for project " + p);
        }
        for (Object sourceSet : allSourceSets) {
            ProcessJsAnnotations process = new ProcessJsAnnotations() {
                @Override
                protected void log(String msg) {
                    p.getLogger().info(msg);
                }
            };
            Iterable cp = invoke(Iterable.class, sourceSet, "getRuntimeClasspath");
            for (Object elem : cp) {
                final File pathElement = (File) elem;
                process.addClasspathEntry(pathElement);
            }
            Iterable<?> outs = invoke(Iterable.class, sourceSet, "getOutput");
            for (Object classes : outs) {
                process.addRoot((File) classes);
            }
            try {
                process.process();
            } catch (IOException ex) {
                throw new GradleException(ex.getMessage(), ex);
            }
        }
    }

    private static <T> T invoke(Class<T> returnType, Object obj, String methodName) {
        try {
            Method methodOutput = obj.getClass().getMethod(methodName);
            Object res = methodOutput.invoke(obj);
            return returnType.cast(res);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

}
