package com.compiladores.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;

public class GerenciadorArquivoFonte {
	
	private static ArrayList<String> codigoFonte = new ArrayList<String>();
	private static String nomeArquivo= "vm/codigo.vm";
	
	public static int adicionarLinha(String linha){
		codigoFonte.add(linha);
		return codigoFonte.size()-1;
	}
	
	public static void setLinha(int numero,String linha){
		codigoFonte.set(numero, linha);
	}
	
	public static String getLinha(int numero){
		return codigoFonte.get(numero);
	}
	
	public static void escreveArquivo(){
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(nomeArquivo));
			Iterator<String> iterator = codigoFonte.iterator();
			
			String conteudoArquivo ="";
			while(iterator.hasNext()){
				conteudoArquivo+=iterator.next()+"\n";
			}
			
			out.write(conteudoArquivo);
			out.close();
			
		} catch (IOException e) {
			CompiladorUtils.LOGGER.log(Level.SEVERE, "Erro de I/O");
			System.exit(0);
		}
		
	}
	
}
