/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

fun Settings.findProperty(propertyName: String) =
        if (extra.has(propertyName))
            extra.get(propertyName)
        else null

val buildCacheUser = settings.findProperty("buildCache.user")?.toString()
        ?: System.getenv()["GRADLE_BUILD_CACHE_USER"]
val buildCachePassword = settings.findProperty("buildCache.password")?.toString()
        ?: System.getenv()["GRADLE_BUILD_CACHE_PASSWORD"]
val isCiServer = System.getenv().containsKey("CI")
val canWrite = buildCacheUser != null && buildCachePassword != null && isCiServer
buildCache {
    if (!gradle.startParameter.isOffline) {
        local {
            isEnabled = !canWrite
        }
        remote<HttpBuildCache> {
            url = uri("http://35.204.219.75:3000/cache/")
            isPush = canWrite
            credentials {
                username = buildCacheUser
                password = buildCachePassword
            }
        }
    }
}