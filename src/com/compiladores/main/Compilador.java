package com.compiladores.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;





import com.compiladores.analisadores.AnalisadorLexico;
import com.compiladores.analisadores.Parser;
import com.compiladores.util.LeitorDeArquivos;
import com.compiladores.util.CompiladorUtils;
import com.compiladores.util.Palavra;
import com.compiladores.util.Numero;
import com.compiladores.util.Tag;
import com.compiladores.util.Token;

public class Compilador {

	// Método main
	public static void main(String args[]) {
		CompiladorUtils.LOGGER.setLevel(Level.SEVERE);
		// Valida os parâmetros
		verificaParametros(args);

		// Lê o arquivo indicado nos parâmetros
		LeitorDeArquivos leitorDeArquivos = new LeitorDeArquivos(args[0]);

		// Realiza as análises
		realizaAnalises(leitorDeArquivos.getReader());

	}

	private static void verificaParametros(String parametros[]) {
		// Verifica se o nome do arquivo foi passado como argumento
		if (parametros.length == 0) {
			CompiladorUtils.LOGGER.log(Level.SEVERE,
					"Número insuficiente de parâmetros");
			System.exit(0);
		}

	}

	private static void realizaAnalises(BufferedReader reader) {

		
		try {
			
			ArrayList<Token> tokens = new ArrayList<Token>();
			
			AnalisadorLexico analisadorLexico = new AnalisadorLexico(reader);
			
			
			Token t = analisadorLexico.scan();

			while (t!=null) {
				tokens.add(t);
				
				// Gera um novo objeto
				t = new Token(0);

				t = analisadorLexico.scan();				
			}

			Parser parser = new Parser (tokens);
			parser.parse();
		
		} catch (IOException e) {
			CompiladorUtils.LOGGER.log(Level.SEVERE, "Erro de I/O");
			System.exit(0);
		}

	}
	
	
	private static void imprimeToken(Token token) {
		if (!(token.getTag() == Tag.NUM)){

			System.out.println(
					((Palavra) token)
					.getTag()+"\t"
							+ ((Palavra) token)
									.getLexema());
		}
		else{

			System.out.println(
					token.getTag()+"\t"
							+ ((Numero)token)
									.getNumero());
		}
		
		
	}
	
	
	private static void imprimeTabelaSimbolos() {
		System.out.println("\nTabela de Símbolos:\n");
		Iterator iterator = CompiladorUtils.tabelaDeSimbolos.keySet().iterator();
		
		for (int i=0;i<CompiladorUtils.tabelaDeSimbolos.keySet().size();i++){
			imprimeToken(CompiladorUtils.tabelaDeSimbolos.get(iterator.next()).getToken());
		}
		
		
	}

}
