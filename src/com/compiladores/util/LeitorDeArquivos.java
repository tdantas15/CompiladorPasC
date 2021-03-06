package com.compiladores.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;

//Responsável por ler o arquivo.
public class LeitorDeArquivos {

	private String nomeArquivo;
	private BufferedReader reader;
	
	//Construtor
	public LeitorDeArquivos(String nomeArquivo){
		this.nomeArquivo=nomeArquivo;
		lerArquivo();
	}

	//Método para mostrar o conteúdo do arquivo
	public void mostraArquivo() throws IOException{
		int i=0;
		char c=' ';
		while ((i = reader.read())!=-1 ){
		c = (char) i;
		System.out.print(c);
		}
	}
	
	//Getters e Setters
	
	public String getNomeArquivo() {
		return nomeArquivo;
	}


	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}



	public BufferedReader getReader() {
		return reader;
	}



	public void setReader(BufferedReader reader) {
		this.reader = reader;
	}


	//Método responsável por abrir o arquivo
	private BufferedReader lerArquivo(){
		try {
			reader = new BufferedReader(new FileReader(nomeArquivo));
			return reader;
		} catch (FileNotFoundException e) {
			CompiladorUtils.LOGGER.log(Level.SEVERE,"Não foi possível abrir o arquivo, certifique-se de que o caminho está correto!");
			System.exit(0);
		}
		return null;
	}
	

	
	
	
}
