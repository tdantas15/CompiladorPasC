package com.compiladores.main;

import java.io.BufferedReader;
import java.util.logging.Level;

import com.compiladores.analisadores.AnalisadorLexico;
import com.compiladores.util.LeitorDeArquivos;
import com.compiladores.util.CompiladorUtils;

public class Compilador {

	//Método main
	public static void main(String args[]){
		
		//Valida os parâmetros
		verificaParametros(args);
		
		//Lê o arquivo indicado nos parâmetros
		LeitorDeArquivos leitorDeArquivos = new LeitorDeArquivos(args[0]);		
		
		//Realiza as análises
		realizaAnalises(leitorDeArquivos.getReader());
		
	}
	
	private static void verificaParametros(String parametros[]){
		//Verifica se o nome do arquivo foi passado como argumento
		if (parametros.length == 0){
			CompiladorUtils.LOGGER.log(Level.SEVERE,"Número insuficiente de parâmetros");
			System.exit(0);
		}	
		
	}
	
	private static void realizaAnalises(BufferedReader reader){
		
		AnalisadorLexico analisadorLexico = new AnalisadorLexico(reader);
		
	}
	
	
}
