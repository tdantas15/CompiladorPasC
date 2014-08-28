package com.compiladores.util;

public class No {

	public static final String INTEIRO = "INTEIRO";
	public static final String REAL = "REAL";
	public static final String TEXTO = "TEXTO";
	public static final String ERRO = "ERRO";
	public static final String BOOLEANO = "BOOLEANO";
	
	private String tipo;
	private boolean resultadoSemantico=false;
	private int posicaoMemoria = -1;
	private int tag =0;
	
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

	public int getPosicaoMemoria() {
		return posicaoMemoria;
	}

	public void setPosicaoMemoria(int posicaoMemoria) {
		this.posicaoMemoria = posicaoMemoria;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}
	
	
}
