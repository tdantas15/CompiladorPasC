package com.compiladores.util;

public class EntradaTabelaSimbolos {

	private Token token;
	private String tipo="";
	
	public EntradaTabelaSimbolos(Token token){
		this.token = token;
		
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	
	
	
}
