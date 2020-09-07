package poly;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class implements evaluate, add and multiply for polynomials.
 * 
 * @author runb-cs112
 *
 */
public class Polynomial {
	
	/**
	 * Reads a polynomial from an input stream (file or keyboard). The storage format
	 * of the polynomial is:
	 * <pre>
	 *     <coeff> <degree>
	 *     <coeff> <degree>
	 *     ...
	 *     <coeff> <degree>
	 * </pre>
	 * with the guarantee that degrees will be in descending order. For example:
	 * <pre>
	 *      4 5
	 *     -2 3
	 *      2 1
	 *      3 0
	 * </pre>
	 * which represents the polynomial:
	 * <pre>
	 *      4*x^5 - 2*x^3 + 2*x + 3 
	 * </pre>
	 * 
	 * @param sc Scanner from which a polynomial is to be read
	 * @throws IOException If there is any input error in reading the polynomial
	 * @return The polynomial linked list (front node) constructed from coefficients and
	 *         degrees read from scanner
	 */
	public static Node read(Scanner sc) 
	throws IOException {
		Node poly = null;
		while (sc.hasNextLine()) {
			Scanner scLine = new Scanner(sc.nextLine());
			poly = new Node(scLine.nextFloat(), scLine.nextInt(), poly);
			scLine.close();
		}
		return poly;
	}
	
	/**
	 * Returns the sum of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list
	 * @return A new polynomial which is the sum of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node add(Node poly1, Node poly2) {
		/** COMPLETE THIS METHOD **/
		Node ptr1 = poly1;
		Node ptr2 = poly2;
		Node addedPolys = new Node(0, 0, null);
		Node ptr3 = addedPolys;
		while (((ptr1 != null) && (ptr2 != null))|| 
				((ptr1 != null) && (ptr2 == null))||
				((ptr1 == null) && (ptr2 != null))) {
			if((ptr1 != null) && (ptr2 != null)){
				if (ptr1.term.degree > ptr2.term.degree) {
					ptr3.next = new Node(ptr1.term.coeff, ptr1.term.degree, null);
					ptr1 = ptr1.next;
				}
				else if (ptr2.term.degree > ptr1.term.degree) {
					ptr3.next = new Node(ptr2.term.coeff, ptr2.term.degree, null);
					ptr2 = ptr2.next;
				}
				else {
					if (ptr1.term.coeff + ptr2.term.coeff == 0.0) {
						ptr1 = ptr1.next;
						ptr2 = ptr2.next;
						continue;
					}
					ptr3.next = new Node (ptr1.term.coeff + ptr2.term.coeff, ptr1.term.degree, null);
					ptr1 = ptr1.next;
					ptr2 = ptr2.next;
				}
			}
			else if((ptr1 != null) && (ptr2 == null)){
				if (ptr1 != null) {
					ptr3.next = new Node (ptr1.term.coeff, ptr1.term.degree, null);
					ptr1 = ptr1.next;
				}
			}
			else if((ptr1 == null) && (ptr2 != null)){
				if (ptr2 != null) {
					ptr3.next = new Node (ptr2.term.coeff, ptr2.term.degree, null);
					ptr2 = ptr2.next;
				}
			}
			ptr3 = ptr3.next;
		}
		
		addedPolys = addedPolys.next;
		sortPolynomial(addedPolys);
		return (addedPolys);
	}
	
	/**
	 * Returns the product of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list)
	 * @return A new polynomial which is the product of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node multiply(Node poly1, Node poly2) {
		/** COMPLETE THIS METHOD **/
		Node ptr1 = poly1;
		Node ptr2 = poly2;
		Node temp = ptr2;
		Node expandedProduct = new Node (0, 0, null);
		Node ptr3 = expandedProduct; //one of the pointers in product LL 
		if ((ptr1 == null) || (ptr2 == null)) return null;
		
		
		while (ptr1 != null) {
			while (ptr2 != null) {
				ptr3.next = new Node(ptr1.term.coeff * ptr2.term.coeff, ptr1.term.degree + ptr2.term.degree, null);
				ptr2 = ptr2.next;
				ptr3 = ptr3.next;
			}
			ptr1 = ptr1.next;
			ptr2 = temp;
		}
		
		expandedProduct = expandedProduct.next;
		Node simplifyPtr = expandedProduct;
		
		while (simplifyPtr != null) {
	        Node temp2 = simplifyPtr; 
	        Node temp3 = temp2.next;
	        while (temp3 != null) {
	            if (simplifyPtr.term.degree == temp3.term.degree) {
	            	simplifyPtr.term.coeff = simplifyPtr.term.coeff + temp3.term.coeff;
	            	temp2.next = temp3.next;
	            }
	            temp2 = temp2.next;
	            temp3 = temp2.next;
	        }
	        simplifyPtr = simplifyPtr.next;
	    }
		sortPolynomial(expandedProduct);
		
		simplifyPtr = expandedProduct;
		Node prev = null;
		
		while (simplifyPtr != null && simplifyPtr.term.coeff == 0.0)  {  
            expandedProduct = simplifyPtr.next; 
            simplifyPtr = expandedProduct; 
        }  
		
		while (simplifyPtr != null) {    
            while (simplifyPtr != null && simplifyPtr.term.coeff != 0.0) {  
                prev = simplifyPtr;  
                simplifyPtr = simplifyPtr.next;  
            }  
            if (simplifyPtr == null) return expandedProduct;  

            prev.next = simplifyPtr.next;   
            simplifyPtr = prev.next;  
        }  
		return expandedProduct;
	}
		
	/**
	 * Evaluates a polynomial at a given value.
	 * 
	 * @param poly Polynomial (front of linked list) to be evaluated
	 * @param x Value at which evaluation is to be done
	 * @return Value of polynomial p at x
	 */
	public static float evaluate(Node poly, float x) {
		/** COMPLETE THIS METHOD **/
		Node ptr = poly;
		float answer = 0;
		if (ptr == null) return 0;
		else {
			while (ptr!=null) {
				answer += ((float)ptr.term.coeff * Math.pow(x, ptr.term.degree));
				ptr = ptr.next;
			}
		}
		return answer;
	}
	
	 private static void sortPolynomial(Node result) {
		 Node current = result;
		 Node ptr = null;
		 Node temp;
		 
		 if(result == null) {  
	            return;  
	        }  
	        else {  
	            while(current != null) {  
	                ptr = current.next; 	                
	                while(ptr != null) {   
	                    if(current.term.degree > ptr.term.degree) {  
	                        temp = new Node(current.term.coeff, current.term.degree, null);  
	                        current.term.coeff = ptr.term.coeff; 
	                        current.term.degree = ptr.term.degree;
	                        ptr.term.coeff = temp.term.coeff; 
	                        ptr.term.degree = temp.term.degree; 
	                    }  
	                    ptr = ptr.next;  
	                }  
	                current = current.next;  
	            } 
	        }
	 }

	/**
	 * Returns string representation of a polynomial
	 * 
	 * @param poly Polynomial (front of linked list)
	 * @return String representation, in descending order of degrees
	 */
	public static String toString(Node poly) {
		if (poly == null) {
			return "0";
		} 
		
		String retval = poly.term.toString();
		for (Node current = poly.next ; current != null ;
		current = current.next) {
			retval = current.term.toString() + " + " + retval;
		}
		return retval;
	}

}
