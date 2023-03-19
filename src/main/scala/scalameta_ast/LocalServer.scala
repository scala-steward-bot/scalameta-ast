package scalameta_ast

import unfiltered.request.Path
import unfiltered.response.HtmlContent
import unfiltered.response.JsContent
import unfiltered.response.NotFound
import unfiltered.response.ResponseString
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files

object LocalServer {
  def main(args: Array[String]): Unit = {
    unfiltered.jetty.Server.anylocal
      .plan(
        unfiltered.filter.Planify { case Path(p) =>
          val f = new File("sources", if (p == "/") "index.html" else s".$p")
          if (f.isFile) {
            val path = f.toPath
            val res = ResponseString(Files.readString(path, StandardCharsets.UTF_8))
            if (p.endsWith(".html")) {
              HtmlContent ~> res
            } else if (p.endsWith(".js")) {
              JsContent ~> res
            } else {
              res
            }
          } else {
            NotFound
          }
        }
      )
      .run { svr =>
        unfiltered.util.Browser.open(svr.portBindings.head.url)
      }
  }
}
