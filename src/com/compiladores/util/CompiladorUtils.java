package com.compiladores.util;

import java.util.Hashtable;
import java.util.logging.Logger;

//Objetos compartilhados no compilador
public class CompiladorUtils {
	
	//Responsável por fornecer o objeto LOGGER que gerenciará os avisos na tela	
	public final static Logger LOGGER = Logger.getLogger(CompiladorUtils.class .getName());
	
	//Tabela de símbolos
	public static Hashtable<String, String>tabelaDeSimbolos = new Hashtable<String,String>();
	
}
