package com.compiladores.util;

public class No {

	public static final String INTEIRO = "INTEIRO";
	public static final String REAL = "REAL";
	public static final String TEXTO = "TEXTO";
	
	private String tipo;
	private boolean resultadoSemantico=false;
	
	public No(String tipo){
		this.tipo = tipo;
	}
	
	public No(){
		
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public boolean isResultadoSemantico() {
		return resultadoSemantico;
	}

	public void setResultadoSemantico(boolean resultadoSemantico) {
		this.resultadoSemantico = resultadoSemantico;
	}
	
	
}
