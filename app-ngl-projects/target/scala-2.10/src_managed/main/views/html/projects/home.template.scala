
package views.html.projects

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
object home extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template1[String,play.api.templates.Html] {

    /**/
    def apply/*1.2*/(code: String):play.api.templates.Html = {
        _display_ {
def /*3.2*/scripts/*3.9*/():play.api.templates.Html = {_display_(

Seq[Any](format.raw/*3.15*/("""
	 <script src=""""),_display_(Seq[Any](/*4.17*/controllers/*4.28*/.projects.tpl.routes.Projects.javascriptRoutes)),format.raw/*4.74*/("""" type="text/javascript" charset="utf-8"></script>
""")))};
Seq[Any](format.raw/*1.16*/("""

"""),format.raw/*5.2*/("""

"""),_display_(Seq[Any](/*7.2*/views/*7.7*/.html.main("projects.page.title", menu("projects."+code), scripts))),format.raw/*7.73*/("""

"""))}
    }
    
    def render(code:String): play.api.templates.Html = apply(code)
    
    def f:((String) => play.api.templates.Html) = (code) => apply(code)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Wed Apr 02 16:22:20 CEST 2014
                    SOURCE: C:/Projets/new/ngl/app-ngl-projects/app/views/projects/home.scala.html
                    HASH: 39a246294b3b33d91c174c55f405f343b7353e06
                    MATRIX: 731->1|805->20|819->27|888->33|941->51|960->62|1027->108|1119->15|1149->161|1188->166|1200->171|1287->237
                    LINES: 26->1|28->3|28->3|30->3|31->4|31->4|31->4|33->1|35->5|37->7|37->7|37->7
                    -- GENERATED --
                */
            