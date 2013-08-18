package com.compiladores.util;

public class Numero extends Token {

	private final float numero;
	
	public Numero (int tag,float numero){
		super(tag);
		this.numero = numero;
	}
	
	public float getNumero(){
		return numero;
	}
	
}
