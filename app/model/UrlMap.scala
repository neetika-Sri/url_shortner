package model


case class UrlMap(url: String, key: String)


object UrlMap {
  var keySeq: Int = 0
  var urls = scala.collection.mutable.ListBuffer[UrlMap]()
  def save(url: String) : String = {
     keySeq = keySeq + 1
     urls += UrlMap(url, keySeq.toString)
     keySeq.toString
  }

  def fetch(key: String) :Option[String] = {
     val res = urls.filter(u => u.key == key).headOption

    if(res.isDefined){
      return Some(res.get.url)
    }
    None
  }


  //def geturlFromJson()



}
