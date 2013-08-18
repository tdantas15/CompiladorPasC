package com.compiladores.analisadores;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;

import com.compiladores.util.CompiladorUtils;
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

	// Construtor
	public Parser(ArrayList<Token> tokens) {
		this.tokens = tokens;
	}

	// Métodos public

	// Realiza a análise sintática
	public boolean parse() {

		if (program()){
			return true;
		}else{
			CompiladorUtils.LOGGER.log(Level.SEVERE, "Erro sintático próximo a "+((atual.getClass() == Palavra.class)?((Palavra)atual).getLexema():((Numero)atual).getNumero()));
			return false;
		}
			

	}

	// Métodos private

	// Tipo o readch
	private void readAtual() {
		indice++;
		while(tokens.get(indice).getTag() == Tag.COMMENT){
			indice++;
		}
		if (indice < tokens.size()) {
			Token token = tokens.get(indice);
			if (token.getTag() != Tag.REGISTRO_SIMBOLOS)
				atual = token;
			else
				atual = CompiladorUtils.tabelaDeSimbolos.get(((Palavra) token)
						.getLexema()).getToken();
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
		return true;
	}

	// Program
	private boolean program() {
		No program = new No();
		CompiladorUtils.LOGGER.log(Level.INFO, "program");
		int indiceAnterior = indice;

		if (eat(Tag.INIT) && body(new No())) {
			return true;
		} else {
			indice = indiceAnterior;
			return false;
		}

	}

	// Body
	private boolean body(No no) {
		No body = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "body");
		int indiceAnterior = indice;
		boolean verdadeiro = true;
		while (verdadeiro) {
			verdadeiro = decl_list(new No());
		}

		if (eat(Tag.BEGIN) && stmt_list(new No()) && eat(Tag.END) && eat(Tag.PONTO)) {
			return true;

		} else {
			indice = indiceAnterior;
			return false;
		}

	}

	// decl-list
	private boolean decl_list(No no) {
		No decl_list = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "decl_list");

		int indiceAnterior = indice;
		boolean verdadeiro = true;
		verdadeiro = decl(new No()) && eat(Tag.PONTO_E_VIRGULA);
		if (!verdadeiro) {
			indice = indiceAnterior;
			return false;
		}
		while (verdadeiro) {
			verdadeiro = decl(new No()) && eat(Tag.PONTO_E_VIRGULA);
		}
		return true;
	}

	// decl
	private boolean decl(No no) {
		No decl = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "decl");
		int indiceAnterior = indice;
		if (type(new No()) && ident_list(new No())) {
			return true;
		} else {
			indice = indiceAnterior;
			return false;
		}

	}

	// ident-list
	private boolean ident_list(No no) {
		No ident_list = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "ident_list");

		int indiceAnterior = indice;
		boolean verdadeiro = true;
		verdadeiro = identifier(new No());
		if (!verdadeiro) {
			indice = indiceAnterior;
			return false;
		}
		while (verdadeiro) {
			verdadeiro = eat(Tag.VIRGULA);
			if (verdadeiro) {
				verdadeiro = identifier(new No());
				if (!verdadeiro) {
					indice = indiceAnterior;
					return false;
				}
			}

		}
		return true;

	}

	// type
	private boolean type(No no) {
		No type = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "type");
		int indiceAnterior = indice;
		if (eat(Tag.REAL))
			return true;
		else if (eat(Tag.INT)) {
			return true;
		} else {
			indice = indiceAnterior;
			return false;
		}

	}

	// stmt_list
	private boolean stmt_list(No no) {
		No stmt_list = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "stmt_list");
		int indiceAnterior = indice;
		boolean verdadeiro = stmt(new No()) && eat(Tag.PONTO_E_VIRGULA);
		if (!verdadeiro) {
			indice = indiceAnterior;
			return false;
		}
		while (verdadeiro) {
			verdadeiro = stmt(new No()) && eat(Tag.PONTO_E_VIRGULA);
		}
		return true;

	}

	// stmt
	private boolean stmt(No no) {
		No stmt = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "stmt");
		int indiceAnterior = indice;
		if (assign_stmt(new No()) || if_stmt(new No()) || do_while_stmt(new No()) || read_stmt(new No())
				|| write_stmt(new No())) {
			return true;
		} else {
			indice = indiceAnterior;
			return false;
		}

	}

	// assign_stmt
	private boolean assign_stmt(No no) {
		No assign_stmt = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "assign_stmt");
		int indiceAnterior = indice;
		if (identifier(new No()) && eat(Tag.ATRIBUICAO) && simple_expr(new No())) {
			return true;
		} else {
			indice = indiceAnterior;
			return false;
		}

	}

	// if_stmt
	private boolean if_stmt(No no) {
		No if_stmt = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "if_stmt");
		int indiceAnterior = indice;
		boolean verdadeiro;
		verdadeiro = eat(Tag.IF) && eat(Tag.ABRE_PARENTESES) && condition(new No())
				&& eat(Tag.FECHA_PARENTESES) && eat(Tag.BEGIN) && stmt_list(new No())
				&& eat(Tag.END);
		if (!verdadeiro) {
			indice = indiceAnterior;
			return false;
		}
		if (eat(Tag.ELSE)) {
			if (eat(Tag.BEGIN) && stmt_list(new No()) && eat(Tag.END)) {
				return true;
			} else {
				indice = indiceAnterior;
				return false;
			}
		}
		return true;
	}

	// condition
	private boolean condition(No no) {
		No condition = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "condition");
		int indiceAnterior = indice;
		if (expression(new No())) {
			return true;
		} else {
			indice = indiceAnterior;
			return false;
		}
	}

	// do_while_statement
	private boolean do_while_stmt(No no) {
		No do_while_stmt = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "do_while_stmt");
		int indiceAnterior = indice;
		if (eat(Tag.DO) && eat(Tag.BEGIN) && stmt_list(new No()) && eat(Tag.END)
				&& stmt_sufix(new No())) {
			return true;
		} else {
			indice = indiceAnterior;
			return false;
		}
	}

	// stmt_sufix
	private boolean stmt_sufix(No no) {
		No stmt_sufix = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "stmt_sufix");
		int indiceAnterior = indice;
		if (eat(Tag.WHILE) && eat(Tag.ABRE_PARENTESES) && condition(new No())
				&& eat(Tag.FECHA_PARENTESES)) {
			return true;
		} else {
			indice = indiceAnterior;
			return false;
		}
	}

	// read_stmt
	private boolean read_stmt(No no) {
		No read_stmt = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "read_stmt");
		int indiceAnterior = indice;
		if (eat(Tag.READ) && eat(Tag.ABRE_PARENTESES) && identifier(new No())
				&& eat(Tag.FECHA_PARENTESES)) {
			return true;
		} else {
			indice = indiceAnterior;
			return false;
		}
	}

	// write_stmt
	private boolean write_stmt(No no) {
		No write_stmt = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "write_stmt");
		int indiceAnterior = indice;
		if (eat(Tag.WRITE) && eat(Tag.ABRE_PARENTESES) && writable(new No())
				&& eat(Tag.FECHA_PARENTESES)) {
			return true;
		} else {
			indice = indiceAnterior;
			return false;
		}
	}

	// writable
	private boolean writable(No no) {
		No writable = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "writable");
		int indiceAnterior = indice;
		if (constant(new No()) || identifier(new No()) || eat(Tag.LITERAL)) {
			return true;
		} else {
			indice = indiceAnterior;
			return false;
		}
	}

	// expression
	private boolean expression(No no) {
		No expression = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "expression");
		int indiceAnterior = indice;
		boolean verdadeiro = simple_expr(new No());
		if (relop(new No())) {
			verdadeiro = simple_expr(new No());
		}

		if (verdadeiro)
			return true;
		else {
			indice = indiceAnterior;
			return false;
		}
	}

	// simpe_exp
	private boolean simple_expr(No no) {
		No simple_expr = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "simple_expr");
		int indiceAnterior = indice;
		if (term(new No()) && simple_expr_gambi(new No())) {
			return true;
		} else {
			indice = indiceAnterior;
			return false;
		}
	}

	// simpe_expr_gambi
	private boolean simple_expr_gambi(No no) {
		No simple_expr_gambi = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "simple_expr_gambi");
		int indiceAnterior = indice;
		if (addop(new No())) {
			if (term(new No()) && simple_expr_gambi(new No())) {
				return true;
			} else {
				indice = indiceAnterior;
				return false;
			}
			// lambda
		} else {
			return true;
		}
	}

	// term
	private boolean term(No no) {
		No term = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "term");
		int indiceAnterior = indice;
		if (factor_a(new No()) && term_gambi(new No())) {
			return true;
		} else {
			indice = indiceAnterior;
			return false;
		}
	}

	// term-gambi
	private boolean term_gambi(No no) {
		No term_gambi = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "term");
		int indiceAnterior = indice;
		if (mulop(new No())) {
			if (factor_a(new No()) && term_gambi(new No())) {
				return true;
			} else {
				indice = indiceAnterior;
				return false;
			}
		} else {
			return true;
		}
	}

	// factor_a
	private boolean factor_a(No no) {
		No factor_a = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "factor_a");
		int indiceAnterior = indice;
		if (eat(Tag.NOT)) {
			if (factor(new No())) {
				return true;
			} else {
				indice = indiceAnterior;
				return false;
			}
		}
		if (eat(Tag.MENOS)) {
			if (factor(new No())) {
				return true;
			} else {
				indice = indiceAnterior;
				return false;
			}
		}

		if (factor(new No())) {
			return true;
		} else {
			indice = indiceAnterior;
			return false;
		}
	}

	// factor
	private boolean factor(No no) {
		No factor = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "factor");
		int indiceAnterior = indice;
		if ((eat(Tag.ABRE_PARENTESES) && expression(new No())
				&& eat(Tag.FECHA_PARENTESES) || identifier(new No()) || constant(new No()))) {
			return true;
		} else {
			indice = indiceAnterior;
			return false;
		}
	}

	// relop
	private boolean relop(No no) {
		No relop = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "relop");
		int indiceAnterior = indice;
		if (eat(Tag.EQ))
			return true;
		if (eat(Tag.GT))
			return true;
		if (eat(Tag.GE))
			return true;
		if (eat(Tag.LT))
			return true;
		if (eat(Tag.LE))
			return true;
		if (eat(Tag.DIFF))
			return true;

		indice = indiceAnterior;
		return false;

	}

	// addop
	private boolean addop(No no) {
		No addop = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "addop");
		int indiceAnterior = indice;
		if (eat(Tag.MAIS))
			return true;
		if (eat(Tag.MENOS))
			return true;
		if (eat(Tag.OR))
			return true;
		indice = indiceAnterior;
		return false;

	}

	// mulop
	private boolean mulop(No no) {
		No mulop = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "mulop");
		int indiceAnterior = indice;
		if (eat(Tag.VEZES))
			return true;
		if (eat(Tag.DIVISAO))
			return true;
		if (eat(Tag.AND))
			return true;
		indice = indiceAnterior;
		return false;

	}

	// constant
	private boolean constant(No no) {
		No constant = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "constant");
		int indiceAnterior = indice;
		if (eat(Tag.NUM_INT))
			return true;
		if (eat(Tag.NUM_REAL))
			return true;
		indice = indiceAnterior;
		return false;

	}

	// identifier
	private boolean identifier(No no) {
		No identifier = no;
		CompiladorUtils.LOGGER.log(Level.INFO, "identifier");
		int indiceAnterior = indice;
		if (eat(Tag.ID)) {
			return true;
		} else {
			indice = indiceAnterior;
			return false;
		}

	}

}
