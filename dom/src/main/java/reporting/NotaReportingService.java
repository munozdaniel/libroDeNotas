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

import dom.nota.Nota;

public class NotaReportingService {

	private final static String MIME_TYPE_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

	private byte[] templates;

	public NotaReportingService() throws IOException {
		final URL templateUrl = Resources.getResource(
				NotaReportingService.class, "Nota.docx");
		templates = Resources.toByteArray(templateUrl);
	}

	@NotContributed(As.ASSOCIATION)
	@NotInServiceMenu
	@Named("Descargar Documento")
	public Blob downloadAsDoc(Nota unaNota) throws LoadInputException,
			LoadTemplateException, MergeException {

		final String html = asInputHtml(unaNota);
		final byte[] byteArray = mergeToDocx(html);

		final String outputFileName = "IMPS_Nota_" + unaNota.getNro_nota()
				+ ".docx";
		return new Blob(outputFileName, MIME_TYPE_DOCX, byteArray);
	}

	// @NotContributed(As.ASSOCIATION)
	// @NotInServiceMenu
	// @Named("Documento con Planilla")
	// public Blob downloadAsDocPlanilla(Nota unaNota) throws
	// LoadInputException,
	// LoadTemplateException, MergeException, IOException {
	// final URL templateUrl = Resources.getResource(
	// NotaReportingService.class, "NotaTabla.docx");
	// templates = Resources.toByteArray(templateUrl);
	// final String html = asInputHtml(unaNota);
	// final byte[] byteArray = mergeToDocx(html);
	//
	// final String outputFileName = "IMPS_Nota_" + unaNota.getNro_nota()
	// + ".docx";
	// return new Blob(outputFileName, MIME_TYPE_DOCX, byteArray);
	// }

	private static String asInputHtml(Nota unaNota) {
		final Element htmlEl = new Element("html");
		Document doc = new Document();
		doc.setRootElement(htmlEl);

		final Element bodyEl = new Element("body");
		htmlEl.addContent(bodyEl);

		bodyEl.addContent(newP("nro_nota", "plain", unaNota.getNro_nota() + ""));
		bodyEl.addContent(newP("fecha", "date", fechaACadena(unaNota)));
		bodyEl.addContent(newP("nombre_sector", "plain", unaNota.getSector()
				.getNombre_sector()));
		bodyEl.addContent(newP("responsable", "plain", unaNota.getSector()
				.getResponsable()));
		bodyEl.addContent(newP("destino", "plain", unaNota.getDestino()));
		bodyEl.addContent(newP("descripcion", "plain", unaNota.getDescripcion()));
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

	private static String fechaACadena(Nota unaNota) {
		LocalDate dueBy = unaNota.getFecha();
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

	@SuppressWarnings("unused")
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
