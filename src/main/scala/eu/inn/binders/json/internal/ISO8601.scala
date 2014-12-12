package eu.inn.binders.json.internal

import java.text.{ParseException, SimpleDateFormat}
import java.util.Date

/**
 * Credits go here: http://stackoverflow.com/a/10621553/795176
 * 
 * Helper class for handling ISO 8601 strings of the following format:
 * "2008-03-01T13:00:00+01:00". It also supports parsing the "Z" timezone.
 */
object ISO8601 {
  /** Transform Date to ISO 8601 string. */
  val formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
  def fromDate(date: Date): String = {
    val s = formatter.format(date)
    s.substring(0, 22) + ":" + s.substring(22)
  }

  /** Transform ISO 8601 string to Date. */
  def fromString(iso8601string: String): Date = {
    val s =
      try {
        val s2 = iso8601string.replace("Z", "+00:00")
        s2.substring(0, 22) + s2.substring(23)
      } catch {
        case e: IndexOutOfBoundsException ⇒
          throw new ParseException("Invalid length", 0)
      }
    formatter.parse(s)
  }
}