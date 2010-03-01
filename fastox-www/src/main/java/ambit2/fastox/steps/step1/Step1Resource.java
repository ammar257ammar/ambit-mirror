package ambit2.fastox.steps.step1;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.fastox.steps.FastoxStepResource;
import ambit2.fastox.users.UserResource;

/**
 * Define structure
 * @author nina
 *
 */
public class Step1Resource extends FastoxStepResource {
	protected String search = "";
	public Step1Resource() {
		super(1);
		helpResource = "step1.html";
	}
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		Form form = getRequest().getResourceRef().getQueryAsForm();
		search = form.getFirstValue(FastoxStepResource.params.text.toString());
		search = search==null?"":search;
	}
	@Override
	protected String getDefaultTab() {
		return "Search";
	}
	@Override
	protected List<String> createTabs() {
		List<String> tabs = new ArrayList<String>();
		tabs.add("Search");
		tabs.add("Draw");
		tabs.add("Upload");
		//tabs.add("Datasets");
		return tabs;
	}

	public void renderFormHeader(Writer writer, String key)  throws IOException {
		writer.write(String.format("<form name='%s' method='POST' %s action='%s/%s/%s/%s%s'>",
				"form",
				key.equals("Upload")?"enctype='multipart/form-data'":"",
				getRootRef(),
				UserResource.resource,
				Reference.encode(session.getUser().getId()),mode,wizard.nextStep(step)));
	}
	@Override
	public void renderFormContent(Writer writer, String key) throws IOException {

				
		
		writer.write("<br style='clear:both;' clear='all' />\n"); // Safari is not happy otherwise with floating elements
		if ("Search".equals(key)) {
		
			writer.write(String.format("<table><tr><th><label for='text'>Free text search <br>(Enter chemical name, registry identifier, SMILES, InChI, any keywords)<em><img src=\"%s/images/star.png\" title='Required field' alt=\"required\" /></em></label></th></tr><tr>",
					getRequest().getRootRef()
					));
			
			writer.write(String.format("<td><input name='text' tabindex='1' type=\"text\" size='80' value='%s'/>",search));
			writer.write(String.format("<div class='errors'>%s</div>",
					session.getError("text")==null?"":session.getError("text").getMessage()
					));				
			writer.write("</td><td>");
			writePageSize(new String[] {"1","5","10","20"}, writer, key);
			
		
			writer.write("</td><tr></table>");
			

		} else if ("Upload".equals(key)) {
			
			writer.write(String.format(
			"<table><tr><th><label for='file'>File upload<em><img src=\"%s/images/star.png\" title='Required field' alt=\"required\" /></em></label></th></tr><tr>"+
			"<td><input type='file' name='file' accept='chemical/x-mdl-sdfile' size='80'>",
			getRequest().getRootRef()
			)
			
			);
			
			writer.write(String.format("<div class='errors'>%s</div>",
					session.getError("file")==null?"":session.getError("file").getMessage()
					));	
			writer.write("</td><tr></table>");
		} else if ("Draw".equals(key)) {

			writer.write("<table>");
			writer.write(String.format("<tr><th><label for='JME'>Structure diagram<em><img src=\"%s/images/star.png\" title='Required field' alt=\"required\" /></em></label>",
					getRequest().getRootRef()
					));
			
			writer.write(String.format("<div class='errors'>%s</errors></div>",
					session.getError("search")==null?"":session.getError("search").getMessage()
					));
			writer.write("</th></tr><tr><td>");
			writer.write(String.format(
			"<applet code=\"JME.class\" name=\"JME\" codebase=\"%s/jme\" archive=\"JME.jar\" width=\"540\" height=\"360\">"+
			"<param name=\"options\" value=\"query,nohydrogens\">"+
			"You have to enable Java and JavaScript on your machine !"+ 
			"</applet>",
			
			getRequest().getRootRef()
			));
			
			writer.write("<input name='search' type='hidden'>");
			writer.write("</td><td>");
			writer.write("<label for='mode'>Search for&nbsp;</label><br>");
			writer.write("<input type='radio' name='mode' checked='checked' value='structure'>Structure<br><input type='radio' name='mode' value='substructure'>Substructure<br>");
			writer.write("<input type='radio' name='mode' value='similarity'>Similarity");

			writeSelectOption(new String[] {"0.9","0.8","0.7","0.6"}, writer, "threshold","Tanimoto","0.9");
			writer.write("<br><br>");
			writePageSize(new String[] {"1","5","10","20"}, writer, key);
			writer.write("</td></tr>");
			writer.write("</table>");
		} else if ("Datasets".equals(key)) {
			writer.write(
			"<table><tr><th>"+
			"<label for='dataset'>Dataset URL</label>"+
			"</th></tr><tr>");
					
			writer.write(String.format("<td><input name='dataset' tabindex='1' type=\"text\" size='80' value='%s'/></td><td>",search));

			writePageSize(new String[] {"1","5","10","20"}, writer, key);
			writer.write("</td><tr></table>");
		}
		writer.write(String.format("<input name='tab' type='hidden' value='%s'>",key));
		writer.write("<p>");
		super.renderFormContent(writer, key);
	}
	@Override
	public void renderErrorsTab(Writer writer, String key) throws IOException {
	}
	@Override
	public void renderResults(Writer writer, String key) throws IOException {
	}
	@Override
	protected boolean isMandatory(String param) {
		return false;
	}
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		Representation r =  super.get(variant);
		//session.setError(null);
		return r;
	}
}
