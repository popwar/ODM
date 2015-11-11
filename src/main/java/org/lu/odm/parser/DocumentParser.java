package org.lu.odm.parser;

import org.lu.odm.annotation.Document;

public class DocumentParser implements AnnotationParser<String> {

	@Override
	public String parse(Class<?> clazz) {
		if (clazz.isAnnotationPresent(Document.class)) {
			Document document = clazz.getAnnotation(Document.class);
			return document.collectionName();
		}
		return clazz.getName();
	}

}
