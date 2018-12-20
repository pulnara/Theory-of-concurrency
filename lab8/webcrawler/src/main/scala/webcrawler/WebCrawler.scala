package webcrawler

import scala.io.StdIn
import org.htmlcleaner.HtmlCleaner
import java.net.URL
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}


object WebCrawler extends App {

  val url = "http://galaxy.agh.edu.pl/~balis/dydakt/tw"
  val cleaner = new HtmlCleaner

  def crawl(maxDepth: Int, node: String, from: String = null, level: Int = 0): Unit = Future {
    var message = "depth " + level + ": " + node
    if (from != null) message += "\tfrom\t" + from
    println(message)

    if (level < maxDepth) {
//      if (!(node contains "http")) {
//        processNode(from + '/' + node, level) onComplete {
//          case Success(urls) => urls.foreach(href => crawl(maxDepth, href, from + '/' + node, level + 1))
//          case Failure(_) =>
//        }
//      }
      processNode(node, level) onComplete {
        case Success(urls) => urls.foreach(href => crawl(maxDepth, href, node, level + 1))
        case Failure(_) =>
      }
    }
  }

  def processNode(node: String, level: Int): Future[Iterable[String]] = Future {
    val rootNode = cleaner.clean(new URL(node))
    val elements = rootNode.getElementsByName("a", true)
    elements
      .map {elem => elem.getAttributeByName("href")}
      .distinct
  }

  crawl(3, url)
  StdIn.readLine()
}
