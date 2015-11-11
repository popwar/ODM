package org.lu.odm;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

public class MongodbConnection {

	private static MongoDatabase mongoDatabase;

	private static MongoClient mongoClient;

	private static final String DEFAULT_URI = "mongodb://localhost";

	private static final int default_port = 27017;

	private MongodbConnection() {
	}

	private static class DbConnectionHolder {
		public static final MongodbConnection connection = new MongodbConnection();
	}

	public static MongodbConnection getInstance(String dbName) {
		MongodbConnection connection = DbConnectionHolder.connection;
		MongodbConnection.mongoDatabase = getDataBaseConnection(DEFAULT_URI,
				default_port, dbName);
		return connection;
	}

	public static synchronized MongoDatabase getDataBaseConnection(String uri, int port,
			String dbName) {
		if (mongoClient == null) {
			System.out.println("first initialize");
			mongoClient = new MongoClient(new MongoClientURI(uri + ":" + port));
		} else {
			System.out.println("use first one");
		}

		return mongoClient.getDatabase(dbName);
	}

	public MongoDatabase getMongoDatabase() {
		return mongoDatabase;
	}

	public static void main(String[] s) {
		Thread t1 = new Thread(new MyThread());

		Thread t2 = new Thread(new MyThread());
		Thread t3 = new Thread(new MyThread());
		t3.start();
		t1.start();
		t2.start();

	}

	static class MyThread implements Runnable {

		@Override
		public void run() {
			MongodbConnection c = MongodbConnection.getInstance("test");
			System.out.println(Thread.currentThread().getName() + ": "
					+ c.hashCode());
		}

	}

}
