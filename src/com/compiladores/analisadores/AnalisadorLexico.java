package com.compiladores.analisadores;

import java.io.BufferedReader;
import java.io.IOException;

//Classe responsável por realizar a análise léxica
public class AnalisadorLexico {

	BufferedReader reader;
	
	public AnalisadorLexico(BufferedReader reader){
		this.reader = reader;
	}
	
	public void mostraArquivo() throws IOException{
		
		reader.read();
		
	}
	
}
