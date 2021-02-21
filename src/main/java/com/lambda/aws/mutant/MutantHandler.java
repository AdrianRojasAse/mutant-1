package com.lambda.aws.mutant;




import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lambda.aws.awspojo.DnaPOJO;


public class MutantHandler implements RequestHandler<DnaPOJO , String> {

	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private static int size, length;
	private static int[] fila = { 0, 1, 1, 1 };
	private static int[] col = { 1, -1, 0, 1 };
	private String DYNAMODB_TABLE_NAME = "Mutant";

	private static boolean posValid(int x, int y) {
		return (x >= 0 && x < size && y >= 0 && y < size);
	}
	

	private void persistData(DnaPOJO dnaPOJO) throws ConditionalCheckFailedException {
		 
		AmazonDynamoDBClient client = new AmazonDynamoDBClient();
	        client.setRegion(Region.getRegion(Regions.SA_EAST_1));
	      
	     DynamoDB dynamoDb = new DynamoDB(client);
		 Item item = Item.fromJSON(new Gson().toJson(dnaPOJO));
		 dynamoDb.getTable(DYNAMODB_TABLE_NAME).putItem(
				 new Item().withPrimaryKey("dna",new Gson().toJson(dnaPOJO)).withString("info", dnaPOJO.getIsMutant()));
    	    }

	public static boolean findSecuence(char[][] mat, int x, int y, char previous, int dir) {
		
		if (!posValid(x, y) || previous != mat[x][y]) {
			return false;
		}
		
		length = length + 1;
		boolean next = findSecuence(mat, x + fila[dir], y + col[dir], mat[x][y], dir);
		
		if (length >= 3) {
			length = 0;
			return true;
				}

		return next;
	}

	public static boolean isMutant(String[] dna) {
		
		size = dna.length;
		int secuencias = 0;
		char[][] mat = new char[size][size];
		
		for (int i = 0; i < size ; i++) {
			mat[i] = dna[i].toCharArray();
		}

		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				for (int k = 0; k < 4; k++) {
					if(findSecuence(mat, x + fila[k], y + col[k], mat[x][y], k)) 
						secuencias=secuencias+1;
						length = 0;
					
				}
			}
		}
		
		return secuencias>=2? true:false;
	}

    @Override
    public String handleRequest(DnaPOJO dnaPojo, Context context) {

    	String response= "No process";
    	
    	dnaPojo.setId((int) Math.floor(Math.random()*(1000000-100+1)+100));
       			
			if(isMutant(dnaPojo.getDna())) {
				response = "OK";	
				dnaPojo.setIsMutant("S");
				persistData(dnaPojo);
			}
			else {
				dnaPojo.setIsMutant("N");
				persistData(dnaPojo);
				throw new RuntimeException("403");
			}
			
		return response;
    }
}
