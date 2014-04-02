
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
object menu extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template1[String,play.api.templates.Html] {

    /**/
    def apply/*1.2*/(nav: String = ""):play.api.templates.Html = {
        _display_ {import play.mvc.Http.Context

import play.api.Play


Seq[Any](format.raw/*1.20*/("""

"""),format.raw/*5.1*/("""
  <!-- Navbar
    ================================================== -->
		
<nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
	<div class="container-fluid">
		<div class="navbar-header">
			<a class="navbar-brand" href=""""),_display_(Seq[Any](/*12.35*/controllers/*12.46*/.main.tpl.routes.Main.home())),format.raw/*12.74*/("""">"""),_display_(Seq[Any](/*12.77*/Messages("application.name"))),_display_(Seq[Any](/*12.106*/helper/*12.112*/.env())),format.raw/*12.118*/("""</a>
    	</div>
    	
    	<div class="collapse navbar-collapse">
    		<ul class="nav navbar-nav">
    	

			</ul>

			<ul class="nav navbar-nav navbar-right">
				<li>
				<a class="dropdown-toggle" data-toggle="dropdown" href="#" role="button" id="user">
				<i class="fa fa-user"></i> """),_display_(Seq[Any](/*24.33*/Context/*24.40*/.current().request().username())),format.raw/*24.71*/(""" <b class="caret"></b></a>
				<ul class="dropdown-menu" role="menu" aria-labelledby="user">
						<li><a href="/authentication/logout">"""),_display_(Seq[Any](/*26.45*/Messages("authentification.dropdown_list.signout"))),format.raw/*26.95*/("""</a></li>
				</ul>
				</li>				
			</ul>
		</div>
	</div>		
</nav>


"""))}
    }
    
    def render(nav:String): play.api.templates.Html = apply(nav)
    
    def f:((String) => play.api.templates.Html) = (nav) => apply(nav)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Wed Apr 02 14:44:23 CEST 2014
                    SOURCE: C:/Projets/new/ngl/app-ngl-projects/app/views/menu.scala.html
                    HASH: a285ac97570a66bda8d0ac3f7f298541ea7da4fd
                    MATRIX: 722->1|868->19|896->73|1175->316|1195->327|1245->355|1284->358|1344->387|1360->393|1389->399|1716->690|1732->697|1785->728|1958->865|2030->915
                    LINES: 26->1|32->1|34->5|41->12|41->12|41->12|41->12|41->12|41->12|41->12|53->24|53->24|53->24|55->26|55->26
                    -- GENERATED --
                */
            