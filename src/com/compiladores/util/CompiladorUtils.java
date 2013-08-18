package com.compiladores.util;

import java.util.HashMap;
import java.util.logging.Logger;

//Objetos compartilhados no compilador
public class CompiladorUtils {
	
	
	//Responsável por fornecer o objeto LOGGER que gerenciará os avisos na tela	
	public final static Logger LOGGER = Logger.getLogger(CompiladorUtils.class .getName());
	
	//Tabela de símbolos
	public static HashMap<String,EntradaTabelaSimbolos> tabelaDeSimbolos = new HashMap<String,EntradaTabelaSimbolos>();
	
}
