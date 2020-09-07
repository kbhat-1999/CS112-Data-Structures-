package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	String moreDelims = delims + "0123456789";
    	expr = expr.replaceAll(" ", "");
    	StringTokenizer token = new StringTokenizer(expr, moreDelims, true); //tokenizes expression and delims
    	//String temp = null;
    	while (token.hasMoreTokens()) {
    		String current = token.nextToken(); 
	 		if (!moreDelims.contains(current)) {
	 			boolean found = false;
	    			if (token.hasMoreTokens() && token.nextToken().equals("[")) {
	    				if(arrays.size() == 0) {
    						Array newArray = new Array(current);
    	    				arrays.add(newArray);
    	    				continue;
	    				}
	    				for (int i=0; i < arrays.size(); i++) {
	    					Array arr = arrays.get(i);
	    					if (arr.name.equals(current)) {
	    						found = true;
	    						break;
	    					}
	    				}
	    				if(!found) {
    						Array newArray = new Array(current);
    	    				arrays.add(newArray);
	    				}
	    			}
	    			else {
    					boolean varFound = false;
    					if(vars.size() == 0) {
    						Variable currVariable = new Variable(current);
    						vars.add(currVariable);
    					}else {
    						for (Variable j : vars) {
    							if (j.name.equals(current)) {
    								varFound = true;
    								break;
    							}
    						}
    						if(!varFound) {
    							Variable currVariable = new Variable(current);
    							vars.add(currVariable);
    						}
    					}
	    			}
	 		}
    	}
    	//System.out.println("==== Expression : ====" + expr);
    	//System.out.println("==== Arrays : ====" + arrays.toString());
    	//System.out.println("==== variables : ====" + vars.toString());
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	expr = expr.replaceAll(" ", "");
    	float finalAnswer = evaluateExpression(expr, vars, arrays);
    	return finalAnswer;
    }
    private static float evaluateExpression(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	double value = 0.0; 
        Stack<Double> values = new Stack<Double>();         
        // Stack for Operators 
        Stack<Character> operators = new Stack<Character>(); 
        Stack<Character> brackets = new Stack<Character>(); 
    	Stack<String> name = new Stack<String>();   
    	String moreDelims = delims + "0123456789";
    	char[] tokens = expr.toCharArray();
    	//System.out.println("Tokens length : " + tokens.length);
    	for(int i = 0; i < tokens.length; i++) {
    		char current = tokens[i];
    		//System.out.println("==== Current token : ====" + current);
    		//token is a number
    		if (current >= '0' && current <= '9') 
            { 
                StringBuffer temp = new StringBuffer(); 
                // There may be more than one digits in number 
                int j=i;
                while (j < tokens.length && tokens[j] >= '0' && tokens[j] <= '9') {
                    temp.append(tokens[j++]); 
                }
               // System.out.println("pushing number on stack" + temp.toString());
                values.push(Double.parseDouble(temp.toString())); 
                i=j-1;
            }
    		else if(!moreDelims.contains(String.valueOf(current))) {
    			boolean isVariable = false;
    			/*
    			 * Variable name can be multi-charactered such as 'varx', not just a or x and so on
    			 */
    			 StringBuffer tempStr = new StringBuffer();
    			 int j=i;
    			 while (j < tokens.length && !moreDelims.contains(String.valueOf(tokens[j]))) {
                     tempStr.append(tokens[j++]); 
                 }
    				
    			 String varName = tempStr.toString();
    			 for(Variable var :vars) {
    				if(var.name.equals(varName)) {
    					isVariable = true;
    					String tempVal = String.valueOf(var.value);
    				    //System.out.println("pushing number on stack" + tempVal);
    					values.push(Double.parseDouble(tempVal));   
    					break;
    				 }
    			 }
    			 
    			 if(!isVariable) {   				 
    				//System.out.println("==== pushing token on brackets stack =====" + tokens[j]);
    				//System.out.println("==== pushing name on names stack =====" + varName);
    				//operators.push(tokens[j]);
    				name.push(varName);
    				//j = j+1;
    				//getArrayIndex(tokens, j, -1, vars, arrays, values, operators, brackets, name);
    			 }
    			 i=j-1;
    		}
    		else if(current == '(') {
    			//token is an operator
    			operators.push(tokens[i]); 
    		}
    		// Closing brace encountered, solve entire brace 
            else if (current == ')') 
            { 
                while (operators.peek() != '(') {
                  value = computeValue(operators.pop(), values.pop(), values.pop());
                  values.push(value); 
                }
                operators.pop(); 
            } 
            else if(current == '[') {
            	brackets.push(current);
            	StringBuffer tempExpr = new StringBuffer(); 
            	int arrVal = -1;
                // There may be more than one digits in number 
                int j=i+1;
                while (j < tokens.length)  {
                	if(tokens[j] == '[') {
                		brackets.push(tokens[j]);
                		tempExpr.append(tokens[j++]);
                	}
                	else if(tokens[j] == ']') {
                    	brackets.pop();
                    	if(!brackets.isEmpty()) {
                    		tempExpr.append(tokens[j++]);
                    	}else {
                    		break;
                    	}
                    }else {
                    	tempExpr.append(tokens[j++]);
                    }     	
                }
                //System.out.println("===Expression ===" + tempExpr.toString());
                float indexVal = evaluateExpression(tempExpr.toString(), vars, arrays);          
        		String arrayName = name.pop();
        		//System.out.println("=== indexVal ===" + indexVal);
        		//System.out.println("=== popping array name from stack ===" + arrayName);
        		for(Array arr :arrays) {
        			if(arr.name.equals(arrayName)) {
        				int[] vals = arr.values;
        				arrVal = vals[(int)Math.round(indexVal)];
        				values.push(Double.parseDouble(String.valueOf(arrVal)));
        				break;
        			}
        		}
        		i=j;
            }
            else if (current == '+' || current == '-' || 
                    current == '*' || current == '/') 
           { 
               // While top of 'operator' has same or greater precedence to current 
               // token, which is an operator. Apply operator on top of 'operator' 
               // to top two elements in values stack 
               while (!operators.isEmpty() && operatorHasPrecedence(current, operators.peek())) {
            	 value = computeValue(operators.pop(), values.pop(), values.pop());
                 values.push(value); 
               }
 
               // Push current token to 'ops'. 
               //System.out.println("pushing operator on stack" + current);
               operators.push(current); 
           } 
    		
    	}
	      // Entire expression has been parsed at this point, apply remaining 
        // ops to remaining values 
        while (!operators.isEmpty()) {
            values.push(computeValue(operators.pop(), values.pop(), values.pop())); 
        }
  
        // Top of 'values' contains result, return it 
        Double dVal = values.pop();
        float answer = dVal.floatValue();
        return answer;
    }
    private static double computeValue(char op, double b, double a){ 
        switch (op) 
        { 
        case '+': 
            return a + b; 
        case '-': 
            return a - b; 
        case '*': 
            return a * b; 
        case '/': 
            if (b != 0) {
            	return a / b; 
            }
        } 
        return 0; 
    } 
    private static boolean operatorHasPrecedence(char op1, char op2) { 
        if (op2 == '(' || op2 == ')') 
            return false; 
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) 
            return false; 
        
        else
            return true; 
    } 
}

