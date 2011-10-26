/**
 * Class to generate tokens from a numerical expression.
 * The tokens can be operators or numbers.
 * @author Deepti Kamat
 *
 */
public class BCTokenizer {

	/** Expression to be evaluated */
	private String expr;

	/** Current index in expression to get token */
	private int exprIndex;

	/** Indicates if next token can be a signed number */
	private boolean canBeSigned = false;

	/***
	 * Checks if character is operator (+-/*)
	 * @param c character to be checked as operator
	 * @return true if character is operator
	 */
	private boolean isOperator(char c) {
		if(c=='-' || c=='+' || c=='*' || c=='/') return true;
		else return false;
	}

	/***
	 * Checks if character is valid for calculator
	 * @param c character to be checked is valid
	 * @return true if c is valid character for calculator
	 */
	private boolean isValidChar(char c) {
		if (Character.isDigit(c) || isOperator(c) || c=='.') return true;
		else return false;
	}

	/***
	 * Setting expression to be tokenized 
	 * @param expr expression to be tokenized
	 */
	public void setExpr(String expr) {
		this.expr = expr;								/* Setting numerical expression to be parsed */
		this.exprIndex =0;								/* Setting index to parse a string to 0 */
		this.canBeSigned = true;						/* First number can be a negative signed number */ 
	}

	/***
	 * Returns next token which can be operator or a number
	 * @return next token to be processed in calculator
	 * @throws BCException 
	 */
	public String getNextToken() throws BCException{
		StringBuffer tokenBuilder = new StringBuffer();	/* Build up a token in tokenBuilder and return immutable String token (Builder pattern :) ) */

		while(exprIndex<expr.length()) {				/* Parse the string from current exprIndex till next valid token is returned or till end of string */
			char c = expr.charAt(exprIndex);
			++exprIndex;

			if (Character.isWhitespace(c)) continue; 	/* Skip whitespace characters */

			if(!isValidChar(c))							/* Check if character is a valid character for this calculator */ 
				throw new BCException(" Tokenizer error (Invalid character found :"+c+")");	

			else if(this.isOperator(c))  				
			{ 
				if(canBeSigned) {						/* Operator can be a sign "+/-" for a number*/
					if(c!='+' && c!='-') throw new BCException(" Tokenizer error (Invalid sign for a number :"+c+")");
					tokenBuilder.append(c);									
					canBeSigned=false;					/* Next token immediately after signed number cannot be a signed number. */ 
					continue;
				}
				else if(tokenBuilder.length()==0){		/* Character is operator as tokenBuilder is empty i.e. no digits are collected in tokenbuilder */ 
					canBeSigned=true; 					/* Next token immediately after this operator can be a signed number. */
					return String.valueOf(c);			
				}
				else {
					--exprIndex;						/* A digit is built up till we find an operator. Rewind the index by one so that the operator is fetched next time. */ 
					return tokenBuilder.toString();
				}
			}	 
			else {										/* If not Operator it has to be a digit or '.' . So keep building up tokenBuilder */
				tokenBuilder.append(c);
				canBeSigned=false;						/* Next token immediately after a number cannot be a signed number. */
			}			
		}
		return (exprIndex==expr.length()) ? tokenBuilder.toString() : null;		/* Return the last token if index reached end. */ 
	}

}
