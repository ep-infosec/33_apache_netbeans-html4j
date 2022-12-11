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
package net.java.html.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** The threading model of classes generated by {@link Model @Model} requires
 * that all operations are perform from the originating thread - unless they
 * are invoked as {@link ModelOperation @ModelOperation} methods.
 * <p>
 * A method in a class annotated by {@link Model @Model} annotation may be
 * annotated by {@link ModelOperation @ModelOperation}. The method has
 * to be static (unless {@link Model#instance() instance mode} is on), 
 * non-private and return <code>void</code>. The first parameter
 * of the method must be the {@link Model#className() model class} followed
 * by any number of additional arguments.
 * <p>
 * As a result method of the same name and the same list of additional arguments
 * (e.g. without the first model class one) will be generated into the 
 * {@link Model#className() model class}. This method can be invoked on any
 * thread, any time and it is the responsibility of model manipulating
 * technology to ensure the model is available and only then call back to 
 * the original method annotated by {@link ModelOperation @ModelOperation}.
 * The call may happen synchronously (if possible), or be delayed and invoked
 * later (on appropriate dispatch thread), without blocking the caller.
 * <pre>
 * 
 * {@link Model @Model}(className="Names", properties={
 *   {@link Property @Property}(name = "names", type=String.class, array = true)
 * })
 * static class NamesModel {
 *   {@link ModelOperation @ModelOperation} static void <b>updateNames</b>(Names model, {@link java.util.List}&lt;String;gt; arr) {
 *     <em>// can safely access the model</em>
 *     model.getNames().addAll(arr);
 *   }
 * 
 *   static void initialize() {
 *     final Names pageModel = new Names();
 *     pageModel.applyBindings();
 * 
 *     <em>// spawn to different thread</em>
 *     {@link java.util.concurrent.Executors}.newSingleThreadExecutor().execute({
 *       List&lt;String&gt; arr = <em>// ... obtain the names somewhere from network</em>
 *       pageModel.<b>updateNames</b>(arr);
 *       // returns immediately, later it invokes the model operation
 *     });
 * 
 *   }
 * }
 * </pre>
 * 
 * @author Jaroslav Tulach
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface ModelOperation {
}
