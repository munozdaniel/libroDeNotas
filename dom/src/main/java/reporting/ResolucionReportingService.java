package reporting;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.NotContributed.As;
import org.apache.isis.applib.annotation.NotInServiceMenu;
import org.apache.isis.applib.services.bookmark.BookmarkService;
import org.apache.isis.applib.value.Blob;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Text;
import org.jdom2.output.XMLOutputter;
import org.joda.time.LocalDate;

import com.danhaywood.isis.domainservice.docx.DocxService;
import com.danhaywood.isis.domainservice.docx.DocxService.MatchingPolicy;
import com.danhaywood.isis.domainservice.docx.LoadInputException;
import com.danhaywood.isis.domainservice.docx.LoadTemplateException;
import com.danhaywood.isis.domainservice.docx.MergeException;
import com.google.common.io.Resources;

import dom.resoluciones.Resoluciones;


public class ResolucionReportingService {

	private final static String MIME_TYPE_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

	private byte[] templates;

	public ResolucionReportingService() throws IOException {
		final URL templateUrl = Resources.getResource(
				NotaReportingService.class, "Resolucion.docx");
		templates = Resources.toByteArray(templateUrl);
	}

	@NotContributed(As.ASSOCIATION)
	// ie contributed as action
	@NotInServiceMenu
	@Named("Descargar Documento")
	public Blob downloadAsDoc(Resoluciones unaResolucion) throws LoadInputException,
			LoadTemplateException, MergeException {

		final String html = asInputHtml(unaResolucion);
		final byte[] byteArray = mergeToDocx(html);

		final String outputFileName = "Resolucion-"
				+ bookmarkService.bookmarkFor(unaResolucion).getIdentifier()
				+ ".docx";
		return new Blob(outputFileName, MIME_TYPE_DOCX, byteArray);
	}

	private static String asInputHtml(Resoluciones unaResolucion) {
		final Element htmlEl = new Element("html");
		Document doc = new Document();
		doc.setRootElement(htmlEl);

		final Element bodyEl = new Element("body");
		htmlEl.addContent(bodyEl);

		bodyEl.addContent(newP("nro_nota", "plain", unaResolucion.getNro_resolucion() + ""));
		bodyEl.addContent(newP("fecha", "date", fechaACadena(unaResolucion)));
		bodyEl.addContent(newP("nombre_sector", "plain", unaResolucion.getSector()
				.getNombre_sector()));
		bodyEl.addContent(newP("responsable", "plain", unaResolucion.getSector()
				.getResponsable()));
		bodyEl.addContent(newP("descripcion", "plain", unaResolucion.getDescripcion()));
		// final Element ulDependencies = new Element("ul");
		// ulDependencies.setAttribute("id", "Dependencies");

		// final SortedSet<ToDoItem> dependencies = toDoItem.getDependencies();
		// for (final ToDoItem dependency : dependencies) {
		// final Element liDependency = new Element("li");
		// ulDependencies.addContent(liDependency);
		// final Element pDependency = new Element("p");
		// pDependency.setContent(new Text(dependency.getDescription()));
		// liDependency.addContent(pDependency);
		// }
		// bodyEl.addContent(ulDependencies);

		final String html = new XMLOutputter().outputString(doc);
		return html;
	}

	private static String fechaACadena(Resoluciones unaResolucion) {
		LocalDate dueBy = unaResolucion.getFecha();
		return dueBy != null ? dueBy.toString("dd/MM/yyyy") : "";
	}

	private static Element newP(String id, String cls, String text) {
		final Element pDescription = new Element("p");
		pDescription.setAttribute("id", id);
		pDescription.setAttribute("class", cls);
		pDescription.setContent(new Text(text));
		return pDescription;
	}

	private byte[] mergeToDocx(final String html) throws LoadInputException,
			LoadTemplateException, MergeException {
		final ByteArrayInputStream docxTemplateIs = new ByteArrayInputStream(
				templates);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		docxService.merge(html, docxTemplateIs, baos, MatchingPolicy.LAX);
		byte[] byteArray = baos.toByteArray();
		return byteArray;
	}

	// //////////////////////////////////////

	@javax.inject.Inject
	private DocxService docxService;

	@javax.inject.Inject
	private BookmarkService bookmarkService;
	//
	// @NotContributed(As.ASSOCIATION)
	// @NotInServiceMenu
	// public String htmlInput(Nota unaNota) throws IOException {
	// // final URL url = NotaReportingService.class.getResource("Nota.docx");
	// // if(url !=null)
	// // return "HTML : "+ url.toString();
	// // else
	// // return "NULLOO";
	// // final URL templateUrl = Resources.getResource(
	// // NotaReportingService.class, "ToDoItem.docx");
	// // final byte[] notaTemplate = Resources.toByteArray(templateUrl);
	// // return "NOTATEMPLATES: " + notaTemplate.length;
	// final URL templateUrl = Resources.getResource(
	// NotaReportingService.class, "ToDoItem.docx");
	// toDoItemTemplates = Resources.toByteArray(templateUrl);
	// return toDoItemTemplates.toString();
	// }
}
