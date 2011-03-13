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

import akka.actor.Actor
import dispatch.Http
import dispatch.json.JsObject
import dispatch.twitter.Search
import scala.collection.immutable.Seq

object ScalaTipsActor {

  sealed trait ScalaTipsMessages

  case object LookupScalaTips extends ScalaTipsMessages

  case object GetAllScalaTips extends ScalaTipsMessages

  private val Date = """\w{3},\s(.*):\d{2}\s\+0000""".r
}

class ScalaTipsActor extends Actor {
  import ScalaTipsActor._

  override protected def receive = {
    case LookupScalaTips => lookupScalaTips()
    case GetAllScalaTips => self.reply(scalaTips)
  }

  private var scalaTips: Seq[ScalaTip] = Nil

  private def lookupScalaTips() {
    log.debug("About to lookup Scala tips ...")

    val scalaTips = {
      def scalaTip(obj: JsObject) = {
        val Search.from_user(user) = obj
        val Search.created_at(dateString) = obj
        val Search.text(message) = obj
        val Date(date) = dateString
        val scalaTip = ScalaTip(user, date, message)
        log.debug("Found " + scalaTip.toString)
        scalaTip
      }
      import Http._
      Http(Search("#Scala tip of the day -RT") lang "en") map scalaTip
    }

    this.scalaTips ++= scalaTips filter { scalaTip => !(this.scalaTips contains scalaTip) }
  }
}