package com.compiladores.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;

import com.compiladores.analisadores.AnalisadorLexico;
import com.compiladores.util.LeitorDeArquivos;
import com.compiladores.util.CompiladorUtils;
import com.compiladores.util.Palavra;
import com.compiladores.util.Numero;
import com.compiladores.util.Tag;
import com.compiladores.util.Token;

public class Compilador {

	// Método main
	public static void main(String args[]) {

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
			AnalisadorLexico analisadorLexico = new AnalisadorLexico(reader);
			
			for (int i = 0; i < 42; i++) {
				
				Token t = analisadorLexico.scan();
				
				if (!(t.getTag() == Tag.NUM)){

					CompiladorUtils.LOGGER.log(
							Level.INFO,
							"\nReconhecido! Tag:" +((Palavra) t)
							.getTag()+"- Lexema:"
									+ ((Palavra) t)
											.getLexema());
				}
				else{

					CompiladorUtils.LOGGER.log(
							Level.INFO,
							"\nReconhecido! Número:"
									+ ((Numero)t)
											.getNumero());
				}
			}

		} catch (IOException e) {
			CompiladorUtils.LOGGER.log(Level.SEVERE, "Erro de I/O");
			System.exit(0);
		}

	}

}
