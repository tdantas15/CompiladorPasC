package com.compiladores.analisadores;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;

import com.compiladores.util.CompiladorUtils;
import com.compiladores.util.GerenciadorArquivoFonte;
import com.compiladores.util.No;
import com.compiladores.util.Numero;
import com.compiladores.util.Palavra;
import com.compiladores.util.Tag;
import com.compiladores.util.Token;

public class Parser {

	// Lista com os tokens gerados pelo analisador léxico
	private ArrayList<Token> tokens;
	private int indice = -1;
	private Token atual = new Token(0);
	private int posicaoMemoria = 0;
	private String literalAtual="";
	private int inteiro=0;
	private float real =0;
	private int contador_labels=65;
	private boolean diff=false;
	private int label_if=0;

	// Construtor
	public Parser(ArrayList<Token> tokens) {
		this.tokens = tokens;
	}

	// Métodos public

	// Realiza a análise sintática
	public boolean parse() {

		if (program().isResultadoSemantico()) {
			return true;
		} else {
			CompiladorUtils.LOGGER
					.log(Level.SEVERE,
							"Erro sintático próximo a "
									+ ((atual.getClass() == Palavra.class) ? ((Palavra) atual)
											.getLexema() : ((Numero) atual)
											.getNumero()));
			return false;
		}

	}

	// Métodos private

	// Tipo o readch
	private void readAtual() {
		indice++;
		while (tokens.get(indice).getTag() == Tag.COMMENT) {
			indice++;
		}
		if (indice < tokens.size()) {
			Token token = tokens.get(indice);
			if (token.getTag() != Tag.REGISTRO_SIMBOLOS)
				atual = token;
			else
				atual = CompiladorUtils.tabelaDeSimbolos.get(
						((Palavra) token).getLexema()).getToken();
		}
	}

	private boolean eat(int t) {

		int indiceAnterior = indice;
		readAtual();
		if (atual.getClass() == Palavra.class) {
			CompiladorUtils.LOGGER.log(Level.INFO, "eat:" + t + "="
					+ ((Palavra) atual).getLexema());
		} else {
			CompiladorUtils.LOGGER.log(Level.INFO, "eat:" + t + "="
					+ ((Numero) atual).getNumero());
		}
		if (t != atual.getTag()) {
			indice = indiceAnterior;
			CompiladorUtils.LOGGER.log(Level.INFO, "eat falhou");
			return false;
		} else
			CompiladorUtils.LOGGER.log(Level.INFO, "eat funcionou");
		
		if (t== Tag.LITERAL) literalAtual = ((Palavra) atual).getLexema();
		if (t== Tag.NUM_INT) inteiro= (int) ((Numero) atual).getNumero();
		if (t== Tag.NUM_REAL) real = ((Numero) atual).getNumero();
		return true;
	}

	private void setTipoTabelaSimbolos(String tipo) {

		int indiceAnterior = indice;
		readAtual();
		CompiladorUtils.tabelaDeSimbolos.get(((Palavra) atual).getLexema())
				.setTipo(tipo);
		CompiladorUtils.tabelaDeSimbolos.get(((Palavra)atual).getLexema()).setPosicaoMemoria(posicaoMemoria);
		posicaoMemoria++;

		
	}
	

	private String getTipoTabelaSimbolos() {

		int indiceAnterior = indice;
		readAtual();

		CompiladorUtils.LOGGER.log(
				Level.INFO,
				((Palavra) atual).getLexema()
						+ " - Tipo:"
						+ CompiladorUtils.tabelaDeSimbolos.get(
								((Palavra) atual).getLexema()).getTipo());

		return CompiladorUtils.tabelaDeSimbolos.get(
				((Palavra) atual).getLexema()).getTipo();
	}
	
	private int getPosicaoMemoriaTabelaSimbolos() {

		CompiladorUtils.LOGGER.log(
				Level.INFO,
				((Palavra) atual).getLexema()
						+ " - Tipo:"
						+ CompiladorUtils.tabelaDeSimbolos.get(
								((Palavra) atual).getLexema()).getTipo());

		return CompiladorUtils.tabelaDeSimbolos.get(
				((Palavra) atual).getLexema()).getPosicaoMemoria();
	}

