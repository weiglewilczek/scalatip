/*
 * Copyright 2011 Weigle Wilczek GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.scalatip
package lib

import dispatch.Http
import dispatch.json.JsObject
import dispatch.twitter.Search
import net.liftweb.common.Loggable
import scala.collection.immutable.Seq

object OnlineScalaTipRepository {

  private val Date = """\w{3},\s(.*):\d{2}\s\+0000""".r
}

class OnlineScalaTipRepository extends ScalaTipRepository with Loggable {
  import OnlineScalaTipRepository._

  override def findAll: Seq[ScalaTip] = {
    def scalaTip(obj: JsObject) = {
      val Search.from_user(user) = obj
      val Search.created_at(dateString) = obj
      val Search.text(message) = obj
      val Date(date) = dateString
      val scalaTip = ScalaTip(user, date, message)
      logger.debug(scalaTip)
      scalaTip
    }
    import dispatch.Http._
    Http(Search("#Scala tip of the day -RT") lang "en") map scalaTip
  }
}
