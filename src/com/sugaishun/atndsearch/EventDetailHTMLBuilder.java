package com.sugaishun.atndsearch;

import java.io.StringWriter;

import net.java.textilej.parser.MarkupParser;
import net.java.textilej.parser.builder.HtmlDocumentBuilder;
import net.java.textilej.parser.markup.Dialect;
import net.java.textilej.parser.markup.textile.TextileDialect;

public class EventDetailHTMLBuilder {
	private String title, date, address, description;
	
	public EventDetailHTMLBuilder(String title, String date, String address, String description) {
		this.title = title;
		this.date = date;
		this.address = address;
		this.description = description;
	}
	
	public String getStringHtml() {
		String css = "body,h1,h2,p{padding:0;margin:0}body{background-color:#F5F5F5;font-size:13px;padding:10px}h1{font-size:20px}h2{font-size:16px}";
		String html = "<html><head><meta http-equiv=\"content-type\" content=\"text/html;charset=UTF-8\">"
				+ "<style type=\"text/css\">" + css + "</style></head><body>"
				+ "<h1>" + title + "</h1>"
				+ "<h2>日時:</h2>" + "<p>" + DateHelper.shortDate(date) + " " + DateHelper.time(date) + "</p>"
				+ "<h2>場所:</h2>" + "<p>" + address + "</p>"
				+ "<h2>イベント概要:</h2>" + TextileToHtml(description) 
				+ "</body></html>";
		return html;
	}
	
	private String TextileToHtml(String text) {
		String html = "";
		try {
			html = textToHtml(text, TextileDialect.class, false);
		} catch (Exception e) { e.printStackTrace(); }
		return html;
	}
	
	private String textToHtml(String textile, Class<? extends Dialect> clazz, boolean isDocument) 
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
