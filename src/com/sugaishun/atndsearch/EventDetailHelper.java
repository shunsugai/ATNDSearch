package com.sugaishun.atndsearch;

import java.io.StringWriter;

import net.java.textilej.parser.MarkupParser;
import net.java.textilej.parser.builder.HtmlDocumentBuilder;
import net.java.textilej.parser.markup.Dialect;

public class EventDetailHelper {
	public static String textToHtml(String textile, Class<? extends Dialect> clazz, boolean isDocument) 
			throws Exception {
		StringWriter sw = new StringWriter();

		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(sw);
		builder.setEmitAsDocument(isDocument);

		MarkupParser parser = new MarkupParser(clazz.newInstance());
		parser.setBuilder(builder);
		parser.parse(textile);

		return sw.toString();
	}
}
