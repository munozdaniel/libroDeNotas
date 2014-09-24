package services.docx;

/*
 *  Copyright 2013~2014 Dan Haywood
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.CssClass;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.NotInServiceMenu;
import org.apache.isis.applib.value.Blob;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.isisaddons.module.docx.dom.DocxService;
import org.isisaddons.module.docx.dom.LoadTemplateException;
import org.isisaddons.module.docx.dom.MergeException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.output.DOMOutputter;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.common.io.Resources;

import dom.nota.Nota;

@DomainService(repositoryFor = Nota.class)
public class NotaServiceDocx {

	// region > init

	private WordprocessingMLPackage wordprocessingMLPackage;

	@PostConstruct
	public void init() throws IOException, LoadTemplateException {
		final byte[] bytes = Resources.toByteArray(Resources.getResource(
				this.getClass(), "nota.docx"));
		wordprocessingMLPackage = docxService
				.loadPackage(new ByteArrayInputStream(bytes));
	}

	// endregion

	// region > downloadCustomerConfirmation (action)

	@NotContributed(NotContributed.As.ASSOCIATION)
	// ie contributed as action
	@NotInServiceMenu
	@ActionSemantics(Of.SAFE)
	@MemberOrder(sequence = "10")
	@Named("Descargar")
	@CssClass("x-highlight")
	public Blob downloadDocumento(final Nota nota) throws IOException,
			JDOMException, MergeException {

		final org.w3c.dom.Document w3cDocument = asInputW3cDocument(nota);

		final ByteArrayOutputStream docxTarget = new ByteArrayOutputStream();
		docxService.merge(w3cDocument, wordprocessingMLPackage, docxTarget,
				DocxService.MatchingPolicy.LAX);

		final String blobName = "Nota-" + nota.getNro_nota() + ".docx";
		final String blobMimeType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
		final byte[] blobBytes = docxTarget.toByteArray();

		return new Blob(blobName, blobMimeType, blobBytes);
	}

	// @NotContributed(NotContributed.As.ASSOCIATION)
	// // ie contributed as action
	// @Prototype
	// @NotInServiceMenu
	// @ActionSemantics(Of.SAFE)
	// @MemberOrder(sequence = "11")
	// public Clob downloadCustomerConfirmationInputHtml(final Nota order)
	// throws IOException, JDOMException, MergeException {
	//
	// Document orderAsHtmlJdomDoc = asInputDocument(order);
	//
	// XMLOutputter xmlOutput = new XMLOutputter();
	// xmlOutput.setFormat(Format.getPrettyFormat());
	//
	// final String html = xmlOutput.outputString(orderAsHtmlJdomDoc);
	//
	// final String clobName = "customerConfirmation-" + order.getNro_nota()
	// + ".html";
	// final String clobMimeType = "text/html";
	// final String clobBytes = html;
	//
	// return new Clob(clobName, clobMimeType, clobBytes);
	// }

	private static org.w3c.dom.Document asInputW3cDocument(Nota nota)
			throws JDOMException {
		Document orderAsHtmlJdomDoc = asInputDocument(nota);

		DOMOutputter domOutputter = new DOMOutputter();
		return domOutputter.output(orderAsHtmlJdomDoc);
	}

	private static Document asInputDocument(Nota nota) {
		Element html = new Element("html");
		Document document = new Document(html);

		Element body = new Element("body");
		html.addContent(body);
		
		addPara(body, "titulo", "plain", " NOTA ");

		addPara(body, "nro", "plain", nota.getNro_nota() + "");
		DateTimeFormatter fmt = DateTimeFormat.forPattern("d MMMM, yyyy");
		addPara(body, "fecha", "plain", nota.getFecha().toString(fmt));
		addPara(body, "origen", "plain", nota.getSector().getNombre_sector());
		addPara(body, "destino", "plain", nota.getDestino());
		addPara(body, "descripcion", "plain", nota.getDescripcion());

		// Element table = addTable(body, "Products");
		// for (OrderLine orderLine : order.getOrderLines()) {
		// addTableRow(
		// table,
		// new String[] { orderLine.getDescription(),
		// orderLine.getCost().toString(),
		// "" + orderLine.getQuantity() });
		// }

		// Element ul = addList(body, "OrderPreferences");
		// for (String preference : preferencesFor(order)) {
		// addListItem(ul, preference);
		// }
		return document;
	}

	// endregion (

	// region > helpers

	private static void addPara(Element body, String id, String clazz,
			String text) {
		Element p = new Element("p");
		body.addContent(p);
		p.setAttribute("id", id);
		p.setAttribute("class", clazz);
		p.setText(text);
	}

	// private static final Function<String, String> TRIM = new Function<String,
	// String>() {
	// @Override
	// public String apply(String input) {
	// return input.trim();
	// }
	// };

	// private static Iterable<String> preferencesFor(Nota order) {
	// final String preferences = order.getPreferences();
	// if (preferences == null) {
	// return Collections.emptyList();
	// }
	// return Iterables.transform(Splitter.on(",").split(preferences), TRIM);
	// }

	// private static Element addList(Element body, String id) {
	// Element ul = new Element("ul");
	// body.addContent(ul);
	// ul.setAttribute("id", id);
	// return ul;
	// }
	//
	// private static Element addListItem(Element ul, String... paras) {
	// Element li = new Element("li");
	// ul.addContent(li);
	// for (String para : paras) {
	// addPara(li, para);
	// }
	// return ul;
	// }

	// private static void addPara(Element li, String text) {
	// if (text == null) {
	// return;
	// }
	// Element p = new Element("p");
	// li.addContent(p);
	// p.setText(text);
	// }

	// private static Element addTable(Element body, String id) {
	// Element table = new Element("table");
	// body.addContent(table);
	// table.setAttribute("id", id);
	// return table;
	// }
	//
	// private static void addTableRow(Element table, String[] cells) {
	// Element tr = new Element("tr");
	// table.addContent(tr);
	// for (String columnName : cells) {
	// Element td = new Element("td");
	// tr.addContent(td);
	// td.setText(columnName);
	// }
	// }

	// endregion

	// region > injected services

	@javax.inject.Inject
	DomainObjectContainer container;

	@javax.inject.Inject
	private DocxService docxService;

	// endregion

}
