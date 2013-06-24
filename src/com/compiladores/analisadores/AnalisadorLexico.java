package com.compiladores.analisadores;

import java.io.BufferedReader;
import java.io.IOException;

import com.compiladores.util.Palavra;
import com.compiladores.util.Tag;
import com.compiladores.util.Token;

//Classe responsável por realizar a análise léxica
public class AnalisadorLexico {

	
	public static int linha = 1;
	private char ch = ' ';
	private BufferedReader reader;
	
	//Construtor
	public AnalisadorLexico(BufferedReader reader) throws IOException{
		this.reader = reader;
	}
	
	//Métodos públicos
	
	public Token scan() throws IOException{
		
		//Desconsidera delimitadores
		desconsideraDelimitadores();
		
		if (ch=='i') return verificaIniciandoComI();
		
		
		return null;
		
	}
	
	
	//Métodos private
	
	//Desconsidera delimitadores
	private void desconsideraDelimitadores() throws IOException{
		
		for (;; readch()) {
			if (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\b') continue;
			else if (ch == '\n') linha++; //conta linhas
			else break;
			}
	}
	
	private Token verificaIniciandoComI() throws IOException{
		//Reconhece if
		if (verificaProximo('f')) return new Palavra (Tag.IF,"if");
		
		if (ch=='n'){
			//Reconhece int
			if (verificaProximo('t'))return new Palavra (Tag.INT,"int");
			//Reconhece INIT
			if (verificaProximo('i') && verificaProximo('t')) return new Palavra (Tag.INIT, "init");	
		}
		
		return null;
	}
	
	private void readch() throws IOException{
		ch = (char) reader.read();
	}

	private boolean verificaProximo(char proximo) throws IOException{
		readch();
		if (ch != proximo) return false;
		ch = ' ';
		return true;
	}

}
