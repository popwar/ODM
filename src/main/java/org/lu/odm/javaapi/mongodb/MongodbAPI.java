package org.lu.odm.javaapi.mongodb;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.types.ObjectId;
import org.hamcrest.core.IsInstanceOf;
import org.lu.odm.MongodbConnection;
import org.lu.odm.annotation.Document;
import org.lu.odm.annotation.Id;
import org.lu.odm.parser.AnnotationParser;
import org.lu.odm.parser.DocumentParser;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;

public class MongodbAPI<T> {

	private MongodbConnection connection;

	public MongodbAPI(String dbName) {
		connection = MongodbConnection.getInstance(dbName);
	}

	@SuppressWarnings("unchecked")
	public List<T> query(T t) {
		Class<?> clazz = t.getClass();
		AnnotationParser<String> parser = new DocumentParser();
		String collectionName = parser.parse(clazz);

		FindIterable<org.bson.Document> iterable = connection
				.getMongoDatabase().getCollection(collectionName).find();

		Field[] fields = clazz.getDeclaredFields();
		List<T> list = new ArrayList<>();
		Object ob = null;
		MongoCursor<org.bson.Document> cursor = iterable.iterator();
		while (cursor.hasNext()) {
			org.bson.Document doc = cursor.next();
			try {
				ob = clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				throw new RuntimeException("bad");
			}
			for (Field field : fields) {
				if (field.getAnnotations() == null
						|| field.getAnnotations().length == 0) {
					set(ob, field.getName(), doc.get(field.getName()));
					continue;
				}
				if (field.isAnnotationPresent(Id.class)) {
					Id id = field.getAnnotation(Id.class);
					set(ob, field.getName(), doc.get(id.idName()));
				}
				if (field
						.isAnnotationPresent(org.lu.odm.annotation.Field.class)) {
					org.lu.odm.annotation.Field attribute = field
							.getAnnotation(org.lu.odm.annotation.Field.class);
					set(ob, field.getName(), doc.get(attribute.fieldName()));
				}
			}
			list.add((T) ob);
		}
		cursor.close();
		return list;
	}

	private boolean set(Object object, String fieldName, Object fieldValue) {
		Class<?> clazz = object.getClass();
		while (clazz != null) {
			try {
				Field field = clazz.getDeclaredField(fieldName);
				field.setAccessible(true);
				field.set(object, fieldValue);
				return true;
			} catch (NoSuchFieldException e) {
				clazz = clazz.getSuperclass();
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
		return false;
	}

	@Document(collectionName = "things")
	public static class Things {
		@Id
		private ObjectId id;
		private double a;
		private double b;

		@org.lu.odm.annotation.Field(fieldName = "c")
		private double cc;

		public ObjectId getId() {
			return id;
		}

		public void setId(ObjectId id) {
			this.id = id;
		}

		public double getA() {
			return a;
		}

		public void setA(double a) {
			this.a = a;
		}

		public double getB() {
			return b;
		}

		public void setB(double b) {
			this.b = b;
		}

		public double getCc() {
			return cc;
		}

		public void setCc(double cc) {
			this.cc = cc;
		}
	}

}
