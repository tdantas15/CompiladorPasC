package com.compiladores.analisadores;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;

import com.compiladores.util.CompiladorUtils;
import com.compiladores.util.Numero;
import com.compiladores.util.Palavra;
import com.compiladores.util.Tag;
import com.compiladores.util.Token;

//Classe responsável por realizar a análise léxica
public class AnalisadorLexico {

	public static int linha = 1;
	private char ch = ' ';
	private BufferedReader reader;

	// Construtor
	public AnalisadorLexico(BufferedReader reader) throws IOException {
		this.reader = reader;
	}

	// Métodos públicos

	// Método responsável por retornar o próximo Token
	public Token scan() throws IOException {
		
		Token token;
		
		// Desconsidera delimitadores
		desconsideraDelimitadores();

		//Verifica se o arquivo terminou
		if (((int)ch)==65535){
			return null;
		}
		
		//Verifica se linguagem contém o símbolo
		if (((int) ch) >= 255 && ch!='“'){
			CompiladorUtils.LOGGER.log(Level.SEVERE,"Erro na linha "+linha+": Caractere invalido!");
			readch();
		}	
		
		token = verificaLimitadores();
		if (token != null)
			return token;

		if (ch=='“'){
			return verificaLiteral();
		}
		
		if (ch=='{'){
			return verificaComentário();
		}

		token = verificaLimitadores();
		if (token != null)
			return token;

		if (ch == 'i')
			return verificaIniciandoComI();

		else if (ch == 'b')
			return verificaIniciandoComB();

		else if (ch == 'r')
			return verificaIniciandoComR();
		
		else if (ch == 'w')
			return verificaIniciandoComW();
		else if (ch == 'e')
			return verificaIniciandoComE();
		
		else if (ch=='“')
			return verificaLiteral();
		
	
		if (Character.isDigit(ch)) {
			return verificaNumero();
		}

		
		token = verificaIdentificador();
		if (token != null)
			return token;

		return null;

	}

	// Métodos private

