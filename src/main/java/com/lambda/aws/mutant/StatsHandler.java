package com.lambda.aws.mutant;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class StatsHandler implements RequestHandler<Object, String> {
	
	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private String DYNAMODB_TABLE_NAME = "Mutant";
	
	private int fingMutant(String info) throws ConditionalCheckFailedException {
		int intr = 0;
		AmazonDynamoDBClient client = new AmazonDynamoDBClient();
		client.setRegion(Region.getRegion(Regions.SA_EAST_1));

		DynamoDB dynamoDb = new DynamoDB(client);
		Table mutant = dynamoDb.getTable(DYNAMODB_TABLE_NAME);

		Map<String, Object> expressionAttributeValues = new HashMap<String, Object>();
		expressionAttributeValues.put(":inf", info);

		ItemCollection<ScanOutcome> items = mutant.scan("info = :inf", "dna, info", null, expressionAttributeValues);
		Iterator<Item> iterator = items.iterator();
		while (iterator.hasNext()) {
			intr++;
		}

		return intr;
	}

    @Override
    public String handleRequest(Object input, Context context) {
    	
        int mutant=fingMutant("S");
        int human =fingMutant("N");
        
        return "{\"count_mutant_dna\":" + mutant
        	  +",\"count_human_dna\":"+ human
        	  +",\"ratio\":"+ String.valueOf((double) mutant/human)
        	  +"}";
    }

}
