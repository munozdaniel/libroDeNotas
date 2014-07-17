package reporting;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import org.apache.isis.applib.DomainObjectContainer;
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

	private final byte[] notaTemplates;
	private String path;

	public NotaReportingService() throws IOException {
		final URL templateUrl = Resources.getResource(
				NotaReportingService.class, "Nota.docx");
		if (templateUrl != null)
			path = templateUrl.getPath();
		notaTemplates = Resources.toByteArray(templateUrl);
	}
	@NotContributed(As.ASSOCIATION)
	@NotInServiceMenu
	public String htmlInput(Nota unaNota)
	{
		return "HTML : "+ asInputHtml(unaNota);
	}
	@NotContributed(As.ASSOCIATION)
	// ie contributed as action
	@NotInServiceMenu
	public Blob downloadAsDoc(Nota unaNota) throws LoadInputException,
			LoadTemplateException, MergeException {

		final String html = asInputHtml(unaNota);
		final byte[] byteArray = mergeToDocx(html);

		final String outputFileName = "Nota-"
				+ bookmarkService.bookmarkFor(unaNota).getIdentifier()
				+ ".docx";
		return new Blob(outputFileName, MIME_TYPE_DOCX, byteArray);
	}

	private static String asInputHtml(Nota unaNota) {
		final Element htmlEl = new Element("html");
		Document doc = new Document();
		doc.setRootElement(htmlEl);

		final Element bodyEl = new Element("body");
		htmlEl.addContent(bodyEl);

		bodyEl.addContent(newP("nro_nota", "plain", unaNota.getNro_nota() + ""));
		// bodyEl.addContent(newP("fecha", "date", dueByOf(unaNota)));
		// bodyEl.addContent(newP("destino", "plain", unaNota.getDestino()));
		// bodyEl.addContent(newP("DueBy", "date", dueByOf(unaNota)));

		// final Element ulDependencies = new Element("ul");
		// ulDependencies.setAttribute("id", "Dependencies");
		//
		// final SortedSet<Nota> dependencies = unaNota.getDependencies();
		// for (final ToDoItem dependency : dependencies) {
		// final Element liDependency = new Element("li");
		// ulDependencies.addContent(liDependency);
		// final Element pDependency = new Element("p");
		// pDependency.setContent(new Text(dependency.getDescription()));
		// liDependency.addContent(pDependency);
		// }
		// bodyEl.addContent(ulDependencies);
		//
		final String html = new XMLOutputter().outputString(doc);
		return html;
	}

	private static String dueByOf(Nota unaNota) {
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
		// try {
		// if (path != null)
		// { 
		// this.container.warnUser("path : " + path);
		// }
		// else
		// this.container.warnUser("path NULL: ");
		this.container.warnUser("html : " + html);
		final ByteArrayInputStream docxTemplateIs = new ByteArrayInputStream(
				notaTemplates);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		docxService.merge(html, docxTemplateIs, baos, MatchingPolicy.LAX);
		byte[] byteArray = baos.toByteArray();
		return byteArray;
		// } catch (LoadTemplateException e) {
		// this.container.warnUser("e.Mensaje : " + e.getMessage());
		// this.container.warnUser("e.Cause : " + e.getCause());
		//
		// }
		// return null;
	}

	// //////////////////////////////////////

	@javax.inject.Inject
	private DocxService docxService;

	@javax.inject.Inject
	private BookmarkService bookmarkService;

	@javax.inject.Inject
	private DomainObjectContainer container;
}
