package com.compiladores.util;

public class EntradaTabelaSimbolos {

	private Token token;
	private String tipo="";
	private int posicaoMemoria;
	
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
	
	public int getPosicaoMemoria(){
		return posicaoMemoria;
	}
	
	public void setPosicaoMemoria(int posicaoMemoria){
		this.posicaoMemoria = posicaoMemoria;
	}
	
	
	
}