	// Desconsidera delimitadores
	private void desconsideraDelimitadores() throws IOException {

		for (;; readch()) {
			if (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\b')
				continue;
			else if (ch == '\n')
				linha++; // conta linhas
			else
				break;
		}
	}

	// Verifica se é número
	private Token verificaNumero() throws IOException {
		
		int num = Integer.parseInt(ch+"");
		float decimais=0;
		readch();
		while (Character.isDigit(ch)) {
			num *= 10;
			num += Integer.parseInt(ch+"");
			readch();
		}
		if (ch=='.'){
			int i=1;
			readch();
			while (Character.isDigit(ch)) {
				int atual = Integer.parseInt(ch+"");
				decimais+=atual*Math.pow(0.1,i);
				readch();
				i++;
			}
			
		}
		return new Numero(Tag.NUM, num+decimais);

	}

	private Token verificaLimitadores() throws IOException {

		if (ch == ',') {
			readch();
			return new Palavra(Tag.VIRGULA, ",");
		}
		if (ch == ';') {
			readch();
			return new Palavra(Tag.PONTO_E_VIRGULA, ";");
		}
		if (ch == '=') {
			readch();
			return new Palavra(Tag.EQ, "=");
		}
		if (ch == '>') {
			if (verificaProximo('=')){
				readch();
				return new Palavra(Tag.GE,">=");
			}	
			return new Palavra(Tag.GT, ">");
		}
		if (ch == '<') {
			if (verificaProximo('=')){
				readch();
				return new Palavra(Tag.LE,"<=");
			}	
			if (ch=='>'){
				readch();
				return new Palavra(Tag.DIFF, "<>");
			}
			return new Palavra(Tag.LT, "<");
		}
		
		if (ch == '&'){
			if (verificaProximo('&')){
				readch();
				return new Palavra(Tag.AND,"&&");
			}
		}

		if (ch == '|'){
			if (verificaProximo('|')){
				readch();
				return new Palavra(Tag.OR,"||");
			}
		}
		
		if (ch == '.') {
			readch();
			return new Palavra(Tag.PONTO, ".");
		}
		if (ch == '(') {
			readch();
			return new Palavra(Tag.ABRE_PARENTESES, "(");
		}
		if (ch == ')') {
			readch();
			return new Palavra(Tag.FECHA_PARENTESES, ")");
		}
		if (ch == '*') {
			readch();
			return new Palavra(Tag.VEZES, "*");
		}
		if (ch == '/') {
			readch();
			return new Palavra(Tag.DIVISAO, "/");
		}
		if (ch == '+') {
			readch();
			return new Palavra(Tag.MAIS, "+");
		}
		if (ch == '-') {
			readch();
			return new Palavra(Tag.MENOS, "-");
		}
		if (ch == ':') {
			if (verificaProximo('=')) {
				readch();
				return new Palavra(Tag.ATRIBUICAO, ":=");
			}
			return new Palavra(Tag.EQ, "=");
		}

		return null;
	}

	// Verifica token iniciando com a letra b (begin)
	private Token verificaIniciandoComB() throws IOException {

		if (verificaProximo('e')) {
			if (verificaProximo('g')) {
				if (verificaProximo('i')) {
					if (verificaProximo('n')) {
						if (!verificaNumLetra(ch))
							return new Palavra(Tag.BEGIN, "begin");
						else
							return verificaIdentificador("begin");
					} else
						return verificaIdentificador("begi");
				} else
					return verificaIdentificador("beg");
			} else
				return verificaIdentificador("be");

		} else
			return verificaIdentificador("b");
	}

	// Verifica token iniciando com a letra r (read)
	private Token verificaIniciandoComR() throws IOException {

		if (verificaProximo('e')) {
			if (verificaProximo('a')) {
				if (verificaProximo('l')) {
					
					if (!verificaNumLetra(ch))
						return new Palavra(Tag.READ, "real");
					else
						return verificaIdentificador("real");				
				}
				else if (ch=='d') {
					readch();
					if (!verificaNumLetra(ch))
						return new Palavra(Tag.READ, "read");
					else
						return verificaIdentificador("read");
				} else
					return verificaIdentificador("rea");
			} else
				return verificaIdentificador("re");
		} else
			return verificaIdentificador("r");

	}
	
	// Verifica token iniciando com a letra e (read)
	private Token verificaIniciandoComE() throws IOException {

		
			if (verificaProximo('n')) {
				if (verificaProximo('d')) {
					if (!verificaNumLetra(ch))
						return new Palavra(Tag.END, "end");
					else
						return verificaIdentificador("end");
				} else
					return verificaIdentificador("en");
			} else{
				if (ch=='l'){
					if (verificaProximo('s')){
						if (verificaProximo('e')){
							if (!verificaNumLetra(ch))
								return new Palavra(Tag.ELSE, "else");
							else
								return verificaIdentificador("else");
							
						}return verificaIdentificador("els");
						
					}return verificaIdentificador("el");
				}
				return verificaIdentificador("e");
			}

	}
	
	// Verifica token iniciando com a letra w (write)
	private Token verificaIniciandoComW() throws IOException {
		
		if (verificaProximo('r')) {
			if (verificaProximo('i')) {
				if (verificaProximo('t')) {
					if (verificaProximo('e')) {
						if (!verificaNumLetra(ch))
							return new Palavra(Tag.WRITE, "write");
						else
							return verificaIdentificador("write");
					} else
						return verificaIdentificador("writ");
				} else
					return verificaIdentificador("wri");
			} else
				return verificaIdentificador("wr");

		} else
			return verificaIdentificador("w");
		

	}

	// Verifica token iniciado com a letra i (if,int,init)
	private Token verificaIniciandoComI() throws IOException {

		// Reconhece if
		if (verificaProximo('f')) {
			readch();
			if (!verificaNumLetra(ch))
				return new Palavra(Tag.IF, "if");
			else
				return verificaIdentificador("if");
		}

		if (ch == 'n') {

			// Reconhece int
			if (verificaProximo('t')) {
				readch();
				if (!verificaNumLetra(ch))
					return new Palavra(Tag.INT, "int");
				else
					return verificaIdentificador("int");
			}
			// Reconhece init
			if (ch == 'i' && verificaProximo('t')) {
				readch();
				if (!verificaNumLetra(ch))
					return new Palavra(Tag.INIT, "init");
				else
					return verificaIdentificador("init");
			}
		}

		return verificaIdentificador("i");
	}

	// Verifica se é Identificador
	private Token verificaIdentificador() throws IOException {

		String identificador = "";
		if (Character.isLetter(ch))
			identificador += ch;
		else{
			CompiladorUtils.LOGGER.log(Level.SEVERE,"Erro na linha "+linha+": Caractere invalido!");
			readch();
			return this.scan();
		}
			
			
		readch();
		while ((verificaNumLetra(ch))) {
			identificador += ch;
			readch();

		}
		Palavra t =new Palavra(Tag.ID, identificador);
		if (!CompiladorUtils.tabelaDeSimbolos.containsKey(t.getLexema())){ 
			CompiladorUtils.tabelaDeSimbolos.put(t.getLexema(), t);
		}
		return new Palavra(Tag.REGISTRO_SIMBOLOS, t.getLexema());

	}
	
	//Reconhece Literal
	private Token verificaLiteral() throws IOException {
		readch();
		String literal="";
		while (ch!='”') {
			if (((int) ch) <= 255 && ch!='\n'){
				literal += ch;
				readch();
			}else break;
		}
		readch();
		return new Palavra(Tag.LITERAL, literal);

	}
	
	//Verifica comentário
	private Token verificaComentário() throws IOException {
		readch();
		
		while (ch!='}') {
			if (((int) ch) != 65535){
				readch();
			}else{
				CompiladorUtils.LOGGER.log(Level.SEVERE,"Erro na linha "+linha+": Comentário não foi finalizado");
				System.exit(0);
			}
		}
		readch();
		return new Palavra(Tag.COMMENT, "");

	}

	// Verifica se é Identificador
	private Token verificaIdentificador(String identificador)
			throws IOException {

		while ((verificaNumLetra(ch))) {
			identificador += ch;
			readch();
		}

		Palavra t =new Palavra(Tag.ID, identificador);
		if (!CompiladorUtils.tabelaDeSimbolos.containsKey(t.getLexema())){ 
			CompiladorUtils.tabelaDeSimbolos.put(t.getLexema(), t);
		}
		return new Palavra(Tag.REGISTRO_SIMBOLOS, t.getLexema());

	}

	private boolean verificaNumLetra(char caractere) {
		return Character.isLetter(caractere) || Character.isDigit(caractere);

	}

	// Lê o próximo caractere
	private void readch() throws IOException {
		ch = (char) reader.read();
		
		
	}

	// Verifica o próximo caractere
	private boolean verificaProximo(char proximo) throws IOException {
		readch();
		if (ch != proximo)
			return false;
		ch = ' ';
		return true;
	}

}
