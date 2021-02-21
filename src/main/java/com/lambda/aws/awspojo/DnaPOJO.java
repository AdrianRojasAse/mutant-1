package com.lambda.aws.awspojo;

public class DnaPOJO {
	
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	private String[] dna;
	
	private String isMutant;

	public String getIsMutant() {
		return isMutant;
	}

	public void setIsMutant(String isMutant) {
		this.isMutant = isMutant;
	}

	public String[] getDna() {
		return dna;
	}

	public void setDna(String[] dna) {
		this.dna = dna;
	}
	
	public String ToString(){
		StringBuilder builder = new StringBuilder();
		for(String s : this.getDna()) {
		    builder.append(s).append(",");
		}
		return builder.toString();
	}

}