	// Program
	private No program() {
		No program = new No();
		CompiladorUtils.LOGGER.log(Level.INFO, "program");
		int indiceAnterior = indice;
		if (eat(Tag.INIT)) {
			GerenciadorArquivoFonte.adicionarLinha("START");
			No body = body(new No());
			if (body.isResultadoSemantico()) {
				program.setTipo(body.getTipo());
				program.setResultadoSemantico(true);
				return program;
			}
		}

		indice = indiceAnterior;
		program.setResultadoSemantico(false);
		return program;

	}

	// Body
	private No body(No no) {
		No body = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "body");
		int indiceAnterior = indice;

		boolean verdadeiro = true;
		while (verdadeiro) {
			No aux = decl_list(new No());
			verdadeiro = aux.isResultadoSemantico();
			if (aux.getTipo() == No.ERRO) {
				body.setTipo(No.ERRO);
				CompiladorUtils.LOGGER.log(Level.SEVERE, "Erro semântico!");
				System.exit(1);
			}
		}

		if (eat(Tag.BEGIN)) {

			No stmt_list = stmt_list(new No());

			if (stmt_list.isResultadoSemantico() && eat(Tag.END)
					&& eat(Tag.PONTO)) {
				GerenciadorArquivoFonte.adicionarLinha("STOP");
				body.setTipo(stmt_list.getTipo());
				body.setResultadoSemantico(true);
				return body;

			}

		}
		indice = indiceAnterior;
		body.setResultadoSemantico(false);
		return body;

	}

	// decl-list
	private No decl_list(No no) {

		No decl_list = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "decl_list");

		int indiceAnterior = indice;
		boolean verdadeiro = true;
		verdadeiro = decl(new No()).isResultadoSemantico()
				&& eat(Tag.PONTO_E_VIRGULA);
		if (!verdadeiro) {
			indice = indiceAnterior;
			decl_list.setResultadoSemantico(false);
			return decl_list;
		}
		while (verdadeiro) {
			verdadeiro = decl(new No()).isResultadoSemantico()
					&& eat(Tag.PONTO_E_VIRGULA);

		}
		decl_list.setResultadoSemantico(true);
		return decl_list;

	}

	// decl
	private No decl(No no) {
		No decl = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "decl");
		int indiceAnterior = indice;
		No type = type(new No());
		if (type.isResultadoSemantico()
				&& ident_list(type).isResultadoSemantico()) {
			decl.setTipo(type.getTipo());
			decl.setResultadoSemantico(true);
			return decl;
		} else {
			indice = indiceAnterior;
			decl.setResultadoSemantico(false);
			return decl;
		}

	}

	// ident-list
	private No ident_list(No no) {
		No ident_list = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "ident_list");

		int numeroIdentificadores = 1;
		int indiceAnterior = indice;
		boolean verdadeiro = true;
		verdadeiro = identifier(ident_list).isResultadoSemantico();
		if (!verdadeiro) {
			indice = indiceAnterior;
			ident_list.setResultadoSemantico(false);
			return ident_list;
		}
		while (verdadeiro) {
			verdadeiro = eat(Tag.VIRGULA);
			if (verdadeiro) {
				verdadeiro = identifier(ident_list).isResultadoSemantico();
				if (!verdadeiro) {
					indice = indiceAnterior;
					ident_list.setResultadoSemantico(false);
					return ident_list;
				}
				numeroIdentificadores++;
			}

		}
		if (no.getTipo() == no.INTEIRO){
			GerenciadorArquivoFonte.adicionarLinha("PUSHN "+numeroIdentificadores);
		}else{
			for (int i=0;i<numeroIdentificadores;i++){
				GerenciadorArquivoFonte.adicionarLinha("PUSHF 0.0");
			}
		}
		ident_list.setResultadoSemantico(true);
		return ident_list;

	}

	// type
	private No type(No no) {
		No type = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "type");
		int indiceAnterior = indice;
		if (eat(Tag.REAL)) {
			type.setTipo(No.REAL);
			type.setResultadoSemantico(true);
			return type;
		} else if (eat(Tag.INT)) {
			type.setTipo(No.INTEIRO);
			type.setResultadoSemantico(true);
			return type;
		} else {
			indice = indiceAnterior;
			type.setResultadoSemantico(false);
			return type;
		}

	}

	// stmt_list
	private No stmt_list(No no) {
		No stmt_list = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "stmt_list");
		int indiceAnterior = indice;
		boolean verdadeiro = stmt(new No()).isResultadoSemantico()
				&& eat(Tag.PONTO_E_VIRGULA);
		if (!verdadeiro) {
			indice = indiceAnterior;
			stmt_list.setResultadoSemantico(false);
			return stmt_list;
		}
		while (verdadeiro) {
			verdadeiro = stmt(new No()).isResultadoSemantico()
					&& eat(Tag.PONTO_E_VIRGULA);
		}
		stmt_list.setResultadoSemantico(true);
		return stmt_list;

	}

	// stmt
	private No stmt(No no) {
		No stmt = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "stmt");
		int indiceAnterior = indice;

		No assign_stmt = assign_stmt(new No());
		if (assign_stmt.isResultadoSemantico()) {
			stmt.setResultadoSemantico(true);
			stmt.setTipo(assign_stmt.getTipo());
			return stmt;
		}

		No if_stmt = if_stmt(new No());
		if (if_stmt.isResultadoSemantico()) {
			stmt.setResultadoSemantico(true);
			stmt.setTipo(if_stmt.getTipo());
			return stmt;
		}

		No do_while_stmt = do_while_stmt(new No());
		if (do_while_stmt.isResultadoSemantico()) {
			stmt.setResultadoSemantico(true);
			stmt.setTipo(do_while_stmt.getTipo());
			return stmt;
		}

		No read_stmt = read_stmt(new No());
		if (read_stmt.isResultadoSemantico()) {
			stmt.setResultadoSemantico(true);
			stmt.setTipo(read_stmt.getTipo());
			return stmt;
		}

		No write_stmt = write_stmt(new No());
		if (write_stmt.isResultadoSemantico()) {
			stmt.setResultadoSemantico(true);
			stmt.setTipo(write_stmt.getTipo());
			return stmt;
		}

		indice = indiceAnterior;
		stmt.setResultadoSemantico(false);
		return stmt;

	}

	// assign_stmt
	private No assign_stmt(No no) {
		No assign_stmt = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "assign_stmt");
		int indiceAnterior = indice;

		No identifier = identifier(new No());
		if (identifier.isResultadoSemantico() && eat(Tag.ATRIBUICAO)) {
			No simple_expr = simple_expr(new No());
			if (simple_expr.isResultadoSemantico()) {
				if (identifier.getTipo() != simple_expr.getTipo()) {
					
					CompiladorUtils.LOGGER.log(Level.SEVERE, "Erro Semântico - Não é possível converter os tipos! ("+identifier.getTipo()+" para "+ simple_expr.getTipo()+")");
					assign_stmt.setTipo(No.ERRO);
					System.exit(1);

				} else {
					assign_stmt.setResultadoSemantico(true);
					assign_stmt.setTipo(identifier.getTipo());
					GerenciadorArquivoFonte.adicionarLinha("STOREL "+identifier.getPosicaoMemoria());
					return assign_stmt;
				}

			}

		}

		indice = indiceAnterior;
		assign_stmt.setResultadoSemantico(false);
		return assign_stmt;

	}

	// if_stmt
	private No if_stmt(No no) {
		int linhas = 0;
		No if_stmt = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "if_stmt");
		int indiceAnterior = indice;
		boolean verdadeiro;
		verdadeiro = eat(Tag.IF) && eat(Tag.ABRE_PARENTESES);
		if (!verdadeiro) {
			indice = indiceAnterior;
			if_stmt.setResultadoSemantico(false);
			return if_stmt;
		}
		No condition = condition(new No());
		verdadeiro = verdadeiro && condition.isResultadoSemantico()
				&& eat(Tag.FECHA_PARENTESES) && eat(Tag.BEGIN);
		linhas = (GerenciadorArquivoFonte.adicionarLinha("")); //Adicionar JUMP
		GerenciadorArquivoFonte.adicionarLinha(""); //Adicionar JUMP
		GerenciadorArquivoFonte.adicionarLinha((char) contador_labels+":");
		label_if=contador_labels;
		contador_labels++;
		if (!verdadeiro) {
			indice = indiceAnterior;
			if_stmt.setResultadoSemantico(false);
			return if_stmt;
		}
		No stmt_list = stmt_list(new No());
		verdadeiro = verdadeiro && stmt_list.isResultadoSemantico()
				&& eat(Tag.END);
		if (!verdadeiro) {
			indice = indiceAnterior;
			if_stmt.setResultadoSemantico(false);
			return if_stmt;
		}
		if (eat(Tag.ELSE)) {
			if (!diff){
				GerenciadorArquivoFonte.setLinha(linhas, "JZ "+(char) contador_labels);
			}else{
				GerenciadorArquivoFonte.setLinha(linhas, "JZ "+(char) label_if);
				GerenciadorArquivoFonte.setLinha(linhas+1, "JUMP "+(char) contador_labels);
				diff=false;
			}
			linhas = GerenciadorArquivoFonte.adicionarLinha(""); //Adicionar JUMP
			
			GerenciadorArquivoFonte.adicionarLinha((char)contador_labels+":");
			contador_labels++;
			if (eat(Tag.BEGIN) && stmt_list(new No()).isResultadoSemantico()
					&& eat(Tag.END)) {
				if (condition.getTipo().equals(No.BOOLEANO)) {
					if_stmt.setTipo(stmt_list.getTipo());
					if_stmt.setResultadoSemantico(true);
					GerenciadorArquivoFonte.setLinha(linhas, "JUMP "+(char) contador_labels);
					GerenciadorArquivoFonte.adicionarLinha((char) contador_labels+":");
					contador_labels++;
					return if_stmt;
				} else {
					CompiladorUtils.LOGGER.log(Level.SEVERE, "Erro Semântico - Condição do if deve retornar um booleano!");
					System.exit(1);
				}
			} else {
				indice = indiceAnterior;
				if_stmt.setResultadoSemantico(false);
				return if_stmt;
			}
		}
		if (condition.getTipo().equals(No.BOOLEANO)) {
			if_stmt.setTipo(stmt_list.getTipo());
			if_stmt.setResultadoSemantico(true);
			GerenciadorArquivoFonte.setLinha(linhas, "JUMP "+(char) contador_labels);
			GerenciadorArquivoFonte.adicionarLinha((char) contador_labels+":");
			contador_labels++;
			return if_stmt;
		} else {
			CompiladorUtils.LOGGER.log(Level.SEVERE, "Erro Semântico");
			System.exit(1);
			return if_stmt;
		}
	}

	// condition
	private No condition(No no) {
		No condition = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "condition");
		int indiceAnterior = indice;
		No expression = expression(new No());
		if (expression.isResultadoSemantico()) {
			condition.setTipo(expression.getTipo());
			condition.setResultadoSemantico(true);
			return condition;
		} else {
			indice = indiceAnterior;
			condition.setResultadoSemantico(false);
			return condition;
		}
	}

	// do_while_statement
	private No do_while_stmt(No no) {
		No do_while_stmt = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "do_while_stmt");
		int indiceAnterior = indice;
		if (eat(Tag.DO) && eat(Tag.BEGIN)) {
			int contador_inicio = contador_labels;
			GerenciadorArquivoFonte.adicionarLinha((char) contador_labels+":");
			contador_labels++;
			No stmt_list = stmt_list(new No());
			if (stmt_list.isResultadoSemantico() && eat(Tag.END)) {
				No stmt_sufix = stmt_sufix(new No());
				if (stmt_sufix.isResultadoSemantico()) {
					if (stmt_sufix.getTipo() == No.BOOLEANO) {
						do_while_stmt.setTipo(stmt_list.getTipo());
						do_while_stmt.setResultadoSemantico(true);
						GerenciadorArquivoFonte.adicionarLinha("JZ "+(char) contador_inicio);
						return do_while_stmt;
						
						
					} else {
						CompiladorUtils.LOGGER.log(Level.SEVERE,
								"Erro Semântico");
						System.exit(1);

					}
				}

			}

		}
		indice = indiceAnterior;
		do_while_stmt.setResultadoSemantico(false);
		return do_while_stmt;

	}

	// stmt_sufix
	private No stmt_sufix(No no) {
		No stmt_sufix = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "stmt_sufix");
		int indiceAnterior = indice;
		if (eat(Tag.WHILE) && eat(Tag.ABRE_PARENTESES)) {
			No condition = condition(new No());
			if (condition.isResultadoSemantico() && eat(Tag.FECHA_PARENTESES)) {
				stmt_sufix.setResultadoSemantico(true);
				stmt_sufix.setTipo(condition.getTipo());
				return stmt_sufix;
			}
		}
		indice = indiceAnterior;
		stmt_sufix.setResultadoSemantico(false);
		return stmt_sufix;

	}

	// read_stmt
	private No read_stmt(No no) {
		No read_stmt = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "read_stmt");
		int indiceAnterior = indice;
		if (eat(Tag.READ) && eat(Tag.ABRE_PARENTESES)) {
			No identifier = identifier(new No());
			if (identifier.isResultadoSemantico() && eat(Tag.FECHA_PARENTESES)) {
				read_stmt.setTipo(identifier.getTipo());
				read_stmt.setResultadoSemantico(true);
				GerenciadorArquivoFonte.adicionarLinha("READ");
				if (identifier.getTipo()==No.INTEIRO){
					GerenciadorArquivoFonte.adicionarLinha("ATOI");
					GerenciadorArquivoFonte.adicionarLinha("STOREL "+identifier.getPosicaoMemoria());
				}else{
					GerenciadorArquivoFonte.adicionarLinha("ATOF");
					GerenciadorArquivoFonte.adicionarLinha("STOREL "+identifier.getPosicaoMemoria());
				}
				return read_stmt;
			}

		}

		indice = indiceAnterior;
		read_stmt.setResultadoSemantico(false);
		return read_stmt;

	}

	// write_stmt
	private No write_stmt(No no) {
		No write_stmt = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "write_stmt");
		int indiceAnterior = indice;
		if (eat(Tag.WRITE) && eat(Tag.ABRE_PARENTESES)) {
			No writable = writable(new No());
			if (writable.isResultadoSemantico() && eat(Tag.FECHA_PARENTESES)) {
				write_stmt.setTipo(writable.getTipo());
				write_stmt.setResultadoSemantico(true);
				return write_stmt;

			}

		}

		indice = indiceAnterior;
		write_stmt.setResultadoSemantico(false);
		return write_stmt;

	}

	// writable
	private No writable(No no) {
		No writable = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "writable");
		int indiceAnterior = indice;
		No constant = constant(new No());
		if (constant.isResultadoSemantico()) {
			writable.setTipo(constant.getTipo());
			writable.setResultadoSemantico(true);
			if (writable.getTipo() == No.INTEIRO){
				GerenciadorArquivoFonte.adicionarLinha("PUSHI "+inteiro);
				GerenciadorArquivoFonte.adicionarLinha("WRITEI");
			}else{
				GerenciadorArquivoFonte.adicionarLinha("PUSHF "+real);
				GerenciadorArquivoFonte.adicionarLinha("WRITEF");
			}
			
			return writable;
		}

		No identifier = identifier(new No());
		if (identifier.isResultadoSemantico()) {
			writable.setTipo(identifier.getTipo());
			writable.setResultadoSemantico(true);
			GerenciadorArquivoFonte.adicionarLinha("PUSHL "+identifier.getPosicaoMemoria());
			if (identifier.getTipo() == No.INTEIRO){
				GerenciadorArquivoFonte.adicionarLinha("WRITEI");
			}
			else{
				GerenciadorArquivoFonte.adicionarLinha("WRITEF");
			}
			return writable;
		}

		if (eat(Tag.LITERAL)) {
			writable.setTipo(No.TEXTO);
			writable.setResultadoSemantico(true);
			GerenciadorArquivoFonte.adicionarLinha("PUSHS \""+literalAtual+"\"");
			GerenciadorArquivoFonte.adicionarLinha("WRITES");
			return writable;
		}

		indice = indiceAnterior;
		writable.setResultadoSemantico(false);
		return writable;

	}

	// expression
	private No expression(No no) {
		No expression = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "expression");
		int indiceAnterior = indice;
		No simple_expr1 = simple_expr(new No());
		No simple_expr2 = new No();
		simple_expr2.setResultadoSemantico(false);
		boolean verdadeiro = simple_expr1.isResultadoSemantico();
		No relop = relop(new No());
		if (relop.isResultadoSemantico()) {
			simple_expr2 = simple_expr(new No());
			verdadeiro = simple_expr2.isResultadoSemantico();
		}

		if (verdadeiro) {
			if (simple_expr2.isResultadoSemantico()) {
				if (simple_expr1.getTipo() == simple_expr2.getTipo()) {
					
					expression.setTipo(No.BOOLEANO);
					expression.setResultadoSemantico(true);
					if (simple_expr1.getTipo()==No.INTEIRO){
						if (relop.getTag()==Tag.EQ) GerenciadorArquivoFonte.adicionarLinha("EQUAL");
						if (relop.getTag()==Tag.GT) GerenciadorArquivoFonte.adicionarLinha("SUP");
						if (relop.getTag()==Tag.GE) GerenciadorArquivoFonte.adicionarLinha("SUPEQ");
						if (relop.getTag()==Tag.LT) GerenciadorArquivoFonte.adicionarLinha("INF");
						if (relop.getTag()==Tag.LE) GerenciadorArquivoFonte.adicionarLinha("INFEQ");
						if (relop.getTag()==Tag.DIFF){ 
							GerenciadorArquivoFonte.adicionarLinha("EQUAL");
							diff=true;
						}
					}else{
				
						if (relop.getTag()==Tag.EQ) GerenciadorArquivoFonte.adicionarLinha("EQUAL");
						if (relop.getTag()==Tag.GT) GerenciadorArquivoFonte.adicionarLinha("FSUP");
						if (relop.getTag()==Tag.GE) GerenciadorArquivoFonte.adicionarLinha("FSUPEQ");
						if (relop.getTag()==Tag.LT) GerenciadorArquivoFonte.adicionarLinha("FINF");
						if (relop.getTag()==Tag.LE) GerenciadorArquivoFonte.adicionarLinha("FINFEQ");
						if (relop.getTag()==Tag.DIFF){ 
							GerenciadorArquivoFonte.adicionarLinha("EQUAL");
							diff=true;
						}
					}
					return expression;
				} else {
					CompiladorUtils.LOGGER.log(Level.SEVERE, "Erro semântico");
					System.exit(1);
				}

			}

			expression.setTipo(simple_expr1.getTipo());
			expression.setResultadoSemantico(true);
			return expression;
		} else {
			indice = indiceAnterior;
			expression.setResultadoSemantico(false);
			return expression;
		}
	}

	// simpe_exp
	private No simple_expr(No no) {
		No simple_expr = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "simple_expr");
		int indiceAnterior = indice;
		No term = term(new No());
		if (term.isResultadoSemantico()
				&& simple_expr_gambi(term).isResultadoSemantico()) {
			simple_expr.setTipo(term.getTipo());
			simple_expr.setResultadoSemantico(true);
			return simple_expr;
		}

		indice = indiceAnterior;
		simple_expr.setResultadoSemantico(false);
		return simple_expr;

	}

	// simpe_expr_gambi
	private No simple_expr_gambi(No no) {
		No simple_expr_gambi = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "simple_expr_gambi");
		int indiceAnterior = indice;
		No addop = addop(new No());
		if (addop.isResultadoSemantico()) {
			No term = term(new No());
			if (term.isResultadoSemantico()
					&& term.getTipo() != simple_expr_gambi.getTipo()) {
				CompiladorUtils.LOGGER.log(Level.SEVERE, "Erro Semântico");
				System.exit(1);
			}
			if (term.isResultadoSemantico()
					&& simple_expr_gambi(term).isResultadoSemantico()) {
				simple_expr_gambi.setResultadoSemantico(true);
				simple_expr_gambi.setTipo(term.getTipo());
				if (addop.getTag()==Tag.MAIS){
					if (simple_expr_gambi.getTipo()==No.INTEIRO)
					GerenciadorArquivoFonte.adicionarLinha("ADD");
					else GerenciadorArquivoFonte.adicionarLinha("FADD");
						
				}
				else if (addop.getTag()==Tag.MENOS){
					if (simple_expr_gambi.getTipo()==No.INTEIRO)
					GerenciadorArquivoFonte.adicionarLinha("SUB");
					else GerenciadorArquivoFonte.adicionarLinha("FSUB");
				}
				else if (addop.getTag()==Tag.OR){
	
				}
				
				
				return simple_expr_gambi;
			} else {
				indice = indiceAnterior;
				simple_expr_gambi.setResultadoSemantico(false);
				return simple_expr_gambi;
			}
			// lambda
		} else {
			simple_expr_gambi.setResultadoSemantico(true);
			return simple_expr_gambi;
		}
	}

	// term
	private No term(No no) {
		No term = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "term");
		int indiceAnterior = indice;
		No factor_a = factor_a(new No());
		if (factor_a.isResultadoSemantico()
				&& term_gambi(factor_a).isResultadoSemantico()) {
			term.setTipo(factor_a.getTipo());
			term.setResultadoSemantico(true);
			return term;
		}
		indice = indiceAnterior;
		term.setResultadoSemantico(false);
		return term;

	}

	// term-gambi
	private No term_gambi(No no) {
		No term_gambi = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "term");
		int indiceAnterior = indice;
		No mulop = mulop(new No());
		if (mulop.isResultadoSemantico()) {
			No factor_a = factor_a(new No());
			if (factor_a.isResultadoSemantico()
					&& factor_a.getTipo() != term_gambi.getTipo()) {
				CompiladorUtils.LOGGER.log(Level.SEVERE, "Erro Semântico - Não é possível converter os tipos! ("+factor_a.getTipo()+" - "+term_gambi.getTipo()+")");
				System.exit(1);
			}
			if (factor_a.isResultadoSemantico()
					&& term_gambi(factor_a).isResultadoSemantico()) {
				term_gambi.setTipo(factor_a.getTipo());
				term_gambi.setResultadoSemantico(true);
				if (mulop.getTag()==Tag.VEZES){
					if (term_gambi.getTipo()==No.INTEIRO)GerenciadorArquivoFonte.adicionarLinha("MUL");
					else GerenciadorArquivoFonte.adicionarLinha("FMUL");
				}else if (mulop.getTag()==Tag.DIVISAO){
					if (term_gambi.getTipo()==No.INTEIRO)GerenciadorArquivoFonte.adicionarLinha("DIV");
					else GerenciadorArquivoFonte.adicionarLinha("FDIV");
				}
				
				return term_gambi;
			} else {
				indice = indiceAnterior;
				term_gambi.setResultadoSemantico(false);
				return term_gambi;
			}
		} else {
			term_gambi.setResultadoSemantico(true);
			return term_gambi;
		}
	}

	// factor_a
	private No factor_a(No no) {
		No factor_a = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "factor_a");
		int indiceAnterior = indice;
		if (eat(Tag.NOT)) {
			No factor = factor(new No());
			if (factor.isResultadoSemantico()) {
				factor_a.setTipo(factor.getTipo());
				factor_a.setResultadoSemantico(true);
				return factor_a;
			} else {
				indice = indiceAnterior;
				factor_a.setResultadoSemantico(false);
				return factor_a;
			}
		}
		if (eat(Tag.MENOS)) {
			No factor = factor(new No());
			if (factor.isResultadoSemantico()) {
				factor_a.setTipo(factor.getTipo());
				factor_a.setResultadoSemantico(true);
				return factor_a;
			} else {
				indice = indiceAnterior;
				factor_a.setResultadoSemantico(false);
				return factor_a;
			}
		}

		No factor = factor(new No());
		if (factor.isResultadoSemantico()) {
			factor_a.setTipo(factor.getTipo());
			factor_a.setResultadoSemantico(true);
			return factor_a;
		} else {
			indice = indiceAnterior;
			factor_a.setResultadoSemantico(false);
			return factor_a;
		}
	}

	// factor
	private No factor(No no) {
		No factor = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "factor");
		int indiceAnterior = indice;
		if (eat(Tag.ABRE_PARENTESES)) {
			No expression = expression(new No());
			if (expression.isResultadoSemantico() && eat(Tag.FECHA_PARENTESES)) {
				factor.setTipo(expression.getTipo());
				factor.setResultadoSemantico(true);
				return factor;
			}
		}
		No identifier = identifier(new No());
		if (identifier.isResultadoSemantico()) {
			factor.setTipo(identifier.getTipo());
			factor.setResultadoSemantico(true);
			GerenciadorArquivoFonte.adicionarLinha("PUSHL "+identifier.getPosicaoMemoria());
			return factor;

		}

		No constant = constant(new No());
		if (constant.isResultadoSemantico()) {
			factor.setTipo(constant.getTipo());
			factor.setResultadoSemantico(true);
			if (factor.getTipo() == No.INTEIRO){
				GerenciadorArquivoFonte.adicionarLinha("PUSHI "+inteiro);
			}else{
				GerenciadorArquivoFonte.adicionarLinha("PUSHF "+real);
			}
			return factor;

		}

		indice = indiceAnterior;
		factor.setResultadoSemantico(false);
		return factor;

	}

	// relop
	private No relop(No no) {
		No relop = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "relop");
		int indiceAnterior = indice;
		if (eat(Tag.EQ)) {
			relop.setResultadoSemantico(true);
			relop.setTag(Tag.EQ);
			return relop;
		}
		if (eat(Tag.GT)) {
			relop.setResultadoSemantico(true);
			relop.setTag(Tag.GT);
			return relop;
		}
		if (eat(Tag.GE)) {
			relop.setResultadoSemantico(true);
			relop.setTag(Tag.GE);
			return relop;
		}
		if (eat(Tag.LT)) {
			relop.setResultadoSemantico(true);
			relop.setTag(Tag.LT);
			return relop;
		}
		if (eat(Tag.LE)) {
			relop.setResultadoSemantico(true);
			relop.setTag(Tag.LE);
			return relop;
		}
		if (eat(Tag.DIFF)) {
			relop.setResultadoSemantico(true);
			relop.setTag(Tag.DIFF);
			return relop;
		}

		indice = indiceAnterior;
		relop.setResultadoSemantico(false);
		return relop;

	}

	// addop
	private No addop(No no) {
		No addop = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "addop");
		int indiceAnterior = indice;
		if (eat(Tag.MAIS)) {
			addop.setResultadoSemantico(true);
			addop.setTag(Tag.MAIS);
			return addop;
		}
		if (eat(Tag.MENOS)) {
			addop.setResultadoSemantico(true);
			addop.setTag(Tag.MENOS);
			return addop;
		}
		if (eat(Tag.OR)) {
			addop.setResultadoSemantico(true);
			addop.setTipo(No.BOOLEANO);
			addop.setTag(Tag.OR);
			return addop;
		}
		indice = indiceAnterior;
		addop.setResultadoSemantico(false);
		return addop;

	}

	// mulop
	private No mulop(No no) {
		No mulop = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "mulop");
		int indiceAnterior = indice;
		if (eat(Tag.VEZES)) {
			mulop.setResultadoSemantico(true);
			mulop.setTag(Tag.VEZES);
			return mulop;
		}
		if (eat(Tag.DIVISAO)) {
			mulop.setResultadoSemantico(true);
			mulop.setTag(Tag.DIVISAO);
			return mulop;
		}
		if (eat(Tag.AND)) {
			mulop.setResultadoSemantico(true);
			mulop.setTipo(No.BOOLEANO);
			mulop.setTag(Tag.AND);
			return mulop;
		}
		indice = indiceAnterior;
		mulop.setResultadoSemantico(false);
		return mulop;

	}

	// constant
	private No constant(No no) {
		No constant = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "constant");
		int indiceAnterior = indice;
		if (eat(Tag.NUM_INT)) {
			constant.setTipo(No.INTEIRO);
			constant.setResultadoSemantico(true);
			return constant;
		}
		if (eat(Tag.NUM_REAL)) {
			constant.setTipo(No.REAL);
			constant.setResultadoSemantico(true);
			return constant;
		}
		indice = indiceAnterior;
		constant.setResultadoSemantico(false);
		return constant;

	}

	// identifier
	private No identifier(No no) {
		No identifier = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "identifier");
		int indiceAnterior = indice;
		if (eat(Tag.ID)) {
			if (identifier.getTipo() != null) {
				indice = indiceAnterior;
				setTipoTabelaSimbolos(identifier.getTipo());
				identifier.setResultadoSemantico(true);
				identifier.setPosicaoMemoria(getPosicaoMemoriaTabelaSimbolos());
				return identifier;
			} else {
				indice = indiceAnterior;
				String tipo = getTipoTabelaSimbolos();
				if (!tipo.isEmpty()) {
					identifier.setTipo(tipo);
					identifier.setResultadoSemantico(true);
					identifier.setPosicaoMemoria(getPosicaoMemoriaTabelaSimbolos());
					return identifier;
				}else{
					CompiladorUtils.LOGGER.log(Level.SEVERE, "Erro Semântico - Variável foi utilizada sem ser declarada!");
					System.exit(1);
					return identifier;
					
				}

			}
		} else {
			indice = indiceAnterior;
			identifier.setResultadoSemantico(false);
			return identifier;

		}

	}

}
