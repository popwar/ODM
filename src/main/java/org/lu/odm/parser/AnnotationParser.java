package org.lu.odm.parser;

public interface AnnotationParser<T> {
	T parse(Class<?> clazz);
}
