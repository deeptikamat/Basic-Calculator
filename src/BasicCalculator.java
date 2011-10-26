import java.util.Map;
import java.util.Stack;
import java.util.HashMap;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/***
 * Class to perform basic calculations in a numeric expression
 * @author Deepti Kamat
 * @version 1.0
 */
public class BasicCalculator {

	/** Map to store precedence values of operators */	
	private Map<String,Integer> precedenceMap = new HashMap<String,Integer>();

	/** Stack for operators */
	private Stack<String> operatorStack;

	/** Stack for numbers in expression and result values of intermediate evaluations */
	private Stack<Double> valueStack;

	public static void main(String[] args) {

		String exprLine = "";
		System.out.println("---------- BASIC CALCULATOR ---------- ");
		System.out.println(" To terminate the calculator type exit ");

		BasicCalculator bc = new BasicCalculator();					/* Instantiating and setting up object for BasicCalculator */
		bc.setup();

		BCTokenizer tokenizer = new BCTokenizer();					/* Instantiating tokenizer object */

		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(converter);

		while(true) {												/* Read lines from input in infinite loop until user ends calculator by typing 'exit' */
			try {
				exprLine = in.readLine();
				if("exit".equals(exprLine.toLowerCase())) {			/* Return when user enters 'exit' */
					return;
				}
				tokenizer.setExpr(exprLine);						/* Set up expression for tokenizer */
				double result = bc.evaluate(tokenizer);				/* Evaluate the expression using basic calculator */
				System.out.println(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method sets up the precedence map for operators in this calculator
	 * Precedence value for '+' and '-' is 1 whereas precedence value of '/' and '*' is 2 
	 */
	private void setup(){
		precedenceMap.put("-", 1);
		precedenceMap.put("+", 1);
		precedenceMap.put("/", 2);
		precedenceMap.put("*", 2);
	}

	/**
	 * Evaluates numerical expression and returns result
	 * @param tokenizer tokenizer object to return tokens in numerical expression
	 * @return result of evaluation
	 */
	public double evaluate(BCTokenizer tokenizer) {
		operatorStack = new Stack<String>();						/* Creating new stack for operators */
		valueStack = new Stack<Double>();							/* Creating new stack for values */
		try {
			String token = tokenizer.getNextToken();				/* Fetching next token from tokenizer */
			while (token!=null && token.length()!=0) {

				if(precedenceMap.containsKey(token)) {				/* Checks if token is operator. All operators are set up in precedenceMap. */

					/* If the precedence of token is less than or equal to precedance of operator on top of operator stack 
					 * higher precedence operation must be performed. 
					 * eg. if token is '+' and top of stack is '*' , multiplication will be performed on the values at the 
					 * top on valueStack and the result will be saved in valueStack itself. Also '+' is pushed on the stack.
					 * If token is '*' and top of stack is '-' , '*' is pushed on the stack. */
					if(!operatorStack.isEmpty() && precedenceMap.get(token) <= precedenceMap.get(operatorStack.peek())) {
						operate();					
					}
					operatorStack.push(token);						/* Push the token on operator stack */
				}
				else {
					valueStack.push(Double.parseDouble(token));		/* token is a number */
				}
				token = tokenizer.getNextToken();					/* Get next token from tokenizer */ 
			}

			/* At this point the entire numerical expression is parsed and all the values and operators are present on stacks */

			while(!operatorStack.isEmpty()) { 						/* Do operations on numbers in valueStack till operatorStack is empty */ 
				operate();
			}

			if(valueStack.isEmpty()) throw new BCException(" Not valid expression ");
			else return valueStack.pop();							/* The final result will be at the top of valueStack */ 
		}
		catch (BCException ex) {
			System.out.println(ex.getMessage());
		} 
		finally {
			operatorStack = null;									/* Set stacks to null so that values in them can be garbage collected */
			valueStack = null;
		}
		return 0.0;													/* Return 0.0 if expression evaluation did not succeed */ 
	}

	/**
	 * operate() performs operation which is on top of operatorStack on two number 
	 * values from the top of valueStack.
	 * The result is pushed on valueStack. 
	 * @throws BCException
	 */
	private void operate() throws BCException
	{
		Double rightNumber,leftNumber;
		String operator;
		 
		if(!operatorStack.isEmpty())								/* Pop the operator from operatorStack to perform operation */ 
			operator = operatorStack.pop(); else throw new BCException(" Operation Error (Operator not present)");

		if(!valueStack.isEmpty())									/* Pop right side Number from stack */ 
			rightNumber = valueStack.pop(); else throw new BCException(" Operation Error (Value absent for operator "+operator+")");

		if(!valueStack.isEmpty())									/* Pop left side Number from stack */ 
			leftNumber = valueStack.pop(); else throw new BCException(" Operation Error (Value absent for operator "+operator+")");

		if(operator.equals("+"))		valueStack.push(leftNumber+rightNumber);			/* Addition */
		else if(operator.equals("-"))	valueStack.push(leftNumber-rightNumber);			/* Subtraction */
		else if(operator.equals("/")) {														/* Division */
			if(rightNumber==0) throw new BCException(" Operation Error (Divide by 0)");
			valueStack.push(leftNumber/rightNumber);
		}
		else if(operator.equals("*"))	valueStack.push(leftNumber*rightNumber);			/* Multiplication */
	}
}
