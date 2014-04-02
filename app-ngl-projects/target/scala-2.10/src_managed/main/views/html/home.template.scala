
package views.html

import play.templates._
import play.templates.TemplateMagic._

import play.api.templates._
import play.api.templates.PlayMagic._
import models._
import controllers._
import java.lang._
import java.util._
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import play.api.i18n._
import play.core.j.PlayMagicForJava._
import play.mvc._
import play.data._
import play.api.data.Field
import play.mvc.Http.Context.Implicit._
import views.html._
/**/
object home extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template0[play.api.templates.Html] {

    /**/
    def apply/*1.2*/():play.api.templates.Html = {
        _display_ {import play.mvc.Http.Context

def /*5.2*/scripts/*5.9*/():play.api.templates.Html = {_display_(

Seq[Any](format.raw/*5.15*/("""
	<script src=""""),_display_(Seq[Any](/*6.16*/controllers/*6.27*/.projects.tpl.routes.Projects.javascriptRoutes)),format.raw/*6.73*/("""" type="text/javascript" charset="utf-8"></script>
""")))};
Seq[Any](format.raw/*1.4*/("""

"""),format.raw/*4.1*/("""
"""),format.raw/*7.2*/("""

"""),_display_(Seq[Any](/*9.2*/views/*9.7*/.html.main("home.page.title", menu("home"), scripts)/*9.59*/ {_display_(Seq[Any](format.raw/*9.61*/("""
 <div  class="row">
  <div class="col-md-12 col-lg-12">
  	<div class="jumbotron">
   		<h1>test</h1>
    	<p>comment</p> 
   	</div>
  </div>
 </div>
""")))})),format.raw/*18.2*/("""
 """))}
    }
    
    def render(): play.api.templates.Html = apply()
    
    def f:(() => play.api.templates.Html) = () => apply()
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Wed Apr 02 16:01:43 CEST 2014
                    SOURCE: C:/Projets/new/ngl/app-ngl-projects/app/views/home.scala.html
                    HASH: 1bebbfa984c854017c1a114aad0954d69b650460
                    MATRIX: 715->1|806->37|820->44|889->50|940->66|959->77|1026->123|1116->3|1144->35|1171->175|1208->178|1220->183|1280->235|1319->237|1503->390
                    LINES: 26->1|29->5|29->5|31->5|32->6|32->6|32->6|34->1|36->4|37->7|39->9|39->9|39->9|39->9|48->18
                    -- GENERATED --
                */
            