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

import com.google.common.io.Resources;

import dom.expediente.Expediente;

@DomainService(repositoryFor = Expediente.class)
public class ExpedienteServiceDocx {

	// region > init

	private WordprocessingMLPackage wordprocessingMLPackage;

	@PostConstruct
	public void init() throws IOException, LoadTemplateException {
		final byte[] bytes = Resources.toByteArray(Resources.getResource(
				this.getClass(), "expediente.docx"));
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
	public Blob downloadDocumento(final Expediente expediente) throws IOException,
			JDOMException, MergeException {

		final org.w3c.dom.Document w3cDocument = asInputW3cDocument(expediente);

		final ByteArrayOutputStream docxTarget = new ByteArrayOutputStream();
		docxService.merge(w3cDocument, wordprocessingMLPackage, docxTarget,
				DocxService.MatchingPolicy.LAX);

		final String blobName = "Expediente-" + expediente.getNro_expediente() + ".docx";
		final String blobMimeType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
		final byte[] blobBytes = docxTarget.toByteArray();

		return new Blob(blobName, blobMimeType, blobBytes);
	}


	private static org.w3c.dom.Document asInputW3cDocument(Expediente expediente)
			throws JDOMException {
		Document orderAsHtmlJdomDoc = asInputDocument(expediente);

		DOMOutputter domOutputter = new DOMOutputter();
		return domOutputter.output(orderAsHtmlJdomDoc);
	}

	private static Document asInputDocument(Expediente expediente) {
		Element html = new Element("html");
		Document document = new Document(html);

		Element body = new Element("body");
		html.addContent(body);

		addPara(body, "titulo", "plain", " EXPEDIENTE ");
		
		addPara(body, "nro", "plain", expediente.getNro_expediente() + "");
		addPara(body, "fecha", "plain", expediente.getFecha());
		addPara(body, "origen", "plain", expediente.getSector().getNombre_sector());
		addPara(body, "descripcion", "plain", expediente.getDescripcion());

		addPara(body, "empresa", "plain", expediente.getExpte_cod_empresa());
		addPara(body, "codigo", "plain", expediente.getExpte_cod_letra());
		addPara(body, "anio", "plain", expediente.getExpte_cod_anio()+"");
		addPara(body, "numero", "plain", expediente.getExpte_cod_numero()+"");

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

	// endregion

	// region > injected services

	@javax.inject.Inject
	DomainObjectContainer container;

	@javax.inject.Inject
	private DocxService docxService;

	// endregion

}
