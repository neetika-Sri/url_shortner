package controllers

import akka.util.ByteString
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._

/**
  * Add your spec here.
  * You can mock out a whole application including requests, plugins etc.
  *
  * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
  */
class UrlShortnerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "UrlShortner " should {

    "respond as a bad request if keyIndex is not found in data store in GET request" in {
      val controller = new UrlShortnerController(stubControllerComponents())
      val urlshort = controller.fetchUrl("").apply(FakeRequest(GET, "/8"))

      status(urlshort) mustBe NOT_FOUND
    }

    "respond as a bad request if request body is empty in POST /save request" in {
      val controller = new UrlShortnerController(stubControllerComponents())
      val urlshort = controller.saveUrls().apply(FakeRequest(POST, "/save"))

      status(urlshort) mustBe BAD_REQUEST
    }

    "respond as a bad request if request body is not valid  in POST /save request" in {
      val controller = new UrlShortnerController(stubControllerComponents())
      val urlshort = controller.saveUrls().apply(FakeRequest(POST, "/save").withRawBody(ByteString("FTP://some url")))

      status(urlshort) mustBe BAD_REQUEST
    }

    "respond the key if request body is  valid  in POST /save request" in {
      val controller = new UrlShortnerController(stubControllerComponents())
      val urlshort = controller.saveUrls().apply(FakeRequest(POST, "/save").withTextBody("https://www.google.com"))

      status(urlshort) mustBe OK
      contentType(urlshort) mustBe Some("text/plain")
      contentAsString(urlshort) must include ("1")
    }


    "redirect to the actual url if keyIndex is found in data store in GET reuest" in {
      val controller = new UrlShortnerController(stubControllerComponents())
      val urlshort = controller.saveUrls().apply(FakeRequest(POST, "/save").withTextBody("https://www.google.com"))
      val key = contentAsString(urlshort)

      val urlFetch =  controller.fetchUrl(key).apply(FakeRequest(GET, s"/${key}"))
      status(urlFetch) mustBe SEE_OTHER
    }

    "render the Save request  from the router" in {
      val controller = new UrlShortnerController(stubControllerComponents())
      val request = FakeRequest(POST, "/save").withTextBody("https://www.google.com")
      val home = route(app, request).get

      status(home) mustBe OK
    }

    "render the fetch request  from the router" in {
      val controller = new UrlShortnerController(stubControllerComponents())
      val urlShort = controller.saveUrls().apply(FakeRequest(POST, "/save").withTextBody("https://www.google.com"))
      val key = contentAsString(urlShort)
      val request = FakeRequest(GET, s"/${key}")
      val home = route(app, request).get

      status(home) mustBe SEE_OTHER
    }
  }
}
