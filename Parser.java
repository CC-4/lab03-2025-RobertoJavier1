/*
    Laboratorio No. 3 - Recursive Descent Parsing
    CC4 - Compiladores

    Clase que representa el parser

    Actualizado: agosto de 2021, Luis Cu
*/

import java.util.LinkedList;
import java.util.Stack;

public class Parser {

    // Puntero next que apunta al siguiente token
    private int next;
    // Stacks para evaluar en el momento
    private Stack<Double> operandos;
    private Stack<Token> operadores;
    // LinkedList de tokens
    private LinkedList<Token> tokens;

    // Funcion que manda a llamar main para parsear la expresion
    public boolean parse(LinkedList<Token> tokens) {
        this.tokens = tokens;
        this.next = 0;
        this.operandos = new Stack<Double>();
        this.operadores = new Stack<Token>();

        boolean aceptado = S();

        // Recursive Descent Parser
        // Imprime si el input fue aceptado
        System.out.println("Aceptada? " + aceptado);

        // Shunting Yard Algorithm
        // Imprime el resultado de operar el input
        if(aceptado){
            System.out.println("Resultado: " + this.operandos.peek());
        }
        
        // Verifica si terminamos de consumir el input
        if(this.next != this.tokens.size()) {
            return false;
        }
        return true;
    }

    // Verifica que el id sea igual que el id del token al que apunta next
    // Si si avanza el puntero es decir lo consume.
    private boolean term(int id) {
        if(this.next < this.tokens.size() && this.tokens.get(this.next).equals(id)) {
            
            // Codigo para el Shunting Yard Algorithm
            
            if (id == Token.NUMBER) {
				// Encontramos un numero
				// Debemos guardarlo en el stack de operandos
				operandos.push( this.tokens.get(this.next).getVal() );

			} else if (id == Token.SEMI) {
				// Encontramos un punto y coma
				// Debemos operar todo lo que quedo pendiente
				while (!this.operadores.empty()) {
					popOp();
				}
				
			}else if(id == Token.LPAREN){
                this.operadores.push(this.tokens.get(this.next));

            }else if (id == Token.RPAREN) {
                while (!this.operadores.empty() && !this.operadores.peek().equals(Token.LPAREN)) {
                    popOp();
                }
                if(this.operadores.empty()){
                    return false; 
                }
                this.operadores.pop();

            }else if (id == Token.UNARY) {

            }else {
				// Encontramos algun otro token, es decir un operador
				// Lo guardamos en el stack de operadores
				// Que pushOp haga el trabajo, no quiero hacerlo yo aqui
				pushOp( this.tokens.get(this.next) );
			}
			

            this.next++;
            return true;
        }
        return false;
    }

    // Funcion que verifica la precedencia de un operador
    private int pre(Token op) {
        /* TODO: Su codigo aqui */

        switch (op.getId()) {
            case Token.PLUS:
                return 1;
            case Token.MINUS:
                return 1;
            case Token.MULT:
                return 2;
            case Token.DIV:
                return 2;
            case Token.MOD:
                return 2;
            case Token.EXP:
                return 3;
            default:
                return -1;
        }

        /* El codigo de esta seccion se explicara en clase */

        // switch(op.getId()) {
        // 	case Token.PLUS:
        // 		return 1;
        // 	case Token.MULT:
        // 		return 2;
        // 	default:
        // 		return -1;
        // }
    }

    private void popOp() {
        Token op =  this.operadores.pop();

        /* TODO: Su codigo aqui */

        double b = this.operandos.pop();
        double a= this.operandos.pop();

        switch (op.getId()) {
            case Token.PLUS:
                this.operandos.push(a + b);
                break;
            case Token.MINUS:
                this.operandos.push(a - b);
                break;
            case Token.MULT:
                this.operandos.push(a * b);
                break;
            case Token.DIV:
                this.operandos.push(a / b);
                break;
            case Token.MOD: 
                this.operandos.push(a % b);
                break;
            case Token.EXP:
                this.operandos.push(Math.pow(a, b));
                break;
            default:
                System.out.println("Error: Operador desconocido " + op);
        }


        /* El codigo de esta seccion se explicara en clase */

        // if (op.equals(Token.PLUS)) {
        // 	double a = this.operandos.pop();
        // 	double b = this.operandos.pop();
        // 	// print para debug, quitarlo al terminar
        // 	System.out.println("suma " + a + " + " + b);
        // 	this.operandos.push(a + b);
        // } else if (op.equals(Token.MULT)) {
        // 	double a = this.operandos.pop();
        // 	double b = this.operandos.pop();
        // 	// print para debug, quitarlo al terminar
        // 	System.out.println("mult " + a + " * " + b);
        // 	this.operandos.push(a * b);
        // }
    }

    private void pushOp(Token op) {
        /* TODO: Su codigo aqui */

        int precedencia = pre(op);
        while (!this.operadores.empty()){
            Token topOperator = this.operadores.peek();
            int preTop = pre(topOperator);
            if(preTop < 0){
                break;
            }
            if(preTop > precedencia || (preTop == precedencia && !derechaAsociativa(op))) {
                popOp();
            } else {
                break;
            }
        }
        this.operadores.push(op);

        /* Casi todo el codigo para esta seccion se vera en clase */
    	
    	// Si no hay operandos automaticamente ingresamos op al stack

    	// Si si hay operandos:
    		// Obtenemos la precedencia de op
        	// Obtenemos la precedencia de quien ya estaba en el stack
        	// Comparamos las precedencias y decidimos si hay que operar
        	// Es posible que necesitemos un ciclo aqui, una vez tengamos varios niveles de precedencia
        	// Al terminar operaciones pendientes, guardamos op en stack

    }

    private boolean S() {
        return E() && term(Token.SEMI);
    }

    private boolean E() {
        return N() && E1();
    }

    private boolean E1() {
        while(lookAhead(Token.PLUS) || lookAhead(Token.MINUS)) {
            Token operator = actual();
            term(operator.getId());
            if(!N()) {
                return false;
            }
        }
        return true;
    }

    private boolean N() {
        return T() && N1();
    }

    private boolean N1() {
        while(lookAhead(Token.MULT) || lookAhead(Token.DIV) || lookAhead(Token.MOD)) {
            Token operator = actual();
            term(operator.getId());
            if(!T()) {
                return false;
            }
        }
        return true;
    }

    private boolean T() {
        if(!P()){
            return false;
        }
        return F1();
    }

    private boolean F1() {
        if(lookAhead(Token.EXP)) {
            Token operator = actual();
            term(operator.getId());
            if(!T()) {
                return false;
            }
        }
        return true;
    }

    private boolean P() {
        if (lookAhead(Token.UNARY)) {
            term(Token.UNARY);
            if (!P()) {
                return false;
            }
            double value = this.operandos.pop();
            this.operandos.push(-value);
            return true;
        } else if (lookAhead(Token.LPAREN)) {
            term(Token.LPAREN);
            if (!E()) {
                return false;
            }
            return term(Token.RPAREN);
        } else if (lookAhead(Token.NUMBER)) {
            return term(Token.NUMBER);
        }
        return false;
    }

    


    /* TODO: sus otras funciones aqui */

    private boolean lookAhead(int id) {
        if (this.next < this.tokens.size() && this.tokens.get(this.next).equals(id)) {
            return true;
        } else {
            return false;
        }
    }
    private Token actual() {
        if (this.next < this.tokens.size()) {
            return this.tokens.get(this.next);
        } else {
            return null;
        }
    }

    private boolean derechaAsociativa(Token op) {
        if (op != null && op.getId() == Token.EXP) {
            return true;
        }
        return false;
    }
}
