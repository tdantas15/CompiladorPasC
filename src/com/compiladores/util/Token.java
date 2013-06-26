package com.compiladores.util;

public class Token {

	//Tag Correspondente ao Token
	private final int tag;
	protected final int numero;
	
	
	public Token (int tag){
		this.tag = tag;
		numero=0;

	}
	
	public Token (int tag,int numero){
		this.tag = tag;
		this.numero = numero;

	}
	
	public int getTag(){
		return tag;
	}
	

	public int getNumero(){
		return numero;
	}
	
}
