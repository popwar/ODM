package org.lu.odm.operation;

import java.util.List;

import org.lu.odm.javaapi.mongodb.MongodbAPI;
import org.lu.odm.javaapi.mongodb.MongodbAPI.Things;

public class MongodbOperation<T> {

	@SuppressWarnings("unchecked")
	public List<T> getAll(Class<?> clazz) {
		MongodbAPI<T> api = new MongodbAPI<>("test");
		T t;
		try {
			t = (T) clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException("bad");
		}
		return api.query(t);
	}

	public static void main(String[] args) {
		MongodbOperation<Things> a = new MongodbOperation<>();
		a.getAll(Things.class).forEach(
				element -> System.out.println(element.getId() + ",  "
						+ element.getCc()));
	}
}
