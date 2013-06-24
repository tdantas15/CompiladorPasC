package com.compiladores.util;

public class Numero extends Token {

	private final int numero;
	
	public Numero (int tag,int numero){
		super(tag);
		this.numero = numero;
	}
	
	public int getNumero(){
		return numero;
	}
	
}
