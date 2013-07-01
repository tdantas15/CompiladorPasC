package com.compiladores.util;

public class Palavra extends Token {
	
	private final String lexema;
	
	public Palavra (int tag,String lexema){
		super(tag);
		this.lexema = lexema;
	}
	
	public String getLexema(){
		return lexema;
	}

}
